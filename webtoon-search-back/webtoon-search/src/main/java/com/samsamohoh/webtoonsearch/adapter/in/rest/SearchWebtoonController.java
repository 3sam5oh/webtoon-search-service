package com.samsamohoh.webtoonsearch.adapter.in.rest;

import com.samsamohoh.webtoonsearch.adapter.in.rest.mapper.WebtoonMapper;
import com.samsamohoh.webtoonsearch.adapter.in.rest.vo.SearchWebtoonsResponseVO;
import com.samsamohoh.webtoonsearch.application.port.in.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webtoons")
@RequiredArgsConstructor
public class SearchWebtoonController {
    private final SearchWebtoonUseCase searchWebtoonUseCase;
    private final WebtoonMapper webtoonMapper;

    @GetMapping("/searchable")
    public ResponseEntity<ApiResponse<SearchWebtoonsResponseVO>> searchWebtoons(@RequestParam(required = false) String title) {
        SearchWebtoonsResponseVO responseVO = webtoonMapper.toResponseVO(searchWebtoonUseCase.searchWebtoons(title));
        return ResponseEntity.ok(new ApiResponse<>(responseVO));
    }
}
