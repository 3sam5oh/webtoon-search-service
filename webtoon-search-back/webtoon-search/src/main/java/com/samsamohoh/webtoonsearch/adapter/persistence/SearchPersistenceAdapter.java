package com.samsamohoh.webtoonsearch.adapter.persistence;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchPersistenceAdapter extends ElasticsearchRepository<SearchWebtoonEntity, String> {

    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^3\"," +
            " \"authors^2\", \"provider\", \"*\"], \"type\": \"best_fields\"}}")
    List<SearchWebtoonEntity> searchAll(String searchTerm);
}
