//package com.samsamohoh.webtoonsearch.domain;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.databind.json.JsonMapper;
//import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;
//import org.junit.jupiter.api.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Arrays;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@DisplayName("SearchableWebtoon 도메인 단위 테스트")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class SearchableWebtoonUnitTest {
//
//    private static final Logger logger = LoggerFactory.getLogger(SearchableWebtoonUnitTest.class);
//    private static final ObjectMapper objectMapper = JsonMapper.builder()
//            .enable(SerializationFeature.INDENT_OUTPUT)
//            .build();
//
//    private SearchableWebtoon sampleWebtoon;
//
//    @BeforeEach
//    void setUp() {
//        sampleWebtoon = SearchableWebtoon.builder()
//                .id("kakao_3727")
//                .title("[드라마 외전] 힘쎈여자 황금주")
//                .provider("KAKAO")
//                .updateDays(Arrays.asList("MON", "TUE"))
//                .url("https://webtoon.kakao.com/content/힘쎈여자-황금주/3727")
//                .thumbnail(Arrays.asList("https://kr-a.kakaopagecdn.com/thumbnail.png"))
//                .isEnd(false)
//                .isFree(true)
//                .isUpdated(true)
//                .ageGrade(15)
//                .freeWaitHour(0)
//                .authors(Arrays.asList("이원식", "참치캔"))
//                .build();
//
//        logger.info("테스트 설정 완료: 샘플 웹툰 생성됨");
//    }
//
//    @Nested
//    @DisplayName("생성 및 기본 속성")
//    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//    class CreateAndBasicProperties {
//
//        @Test
//        @Order(1)
//        @DisplayName("빌더를 사용하여 SearchableWebtoon 객체를 올바르게 생성해야 한다")
//        void createSearchableWebtoon() throws Exception {
//            logger.info("생성된 SearchableWebtoon 객체: {}", objectMapper.writeValueAsString(sampleWebtoon));
//
//            assertThat(sampleWebtoon)
//                    .hasFieldOrPropertyWithValue("id", "kakao_3727")
//                    .hasFieldOrPropertyWithValue("title", "[드라마 외전] 힘쎈여자 황금주")
//                    .hasFieldOrPropertyWithValue("provider", "KAKAO")
//                    .hasFieldOrPropertyWithValue("updateDays", Arrays.asList("MON", "TUE"))
//                    .hasFieldOrPropertyWithValue("url", "https://webtoon.kakao.com/content/힘쎈여자-황금주/3727")
//                    .hasFieldOrPropertyWithValue("thumbnail", Arrays.asList("https://kr-a.kakaopagecdn.com/thumbnail.png"))
//                    .hasFieldOrPropertyWithValue("isEnd", false)
//                    .hasFieldOrPropertyWithValue("isFree", true)
//                    .hasFieldOrPropertyWithValue("isUpdated", true)
//                    .hasFieldOrPropertyWithValue("ageGrade", 15)
//                    .hasFieldOrPropertyWithValue("freeWaitHour", 0)
//                    .hasFieldOrPropertyWithValue("authors", Arrays.asList("이원식", "참치캔"));
//
//            logger.info("SearchableWebtoon 객체 생성 테스트 통과");
//        }
//
//        @Test
//        @Order(2)
//        @DisplayName("기본값이 적용된 SearchableWebtoon 객체를 생성해야 한다")
//        void createSearchableWebtoonWithDefaults() {
//            SearchableWebtoon defaultWebtoon = SearchableWebtoon.builder()
//                    .id("test_id")
//                    .title("Test Title")
//                    .provider("TEST")
//                    .url("http://test.com")
//                    .build();
//
//            logger.info("기본값이 적용된 SearchableWebtoon 객체: {}", defaultWebtoon);
//
//            assertThat(defaultWebtoon.getUpdateDays()).isNull();
//            assertThat(defaultWebtoon.getThumbnail()).isNull();
//            assertThat(defaultWebtoon.getAuthors()).isNull();
//            assertThat(defaultWebtoon.isEnd()).isFalse();
//            assertThat(defaultWebtoon.isFree()).isFalse();
//            assertThat(defaultWebtoon.isUpdated()).isFalse();
//            assertThat(defaultWebtoon.getAgeGrade()).isZero();
//            assertThat(defaultWebtoon.getFreeWaitHour()).isNull();
//
//            logger.info("기본값 적용 테스트 통과");
//        }
//    }
//
//    @Nested
//    @DisplayName("비즈니스 로직")
//    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//    class BusinessLogic {
//
//        @Test
//        @Order(1)
//        @DisplayName("성인용 웹툰 판별 로직이 올바르게 동작해야 한다")
//        void isForAdults() {
//            SearchableWebtoon adultWebtoon = SearchableWebtoon.builder()
//                    .id("adult_1")
//                    .title("Adult Webtoon")
//                    .provider("TEST")
//                    .url("http://test.com")
//                    .ageGrade(19)
//                    .build();
//
//            SearchableWebtoon teenWebtoon = SearchableWebtoon.builder()
//                    .id("teen_1")
//                    .title("Teen Webtoon")
//                    .provider("TEST")
//                    .url("http://test.com")
//                    .ageGrade(15)
//                    .build();
//
//            logger.info("성인용 웹툰 (19세 이상) 확인: {}", adultWebtoon.isForAdults());
//            logger.info("청소년용 웹툰 (15세) 확인: {}", teenWebtoon.isForAdults());
//
//            assertThat(adultWebtoon.isForAdults()).isTrue();
//            assertThat(teenWebtoon.isForAdults()).isFalse();
//
//            logger.info("성인용 웹툰 판별 로직 테스트 통과");
//        }
//
//        @Test
//        @Order(2)
//        @DisplayName("무료 열람 가능 여부 판별 로직이 올바르게 동작해야 한다")
//        void isFreeToRead() {
//            SearchableWebtoon freeWebtoon = SearchableWebtoon.builder()
//                    .id("free_1")
//                    .title("Free Webtoon")
//                    .provider("TEST")
//                    .url("http://test.com")
//                    .isFree(true)
//                    .build();
//
//            SearchableWebtoon paidWebtoon = SearchableWebtoon.builder()
//                    .id("paid_1")
//                    .title("Paid Webtoon")
//                    .provider("TEST")
//                    .url("http://test.com")
//                    .isFree(false)
//                    .freeWaitHour(24)
//                    .build();
//
//            SearchableWebtoon immediatelyFreeWebtoon = SearchableWebtoon.builder()
//                    .id("imm_free_1")
//                    .title("Immediately Free Webtoon")
//                    .provider("TEST")
//                    .url("http://test.com")
//                    .isFree(false)
//                    .freeWaitHour(0)
//                    .build();
//
//            logger.info("무료 웹툰 확인: {}", freeWebtoon.isFreeToRead());
//            logger.info("유료 웹툰 (24시간 후 무료) 확인: {}", paidWebtoon.isFreeToRead());
//            logger.info("즉시 무료로 전환된 웹툰 확인: {}", immediatelyFreeWebtoon.isFreeToRead());
//
//            assertThat(freeWebtoon.isFreeToRead()).isTrue();
//            assertThat(paidWebtoon.isFreeToRead()).isFalse();
//            assertThat(immediatelyFreeWebtoon.isFreeToRead()).isTrue();
//
//            logger.info("무료 열람 가능 여부 판별 로직 테스트 통과");
//        }
//
//        @Test
//        @Order(3)
//        @DisplayName("최근 업데이트 여부 판별 로직이 올바르게 동작해야 한다")
//        void isRecentlyUpdated() {
//            SearchableWebtoon updatedOngoingWebtoon = SearchableWebtoon.builder()
//                    .id("ongoing_1")
//                    .title("Updated Ongoing Webtoon")
//                    .provider("TEST")
//                    .url("http://test.com")
//                    .isUpdated(true)
//                    .isEnd(false)
//                    .build();
//
//            SearchableWebtoon updatedEndedWebtoon = SearchableWebtoon.builder()
//                    .id("ended_1")
//                    .title("Updated Ended Webtoon")
//                    .provider("TEST")
//                    .url("http://test.com")
//                    .isUpdated(true)
//                    .isEnd(true)
//                    .build();
//
//            SearchableWebtoon notUpdatedWebtoon = SearchableWebtoon.builder()
//                    .id("not_updated_1")
//                    .title("Not Updated Webtoon")
//                    .provider("TEST")
//                    .url("http://test.com")
//                    .isUpdated(false)
//                    .isEnd(false)
//                    .build();
//
//            logger.info("최근 업데이트된 연재중 웹툰 확인: {}", updatedOngoingWebtoon.isRecentlyUpdated());
//            logger.info("최근 업데이트되었지만 완결된 웹툰 확인: {}", updatedEndedWebtoon.isRecentlyUpdated());
//            logger.info("업데이트되지 않은 웹툰 확인: {}", notUpdatedWebtoon.isRecentlyUpdated());
//
//            assertThat(updatedOngoingWebtoon.isRecentlyUpdated()).isTrue();
//            assertThat(updatedEndedWebtoon.isRecentlyUpdated()).isFalse();
//            assertThat(notUpdatedWebtoon.isRecentlyUpdated()).isFalse();
//
//            logger.info("최근 업데이트 여부 판별 로직 테스트 통과");
//        }
//    }
//}
