package com.samsamohoh.webtoonsearch.adapter.api.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonCommand;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.WebtoonResult;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.common.ApiResponse;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webtoons")
@Counted(value = "search.request.count", extraTags = {"class", "search-webtoon-controller"})
@RequiredArgsConstructor
@Slf4j
public class SearchWebtoonController {
    private final SearchWebtoonUseCase searchWebtoonUseCase;

    @Timed(value = "search.request.duration"
            , extraTags = {"class", "search-webtoon-controller", "endpoint", "/webtoons/search"}
            , description = "duration until search webtoon list")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SearchWebtoonResponse>> searchWebtoon(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("검색어를 입력해주세요"));
            }

            WebtoonResult result = searchWebtoonUseCase.searchWebtoons(
                    new SearchWebtoonCommand(query)
            );

            SearchWebtoonResponse response = SearchWebtoonResponse.fromWebtoonResult(result);

            if (response.getWebtoons().isEmpty()) {
                return ResponseEntity.ok(
                        ApiResponse.success("검색 결과가 없습니다", response)
                );
            }

            return ResponseEntity.ok(
                    ApiResponse.success(
                            String.format("'%s'에 대한 검색 결과 %d건이 있습니다",
                                    query, response.getWebtoons().size()),
                            response
                    )
            );

        } catch (Exception e) {
            log.error("웹툰 검색 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("웹툰 검색 중 오류가 발생했습니다"));
        }
    }
}
