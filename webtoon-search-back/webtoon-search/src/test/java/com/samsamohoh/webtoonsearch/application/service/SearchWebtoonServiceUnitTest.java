package com.samsamohoh.webtoonsearch.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SearchWebtoonCommand;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.WebtoonResult;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonPort;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.LoadWebtoonQuery;
import com.samsamohoh.webtoonsearch.common.metrics.CustomMetrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("SearchWebtoonService 단위 테스트")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SearchWebtoonServiceUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(SearchWebtoonServiceUnitTest.class);
    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    @Mock
    private LoadWebtoonPort loadWebtoonPort;

    private CustomMetrics customMetrics;
    private MeterRegistry meterRegistry;

    private SearchWebtoonService searchWebtoonService;
    private WebtoonResult.WebtoonDTO sampleWebtoonDTO;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        customMetrics = new CustomMetrics(meterRegistry);
        searchWebtoonService = new SearchWebtoonService(loadWebtoonPort, customMetrics);

        sampleWebtoonDTO = WebtoonResult.WebtoonDTO.builder()
                .id("kakao_3727")
                .title("[드라마 외전] 힘쎈여자 황금주")
                .provider("KAKAO")
                .updateDays(Arrays.asList("MON", "TUE"))
                .url("https://webtoon.kakao.com/content/힘쎈여자-황금주/3727")
                .thumbnail(Arrays.asList("https://kr-a.kakaopagecdn.com/thumbnail.png"))
                .isEnd(false)
                .isFree(true)
                .isUpdated(true)
                .ageGrade(15)
                .freeWaitHour(0)
                .authors(Arrays.asList("이원식", "참치캔"))
                .build();

        logger.info("테스트 설정 완료: 샘플 웹툰 및 서비스 생성됨");
    }

    @Nested
    @DisplayName("웹툰 검색 기능")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class SearchWebtoons {

        @Test
        @Order(1)
        @DisplayName("유효한 검색어로 웹툰 검색 시 결과를 반환해야 한다")
        void searchWebtoons_WithValidQuery_ShouldReturnResult() throws Exception {
            // Given
            SearchWebtoonCommand command = new SearchWebtoonCommand("힘쎈여자");
            WebtoonResult expectedResult = new WebtoonResult(Arrays.asList(sampleWebtoonDTO));

            when(loadWebtoonPort.loadWebtoons(any(LoadWebtoonQuery.class))).thenReturn(expectedResult);

            // When
            WebtoonResult result = searchWebtoonService.searchWebtoons(command);

            // Then
            logger.info("검색 결과: {}", objectMapper.writeValueAsString(result));
            logger.info("기대 결과: {}", objectMapper.writeValueAsString(expectedResult));

            assertThat(result.getWebtoons())
                    .as("검색 결과가 기대한 결과와 일치해야 함")
                    .hasSameElementsAs(expectedResult.getWebtoons());

            verify(loadWebtoonPort).loadWebtoons(any(LoadWebtoonQuery.class));
            logger.info("LoadWebtoonPort.loadWebtoons() 메서드가 호출되었음");

            // Metric 검증
            Counter nullCounter = meterRegistry.find("search.condition.null.count").counter();
            assertThat(nullCounter)
                    .as("유효한 검색어에 대해서는 null 카운터가 생성되지 않아야 함")
                    .isNull();
            logger.info("Null 검색어 카운터가 생성되지 않음");

            logger.info("유효한 검색어 테스트 통과");
        }

        @Test
        @Order(2)
        @DisplayName("null 검색어로 웹툰 검색 시 예외를 발생시키고 메트릭을 기록해야 한다")
        void searchWebtoons_WithNullQuery_ShouldThrowExceptionAndRecordMetric() {
            // Given
            SearchWebtoonCommand command = new SearchWebtoonCommand(null);

            // When & Then
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> searchWebtoonService.searchWebtoons(command),
                    "Null 검색어로 검색 시 IllegalArgumentException이 발생해야 함");
            logger.info("발생한 예외: {}", exception.getMessage());

            // Metric 검증
            Counter counter = meterRegistry.find("search.condition.null.count").counter();
            assertThat(counter)
                    .as("Null 검색어 카운터가 생성되어야 함")
                    .isNotNull();
            assertThat(counter.count())
                    .as("Null 검색어 카운터가 1 증가해야 함")
                    .isEqualTo(1);
            logger.info("Null 검색어 카운터 값: {}", counter.count());

            // 태그 검증
            List<Tag> expectedTags = Arrays.asList(
                    Tag.of("class", "search-webtoon-service"),
                    Tag.of("method", "search-webtoons"),
                    Tag.of("endpoint", "/webtoons/search")
            );
            assertThat(counter.getId().getTags())
                    .as("카운터에 올바른 태그가 포함되어야 함")
                    .containsExactlyInAnyOrderElementsOf(expectedTags);
            logger.info("카운터 태그: {}", counter.getId().getTags());

            // 설명 검증
            assertThat(counter.getId().getDescription())
                    .as("카운터 설명이 올바르게 설정되어야 함")
                    .isEqualTo("title is null");
            logger.info("카운터 설명: {}", counter.getId().getDescription());

            verifyNoInteractions(loadWebtoonPort);
            logger.info("LoadWebtoonPort와 상호작용이 없었음");

            logger.info("null 검색어 테스트 통과");
        }

        @Test
        @Order(3)
        @DisplayName("빈 문자열 검색어로 웹툰 검색 시 예외를 발생시키고 메트릭을 기록해야 한다")
        void searchWebtoons_WithEmptyQuery_ShouldThrowExceptionAndRecordMetric() {
            // Given
            SearchWebtoonCommand command = new SearchWebtoonCommand("");

            // When & Then
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> searchWebtoonService.searchWebtoons(command),
                    "빈 문자열 검색어로 검색 시 IllegalArgumentException이 발생해야 함");
            logger.info("발생한 예외: {}", exception.getMessage());

            // Metric 검증
            Counter counter = meterRegistry.find("search.condition.null.count").counter();
            assertThat(counter)
                    .as("빈 문자열 검색어 카운터가 생성되어야 함")
                    .isNotNull();
            assertThat(counter.count())
                    .as("빈 문자열 검색어 카운터가 1 증가해야 함")
                    .isEqualTo(1);
            logger.info("빈 문자열 검색어 카운터 값: {}", counter.count());

            // 태그 검증
            List<Tag> expectedTags = Arrays.asList(
                    Tag.of("class", "search-webtoon-service"),
                    Tag.of("method", "search-webtoons"),
                    Tag.of("endpoint", "/webtoons/search")
            );
            assertThat(counter.getId().getTags())
                    .as("카운터에 올바른 태그가 포함되어야 함")
                    .containsExactlyInAnyOrderElementsOf(expectedTags);
            logger.info("카운터 태그: {}", counter.getId().getTags());

            // 설명 검증
            assertThat(counter.getId().getDescription())
                    .as("카운터 설명이 올바르게 설정되어야 함")
                    .isEqualTo("title is null");
            logger.info("카운터 설명: {}", counter.getId().getDescription());

            verifyNoInteractions(loadWebtoonPort);
            logger.info("LoadWebtoonPort와 상호작용이 없었음");

            logger.info("빈 문자열 검색어 테스트 통과");
        }
    }
}
