package com.samsamohoh.webtoonsearch.adapter.out.opensearch;

import com.samsamohoh.webtoonsearch.adapter.out.opensearch.entity.SearchableWebtoonEntity;
import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenSearchWebtoonRepository extends ElasticsearchRepository<SearchableWebtoon, Long> {
    List<SearchableWebtoonEntity> findByTitleContaining(String title);
}
