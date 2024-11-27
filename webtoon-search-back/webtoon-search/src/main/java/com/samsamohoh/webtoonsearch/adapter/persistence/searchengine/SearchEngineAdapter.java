package com.samsamohoh.webtoonsearch.adapter.persistence.searchengine;

import com.samsamohoh.webtoonsearch.adapter.persistence.searchengine.entity.SearchWebtoonEntity;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonResponse;
import com.samsamohoh.webtoonsearch.application.port.out.webtoon.LoadWebtoonPort;
import com.samsamohoh.webtoonsearch.application.port.out.webtoon.dto.LoadWebtoonRequest;
import com.samsamohoh.webtoonsearch.application.port.out.webtoon.dto.LoadWebtoonResponse;
import com.samsamohoh.webtoonsearch.common.metrics.CustomMetrics;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchEngineAdapter implements LoadWebtoonPort {

    private final OpenSearchClient openSearchClient;
    private final SearchEnginePersistenceMapper searchEngineMapper;
    private final CustomMetrics customMetrics;

    @Override
    @Timed(value = "search.opensearch.reply.duration",
            extraTags = {"class", "search-engine-adapter", "method", "load-webtoons", "endpoint", "/webtoons/search"},
            description = "duration until opensearch reply")
    public List<LoadWebtoonResponse> loadWebtoons(LoadWebtoonRequest term) {
        try {
            SearchRequest request = createSearchRequest(term.getQuery());
            SearchResponse<SearchWebtoonEntity> response = openSearchClient.search(request, SearchWebtoonEntity.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .map(searchEngineMapper::toResponse)
                    .toList();

        } catch (IOException e) {
            customMetrics.getCounter("opensearch.connection.fail.count",
                    "metrics for opensearch connecting failure",
                    Arrays.asList(Tag.of("class", "search-engine-adapter"),
                            Tag.of("method", "load-webtoons"),
                            Tag.of("endpoint", "/webtoons/search"))).increment();
            throw new DataAccessResourceFailureException(e.getMessage());
        }
    }

    // OpenSearch한테 요청을 보내는 쿼리문
    private SearchRequest createSearchRequest(String searchTerm) {
        return SearchRequest.of(searchRequest ->
                searchRequest.index("webtoon-kr")
                        .query(query ->
                                query.bool(bool ->
                                        bool.should(should ->
                                                        should.wildcard(wildcard ->
                                                                wildcard.field("title")
                                                                        .value("*" + searchTerm + "*")
                                                        )
                                                )
                                                .should(should ->
                                                        should.wildcard(wildcard ->
                                                                wildcard.field("authors")
                                                                        .value("*" + searchTerm + "*")
                                                        )
                                                )
                                                .should(should ->
                                                        should.wildcard(wildcard ->
                                                                wildcard.field("provider")
                                                                        .value("*" + searchTerm + "*")
                                                        )
                                                )
                                                .should(should ->
                                                        should.wildcard(wildcard ->
                                                                wildcard.field("*")
                                                                        .value("*" + searchTerm + "*")
                                                        )
                                                )
                                )
                        )
        );
    }
}
