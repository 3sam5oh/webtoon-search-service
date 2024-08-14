package com.samsamohoh.webtoonsearch.adapter.searchengine;
//
//import com.samsamohoh.webtoonsearch.adapter.persistence.SearchPersistenceAdapter;
//import com.samsamohoh.webtoonsearch.adapter.persistence.SearchWebtoonEntity;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.WebtoonResult;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonPort;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.LoadWebtoonQuery;
//import com.samsamohoh.webtoonsearch.common.metrics.CustomMetrics;
import io.micrometer.core.annotation.Timed;
//import io.micrometer.core.instrument.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
@Component
//@RequiredArgsConstructor
public class SearchEngineAdapter implements LoadWebtoonPort {
//    private final SearchPersistenceAdapter searchPersistenceAdapter;
//    private final CustomMetrics customMetrics;
//
    @Override
    @Timed(value = "search.opensearch.reply.duration",
            extraTags = {"class","search-engine-adapter", "method", "load-webtoons", "endpoint", "/webtoons/search"},
            description = "duration until opensearch reply")
    public WebtoonResult loadWebtoons(LoadWebtoonQuery query) {
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
        return null;
    }
//
//    private WebtoonResult.WebtoonDTO mapToWebtoonDTO(SearchWebtoonEntity entity) {
//        return WebtoonResult.WebtoonDTO.builder()
//                .id(entity.getId())
//                .title(entity.getTitle())
//                .provider(entity.getProvider())
//                .updateDays(entity.getUpdateDays())
//                .url(entity.getUrl())
//                .thumbnail(entity.getThumbnail())
//                .isEnd(entity.isEnd())
//                .isFree(entity.isFree())
//                .isUpdated(entity.isUpdated())
//                .ageGrade(entity.getAgeGrade())
//                .freeWaitHour(entity.getFreeWaitHour())
//                .authors(entity.getAuthors())
//                .build();
//    }
}
