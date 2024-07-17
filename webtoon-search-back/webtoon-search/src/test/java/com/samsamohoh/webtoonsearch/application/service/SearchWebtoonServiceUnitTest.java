package com.samsamohoh.webtoonsearch.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("SearchWebtoonService 클래스")
class SearchWebtoonServiceUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(SearchWebtoonServiceUnitTest.class);
    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    @Mock
    private LoadWebtoonPort loadWebtoonPort;

    private SearchWebtoonService searchWebtoonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        searchWebtoonService = new SearchWebtoonService(loadWebtoonPort);
    }

    @Nested
    @DisplayName("searchWebtoons 메서드는")
    class Describe_searchWebtoons {

        @Nested
        @DisplayName("유효한 제목이 주어졌을 때")
        class Context_with_valid_title {

            @Test
            @DisplayName("해당 제목과 관련된 웹툰 목록을 반환해야 한다")
            void it_returns_webtoons_related_to_the_title() throws Exception {
                String title = "힘쎈여자";
                List<SearchableWebtoon> expectedWebtoons = Arrays.asList(
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

                when(loadWebtoonPort.searchWebtoonsByTitle(title)).thenReturn(expectedWebtoons);

                logger.info("검색할 제목: {}", title);
                logger.info("기대하는 검색 결과: {}", objectMapper.writeValueAsString(expectedWebtoons));

                List<SearchableWebtoon> result = searchWebtoonService.searchWebtoons(title);

                logger.info("실제 검색 결과: {}", objectMapper.writeValueAsString(result));

                assertEquals(expectedWebtoons, result);
                verify(loadWebtoonPort, times(1)).searchWebtoonsByTitle(title);
            }
        }

        @Nested
        @DisplayName("빈 문자열이 주어졌을 때")
        class Context_with_empty_string {

            @Test
            @DisplayName("빈 목록을 반환해야 한다")
            void it_returns_empty_list() throws Exception {
                String title = "";

                logger.info("빈 문자열 제목을 검색합니다.");

                List<SearchableWebtoon> result = searchWebtoonService.searchWebtoons(title);

                logger.info("검색 결과: {}", objectMapper.writeValueAsString(result));

                assertTrue(result.isEmpty());
                verify(loadWebtoonPort, never()).searchWebtoonsByTitle(anyString());
            }
        }
    }
}