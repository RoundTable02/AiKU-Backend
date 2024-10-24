package aiku_main.service;

import aiku_main.application_event.domain.ScheduleBetting;
import aiku_main.application_event.domain.ScheduleBettingMember;
import aiku_main.application_event.domain.ScheduleBettingResult;
import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.dto.BettingAddDto;
import aiku_main.exception.BettingException;
import aiku_main.exception.ScheduleException;
import aiku_main.repository.BettingQueryRepository;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.ScheduleQueryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.domain.Betting;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import common.domain.member.Member;
import common.domain.value_reference.ScheduleMemberValue;
import common.exception.NotEnoughPoint;
import common.exception.PaidMemberLimitException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static aiku_main.application_event.event.PointChangeReason.BETTING;
import static aiku_main.application_event.event.PointChangeReason.BETTING_CANCLE;
import static aiku_main.application_event.event.PointChangeType.MINUS;
import static aiku_main.application_event.event.PointChangeType.PLUS;
import static common.domain.ExecStatus.TERM;
import static common.domain.ExecStatus.WAIT;
import static common.domain.Status.ALIVE;
import static common.domain.Status.DELETE;
import static common.response.status.BaseErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BettingService {

    private final MemberRepository memberRepository;
    private final BettingQueryRepository bettingQueryRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final PointChangeEventPublisher pointChangeEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long addBetting(Member member, Long scheduleId, BettingAddDto bettingDto){
        //검증 로직
        checkExistSchedule(scheduleId);
        checkScheduleWait(scheduleId);
        checkPaidScheduleMember(member.getId(), scheduleId);
        checkPaidScheduleMember(bettingDto.getBeteeMemberId(), scheduleId);

        ScheduleMemberValue bettor = new ScheduleMemberValue(findScheduleMember(member.getId(), scheduleId));
        ScheduleMemberValue bettee = new ScheduleMemberValue(findScheduleMember(bettingDto.getBeteeMemberId(), scheduleId));

        checkAlreadyHasBetting(bettor, scheduleId);
        checkEnoughPoint(member, bettingDto.getPointAmount());

        //서비스 로직
        Betting betting = Betting.create(bettor, bettee, bettingDto.getPointAmount());
        bettingQueryRepository.save(betting);

        pointChangeEventPublisher.publish(member, MINUS, bettingDto.getPointAmount(), BETTING, betting.getId());

        return betting.getId();
    }

    @Transactional
    public Long cancelBetting(Member member, Long scheduleId, Long bettingId){
        //검증 로직
        ScheduleMember bettor = findScheduleMember(member.getId(), scheduleId);
        Betting betting = findBettingById(bettingId);
        checkBettingMember(betting, bettor);

        //서비스 로직
        betting.setStatus(DELETE);

        pointChangeEventPublisher.publish(member, PLUS, betting.getPointAmount(), BETTING, bettingId);

        return betting.getId();
    }

    //==이벤트 핸들러==
    @Transactional
    public void exitSchedule_deleteBettingForBettor(Long memberId, Long scheduleMemberId, Long scheduleId){
        Betting bettingForBettor = bettingQueryRepository.findByBettorIdAndStatus(scheduleMemberId, ALIVE).orElse(null);
        bettingForBettor.setStatus(DELETE);

        int pointAmount = bettingForBettor.getPointAmount();
        Member member = memberRepository.findById(memberId).orElseThrow();
        pointChangeEventPublisher.publish(member, PLUS, pointAmount, BETTING_CANCLE, bettingForBettor.getId());
    }

    @Transactional
    public void exitSchedule_deleteBettingForBetee(Long memberId, Long scheduleMemberId, Long scheduleId){
        Betting bettingForBetee = bettingQueryRepository.findByBeteeIdAndStatus(scheduleMemberId, ALIVE).orElse(null);
        bettingForBetee.setStatus(DELETE);

        int pointAmount = bettingForBetee.getPointAmount();
        Member bettor = scheduleQueryRepository.findScheduleMemberWithMemberById(bettingForBetee.getBettor().getId()).orElseThrow()
                .getMember();
        pointChangeEventPublisher.publish(bettor, PLUS, pointAmount, BETTING_CANCLE, bettingForBetee.getId());
    }

    @Transactional
    public void processBettingResult(Long scheduleId) {
        List<Betting> bettings = bettingQueryRepository.findBettingsInSchedule(scheduleId, WAIT);
        if(bettings.isEmpty()){
            return;
        }

        Map<Long, ScheduleMember> scheduleMembers =
                scheduleQueryRepository.findScheduleMembersWithMember(scheduleId).stream()
                .collect(Collectors.toMap(
                        sm -> sm.getId(),
                        sm -> sm
                ));

        LocalDateTime latestTime = getLatestTimeOfLateMember(scheduleMembers.values());
        long winBettingCount = countWinBetting(scheduleMembers, bettings, latestTime);

        if(winBettingCount == 0) {
            for (Betting betting : bettings) {
                betting.setDraw();
                pointChangeEventPublisher.publish(scheduleMembers.get(betting.getBettor().getId()).getMember(),
                        PLUS, betting.getPointAmount(), BETTING, betting.getId());
            }
            return;
        }

        int bettingPointAmount = getBettingPointAmount(bettings);
        int winnerPointAmount = getWinnersPointAmount(scheduleMembers, bettings, latestTime);

        for (Betting betting : bettings) {
            if(scheduleMembers.get(betting.getBetee().getId()).getArrivalTime().equals(latestTime)){
                int rewardPoint = getBettingRewardPoint(betting.getPointAmount(), winnerPointAmount, bettingPointAmount);
                betting.setWin(rewardPoint);
                pointChangeEventPublisher.publish(scheduleMembers.get(betting.getBettor().getId()).getMember(),
                        PLUS, rewardPoint, BETTING, betting.getId());
            }else {
                betting.setLose();
            }
        }
    }

    private LocalDateTime getLatestTimeOfLateMember(Collection<ScheduleMember> scheduleMembers){
        return scheduleMembers.stream()
                .filter(scheduleMember -> scheduleMember.getArrivalTimeDiff() < 0)
                .max(Comparator.comparing(ScheduleMember::getArrivalTime))
                .map(ScheduleMember::getArrivalTime)
                .orElse(null);
    }

    private long countWinBetting(Map<Long, ScheduleMember> scheduleMembers, List<Betting> bettings, LocalDateTime latestTime){
        if (latestTime == null) return 0L;
        return bettings.stream()
                .filter(betting -> scheduleMembers.get(betting.getBetee().getId()).getArrivalTime().equals(latestTime))
                .count();
    }

    private int getBettingPointAmount(List<Betting> bettings){
        return bettings.stream().mapToInt(Betting::getPointAmount).sum();
    }

    private int getWinnersPointAmount(Map<Long, ScheduleMember> scheduleMembers, List<Betting> bettings, LocalDateTime latestTime){
        return bettings.stream()
                .filter(betting -> scheduleMembers.get(betting.getBetee().getId()).getArrivalTime().equals(latestTime))
                .mapToInt(Betting::getPointAmount)
                .sum();
    }

    private int getBettingRewardPoint(int bettingPoint, int winnerPointAmount, int bettingPointAmount) {
        return (int) ((double) bettingPoint / winnerPointAmount * bettingPointAmount);
    }

    @Transactional
    public void analyzeScheduleBettingResult(Long scheduleId) {
        List<Betting> bettings = bettingQueryRepository.findBettingsInSchedule(scheduleId, TERM);
        if(bettings.isEmpty()){
            return;
        }

        Map<Long, ScheduleMember> scheduleMembers =
                scheduleQueryRepository.findScheduleMembersWithMember(scheduleId).stream()
                        .collect(Collectors.toMap(
                                sm -> sm.getId(),
                                sm -> sm
                        ));

        List<ScheduleBetting> bettingDtoList = bettings.stream()
                .map(betting -> {
                    ScheduleBettingMember bettor = new ScheduleBettingMember(scheduleMembers.get(betting.getBettor().getId()).getMember());
                    ScheduleBettingMember betee = new ScheduleBettingMember(scheduleMembers.get(betting.getBetee().getId()).getMember());
                    return new ScheduleBetting(bettor, betee, betting.getPointAmount());
                }).toList();

        ScheduleBettingResult result = new ScheduleBettingResult(scheduleId, bettingDtoList);

        Schedule schedule = scheduleQueryRepository.findById(scheduleId).orElseThrow();
        try {
            schedule.setScheduleBettingResult(objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            throw new BettingException(INTERNAL_SERVER_ERROR, "Can't Parse ScheduleBettingResult");
        }
    }

    //==* 기타 메서드 *==
    private Betting findBettingById(Long bettingId){
        Betting betting = bettingQueryRepository.findByIdAndStatus(bettingId, ALIVE).orElse(null);
        if (betting == null) {
            throw new BettingException(NO_SUCH_BETTING);
        }
        return betting;
    }

    private void checkExistSchedule(Long scheduleId){
        if (!scheduleQueryRepository.existsByIdAndStatus(scheduleId, ALIVE)) {
            throw new ScheduleException(NO_SUCH_SCHEDULE);
        }
    }

    private ScheduleMember findScheduleMember(Long memberId, Long scheduleId) {
        ScheduleMember scheduleMember = scheduleQueryRepository.findScheduleMember(memberId, scheduleId).orElse(null);
        if (scheduleMember == null) {
            throw new ScheduleException(NOT_IN_SCHEDULE);
        }
        return scheduleMember;
    }

    private void checkScheduleWait(Long scheduleId) {
        if(!scheduleQueryRepository.existsByIdAndScheduleStatusAndStatus(scheduleId, WAIT, ALIVE)){
            throw new ScheduleException(NO_WAIT_SCHEDULE);
        }
    }

    private void checkAlreadyHasBetting(ScheduleMemberValue bettor, Long scheduleId) {
        if(bettingQueryRepository.existBettorInSchedule(bettor, scheduleId)){
            throw new BettingException(ALREADY_IN_BETTING);
        }
    }

    private void checkPaidScheduleMember(Long memberId, Long scheduleId){
        if(!scheduleQueryRepository.existPaidScheduleMember(memberId, scheduleId)){
            throw new PaidMemberLimitException();
        }
    }

    private void checkBettingMember(Betting betting, ScheduleMember bettor){
        if(!betting.getBettor().getId().equals(bettor.getId())){
            throw new BettingException(NOT_IN_BETTING);
        }
    }

    private void checkEnoughPoint(Member member, int point){
        if(member.getPoint() < point){
            throw new NotEnoughPoint();
        }
    }
}
