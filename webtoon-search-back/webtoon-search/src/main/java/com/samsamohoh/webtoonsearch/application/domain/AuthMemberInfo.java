package com.samsamohoh.webtoonsearch.application.domain;

import com.samsamohoh.webtoonsearch.exception.MemberDomainException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @- 회원 도메인 모델
 * @- OAuth2.0 기반의 회원 정보와 상태를 관리
 */
@Value
@Builder(access = AccessLevel.PRIVATE)
public class AuthMemberInfo {
    /**
     * 도메인 규칙 상수
     */
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_NAME_LENGTH = 2;

    /**
     * 핵심 도메인 필드
     */
    MemberId id;              // 회원 고유 식별자 - VO
    String providerId;        // OAuth 고유 식별자
    Provider provider;        // OAuth 제공자 (NAVER, GOOGLE) Enum
    Email email;             // 사용자 이메일 - VO
    Name name;               // 사용자 이름 - VO
    Role role;               // 회원 권한 (default: ROLE_USER) - Enum
    AgeRange ageRange;       // 연령대 (예: "20-29") - Enum
    Gender gender;           // 성별 (M/F) - Enum
    MemberStatus status;     // 회원 상태 - Enum
    EventStore eventStore;

    /**
     * 회원 ID Value Object
     */
    public record MemberId(long value) {
        public MemberId {
            if (value <= 0) {
                throw new IllegalArgumentException("Member ID must be positive");
            }
        }
    }

    /**
     * 이메일 Value Object
     */
    @Value
    public static class Email {
        String value;

        private Email(String value) {
            if (value == null || !value.matches(EMAIL_PATTERN)) {
                throw new IllegalArgumentException("Invalid email format");
            }
            this.value = value;
        }

        public static Email of(String value) {
            return new Email(value);
        }
    }

    /**
     * 이름 Value Object
     */
    @Value
    public static class Name {
        String value;

        private Name(String value) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            if (value.length() < MIN_NAME_LENGTH || value.length() > MAX_NAME_LENGTH) {
                throw new IllegalArgumentException(
                        "Name length must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH
                );
            }
            this.value = value.trim();
        }

