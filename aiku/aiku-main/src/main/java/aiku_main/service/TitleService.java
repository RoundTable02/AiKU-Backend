package aiku_main.service;

import aiku_main.exception.TitleException;
import aiku_main.kafka.KafkaProducerService;
import aiku_main.repository.TitleQueryRepository;
import common.domain.member.Member;
import common.domain.title.Title;
import common.domain.title.TitleCode;
import common.kafka_message.alarm.AlarmMemberInfo;
import common.kafka_message.alarm.AlarmMessageType;
import common.kafka_message.alarm.TitleGrantedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static common.domain.title.TitleCode.*;
import static common.kafka_message.KafkaTopic.alarm;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TitleService {

    private final TitleQueryRepository titleQueryRepository;
    private final KafkaProducerService kafkaProducerService;

    public List<Long> getMemberIdsInSchedule(Long scheduleId) {
        return titleQueryRepository.findMemberIdsInSchedule(scheduleId);
    }

    @Transactional
    public void checkAndGivePointsMoreThan10kTitle(List<Long> memberIds) {
        // ** 1만 포인트 달성 칭호 **

        // 해당 스케줄 조건 만족 멤버 불러오기
        List<Member> members = titleQueryRepository.find10kPointsMembersByMemberIds(memberIds);

        // 타이틀 조회
        Title title = getTitle(POINTS_MORE_THAN_10K);

        // 타이틀 부여
        giveTitleToMembers(title, members);
    }

    @Transactional
    public void checkAndGiveEarlyArrival10TimesTitle(List<Long> memberIds) {
        // ** 누적 일찍 도착 10회 이상 칭호 **

        // 해당 스케줄 조건 만족 멤버 불러오기
        List<Member> members = titleQueryRepository.findEarlyArrival10TimesMembersByMemberIds(memberIds);

        // 타이틀 조회
        Title title = getTitle(EARLY_ARRIVAL_10_TIMES);

        // 타이틀 부여
        giveTitleToMembers(title, members);
    }

    @Transactional
    public void checkAndGiveLateArrival5TimesTitle(List<Long> memberIds) {
        // ** 누적 지각 도착 5회 이상 칭호 **

        // 해당 스케줄 조건 만족 멤버 불러오기
        List<Member> members = titleQueryRepository.findLateArrival5TimesMembersByMemberIds(memberIds);

        // 타이틀 조회
        Title title = getTitle(LATE_ARRIVAL_5_TIMES);

        // 타이틀 부여
        giveTitleToMembers(title, members);
    }

    @Transactional
    public void checkAndGiveLateArrival10TimesTitle(List<Long> memberIds) {
        // ** 누적 지각 도착 5회 이상 칭호 **

        // 해당 스케줄 조건 만족 멤버 불러오기
        List<Member> members = titleQueryRepository.findLateArrival10TimesMembersByMemberIds(memberIds);

        // 타이틀 조회
        Title title = getTitle(LATE_ARRIVAL_10_TIMES);

        // 타이틀 부여
        giveTitleToMembers(title, members);
    }

    @Transactional
    public void checkAndGiveBettingWinning5TimesTitle(List<Long> memberIds) {
        // ** 누적 베팅 승리 5회 이상 칭호 **

        // 해당 스케줄 조건 만족 멤버 불러오기
        List<Member> members = titleQueryRepository.findBettingWinning5TimesMembersByMemberIds(memberIds);

        // 타이틀 조회
        Title title = getTitle(BETTING_WINNING_5_TIMES);

        // 타이틀 부여
        giveTitleToMembers(title, members);
    }

    @Transactional
    public void checkAndGiveBettingLosing10TimesTitle(List<Long> memberIds) {
        // ** 누적 베팅 승리 10회 이상 칭호 **

        // 해당 스케줄 조건 만족 멤버 불러오기
        List<Member> members = titleQueryRepository.findBettingLosing10TimesMembersByMemberIds(memberIds);

        // 타이틀 조회
        Title title = getTitle(BETTING_LOSING_10_TIMES);

        // 타이틀 부여
        giveTitleToMembers(title, members);
    }

    private Title getTitle(TitleCode titleCode) {
        return titleQueryRepository.findByTitleCode(titleCode)
                .orElseThrow(() -> new TitleException());
    }

    private void giveTitleToMembers(Title title, List<Member> members) {
        members.forEach(m -> {
            // 이미 해당 타이틀을 보유하고 있는지 확인
            if(!titleQueryRepository.existTitleMember(m.getId(), title.getId())) {
                title.giveTitleToMember(m);
                sendMessage(m, title);
            }
        });
    }

    private void sendMessage(Member member, Title title) {
        kafkaProducerService.sendMessage(alarm, new TitleGrantedMessage(
                List.of(member.getFirebaseToken()), AlarmMessageType.TITLE_GRANTED,
                title.getId(), title.getTitleName(), title.getTitleDescription(), title.getTitleCode()));
    }
}
