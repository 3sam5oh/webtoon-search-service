package com.samsamohoh.webtoonsearch.adapter.api.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SelectRecordUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SelectWebtoonRequest;
import com.samsamohoh.webtoonsearch.common.ApiResponse;
import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webtoons")
@Counted(value = "select.request.count", extraTags = {"class", "select-webtoon-controller"})
@RequiredArgsConstructor
@Slf4j
public class SelectRecordController {
    private final SelectRecordUseCase selectRecordUseCase;

    @PostMapping("/select")
    public ResponseEntity<ApiResponse<Boolean>> selectWebtoon(@RequestBody SelectRecordRequest webtoonRequest) {
        try {
            // 요청 데이터 유효성 검사
            if (!isValidRequest(webtoonRequest)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("잘못된 웹툰 정보입니다."));
            }

            boolean isRecorded = selectRecordUseCase.addClickRecord(
                    new SelectWebtoonRequest(
                            webtoonRequest.getId(),
                            webtoonRequest.getUrl(),
                            webtoonRequest.getTitle(),
                            webtoonRequest.getPlatform()
                    )
            );

            if (isRecorded) {
                return ResponseEntity.ok(
                        ApiResponse.success(
                                String.format("'%s' 웹툰 선택이 기록되었습니다", webtoonRequest.getTitle()),
                                true
                        )
                );
            } else {
                return ResponseEntity.ok(
                        ApiResponse.error("웹툰 선택 기록에 실패했습니다")
                );
            }

        } catch (Exception e) {
            log.error("웹툰 선택 기록 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("웹툰 선택 기록 중 오류가 발생했습니다"));
        }
    }

    private boolean isValidRequest(SelectRecordRequest request) {
        return request != null
                && request.getId() != null
                && request.getUrl() != null && !request.getUrl().trim().isEmpty()
                && request.getTitle() != null && !request.getTitle().trim().isEmpty()
                && request.getPlatform() != null && !request.getPlatform().trim().isEmpty();
    }
}
