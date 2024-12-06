package alarm.util;

import java.lang.reflect.Field;
import java.util.*;

public class ReflectionJsonUtil {
    public static <T> List<Field> getAllFieldsRecursive(T t) {
        Objects.requireNonNull(t);

        List<Field> fields = new ArrayList<>();
        collectFieldsRecursive(t.getClass(), fields);
        return fields;
    }

    public static void collectFieldsRecursive(Class<?> clazz, List<Field> fields) {
        if (clazz == null) return;

        // 현재 클래스의 모든 필드 추가
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // private 필드도 접근 가능하도록 설정
            fields.add(field);

            // 필드 타입이 클래스 타입이면 내부 필드도 탐색
            if (!field.getType().isPrimitive() && !field.getType().isEnum() && !field.getType().getName().startsWith("java.")) {
                collectFieldsRecursive(field.getType(), fields);
            }
        }

        // 상위 클래스 필드 탐색
        collectFieldsRecursive(clazz.getSuperclass(), fields);
    }

    public static <T> Map<String, String> getAllFieldValuesRecursive(T obj) {
        Objects.requireNonNull(obj);

        Map<String, String> fieldValues = new LinkedHashMap<>();
        try {
            for (Field field : getAllFieldsRecursive(obj)) {
                // 필드가 선언된 클래스와 현재 객체의 클래스가 일치하지 않으면 건너뜀
                if (!field.getDeclaringClass().isAssignableFrom(obj.getClass())) {
                    continue;
                }

                Object value = field.get(obj); // 필드 값 가져오기

                if (value != null) {
                    if (!field.getType().isPrimitive() && !field.getType().isEnum() && !field.getType().getName().startsWith("java.")) {
                        // 객체라면 내부 필드를 탐색하여 추가
                        Map<String, String> allFieldValuesRecursive = getAllFieldValuesRecursive(value);
                        fieldValues.put(field.getName(), allFieldValuesRecursive.toString());
                    } else {
                        // 원시 타입 또는 문자열로 변환 가능한 값
                        String valueString = value.toString();
                        fieldValues.put(field.getName(), valueString);
                    }
                } else {
                    // null 값 처리
                    fieldValues.put(field.getName(), null);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fieldValues;
    }

}
