package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.LoadWebtoonQuery;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonRequest;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.WebtoonResult;
import com.samsamohoh.webtoonsearch.application.port.out.webtoon.LoadWebtoonPort;
import com.samsamohoh.webtoonsearch.common.metrics.CustomMetrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("SearchWebtoonService 단위 테스트")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SearchWebtoonServiceUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(SearchWebtoonServiceUnitTest.class);

    @Mock
    private LoadWebtoonPort loadWebtoonPort;

    @Mock
    private CustomMetrics customMetrics;

    private SearchWebtoonService searchWebtoonService;
    private WebtoonResult.WebtoonDTO sampleWebtoonDTO;

    @BeforeEach
    void setUp() {
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
    }

    @Nested
    @DisplayName("웹툰 검색 기능")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class SearchWebtoons {

        @Test
        @Order(1)
        @DisplayName("유효한 검색어로 웹툰 검색 시 결과를 반환해야 한다")
        void searchWebtoons_WithValidQuery_ShouldReturnResult() {
            logger.info("테스트 시작: 유효한 검색어로 웹툰 검색");

            // Given
            String query = "힘쎈여자";
            SearchWebtoonRequest command = new SearchWebtoonRequest(query);
            WebtoonResult expectedResult = new WebtoonResult(Arrays.asList(sampleWebtoonDTO));

            logger.info("검색어: {}", query);
            logger.info("예상 결과: {}", expectedResult);

            when(loadWebtoonPort.loadWebtoons(any(LoadWebtoonQuery.class))).thenReturn(expectedResult);

            // When
            logger.info("searchWebtoons 메서드 호출");
            WebtoonResult result = searchWebtoonService.searchWebtoons(command);

            // Then
            logger.info("결과 검증");
            assertThat(result.getWebtoons()).hasSameElementsAs(expectedResult.getWebtoons());
            logger.info("결과 일치 확인");

            verify(loadWebtoonPort).loadWebtoons(any(LoadWebtoonQuery.class));
            logger.info("loadWebtoons 메서드 호출 확인");

            verifyNoInteractions(customMetrics);
            logger.info("customMetrics와 상호작용 없음 확인");

            logger.info("테스트 종료: 유효한 검색어로 웹툰 검색");
        }

        @Test
        @Order(2)
        @DisplayName("null 검색어로 웹툰 검색 시 예외를 발생시키고 메트릭을 기록해야 한다")
        void searchWebtoons_WithNullQuery_ShouldThrowExceptionAndRecordMetric() {
            logger.info("테스트 시작: null 검색어로 웹툰 검색");

            // Given
            SearchWebtoonRequest command = new SearchWebtoonRequest(null);
            Counter mockCounter = mock(Counter.class);
            when(customMetrics.getCounter(anyString(), anyString(), anyList())).thenReturn(mockCounter);

            logger.info("null 검색어로 커맨드 생성");

            // When & Then
            logger.info("예외 발생 및 메트릭 기록 검증");
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> searchWebtoonService.searchWebtoons(command));

            logger.info("발생한 예외 메시지: {}", exception.getMessage());
            assertThat(exception).hasMessage("검색 조건이 존재 하지 않음.");

            verify(customMetrics).getCounter(
                    eq("search.condition.null.count"),
                    eq("title is null"),
                    argThat(tags -> tags.containsAll(Arrays.asList(
                            Tag.of("class", "search-webtoon-service"),
                            Tag.of("method", "search-webtoons"),
                            Tag.of("endpoint", "/webtoons/search")
                    )))
            );
            logger.info("customMetrics.getCounter 호출 확인");

            verify(mockCounter).increment();
            logger.info("카운터 증가 확인");

            verifyNoInteractions(loadWebtoonPort);
            logger.info("loadWebtoonPort와 상호작용 없음 확인");

            logger.info("테스트 종료: null 검색어로 웹툰 검색");
        }

        @Test
        @Order(3)
        @DisplayName("빈 문자열 검색어로 웹툰 검색 시 예외를 발생시키고 메트릭을 기록해야 한다")
        void searchWebtoons_WithEmptyQuery_ShouldThrowExceptionAndRecordMetric() {
            logger.info("테스트 시작: 빈 문자열 검색어로 웹툰 검색");

            // Given
            SearchWebtoonRequest command = new SearchWebtoonRequest("");
            Counter mockCounter = mock(Counter.class);
            when(customMetrics.getCounter(anyString(), anyString(), anyList())).thenReturn(mockCounter);

            logger.info("빈 문자열 검색어로 커맨드 생성");

            // When & Then
            logger.info("예외 발생 및 메트릭 기록 검증");
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> searchWebtoonService.searchWebtoons(command));

            logger.info("발생한 예외 메시지: {}", exception.getMessage());
            assertThat(exception).hasMessage("검색 조건이 존재 하지 않음.");

            verify(customMetrics).getCounter(
                    eq("search.condition.null.count"),
                    eq("title is null"),
                    argThat(tags -> tags.containsAll(Arrays.asList(
                            Tag.of("class", "search-webtoon-service"),
                            Tag.of("method", "search-webtoons"),
                            Tag.of("endpoint", "/webtoons/search")
                    )))
            );
            logger.info("customMetrics.getCounter 호출 확인");

            verify(mockCounter).increment();
            logger.info("카운터 증가 확인");

            verifyNoInteractions(loadWebtoonPort);
            logger.info("loadWebtoonPort와 상호작용 없음 확인");

            logger.info("테스트 종료: 빈 문자열 검색어로 웹툰 검색");
        }
    }
}
