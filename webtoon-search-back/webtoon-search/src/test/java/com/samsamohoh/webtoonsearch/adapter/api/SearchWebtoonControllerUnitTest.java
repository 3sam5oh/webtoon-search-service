package com.samsamohoh.webtoonsearch.adapter.api;

import com.samsamohoh.webtoonsearch.adapter.api.webtoon.SearchWebtoonController;
import com.samsamohoh.webtoonsearch.adapter.api.webtoon.SearchWebtoonResponse;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonRequest;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.WebtoonResult;
import com.samsamohoh.webtoonsearch.common.ApiResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        @DisplayName("유효한 검색어로 웹툰 검색 시 성공 응답을 반환해야 한다")
        void withValidQuery_shouldReturnSuccessResponse() {
            // Given
            String query = "힘쎈여자";
            WebtoonResult.WebtoonDTO sampleWebtoon = WebtoonResult.WebtoonDTO.builder()
                    .id("kakao_3727")
                    .title("[드라마 외전] 힘쎈여자 황금주")
                    .provider("KAKAO")
                    .build();
            WebtoonResult mockResult = new WebtoonResult(Collections.singletonList(sampleWebtoon));

            when(searchWebtoonUseCase.searchWebtoons(any(SearchWebtoonRequest.class))).thenReturn(mockResult);

            // When
            ResponseEntity<ApiResponse<SearchWebtoonResponse>> responseEntity = controller.searchWebtoon(query);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            ApiResponse<SearchWebtoonResponse> response = responseEntity.getBody();
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).contains("'힘쎈여자'에 대한 검색 결과");
            assertThat(response.getData().getWebtoons()).hasSize(1);
            assertThat(response.getData().getWebtoons().get(0).getId()).isEqualTo("kakao_3727");

            verify(searchWebtoonUseCase).searchWebtoons(any(SearchWebtoonRequest.class));
            logger.info("유효한 검색어 테스트 통과");
        }

        @Test
        @Order(2)
        @DisplayName("빈 검색어로 웹툰 검색 시 에러 응답을 반환해야 한다")
        void withEmptyQuery_shouldReturnErrorResponse() {
            // Given
            String query = "";

            // When
            ResponseEntity<ApiResponse<SearchWebtoonResponse>> responseEntity = controller.searchWebtoon(query);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            ApiResponse<SearchWebtoonResponse> response = responseEntity.getBody();
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("검색어를 입력해주세요");
            assertThat(response.getData()).isNull();

            logger.info("빈 검색어 테스트 통과");
        }

        @Test
        @Order(3)
        @DisplayName("검색 결과가 없을 때 적절한 응답을 반환해야 한다")
        void withNoResults_shouldReturnEmptyResponse() {
            // Given
            String query = "존재하지않는웹툰";
            WebtoonResult mockResult = new WebtoonResult(Collections.emptyList());

            when(searchWebtoonUseCase.searchWebtoons(any(SearchWebtoonRequest.class))).thenReturn(mockResult);

            // When
            ResponseEntity<ApiResponse<SearchWebtoonResponse>> responseEntity = controller.searchWebtoon(query);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            ApiResponse<SearchWebtoonResponse> response = responseEntity.getBody();
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("검색 결과가 없습니다");
            assertThat(response.getData().getWebtoons()).isEmpty();

            verify(searchWebtoonUseCase).searchWebtoons(any(SearchWebtoonRequest.class));
            logger.info("결과 없음 테스트 통과");
        }

        @Test
        @Order(4)
        @DisplayName("검색 중 예외 발생 시 에러 응답을 반환해야 한다")
        void whenExceptionOccurs_shouldReturnErrorResponse() {
            // Given
            String query = "테스트";
            when(searchWebtoonUseCase.searchWebtoons(any(SearchWebtoonRequest.class)))
                    .thenThrow(new RuntimeException("검색 중 오류 발생"));

            // When
            ResponseEntity<ApiResponse<SearchWebtoonResponse>> responseEntity = controller.searchWebtoon(query);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            ApiResponse<SearchWebtoonResponse> response = responseEntity.getBody();
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("웹툰 검색 중 오류가 발생했습니다");
            assertThat(response.getData()).isNull();

            logger.info("예외 처리 테스트 통과");
        }
    }
}
