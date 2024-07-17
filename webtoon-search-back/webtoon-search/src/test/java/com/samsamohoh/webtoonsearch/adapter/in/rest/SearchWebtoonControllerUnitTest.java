package com.samsamohoh.webtoonsearch.adapter.in.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.samsamohoh.webtoonsearch.adapter.in.rest.dto.SearchableWebtoonDTO;
import com.samsamohoh.webtoonsearch.adapter.in.rest.mapper.WebtoonMapper;
import com.samsamohoh.webtoonsearch.adapter.in.rest.vo.SearchWebtoonsResponseVO;
import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;
import com.samsamohoh.webtoonsearch.application.port.in.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.common.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SearchWebtoonController 클래스")
class SearchWebtoonControllerUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(SearchWebtoonControllerUnitTest.class);
    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    @Mock
    private SearchWebtoonUseCase searchWebtoonUseCase;

    @Mock
    private WebtoonMapper webtoonMapper;

    private SearchWebtoonController searchWebtoonController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        searchWebtoonController = new SearchWebtoonController(searchWebtoonUseCase, webtoonMapper);
    }

    @Nested
    @DisplayName("searchWebtoons 메서드는")
    class Describe_searchWebtoons {

        @Nested
        @DisplayName("유효한 제목이 주어졌을 때")
        class Context_with_valid_title {

            @Test
            @DisplayName("OK 상태와 함께 웹툰 목록을 반환해야 한다")
            void it_returns_webtoon_list_with_ok_status() throws JsonProcessingException {
                String title = "힘쎈여자";
                List<SearchableWebtoon> webtoons = Arrays.asList(
                        SearchableWebtoon.create(
                                "kakao_3727",
                                "[드라마 외전] 힘쎈여자 황금주",
                                "KAKAO",
                                Arrays.asList(),
                                "https://webtoon.kakao.com/content/힘쎈여자-황금주/3727",
                                Arrays.asList("https://kr-a.kakaopagecdn.com/thumbnail.png"),
                                true,
                                true,
                                false,
                                0,
                                24,
                                Arrays.asList("이원식", "참치캔")
                        )
                );
                // 변환된 VO가 웹툰 목록을 포함하도록 설정
                SearchWebtoonsResponseVO expectedResponse = SearchWebtoonsResponseVO.builder()
                        .webtoonlist(Arrays.asList(
                                SearchableWebtoonDTO.builder()
                                        .id("kakao_3727")
                                        .title("[드라마 외전] 힘쎈여자 황금주")
                                        .provider("KAKAO")
                                        .updateDays(Collections.emptyList())
                                        .url("https://webtoon.kakao.com/content/힘쎈여자-황금주/3727")
                                        .thumbnail(Arrays.asList("https://kr-a.kakaopagecdn.com/thumbnail.png"))
                                        .ageGrade(0)
                                        .freeWaitHour(24)
                                        .authors(Arrays.asList("이원식", "참치캔"))
                                        .isFree(true)
                                        .isEnd(true)
                                        .isUpdated(false)
                                        .build()
                        ))
                        .build();

                when(searchWebtoonUseCase.searchWebtoons(title)).thenReturn(webtoons);
                when(webtoonMapper.toResponseVO(webtoons)).thenReturn(expectedResponse);

                logger.info("제목: {}", title);
                logger.info("검색 결과: {}", objectMapper.writeValueAsString(webtoons));
                logger.info("응답 VO: {}", objectMapper.writeValueAsString(expectedResponse));

                ResponseEntity<ApiResponse<SearchWebtoonsResponseVO>> response = searchWebtoonController.searchWebtoons(title);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().isSuccess());
                assertEquals("Success", response.getBody().getMessage());
                assertEquals(expectedResponse, response.getBody().getData());

                logger.info("응답 결과: {}", objectMapper.writeValueAsString(response.getBody()));

                verify(searchWebtoonUseCase, times(1)).searchWebtoons(title);
                verify(webtoonMapper, times(1)).toResponseVO(webtoons);
            }
        }

        @Nested
        @DisplayName("빈 제목이 주어졌을 때")
        class Context_with_empty_title {

            @Test
            @DisplayName("빈 목록을 반환해야 한다")
            void it_returns_empty_list() throws JsonProcessingException {
                String title = "";
                SearchWebtoonsResponseVO expectedResponse = SearchWebtoonsResponseVO.builder()
                        .webtoonlist(Collections.emptyList())
                        .build();

                when(searchWebtoonUseCase.searchWebtoons(title)).thenReturn(Collections.emptyList());
                when(webtoonMapper.toResponseVO(Collections.emptyList())).thenReturn(expectedResponse);

                logger.info("제목: {}", title);
                logger.info("검색 결과: {}", objectMapper.writeValueAsString(Collections.emptyList()));
                logger.info("응답 VO: {}", objectMapper.writeValueAsString(expectedResponse));

                ResponseEntity<ApiResponse<SearchWebtoonsResponseVO>> response = searchWebtoonController.searchWebtoons(title);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().isSuccess());
                assertEquals("Success", response.getBody().getMessage());
                assertEquals(expectedResponse, response.getBody().getData());

                logger.info("응답 결과: {}", objectMapper.writeValueAsString(response.getBody()));

                verify(searchWebtoonUseCase, times(1)).searchWebtoons(title);
                verify(webtoonMapper, times(1)).toResponseVO(Collections.emptyList());
            }
        }

        @Nested
        @DisplayName("null 제목이 주어졌을 때")
        class Context_with_null_title {

            @Test
            @DisplayName("빈 목록을 반환해야 한다")
            void it_returns_empty_list() throws JsonProcessingException {
                String title = null;
                SearchWebtoonsResponseVO expectedResponse = SearchWebtoonsResponseVO.builder()
                        .webtoonlist(Collections.emptyList())
                        .build();

                when(searchWebtoonUseCase.searchWebtoons(title)).thenReturn(Collections.emptyList());
                when(webtoonMapper.toResponseVO(Collections.emptyList())).thenReturn(expectedResponse);

                logger.info("제목: {}", title);
                logger.info("검색 결과: {}", objectMapper.writeValueAsString(Collections.emptyList()));
                logger.info("응답 VO: {}", objectMapper.writeValueAsString(expectedResponse));

                ResponseEntity<ApiResponse<SearchWebtoonsResponseVO>> response = searchWebtoonController.searchWebtoons(title);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().isSuccess());
                assertEquals("Success", response.getBody().getMessage());
                assertEquals(expectedResponse, response.getBody().getData());

                logger.info("응답 결과: {}", objectMapper.writeValueAsString(response.getBody()));

                verify(searchWebtoonUseCase, times(1)).searchWebtoons(title);
                verify(webtoonMapper, times(1)).toResponseVO(Collections.emptyList());
            }
        }

        @Nested
        @DisplayName("검색 결과가 없을 때")
        class Context_with_no_results {

            @Test
            @DisplayName("빈 목록을 반환해야 한다")
            void it_returns_no_content_status() throws JsonProcessingException {
                String title = "존재하지 않는 웹툰";
                SearchWebtoonsResponseVO emptyResponse = SearchWebtoonsResponseVO.builder()
                        .webtoonlist(Collections.emptyList())
                        .build();

                when(searchWebtoonUseCase.searchWebtoons(title)).thenReturn(Collections.emptyList());
                when(webtoonMapper.toResponseVO(Collections.emptyList())).thenReturn(emptyResponse);

                logger.info("제목: {}", title);
                logger.info("검색 결과: {}", objectMapper.writeValueAsString(Collections.emptyList()));
                logger.info("응답 VO: {}", objectMapper.writeValueAsString(emptyResponse));

                ResponseEntity<ApiResponse<SearchWebtoonsResponseVO>> response = searchWebtoonController.searchWebtoons(title);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().isSuccess());
                assertEquals("Success", response.getBody().getMessage());
                assertEquals(emptyResponse, response.getBody().getData());

                logger.info("응답 결과: {}", objectMapper.writeValueAsString(response.getBody()));

                verify(searchWebtoonUseCase, times(1)).searchWebtoons(title);
                verify(webtoonMapper, times(1)).toResponseVO(Collections.emptyList());
            }
        }

        @Nested
        @DisplayName("검색 중 예외가 발생할 때")
        class Context_with_exception {

            @Test
            @DisplayName("내부 서버 오류 상태를 반환해야 한다")
            void it_returns_internal_server_error_status() {
                String title = "힘쎈여자";
                when(searchWebtoonUseCase.searchWebtoons(title)).thenThrow(new RuntimeException("예상치 못한 오류"));

                ResponseEntity<ApiResponse<SearchWebtoonsResponseVO>> response = null;
                try {
                    response = searchWebtoonController.searchWebtoons(title);
                    fail("예외가 발생해야 합니다.");
                } catch (RuntimeException e) {
                    assertEquals("예상치 못한 오류", e.getMessage());
                    logger.error("예외 발생: {}", e.getMessage());
                }

                verify(searchWebtoonUseCase, times(1)).searchWebtoons(title);
            }
        }
    }
}