package com.samsamohoh.webtoonsearch.application.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SearchableWebtoon 도메인 모델")
class SearchableWebtoonUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(SearchableWebtoonUnitTest.class);
    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    @Nested
    @DisplayName("create 메서드는")
    class Describe_create {

        @Test
        @DisplayName("올바른 파라미터로 SearchableWebtoon 객체를 생성해야 한다")
        void it_creates_searchable_webtoon_with_correct_parameters() throws JsonProcessingException {
            String id = "kakao_3727";
            String title = "[드라마 외전] 힘쎈여자 황금주";
            String provider = "KAKAO";
            var updateDays = Arrays.asList("MON", "TUE");
            String url = "https://webtoon.kakao.com/content/힘쎈여자-황금주/3727";
            var thumbnail = Arrays.asList("https://kr-a.kakaopagecdn.com/thumbnail.png");
            boolean isEnd = true;
            boolean isFree = true;
            boolean isUpdated = false;
            int ageGrade = 15;
            Integer freeWaitHour = 24;
            var authors = Arrays.asList("이원식", "참치캔");

            logger.info("SearchableWebtoon 객체 생성 중: id={}, 제목={}, 제공자={}, 업데이트 요일={}, URL={}, 썸네일={}, 종료 여부={}, 무료 여부={}, 업데이트 여부={}, 연령 등급={}, 무료 대기 시간={}, 작가={}",
                    id, title, provider, updateDays, url, thumbnail, isEnd, isFree, isUpdated, ageGrade, freeWaitHour, authors);

            SearchableWebtoon webtoon = SearchableWebtoon.create(
                    id, title, provider, updateDays, url, thumbnail,
                    isEnd, isFree, isUpdated, ageGrade, freeWaitHour, authors
            );

            logger.info("생성된 SearchableWebtoon 객체: {}", objectMapper.writeValueAsString(webtoon));

            assertEquals(id, webtoon.getId());
            assertEquals(title, webtoon.getTitle());
            assertEquals(provider, webtoon.getProvider());
            assertEquals(updateDays, webtoon.getUpdateDays());
            assertEquals(url, webtoon.getUrl());
            assertEquals(thumbnail, webtoon.getThumbnail());
            assertEquals(isEnd, webtoon.isEnd());
            assertEquals(isFree, webtoon.isFree());
            assertEquals(isUpdated, webtoon.isUpdated());
            assertEquals(ageGrade, webtoon.getAgeGrade());
            assertEquals(freeWaitHour, webtoon.getFreeWaitHour());
            assertEquals(authors, webtoon.getAuthors());

            logger.info("올바른 파라미터로 객체 생성 테스트 통과");
        }

        @Test
        @DisplayName("불변성을 보장해야 한다")
        void it_ensures_immutability() throws JsonProcessingException {
            var updateDays = Arrays.asList("MON", "TUE");
            var thumbnail = Arrays.asList("https://thumbnail.url");
            var authors = Arrays.asList("Author 1", "Author 2");

            logger.info("SearchableWebtoon 불변성 테스트 중: 업데이트 요일={}, 썸네일={}, 작가={}",
                    updateDays, thumbnail, authors);

            SearchableWebtoon webtoon = SearchableWebtoon.create(
                    "id", "title", "provider", updateDays, "url", thumbnail,
                    true, true, false, 15, 24, authors
            );

            logger.info("생성된 SearchableWebtoon 객체: {}", objectMapper.writeValueAsString(webtoon));

            // 리스트 변경 시도
            assertThrows(UnsupportedOperationException.class, () -> webtoon.getUpdateDays().add("WED"));
            assertThrows(UnsupportedOperationException.class, () -> webtoon.getThumbnail().add("new_thumbnail"));
            assertThrows(UnsupportedOperationException.class, () -> webtoon.getAuthors().add("New Author"));

            logger.info("불변성 테스트 통과");
        }
    }

    @Nested
    @DisplayName("equals 메서드는")
    class Describe_equals {

        @Test
        @DisplayName("동일한 속성을 가진 두 객체가 동등해야 한다")
        void it_considers_two_objects_with_same_properties_as_equal() throws JsonProcessingException {
            logger.info("두 SearchableWebtoon 객체의 동등성 테스트 중");

            SearchableWebtoon webtoon1 = SearchableWebtoon.create(
                    "id", "title", "provider", Collections.emptyList(), "url", Collections.emptyList(),
                    true, true, false, 15, 24, Collections.emptyList()
            );

            SearchableWebtoon webtoon2 = SearchableWebtoon.create(
                    "id", "title", "provider", Collections.emptyList(), "url", Collections.emptyList(),
                    true, true, false, 15, 24, Collections.emptyList()
            );

            logger.info("SearchableWebtoon 1: {}", objectMapper.writeValueAsString(webtoon1));
            logger.info("SearchableWebtoon 2: {}", objectMapper.writeValueAsString(webtoon2));

            assertEquals(webtoon1, webtoon2);
            assertEquals(webtoon1.hashCode(), webtoon2.hashCode());

            logger.info("동등성 테스트 통과");
        }
    }
}