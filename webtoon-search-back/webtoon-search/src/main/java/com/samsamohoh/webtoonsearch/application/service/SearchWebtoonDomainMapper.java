package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonResponse;
import com.samsamohoh.webtoonsearch.application.port.out.webtoon.dto.LoadWebtoonResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchWebtoonDomainMapper {

    /**
     * 영속성 응답 객체를 도메인 모델로 변환
     *
     * @param response      영속성 계층의 웹툰 검색 결과
     * @param searchLatency 검색 소요 시간 (밀리초)
     * @return 도메인 모델로 변환된 웹툰 정보
     */
    public SearchableWebtoon toDomainModel(LoadWebtoonResponse response, long searchLatency) {
        if (response == null) {
            throw new IllegalArgumentException("LoadWebtoonResponse cannot be null");
        }

        return SearchableWebtoon.createFrom(
                response.getId(),
                response.getTitle(),
                response.getProvider(),
                response.getUpdateDays(),
                response.getUrl(),
                response.getThumbnail(),
                response.isEnd(),
                response.isFree(),
                response.isUpdated(),
                response.getAgeGrade(),
                response.getFreeWaitHour(),
                response.getAuthors(),
                1.0f,            // 기본 검색 점수
                searchLatency,
                "title"

        );
    }

    /**
     * 도메인 모델을 API 응답 DTO로 변환
     *
     * @param webtoon 도메인 모델의 웹툰 정보
     * @return 클라이언트에게 반환될 웹툰 정보
     * @throws IllegalArgumentException webtoon이 null인 경우
     */
    public SearchWebtoonResponse toResponse(SearchableWebtoon webtoon) {
        if (webtoon == null) {
            throw new IllegalArgumentException("SearchableWebtoon cannot be null");
        }

        return new SearchWebtoonResponse(
                webtoon.getId(),
                webtoon.getTitle(),
                webtoon.getProvider(),
                webtoon.getUpdateDays(),
                webtoon.getUrl(),
                webtoon.getThumbnail(),
                webtoon.isEnd(),
                webtoon.isFree(),
                webtoon.isUpdated(),
                webtoon.getAgeGrade(),
                webtoon.getFreeWaitHour(),
                webtoon.getAuthors()
        );
    }

    /**
     * 도메인 모델 리스트를 API 응답 DTO 리스트로 변환
     *
     * @param webtoons 도메인 모델의 웹툰 정보 리스트
     * @return 클라이언트에게 반환될 웹툰 정보 리스트
     */
    public List<SearchWebtoonResponse> toResponseList(List<SearchableWebtoon> webtoons) {
        if (webtoons == null) {
            throw new IllegalArgumentException("Webtoons list cannot be null");
        }

        return webtoons.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
