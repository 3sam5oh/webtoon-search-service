package com.samsamohoh.webtoonsearch.adapter.api.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonRequest;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonResponse;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.common.ApiResponse;
import com.samsamohoh.webtoonsearch.exception.WebtoonSearchException;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
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
    public ResponseEntity<ApiResponse<List<SearchWebtoonResponse>>> searchWebtoon(@RequestParam String query) {
        try {
            List<SearchWebtoonResponse> result = searchWebtoonUseCase.searchWebtoons(
                    new SearchWebtoonRequest(query)
            );
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("'%s'에 대한 검색 결과 %d건이 있습니다", query, result.size()),
                    result
            ));

        } catch (IllegalArgumentException | WebtoonSearchException e) {
            // ApiResponse의 error 메소드는 이미 success=false, data=null로 설정됨
            ApiResponse<List<SearchWebtoonResponse>> errorResponse = ApiResponse.error(e.getMessage());

            return e instanceof IllegalArgumentException ?
                    ResponseEntity.badRequest().body(errorResponse) :
                    ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
