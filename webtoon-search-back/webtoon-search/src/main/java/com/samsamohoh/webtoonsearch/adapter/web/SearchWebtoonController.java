package com.samsamohoh.webtoonsearch.adapter.web;

import com.samsamohoh.webtoonsearch.application.port.in.SearchWebtoonCommand;
import com.samsamohoh.webtoonsearch.application.port.in.WebtoonResult;
import com.samsamohoh.webtoonsearch.application.port.in.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.common.ApiResponse;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webtoons")
@Counted(value = "search.request.count", extraTags = {"class", "search-webtoon-controller"})
@RequiredArgsConstructor
public class SearchWebtoonController {
    private final SearchWebtoonUseCase searchWebtoonUseCase;

    @Timed(value = "search.request.duration"
            , extraTags = {"class", "search-webtoon-controller", "endpoint", "/webtoons/search"}
            , description = "duration until search webtoon list")
    @GetMapping("/search")
    public ApiResponse<SearchWebtoonResponse> searchWebtoon(@RequestParam String query) {

        WebtoonResult result = searchWebtoonUseCase.searchWebtoons(new SearchWebtoonCommand(query));
        return new ApiResponse<>(SearchWebtoonResponse.fromWebtoonResult(result));
    }

    @GetMapping("health")
    public String healthCheck() {
        return "fine working!";
    }
}
