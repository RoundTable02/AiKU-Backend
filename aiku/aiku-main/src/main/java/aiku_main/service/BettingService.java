package aiku_main.service;

import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.dto.BettingAddDto;
import aiku_main.exception.CanNotBettingException;
import aiku_main.repository.BettingRepository;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.ScheduleRepository;
import common.domain.Betting;
import common.domain.ExecStatus;
import common.domain.ScheduleMember;
import common.domain.Status;
import common.domain.member.Member;
import common.domain.value_reference.ScheduleMemberValue;
import common.exception.BaseExceptionImpl;
import common.exception.NoAuthorityException;
import common.exception.NotEnoughPoint;
import common.response.status.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static aiku_main.application_event.event.PointChangeReason.BETTING;
import static aiku_main.application_event.event.PointChangeReason.BETTING_CANCLE;
import static aiku_main.application_event.event.PointChangeType.MINUS;
import static aiku_main.application_event.event.PointChangeType.PLUS;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BettingService {

    private final MemberRepository memberRepository;
    private final BettingRepository bettingRepository;
    private final ScheduleRepository scheduleRepository;
    private final PointChangeEventPublisher pointChangeEventPublisher;

    //TODO 전체적으로 알림 전송을 위한 카프카 연결 해야됨
    @Transactional
    public Long addBetting(Member member, Long scheduleId, BettingAddDto bettingDto){
        //검증 로직
        checkScheduleMember(member.getId(), scheduleId);
        checkScheduleUsable(scheduleId);

        ScheduleMemberValue bettor = new ScheduleMemberValue(findScheduleMember(member.getId(), scheduleId));
        ScheduleMemberValue bettee = new ScheduleMemberValue(findScheduleMember(bettingDto.getBeteeMemberId(), scheduleId));

        checkAlreadyHasBetting(bettor, scheduleId);
        checkEnoughPoint(member, bettingDto.getPointAmount());

        //서비스 로직
        Betting betting = Betting.create(bettor, bettee, bettingDto.getPointAmount());
        bettingRepository.save(betting);

        pointChangeEventPublisher.publish(member, MINUS, bettingDto.getPointAmount(), BETTING, betting.getId());

        return betting.getId();
    }

    @Transactional
    public Long cancelBetting(Member member, Long scheduleId, Long bettingId){
        //검증 로직
        ScheduleMember bettor = findScheduleMember(member.getId(), scheduleId);
        Betting betting = bettingRepository.findById(bettingId).orElseThrow();
        checkBettingMember(betting, bettor);
        checkBettingAlive(betting);

        //서비스 로직
        betting.setStatus(Status.DELETE);

        pointChangeEventPublisher.publish(member, PLUS, betting.getPointAmount(), BETTING, bettingId);

        return betting.getId();
    }

    //==이벤트 핸들러==
    @Transactional
    public void exitSchedule_deleteBettingForBettor(Long memberId, Long scheduleMemberId, Long scheduleId){
        Betting bettingForBettor = bettingRepository.findByBettorIdAndStatus(scheduleMemberId, Status.ALIVE).orElse(null);
        bettingForBettor.setStatus(Status.DELETE);

        int pointAmount = bettingForBettor.getPointAmount();
        Member member = memberRepository.findById(memberId).orElseThrow();
        pointChangeEventPublisher.publish(member, PLUS, pointAmount, BETTING_CANCLE, bettingForBettor.getId());
    }

    @Transactional
    public void exitSchedule_deleteBettingForBetee(Long memberId, Long scheduleMemberId, Long scheduleId){
        Betting bettingForBetee = bettingRepository.findByBeteeIdAndStatus(scheduleMemberId, Status.ALIVE).orElse(null);
        bettingForBetee.setStatus(Status.DELETE);

        int pointAmount = bettingForBetee.getPointAmount();
        Member bettor = scheduleRepository.findScheduleMemberWithMemberById(bettingForBetee.getBettor().getId()).orElseThrow()
                .getMember();
        pointChangeEventPublisher.publish(bettor, PLUS, pointAmount, BETTING_CANCLE, bettingForBetee.getId());

        //TODO 베터한테 푸쉬 알림 줘야함. 강제 베팅 취소기 때문에..
    }

    //==엔티티 조회 메서드==
    private ScheduleMember findScheduleMember(Long memberId, Long scheduleId) {
        return scheduleRepository.findAliveScheduleMember(memberId, scheduleId).orElseThrow();
    }

    //==편의 메서드==
    private void checkScheduleUsable(Long scheduleId) {
        if(!scheduleRepository.existsByIdAndScheduleStatusAndStatus(scheduleId, ExecStatus.WAIT, Status.ALIVE)){
            throw new NoAuthorityException("유효하지 않은 스케줄입니다.");
        }
    }

    private void checkBettingAlive(Betting betting) {
        if (betting.getStatus() == Status.DELETE){
            throw new BaseExceptionImpl(BaseErrorCode.FORBIDDEN, "취소된 베팅입니다.");
        }
    }

    private void checkAlreadyHasBetting(ScheduleMemberValue bettor, Long scheduleId) {
        if(bettingRepository.existBettorInSchedule(bettor, scheduleId)){
            throw new CanNotBettingException();
        }
    }

    private void checkScheduleMember(Long memberId, Long scheduleId){
        if(!scheduleRepository.existScheduleMember(memberId, scheduleId)){
            throw new NoAuthorityException();
        }
    }

    private void checkBettingMember(Betting betting, ScheduleMember bettor){
        if(betting.getBettor().getId() != bettor.getId()){
            throw new NoAuthorityException();
        }
    }

    private void checkEnoughPoint(Member member, int point){
        if(member.getPoint() < point){
            throw new NotEnoughPoint();
        }
    }
}
