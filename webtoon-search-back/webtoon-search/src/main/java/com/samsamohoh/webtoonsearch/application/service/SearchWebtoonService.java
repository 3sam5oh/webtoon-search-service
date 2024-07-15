package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;
import com.samsamohoh.webtoonsearch.application.dto.SearchWebtoonResultDTO;
import com.samsamohoh.webtoonsearch.application.port.in.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchWebtoonService implements SearchWebtoonUseCase {

    private final LoadWebtoonPort loadWebtoonPort;

    @Override
    @Transactional(readOnly = true)
    public List<SearchWebtoonResultDTO> searchWebtoons(String title) {
        List<SearchableWebtoon> webtoons = loadWebtoonPort.searchWebtoonsByTitle(title);
        return webtoons.stream()
                .map(SearchableWebtoon::toDTO)
                .collect(Collectors.toList());
    }
}
