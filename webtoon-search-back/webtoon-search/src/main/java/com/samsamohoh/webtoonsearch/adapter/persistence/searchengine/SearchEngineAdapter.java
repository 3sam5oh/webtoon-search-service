package com.samsamohoh.webtoonsearch.adapter.persistence.searchengine;

import com.samsamohoh.webtoonsearch.adapter.persistence.searchengine.entity.SearchWebtoonEntity;
import com.samsamohoh.webtoonsearch.application.port.out.webtoon.LoadWebtoonPort;
import com.samsamohoh.webtoonsearch.application.port.out.webtoon.dto.LoadWebtoonRequest;
import com.samsamohoh.webtoonsearch.application.port.out.webtoon.dto.LoadWebtoonResponse;
import com.samsamohoh.webtoonsearch.common.metrics.CustomMetrics;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.TrackHits;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * OpenSearch 기반 웹툰 검색 어댑터
 * 검색 요청을 처리하고 메트릭을 수집합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchEngineAdapter implements LoadWebtoonPort {

    // Micrometer 메트릭 관리용 커스텀 클래스 - 내부적으로 메트릭을 캐싱하므로 동일한 이름과 태그로 요칭 시 메트릭 객체를 재사용함
    private final CustomMetrics customMetrics;
    private final OpenSearchClient openSearchClient;
    private final SearchEnginePersistenceMapper searchEngineMapper;

    private static final String CACHE_NAME = "webtoonSearchCache";

    /**
     * 웹툰 검색 실행 및 결과 반환
     *
     * @param query 검색 요청 정보
     * @return 검색된 웹툰 목록
     * @throws DataAccessResourceFailureException 검색 실행 중 오류 발생시
     */
    @Override
    @Timed(value = "search.opensearch.reply.duration",
            extraTags = {"class", "search-engine-adapter", "method", "load-webtoons"},
            description = "OpenSearch query execution duration")
    @Cacheable(value = CACHE_NAME, key = "#query.query", unless = "#result == null")
    public List<LoadWebtoonResponse> loadWebtoons(LoadWebtoonRequest query) {
        try {
            // 검색 요청 생성 및 실행
            SearchResponse<SearchWebtoonEntity> response =
                    openSearchClient.search(createSearchRequest(query.getQuery()), SearchWebtoonEntity.class
            );

            //  조회 속도 메트릭 수집
            customMetrics.recordSearchPerformanceMetrics(response, query.getQuery());
            // 검색어 패턴 메트릭 수집
            customMetrics.recordSearchTermMetrics(query.getQuery());

            return response.hits().hits().stream()
                    .map(hit -> searchEngineMapper.toResponse(hit.source()))
                    .toList();

        } catch (IOException e) {
            customMetrics.getCounter(
                    "search.error",
                    "Search operation errors",
                    List.of(Tag.of("error_type", e.getClass().getSimpleName()))
            ).increment();

            log.error("Search failed: query={}, error={}", query.getQuery(), e.getMessage(), e);
            throw new DataAccessResourceFailureException("Failed to execute search", e);
        }
    }

    private SearchRequest createSearchRequest(String searchTerm) {
        return SearchRequest.of(searchRequest ->
                searchRequest.index("webtoon-kr")
                        .query(query ->
                                query.bool(bool ->
                                        bool.should(should ->
                                                        should.match(match ->
                                                                match.field("title")
                                                                        .query(FieldValue.of(searchTerm))
                                                                        .boost(2.0f)
                                                        )
                                                )
                                                .should(should ->
                                                        should.match(match ->
                                                                match.field("authors")
                                                                        .query(FieldValue.of(searchTerm))
                                                                        .boost(1.5f)
                                                        )
                                                )
                                                .should(should ->
                                                        should.match(match ->
                                                                match.field("provider")
                                                                        .query(FieldValue.of(searchTerm))
                                                                        .boost(1.0f)
                                                        )
                                                )
                                                .minimumShouldMatch("1")
                                )
                        )
                        .size(15)
                        .trackScores(true)
                        .trackTotalHits(TrackHits.of(th -> th.enabled(true)))
        );
    }
}
