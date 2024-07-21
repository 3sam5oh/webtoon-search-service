package com.samsamohoh.webtoonsearch.adapter.web;

import com.samsamohoh.webtoonsearch.application.port.in.SearchWebtoonCommand;
import com.samsamohoh.webtoonsearch.application.port.in.WebtoonResult;
import com.samsamohoh.webtoonsearch.application.port.in.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.common.ApiResponse;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webtoons")
@Counted("search.controller.request.count")
public class SearchWebtoonController {
    private final SearchWebtoonUseCase searchWebtoonUseCase;
    private final MeterRegistry meterRegistry;

    @Autowired
    public SearchWebtoonController(SearchWebtoonUseCase searchWebtoonUseCase, MeterRegistry meterRegistry) {
        this.searchWebtoonUseCase = searchWebtoonUseCase;
        this.meterRegistry = meterRegistry;
    }

    @Timed(value = "find.search.list.duration", extraTags = {"/webtoons/search", "GET"},
            description = "find webtoon lists by title")
    @Counted("search.title.request.count")
    @GetMapping("/search")
    public ApiResponse<SearchWebtoonResponse> searchWebtoon(@RequestParam String query) {

        if (query.equals("1")){
            counter(meterRegistry, "search.title.request.error.count", "Invalid input query")
                    .increment();
            throw new IllegalArgumentException("실패함");
        }

        WebtoonResult result = searchWebtoonUseCase.searchWebtoons(new SearchWebtoonCommand(query));
        return new ApiResponse<>(SearchWebtoonResponse.fromWebtoonResult(result));
    }

    @GetMapping("health")
    public String healthCheck() {
        return "fine working!";
    }

    private Counter counter(MeterRegistry meterRegistry, String name, String describe){

        return Counter.builder(name)
                .description(describe)
                .register(meterRegistry);
    }
}
