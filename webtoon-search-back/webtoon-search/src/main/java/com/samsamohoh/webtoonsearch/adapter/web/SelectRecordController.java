package com.samsamohoh.webtoonsearch.adapter.web;

import com.samsamohoh.webtoonsearch.application.port.in.SelectRecordUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.SelectWebtoonDTO;
import com.samsamohoh.webtoonsearch.common.ApiResponse;
import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webtoons")
@Counted(value = "select.request.count", extraTags = {"class", "select-webtoon-controller"})
@RequiredArgsConstructor
public class SelectRecordController {
    private final SelectRecordUseCase selectRecordUseCase;

    @PostMapping
    public ApiResponse<Boolean> selectWebtoon(@RequestBody SelectRecordRequest webtoonRequest) {

        if(selectRecordUseCase.addClickRecord(new SelectWebtoonDTO(
                webtoonRequest.getId(),
                webtoonRequest.getUrl(),
                webtoonRequest.getTitle(),
                webtoonRequest.getPlatform()
        )))
            return new ApiResponse<>();

        return new ApiResponse<>();
    }
}
