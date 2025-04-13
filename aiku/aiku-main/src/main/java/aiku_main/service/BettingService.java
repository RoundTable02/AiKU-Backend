package aiku_main.service;

import aiku_main.dto.betting.ScheduleBetting;
import aiku_main.dto.betting.ScheduleBettingMember;
import aiku_main.dto.betting.ScheduleBettingResult;
import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.dto.betting.BettingAddDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static aiku_main.application_event.event.PointChangeReason.*;
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
    public Long addBetting(Long memberId, Long scheduleId, BettingAddDto bettingDto){
        Member member = findMember(memberId);
        checkScheduleWait(scheduleId);

        ScheduleMemberValue bettor = new ScheduleMemberValue(findScheduleMemberId(member.getId(), scheduleId));
        ScheduleMemberValue bettee = new ScheduleMemberValue(findScheduleMemberId(bettingDto.getBeteeMemberId(), scheduleId));

        checkAlreadyHasBetting(bettor.getId(), scheduleId);
        checkEnoughPoint(member, bettingDto.getPointAmount());

        Betting betting = Betting.create(bettor, bettee, bettingDto.getPointAmount());
        bettingQueryRepository.save(betting);

        pointChangeEventPublisher.publish(
                memberId,
                MINUS,
                bettingDto.getPointAmount(),
                BETTING,
                betting.getId()
        );

        return betting.getId();
    }

    @Transactional
    public Long cancelBetting(Long memberId, Long scheduleId, Long bettingId){
        ScheduleMember bettor = findScheduleMember(memberId, scheduleId);
        Betting betting = findBettingById(bettingId);
        checkBettingMember(betting, bettor);

        betting.setStatus(DELETE);

        pointChangeEventPublisher.publish(
                memberId,
                PLUS,
                betting.getPointAmount(),
                BETTING_CANCLE,
                bettingId
        );

        return betting.getId();
    }

    @Transactional
    public void exitSchedule_deleteBettingForBettor(Long memberId, Long scheduleMemberId, Long scheduleId){
        bettingQueryRepository.findByBettorIdAndStatus(scheduleMemberId, ALIVE)
                .ifPresent((betting) -> {
                    betting.setStatus(DELETE);

                    pointChangeEventPublisher.publish(
                            memberId,
                            PLUS,
                            betting.getPointAmount(),
                            BETTING_CANCLE,
                            betting.getId()
                    );
                });
    }

    @Transactional
    public void exitSchedule_deleteBettingForBetee(Long memberId, Long scheduleMemberId, Long scheduleId){
        bettingQueryRepository.findByBeteeIdAndStatus(scheduleMemberId, ALIVE)
                .ifPresent((betting) -> {
                    betting.setStatus(DELETE);

                    Long memberIdOfBettor = scheduleQueryRepository.findMemberIdOfScheduleMember(betting.getBettor().getId()).orElseThrow();
                    pointChangeEventPublisher.publish(
                            memberIdOfBettor,
                            PLUS,
                            betting.getPointAmount(),
                            BETTING_CANCLE,
                            betting.getId()
                    );
                });
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
                pointChangeEventPublisher.publish(
                        scheduleMembers.get(betting.getBettor().getId()).getMember().getId(),
                        PLUS,
                        betting.getPointAmount(),
                        BETTING_REWARD,
                        betting.getId()
                );
            }
            return;
        }

        int bettingPointAmount = getBettingPointAmount(bettings);
        int winnerPointAmount = getWinnersPointAmount(scheduleMembers, bettings, latestTime);

        for (Betting betting : bettings) {
            if(scheduleMembers.get(betting.getBetee().getId()).getArrivalTime().equals(latestTime)){
                int rewardPoint = getBettingRewardPoint(betting.getPointAmount(), winnerPointAmount, bettingPointAmount);
                betting.setWin(rewardPoint);
                pointChangeEventPublisher.publish(
                        scheduleMembers.get(betting.getBettor().getId()).getMember().getId(),
                        PLUS,
                        rewardPoint,
                        BETTING_REWARD,
                        betting.getId()
                );
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

    private Member findMember(Long memberId){
        return memberRepository.findById(memberId).orElseThrow();
    }

    private Betting findBettingById(Long bettingId){
        return bettingQueryRepository.findByIdAndStatus(bettingId, ALIVE)
                .orElseThrow(() -> new BettingException(NO_SUCH_BETTING));
    }

    private ScheduleMember findScheduleMember(Long memberId, Long scheduleId) {
        return scheduleQueryRepository.findScheduleMember(memberId, scheduleId)
                .orElseThrow(() -> new ScheduleException(NOT_IN_SCHEDULE));
    }

    private Long findScheduleMemberId(Long memberId, Long scheduleId) {
        return scheduleQueryRepository.findScheduleMemberId(memberId, scheduleId)
                .orElseThrow(() -> new ScheduleException(NOT_IN_SCHEDULE));
    }

    private void checkScheduleWait(Long scheduleId) {
        if(!scheduleQueryRepository.existsByIdAndScheduleStatusAndStatus(scheduleId, WAIT, ALIVE)){
            throw new ScheduleException(NO_WAIT_SCHEDULE);
        }
    }

    private void checkAlreadyHasBetting(Long scheduleMemberIdOfBettor, Long scheduleId) {
        if(bettingQueryRepository.existBettorInSchedule(scheduleMemberIdOfBettor, scheduleId)){
            throw new BettingException(ALREADY_IN_BETTING);
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
