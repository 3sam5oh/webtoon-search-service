package com.samsamohoh.webtoonsearch.util;

/**
 * 보안 관련 유틸리티 메서드를 제공하는 클래스
 */
public final class SecurityUtils {
    private SecurityUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

   /**
    * 이메일 주소를 마스킹 처리
    * 예: "user@example.com" -> "us***@example.com"
    */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        return maskValue(parts[0]) + "@" + parts[1];
    }

    /**
     * @이름을-마스킹-처리
     * @- 영문: "John Doe" -> "Jo** D**"
     * @- 한글: "홍길동" -> "홍**"
     * @- 한글: "남궁민수" -> "남궁**"
     *
     * @규칙:
     * @1. 한글 이름: 성은 보존하고 이름은 '*'로 마스킹
     * @2. 영문 이름: 각 단어의 첫 2글자는 보존하고 나머지는 '*'로 마스킹
     * @3. 2글자 이하의 이름: 첫 글자만 보존하고 나머지는 '*'로 마스킹
     */
    public static String maskName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "***";
        }

        name = name.trim();

        // 2글자 이하 처리
        if (name.length() <= 2) {
            return name.substring(0, 1) + "*".repeat(name.length() - 1);
        }

        // 한글 이름 처리
        if (containsHangul(name)) {
            return maskKoreanName(name);
        }

        // 영문 이름 처리
        return maskEnglishName(name);
    }

    /**
     * 한글 포함 여부 확인
     */
    private static boolean containsHangul(String text) {
        return text.chars().anyMatch(c ->
                (c >= 0xAC00 && c <= 0xD7AF) ||  // 완성형 한글
                        (c >= 0x3131 && c <= 0x318E)     // 자음/모음
        );
    }

    /**
     * 한글 이름 마스킹 처리,
     * 성씨 처리 규칙 적용
     */
    private static String maskKoreanName(String name) {
        // 한글 성씨는 1~2글자가 일반적
        int surnameLength = getSurnameLength(name);
        return name.substring(0, surnameLength) +
                "*".repeat(name.length() - surnameLength);
    }

    /**
     * 성씨 길이 판단
     * - 2글자 성씨 (남궁, 선우 등) 처리
     */
    private static int getSurnameLength(String name) {
        // 흔한 2글자 성씨 목록
        String[] twoCharSurnames = {"남궁", "선우", "사공", "삼봉", "서문", "독고"};

        for (String surname : twoCharSurnames) {
            if (name.startsWith(surname)) {
                return 2;
            }
        }

        return 1; // 기본적으로 1글자 성씨로 처리
    }

    /**
     * 영문 이름 마스킹 처리,
     * 공백으로 구분된 각 단어의 처음 2글자는 보존
     */
    private static String maskEnglishName(String name) {
        String[] parts = name.split("\\s+");
        StringBuilder masked = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.length() <= 2) {
                masked.append(part.substring(0, 1))
                        .append("*".repeat(part.length() - 1));
            } else {
                masked.append(part.substring(0, 2))
                        .append("*".repeat(part.length() - 2));
            }

            if (i < parts.length - 1) {
                masked.append(" ");
            }
        }

        return masked.toString();
    }

    /**
     * 일반적인 문자열 마스킹 처리,
     * 문자열의 처음 2자와 마지막 2자를 제외한 나머지를 '*'로 변환
     */
    public static String maskValue(String value) {
        if (value == null) {
            return "***";
        }
        if (value.length() <= 4) {
            return value.substring(0, 1) + "*".repeat(Math.max(value.length() - 1, 1));
        }
        return value.substring(0, 2) + "*".repeat(value.length() - 4) + value.substring(value.length() - 2);
    }
}
