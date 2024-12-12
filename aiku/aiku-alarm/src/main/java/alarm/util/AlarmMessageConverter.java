package alarm.util;

import common.kafka_message.alarm.*;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@NoArgsConstructor
@Component
public class AlarmMessageConverter {

    // Firebase 알림 전달 용 메시지 생성
    public Map<String, String> getMessage(AlarmMessage alarmMessage) {
        return ReflectionJsonUtil.getAllFieldValuesRecursive(alarmMessage);
    }

    // DB 알림 저장 용
    public String getSimpleAlarmInfo(AlarmMessage alarmMessage) {
        switch (alarmMessage.getAlarmMessageType()) {
            case SCHEDULE_ADD -> {
                return getScheduleStatement(alarmMessage) + " 가 추가되었습니다.";
            }
            case SCHEDULE_ENTER -> {
                return getScheduleStatement(alarmMessage) + " 에 멤버가 입장하였습니다.";
            }
            case SCHEDULE_EXIT -> {
                return getScheduleStatement(alarmMessage) + " 에서 퇴장하였습니다.";
            }
            case SCHEDULE_UPDATE -> {
                return getScheduleStatement(alarmMessage) + " 이 업데이트 되었습니다.";
            }
            case SCHEDULE_OWNER -> {
                return getScheduleStatement(alarmMessage) + " 의 스케줄 장이 변경되었습니다.";
            }
            case SCHEDULE_OPEN -> {
                return getScheduleStatement(alarmMessage) + " 맵이 생성되었습니다!";
            }
            case SCHEDULE_AUTO_CLOSE -> {
                return getScheduleStatement(alarmMessage) + " 이 자동 종료되었습니다.";
            }
            case MEMBER_ARRIVAL -> {
                ArrivalAlarmMessage arrivalAlarmMessage = (ArrivalAlarmMessage) alarmMessage;
                return "약속 : " + arrivalAlarmMessage.getScheduleName() + "에서 멤버 " + arrivalAlarmMessage.getArriveMemberInfo().getNickname() + "가 약속 장소에 도착하였습니다!";
            }
            case SCHEDULE_MAP_CLOSE -> {
                ScheduleClosedMessage scheduleClosedMessage = (ScheduleClosedMessage) alarmMessage;
                return "약속 : " + scheduleClosedMessage.getScheduleName() + "가 종료되었습니다.";
            }
            case EMOJI -> {
                EmojiMessage emojiMessage = (EmojiMessage) alarmMessage;
                return "약속 : " + emojiMessage.getScheduleName() + "에서 멤버 " + emojiMessage.getSenderInfo().getNickname() + "가 " + emojiMessage.getEmojiType() + " 이모지를 전달했습니다.";
            }
            case ASK_RACING -> {
                AskRacingMessage askRacingMessage = (AskRacingMessage) alarmMessage;
                return "약속 : " + askRacingMessage.getScheduleName() + "에서 멤버 " + askRacingMessage.getFirstRacerInfo().getNickname() + "가 " + askRacingMessage.getPoint() + "아쿠의 레이싱을 신청하였습니다.";
            }
            case RACING_AUTO_DELETED -> {
                RacingAutoDeletedMessage racingAutoDeletedMessage = (RacingAutoDeletedMessage) alarmMessage;
                return "약속 : " + racingAutoDeletedMessage.getScheduleName() + "에서 멤버 " + racingAutoDeletedMessage.getSecondRacerInfo().getNickname() +
                        "에게 신청한 레이싱이 거절되었습니다.";
            }
            case RACING_DENIED -> {
                RacingDeniedMessage racingDeniedMessage = (RacingDeniedMessage) alarmMessage;
                return "약속 : " + racingDeniedMessage.getScheduleName() + "에서 멤버 " + racingDeniedMessage.getSecondRacerInfo().getNickname() +
                        "에게 신청한 레이싱이 거절되었습니다.";
            }
            case RACING_TERM -> {
                RacingTermMessage racingTermMessage = (RacingTermMessage) alarmMessage;
                return "약속 : " + racingTermMessage.getScheduleName() + "에서 멤버 " + racingTermMessage.getWinnerRacerInfo().getNickname() + "와 " + racingTermMessage.getLoserRacerInfo().getNickname()
                        + "의 레이싱이 "+ racingTermMessage.getWinnerRacerInfo().getNickname() + "의 승리로 종료되었습니다.";
            }
            case RACING_START -> {
                RacingStartMessage racingStartMessage = (RacingStartMessage) alarmMessage;
                return "약속 : " + racingStartMessage.getScheduleName() + "에서 멤버 " + racingStartMessage.getFirstRacerInfo().getNickname() + "와 " + racingStartMessage.getSecondRacerInfo().getNickname()
                        + "의 레이싱이 시작되었습니다.";
            }
            case TITLE_GRANTED -> {
                TitleGrantedMessage titleGrantedMessage = (TitleGrantedMessage) alarmMessage;
                return "칭호 : " + titleGrantedMessage.getTitleName() + "을 획득하였습니다!";
            }
            case PAYMENT_SUCCESS -> {
                PaymentSuccessMessage paymentSuccessMessage = (PaymentSuccessMessage) alarmMessage;
                return "결제 : " + paymentSuccessMessage.getPoint() + "원이 충전되었습니다.";
            }
            case PAYMENT_FAILED -> {
                PaymentFailedMessage paymentFailedMessage = (PaymentFailedMessage) alarmMessage;
                return "결제 : " + paymentFailedMessage.getPrice() + "원의 결제가 실패하였습니다.";
            }
            default -> {
                return null;
            }
        }
    }

    private String getScheduleStatement(AlarmMessage alarmMessage) {
        if (alarmMessage instanceof ScheduleAlarmMessage) {
            ScheduleAlarmMessage scheduleAlarmMessage = (ScheduleAlarmMessage) alarmMessage;
            return "약속 : " + scheduleAlarmMessage.getScheduleName() + " 가 추가되었습니다.";
        }
        else if (alarmMessage instanceof ScheduleMemberAlarmMessage) {
            ScheduleMemberAlarmMessage scheduleMemberAlarmMessage = (ScheduleMemberAlarmMessage) alarmMessage;
            return "약속 : " + scheduleMemberAlarmMessage.getScheduleName() + " 가 추가되었습니다.";
        }
        return "";
    }


}
