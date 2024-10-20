package com.samsamohoh.webtoonsearch.adapter.api.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SelectRecordUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SelectWebtoonDTO;
import com.samsamohoh.webtoonsearch.adapter.api.ApiResponse;
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
            return new ApiResponse<>(true);

        return new ApiResponse<>(false);
    }
}
