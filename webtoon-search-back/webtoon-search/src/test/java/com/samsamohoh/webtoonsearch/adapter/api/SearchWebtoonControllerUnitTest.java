package com.samsamohoh.webtoonsearch.adapter.api;

import com.samsamohoh.webtoonsearch.adapter.api.webtoon.SearchWebtoonController;
import com.samsamohoh.webtoonsearch.adapter.api.webtoon.SearchWebtoonResponse;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SearchWebtoonCommand;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.WebtoonResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("SearchWebtoonController 단위 테스트")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SearchWebtoonControllerUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(SearchWebtoonControllerUnitTest.class);

    @Mock
    private SearchWebtoonUseCase searchWebtoonUseCase;

    private SearchWebtoonController controller;

    @BeforeEach
    void setUp() {
        controller = new SearchWebtoonController(searchWebtoonUseCase);
        logger.info("테스트 설정 완료: 컨트롤러 생성됨");
    }

    @Nested
    @DisplayName("searchWebtoon 메서드")
    class SearchWebtoonMethod {

        @Test
        @Order(1)
        @DisplayName("유효한 검색어로 웹툰 검색 시 결과를 반환해야 한다")
        void withValidQuery_shouldReturnResults() {
            // Given
            String query = "힘쎈여자";
            WebtoonResult.WebtoonDTO sampleWebtoon = WebtoonResult.WebtoonDTO.builder()
                    .id("kakao_3727")
                    .title("[드라마 외전] 힘쎈여자 황금주")
                    .provider("KAKAO")
                    .build();
            WebtoonResult mockResult = new WebtoonResult(Collections.singletonList(sampleWebtoon));

            when(searchWebtoonUseCase.searchWebtoons(any(SearchWebtoonCommand.class))).thenReturn(mockResult);

            // When
            ApiResponse<SearchWebtoonResponse> response = controller.searchWebtoon(query);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getData().getWebtoons()).hasSize(1);
            assertThat(response.getData().getWebtoons().get(0).getId()).isEqualTo("kakao_3727");

            verify(searchWebtoonUseCase).searchWebtoons(any(SearchWebtoonCommand.class));
            logger.info("유효한 검색어 테스트 통과");
        }

        @Test
        @Order(2)
        @DisplayName("빈 검색어로 웹툰 검색 시 빈 결과를 반환해야 한다")
        void withEmptyQuery_shouldReturnEmptyResults() {
            // Given
            String query = "";
            WebtoonResult mockResult = new WebtoonResult(Collections.emptyList());

            when(searchWebtoonUseCase.searchWebtoons(any(SearchWebtoonCommand.class))).thenReturn(mockResult);

            // When
            ApiResponse<SearchWebtoonResponse> response = controller.searchWebtoon(query);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getData().getWebtoons()).isEmpty();

            verify(searchWebtoonUseCase).searchWebtoons(any(SearchWebtoonCommand.class));
            logger.info("빈 검색어 테스트 통과");
        }
    }

    @Nested
    @DisplayName("healthCheck 메서드")
    class HealthCheckMethod {

        @Test
        @Order(1)
        @DisplayName("건강 체크 메서드가 정상적으로 동작해야 한다")
        void shouldReturnOkMessage() {
            // When
            String result = controller.healthCheck();

            // Then
            assertThat(result).isEqualTo("fine working!");
            logger.info("건강 체크 테스트 통과");
        }
    }
}
