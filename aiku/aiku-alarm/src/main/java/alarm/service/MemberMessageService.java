package alarm.service;

import alarm.controller.dto.MemberMessageDto;
import alarm.domain.MemberMessage;
import alarm.fcm.MessageSender;
import alarm.repository.MemberMessageRepository;
import alarm.repository.MemberRepository;
import common.kafka_message.alarm.AlarmMessage;
import common.kafka_message.alarm.AlarmMessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberMessageService {

    private final MemberMessageRepository memberMessageRepository;
    private final MemberRepository memberRepository;
    private final MessageSender messageSender;

    @Transactional
    public void sendAndSaveMessage(AlarmMessage message) {
        List<String> fcmTokens = message.getAlarmReceiverTokens();
        Map<String, String> messageData = message.getMessage();

        messageSender.sendMessage(messageData, fcmTokens);

        if (!message.getAlarmMessageType().equals(AlarmMessageType.MEMBER_REAL_TIME_LOCATION)) {
            // 멤버 실시간 위치는 직접적인 알림이 아니기 때문에 제외
            List<Long> memberIds = memberRepository.findMemberIdsByFirebaseTokenList(fcmTokens);

            List<MemberMessage> memberMessages = new ArrayList<>();
            memberIds.forEach(memberId -> memberMessages.add(
                            new MemberMessage(message.getAlarmMessageType(),
                                    memberId,
                                    message.getSimpleAlarmInfo()
                            )
                    )
            );

            memberMessageRepository.saveAll(memberMessages);
        }
    }

    public List<MemberMessageDto> getMemberMessageByMemberId(Long memberId) {
        return memberMessageRepository.findAllByMemberId(memberId).stream()
                .map(MemberMessageDto::toDto)
                .collect(Collectors.toList());
    }
}
