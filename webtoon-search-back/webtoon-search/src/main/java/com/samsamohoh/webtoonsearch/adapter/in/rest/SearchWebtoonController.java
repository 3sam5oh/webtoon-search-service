package com.samsamohoh.webtoonsearch.adapter.in.rest;

import com.samsamohoh.webtoonsearch.adapter.in.rest.vo.SearchWebtoonResponseVO;
import com.samsamohoh.webtoonsearch.application.dto.SearchWebtoonResultDTO;
import com.samsamohoh.webtoonsearch.application.port.in.SearchWebtoonUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SearchWebtoonController {
    private final SearchWebtoonUseCase searchWebtoonUseCase;

    @GetMapping("/webtoons")
    public List<SearchWebtoonResponseVO> searchWebtoons(@RequestParam String title) {
        List<SearchWebtoonResultDTO> webtoons = searchWebtoonUseCase.searchWebtoons(title);
        return webtoons.stream()
                .map(SearchWebtoonResponseVO::fromDTO)
                .collect(Collectors.toList());
    }
}
