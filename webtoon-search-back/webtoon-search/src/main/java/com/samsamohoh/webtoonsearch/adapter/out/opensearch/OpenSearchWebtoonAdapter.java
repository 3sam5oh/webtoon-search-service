package com.samsamohoh.webtoonsearch.adapter.out.opensearch;

import com.samsamohoh.webtoonsearch.adapter.out.opensearch.entity.SearchableWebtoonEntity;
import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OpenSearchWebtoonAdapter implements LoadWebtoonPort {
    private final OpenSearchWebtoonRepository openSearchWebtoonRepository;

    @Override
    public List<SearchableWebtoon> searchWebtoonsByTitle(String title) {
        List<SearchableWebtoonEntity> entities = openSearchWebtoonRepository.findByTitleContaining(title);
        return entities.stream()
                .map(this::convertToSearchableWebtoon)
                .collect(Collectors.toList());
    }

    private SearchableWebtoon convertToSearchableWebtoon(SearchableWebtoonEntity entity) {
        return SearchableWebtoon.create(
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
}
