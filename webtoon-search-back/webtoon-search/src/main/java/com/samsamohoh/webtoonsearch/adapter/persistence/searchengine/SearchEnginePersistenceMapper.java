package com.samsamohoh.webtoonsearch.adapter.persistence.searchengine;

import com.samsamohoh.webtoonsearch.adapter.persistence.searchengine.entity.SearchWebtoonEntity;
import com.samsamohoh.webtoonsearch.application.port.out.webtoon.dto.LoadWebtoonResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * OpenSearch 검색 결과와 애플리케이션 도메인 객체 간의 변환을 담당하는 매퍼
 */
@Component
public class SearchEnginePersistenceMapper {

    /**
     * SearchWebtoonEntity를 LoadWebtoonResponse로 변환
     */
    public LoadWebtoonResponse toResponse(SearchWebtoonEntity entity) {
        return new LoadWebtoonResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getProvider(),
                entity.getUpdateDays(),
                entity.getUrl(),
                entity.getThumbnail(),
                entity.isEnd(),
                entity.isFree(),
                entity.isUpdated(),
                entity.getAgeGrade(),
                entity.getFreeWaitHour(),
                entity.getAuthors()
        );
    }

    /**
     * SearchWebtoonEntity 리스트를 LoadWebtoonResponse 리스트로 변환
     */
    public List<LoadWebtoonResponse> toResponseList(List<SearchWebtoonEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
