package com.samsamohoh.webtoonsearch.adapter.in.rest.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.samsamohoh.webtoonsearch.adapter.in.rest.dto.SearchableWebtoonDTO;
import com.samsamohoh.webtoonsearch.adapter.in.rest.vo.SearchWebtoonsResponseVO;
import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WebtoonMapper 클래스")
class WebtoonMapperUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(WebtoonMapperUnitTest.class);
    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    private final WebtoonMapper webtoonMapper = new WebtoonMapper();

    @Test
    @DisplayName("SearchableWebtoon을 SearchableWebtoonDTO로 변환해야 한다")
    void should_convert_searchable_webtoon_to_dto() throws JsonProcessingException {
        logger.info("테스트 시작: SearchableWebtoon을 SearchableWebtoonDTO로 변환");

        SearchableWebtoon webtoon = SearchableWebtoon.create(
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
        );

        logger.info("SearchableWebtoon 생성 완료:\n{}", objectMapper.writeValueAsString(webtoon));

        SearchableWebtoonDTO dto = webtoonMapper.toDTO(webtoon);

        logger.info("SearchableWebtoonDTO 변환 완료:\n{}", objectMapper.writeValueAsString(dto));

        assertAll(
                () -> assertEquals(webtoon.getId(), dto.getId(), "ID가 일치해야 합니다."),
                () -> assertEquals(webtoon.getTitle(), dto.getTitle(), "제목이 일치해야 합니다."),
                () -> assertEquals(webtoon.getProvider(), dto.getProvider(), "제공자가 일치해야 합니다."),
                () -> assertEquals(webtoon.getUpdateDays(), dto.getUpdateDays(), "업데이트 날짜가 일치해야 합니다."),
                () -> assertEquals(webtoon.getUrl(), dto.getUrl(), "URL이 일치해야 합니다."),
                () -> assertEquals(webtoon.getThumbnail(), dto.getThumbnail(), "썸네일이 일치해야 합니다."),
                () -> assertEquals(webtoon.isEnd(), dto.isEnd(), "종료 여부가 일치해야 합니다."),
                () -> assertEquals(webtoon.isFree(), dto.isFree(), "무료 여부가 일치해야 합니다."),
                () -> assertEquals(webtoon.isUpdated(), dto.isUpdated(), "업데이트 여부가 일치해야 합니다."),
                () -> assertEquals(webtoon.getAgeGrade(), dto.getAgeGrade(), "연령 등급이 일치해야 합니다."),
                () -> assertEquals(webtoon.getFreeWaitHour(), dto.getFreeWaitHour(), "무료 대기 시간이 일치해야 합니다."),
                () -> assertEquals(webtoon.getAuthors(), dto.getAuthors(), "작가 목록이 일치해야 합니다.")
        );

        logger.info("테스트 종료: SearchableWebtoon을 SearchableWebtoonDTO로 변환");
    }

    @Test
    @DisplayName("SearchableWebtoon 목록을 SearchWebtoonsResponseVO로 변환해야 한다")
    void should_convert_searchable_webtoon_list_to_response_vo() throws JsonProcessingException {
        logger.info("테스트 시작: SearchableWebtoon 목록을 SearchWebtoonsResponseVO로 변환");

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

        logger.info("SearchableWebtoon 목록 생성 완료:\n{}", objectMapper.writeValueAsString(webtoons));

        SearchWebtoonsResponseVO responseVO = webtoonMapper.toResponseVO(webtoons);

        logger.info("SearchWebtoonsResponseVO 변환 완료:\n{}", objectMapper.writeValueAsString(responseVO));

        assertEquals(1, responseVO.getWebtoonlist().size(), "웹툰 목록의 크기가 일치해야 합니다.");
        assertEquals(webtoons.get(0).getTitle(), responseVO.getWebtoonlist().get(0).getTitle(), "제목이 일치해야 합니다.");

        logger.info("테스트 종료: SearchableWebtoon 목록을 SearchWebtoonsResponseVO로 변환");
    }
}