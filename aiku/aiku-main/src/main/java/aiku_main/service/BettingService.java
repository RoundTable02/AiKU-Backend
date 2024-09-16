package aiku_main.service;

import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.dto.BettingAddDto;
import aiku_main.repository.BettingRepository;
import aiku_main.repository.ScheduleRepository;
import common.domain.Betting;
import common.domain.ExecStatus;
import common.domain.ScheduleMember;
import common.domain.Status;
import common.domain.member.Member;
import common.domain.value_reference.ScheduleMemberValue;
import common.exception.NoAuthorityException;
import common.exception.NotEnoughPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static aiku_main.application_event.event.PointChangeReason.BETTING;
import static aiku_main.application_event.event.PointChangeType.MINUS;
import static aiku_main.application_event.event.PointChangeType.PLUS;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BettingService {

    private final BettingRepository bettingRepository;
    private final ScheduleRepository scheduleRepository;
    private final PointChangeEventPublisher pointChangeEventPublisher;

    //TODO 전체적으로 알림 전송을 위한 카프카 연결 해야됨
    @Transactional
    public Long addBetting(Member member, Long scheduleId, BettingAddDto bettingDto){
        //검증 로직
        checkScheduleMember(member.getId(), scheduleId);
        checkScheduleUsable(scheduleId);
        checkEnoughPoint(member, bettingDto.getPointAmount());

        ScheduleMemberValue bettor = new ScheduleMemberValue(findScheduleMember(member.getId(), scheduleId));
        ScheduleMemberValue bettee = new ScheduleMemberValue(findScheduleMember(bettingDto.getBeteeMemberId(), scheduleId));

        //서비스 로직
        Betting betting = Betting.create(bettor, bettee, bettingDto.getPointAmount());
        bettingRepository.save(betting);

        pointChangeEventPublisher.publish(member.getId(), MINUS, bettingDto.getPointAmount(), BETTING, betting.getId());

        return betting.getId();
    }

    @Transactional
    public Long cancelBetting(Member member, Long scheduleId, Long bettingId){
        //검증 로직
        ScheduleMember bettor = findScheduleMember(member.getId(), scheduleId);
        Betting betting = bettingRepository.findById(bettingId).orElseThrow();
        checkBettingMember(betting, bettor);

        //서비스 로직
        betting.setStatus(Status.DELETE);

        pointChangeEventPublisher.publish(member.getId(), PLUS, betting.getPointAmount(), BETTING, bettingId);

        return betting.getId();
    }

    //==엔티티 조회 메서드==
    private ScheduleMember findScheduleMember(Long memberId, Long scheduleId) {
        return scheduleRepository.findScheduleMember(memberId, scheduleId).orElseThrow();
    }

    //==편의 메서드==
    private void checkScheduleUsable(Long scheduleId) {
        if(!scheduleRepository.existsByIdAndScheduleStatusAndStatus(scheduleId, ExecStatus.WAIT, Status.ALIVE)){
            throw new NoAuthorityException("유효하지 않은 스케줄입니다.");
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
