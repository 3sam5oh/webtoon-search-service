package com.samsamohoh.webtoonsearch.adapter.searchengine;

import com.samsamohoh.webtoonsearch.adapter.persistence.SearchPersistenceAdapter;
import com.samsamohoh.webtoonsearch.adapter.persistence.SearchWebtoonEntity;
import com.samsamohoh.webtoonsearch.application.port.in.WebtoonResult;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonPort;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchEngineAdapter implements LoadWebtoonPort {
    private final SearchPersistenceAdapter searchPersistenceAdapter;

    @Override
    public WebtoonResult loadWebtoons(LoadWebtoonQuery query) {
        String searchTerm = query.getSearchTerm();

        List<SearchWebtoonEntity> entities = searchPersistenceAdapter.searchAll(searchTerm);

        List<WebtoonResult.WebtoonDTO> webtoons = entities.stream()
                .map(this::mapToWebtoonDTO)
                .collect(Collectors.toList());

        return new WebtoonResult(webtoons);
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
