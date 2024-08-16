package com.samsamohoh.webtoonsearch.adapter.searchengine;

import com.samsamohoh.webtoonsearch.adapter.persistence.SearchWebtoonEntity;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.WebtoonResult;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonPort;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.LoadWebtoonQuery;
import com.samsamohoh.webtoonsearch.common.metrics.CustomMetrics;
import io.micrometer.core.annotation.Timed;
//import org.springframework.dao.DataAccessResourceFailureException;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
//@RequiredArgsConstructor
public class SearchEngineAdapter implements LoadWebtoonPort {
    //    private final SearchPersistenceAdapter searchPersistenceAdapter;
    private final CustomMetrics customMetrics;
    private final OpenSearchClient openSearchClient;

    @Override
    @Timed(value = "search.opensearch.reply.duration",
            extraTags = {"class", "search-engine-adapter", "method", "load-webtoons", "endpoint", "/webtoons/search"},
            description = "duration until opensearch reply")
    public WebtoonResult loadWebtoons(LoadWebtoonQuery term) {

        String searchTerm = term.getSearchTerm();
        List<SearchWebtoonEntity> resultList = new ArrayList<>();

        try {
            SearchRequest request = createSearchRequest(searchTerm);
            SearchResponse<SearchWebtoonEntity> response = openSearchClient.search(request, SearchWebtoonEntity.class);
            List<WebtoonResult.WebtoonDTO> results = response.hits().hits().stream()
                    .map(hit -> mapToWebtoonDTO(hit.source()))
                    .toList();

            return new WebtoonResult(results);

        } catch (IOException e) {
            customMetrics.getCounter("opensearch.connection.fail.count"
                    , "metrics for opensearch connecting failure"
                    , Arrays.asList(Tag.of("class", "search-engine-adapter"),
                            Tag.of("method", "load-webtoons"),
                            Tag.of("endpoint", "/webtoons/search"))).increment();
            throw new DataAccessResourceFailureException(e.getMessage());
        }
    }

    //        String searchTerm = query.getSearchTerm();
//
//        try {
//            List<SearchWebtoonEntity> entities = searchPersistenceAdapter.searchAll(searchTerm);
//
//            List<WebtoonResult.WebtoonDTO> webtoons = entities.stream()
//                    .map(this::mapToWebtoonDTO)
//                    .collect(Collectors.toList());
//
//            return new WebtoonResult(webtoons);
//
//        } catch(DataAccessResourceFailureException e) {
//            customMetrics.getCounter("opensearch.connection.fail.count"
//                    ,"metrics for opensearch connecting failure"
//                    , Arrays.asList(Tag.of("class","search-engine-adapter"),
//                                    Tag.of("method","load-webtoons"),
//                                    Tag.of("endpoint","/webtoons/search"))).increment();
//            throw new DataAccessResourceFailureException(e.getMessage());
//        }
//        return null;
//    }
//

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

    private WebtoonResult.WebtoonDTO mapToWebtoonDTO(SearchWebtoonEntity entity) {
        return WebtoonResult.WebtoonDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .provider(entity.getProvider())
                .updateDays(entity.getUpdateDays())
                .url(entity.getUrl())
                .thumbnail(entity.getThumbnail())
                .isEnd(entity.isEnd())
                .isFree(entity.isFree())
                .isUpdated(entity.isUpdated())
                .ageGrade(entity.getAgeGrade())
                .freeWaitHour(entity.getFreeWaitHour())
                .authors(entity.getAuthors())
                .build();
    }
}