        public static Name of(String value) {
            return new Name(value);
        }
    }

    /**
     * OAuth 제공자 정의
     */
    public enum Provider {
        NAVER("naver"),
        GOOGLE("google");

        private final String value;

        Provider(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Provider fromString(String text) {
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("Provider cannot be null or empty");
            }

            return switch (text.toLowerCase()) {
                case "naver" -> NAVER;
                case "google" -> GOOGLE;
                default -> throw new IllegalArgumentException("Unknown provider: " + text);
            };
        }
    }

    /**
     * 회원 권한 정의
     */
    public enum Role {
        USER("ROLE_USER"),
        ADMIN("ROLE_ADMIN");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Role fromString(String text) {
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("Role cannot be null or empty");
            }

            return switch (text.toUpperCase()) {
                case "ROLE_USER" -> USER;
                case "ROLE_ADMIN" -> ADMIN;
                default -> throw new IllegalArgumentException("Invalid role value: " + text);
            };
        }
    }

    /**
     * 회원 연령대 정의
     */
    public enum AgeRange {
        TEENAGERS("10-19"),
        TWENTIES("20-29"),
        THIRTIES("30-39"),
        FORTIES("40-49"),
        FIFTIES("50-59"),
        OVER_SIXTIES("60+");

        private final String value;

        AgeRange(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static AgeRange fromString(String text) {
            if (text == null || text.trim().isEmpty()) {
                return null;
            }

            return switch (text.toUpperCase()) {
                case "10-19" -> TEENAGERS;
                case "20-29" -> TWENTIES;
                case "30-39" -> THIRTIES;
                case "40-49" -> FORTIES;
                case "50-59" -> FIFTIES;
                case "60+" -> OVER_SIXTIES;
                default -> throw new IllegalArgumentException("Unknown age range: " + text);
            };
        }
    }

    /**
     * 회원 성별 정의
     */
    public enum Gender {
        MALE("M"),
        FEMALE("F");

        private final String value;

        Gender(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Gender fromString(String text) {
            if (text == null || text.trim().isEmpty()) {
                return null;
            }

            return switch (text.toUpperCase()) {
                case "M", "MALE" -> MALE;
                case "F", "FEMALE" -> FEMALE;
                default -> throw new IllegalArgumentException("Invalid gender value: " + text);
            };
        }
    }

    /**
     * 회원 상태 정의
     */
    public enum MemberStatus {
        ACTIVE("ACTIVE"),
        INACTIVE("INACTIVE"),
        WITHDRAWN("WITHDRAWN");

        private final String value;

        MemberStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public boolean isActive() {
            return this == ACTIVE;
        }

        // 상태 전이 규칙
        public boolean canTransitionTo(MemberStatus newStatus) {
            return switch (this) {
                case ACTIVE -> newStatus == INACTIVE || newStatus == WITHDRAWN;
                case INACTIVE -> newStatus == ACTIVE;
                case WITHDRAWN -> false;  // 탈퇴는 최종 상태
            };
        }

        public static MemberStatus fromString(String text) {
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("Status cannot be null or empty");
            }

            return Arrays.stream(MemberStatus.values())
                    .filter(status -> status.value.equals(text))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown status: " + text));
        }
    }

//********************************************************************************************************************//
//********************************************************************************************************************//
//********************************************************************************************************************//

    /**
     * 이벤트 저장소 인터페이스
     */
    private interface EventStore {
        void add(MemberEvent event);

        List<MemberEvent> getAll();

        void clear();

        default int getEventCount() {
            return getAll().size();
        }
    }

    /**
     * 기본 이벤트 저장소 구현
     */
    private static class SimpleEventStore implements EventStore {
        private final List<MemberEvent> events = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void add(MemberEvent event) {
            events.add(Objects.requireNonNull(event));
        }

        @Override
        public List<MemberEvent> getAll() {
            return List.copyOf(events);
        }

        @Override
        public void clear() {
            events.clear();
        }
    }

    /**
     * 동시성 지원 이벤트 저장소 구현
     */
    private static class ConcurrentEventStore implements EventStore {
        private final List<MemberEvent> events = new CopyOnWriteArrayList<>();

        @Override
        public void add(MemberEvent event) {
            events.add(Objects.requireNonNull(event));
        }

        @Override
        public List<MemberEvent> getAll() {
            return Collections.unmodifiableList(events);
        }

        @Override
        public void clear() {
            events.clear();
        }
    }

    // 현재는 항상 SimpleEventStore 만 사용
    // 동시성 처리가 필요한 경우 선택할 수 있도록 수정
    private static EventStore createEventStore(boolean concurrent) {
        return concurrent ? new ConcurrentEventStore() : new SimpleEventStore();
    }

    /**
     * 도메인 이벤트 정의
     */
    public sealed interface MemberEvent {
        record MemberCreated(
                MemberId memberId,
                String providerId,
                Provider provider) implements MemberEvent {
        }

        record MemberProfileCompleted(MemberId memberId) implements MemberEvent {
        }

        record MemberStatusChanged(MemberId memberId, MemberStatus oldStatus,
                                   MemberStatus newStatus) implements MemberEvent {
        }
    }

    /**
     * 도메인 이벤트 추가
     */
    protected void addDomainEvent(MemberEvent event) {
        eventStore.add(Objects.requireNonNull(event));
    }

    /**
     * 도메인 이벤트 조회
     */
    public List<MemberEvent> getDomainEvents() {
        return eventStore.getAll();
    }

    /**
     * 도메인 이벤트 초기화
     */
    public void clearDomainEvents() {
        eventStore.clear();
    }

//********************************************************************************************************************//
//********************************************************************************************************************//
//********************************************************************************************************************//

    /**
     * 신규 회원 생성
     */
    public static AuthMemberInfo createNewMember(
            String providerId,
            String provider,
            String email,
            String name,
            String age,
            String gender) {

        AuthMemberInfo authMemberInfo = builder()
                .providerId(providerId)
                .provider(Provider.fromString(provider))
                .email(Email.of(email))
                .name(Name.of(name))
                .ageRange(age != null ? AgeRange.fromString(age) : null)
                .gender(gender != null ? Gender.fromString(gender) : null)
                .status(MemberStatus.ACTIVE)
                .role(Role.USER)
                .eventStore(new SimpleEventStore())
                .build();

        authMemberInfo.validateState();

        return authMemberInfo;
    }

    /**
     * 영속성 데이터로부터 회원 도메인 객체 생성
     * DB에서 회원 정보를 조회할 때 필요
     */
    public static AuthMemberInfo createFromPersistence(
            long id,
            String providerId,
            String provider,
            String email,
            String name,
            String role,
            String ageRange,
            String gender,
            String status) {

        AuthMemberInfo authMemberInfo = builder()
                .id(new MemberId(id))
                .providerId(providerId)
                .provider(Provider.fromString(provider))
                .email(Email.of(email))
                .name(Name.of(name))
                .role(role != null ? Role.fromString(role) : Role.USER)
                .ageRange(ageRange != null ? AgeRange.fromString(ageRange) : null)
                .gender(gender != null ? Gender.fromString(gender) : null)
                .status(status != null ? MemberStatus.valueOf(status) : MemberStatus.ACTIVE)
                .eventStore(new SimpleEventStore())
                .build();

        authMemberInfo.validateState();
        return authMemberInfo;
    }

    /**
     * 회원 프로필 업데이트
     */
    public AuthMemberInfo updateProfile(Name newName, AgeRange newAgeRange, Gender newGender) {
        if (newName == null || newAgeRange == null || newGender == null) {
            throw new MemberDomainException("Name, age range and gender cannot be null");
        }

        // 기존 이벤트들 복사
        List<MemberEvent> existingEvents = new ArrayList<>(getDomainEvents());

        AuthMemberInfo updated = AuthMemberInfo.builder()
                .id(this.id)
                .providerId(this.providerId)
                .provider(this.provider)
                .email(this.email)
                .name(newName)
                .ageRange(newAgeRange)
                .gender(newGender)
                .status(this.status)
                .role(this.role)
                .eventStore(new SimpleEventStore())  // 새로운 이벤트 저장소 생성
                .build();

        // 기존 이벤트들 복원
        existingEvents.forEach(updated::addDomainEvent);

        // 새로운 이벤트 추가
        updated.addDomainEvent(new MemberEvent.MemberProfileCompleted(this.id));

        return updated;
    }

    // 회원 상태 변경
    public AuthMemberInfo updateStatus(MemberStatus newStatus) {
        if (newStatus == null) {
            throw new MemberDomainException("New status cannot be null");
        }

        // 상태 천이 규칙 검증 추가
        if (!this.status.canTransitionTo(newStatus)) {
            throw new MemberDomainException(
                    String.format("Invalid status transition from %s to %s", this.status, newStatus)
            );
        }

        // 기존 이벤트들 복사
        List<MemberEvent> existingEvents = new ArrayList<>(getDomainEvents());

        AuthMemberInfo updated = AuthMemberInfo.builder()
                .id(this.id)
                .providerId(this.providerId)
                .provider(this.provider)
                .email(this.email)
                .name(this.name)
                .ageRange(this.ageRange)
                .gender(this.gender)
                .status(newStatus)
                .role(this.role)
                .eventStore(new SimpleEventStore())
                .build();

        // 기존 이벤트들 복원
        existingEvents.forEach(updated::addDomainEvent);

        // 상태 변경 이벤트 추가
        updated.addDomainEvent(
                new MemberEvent.MemberStatusChanged(this.id, this.status, newStatus)
        );

        return updated;
    }

    /**
     * 추천 수신 가능 여부 확인
     */
    public boolean canReceiveRecommendations() {
        return status == MemberStatus.ACTIVE && hasRecommendationInfo();
    }

    /**
     * 추천 정보 존재 여부 확인
     */
    public boolean hasRecommendationInfo() {
        return ageRange != null && gender != null;
    }

    /**
     * 인구통계학적 매칭 확인
     */
    public boolean matchesDemographic(AgeRange targetAge, Gender targetGender) {
        return this.ageRange == targetAge && this.gender == targetGender;
    }

    /**
     * 활성 상태 확인
     */
    public boolean isActive() {
        return status == MemberStatus.ACTIVE;
    }

    /**
     * 상태 유효성 검증
     */
    private void validateState() {
        if (providerId == null || providerId.trim().isEmpty()) {
            throw new MemberDomainException("Provider ID cannot be empty");
        }
        if (provider == null) {
            throw new MemberDomainException("Provider cannot be null");
        }
        if (email == null) {
            throw new MemberDomainException("Email cannot be null");
        }
        if (name == null) {
            throw new MemberDomainException("Name cannot be null");
        }
        if (role == null) {
            throw new MemberDomainException("Role cannot be null");
        }
        if (status == null) {
            throw new MemberDomainException("Status cannot be null");
        }
        if (eventStore == null) {
            throw new MemberDomainException("EventStore cannot be null");
        }
    }
}
