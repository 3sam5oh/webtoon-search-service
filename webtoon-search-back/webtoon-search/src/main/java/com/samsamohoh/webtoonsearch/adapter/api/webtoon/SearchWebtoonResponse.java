package com.samsamohoh.webtoonsearch.adapter.api.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.WebtoonResult;
import lombok.Value;

import java.util.List;

@Value
public class SearchWebtoonResponse {
    List<WebtoonResponseDTO> webtoons;

    public static SearchWebtoonResponse fromWebtoonResult(WebtoonResult result) {
        List<WebtoonResponseDTO> webtoonResponseDTOs = result.getWebtoons().stream()
                .map(WebtoonResponseDTO::fromWebtoonDTO)
                .toList();
        return new SearchWebtoonResponse(webtoonResponseDTOs);
    }

    @Value
    public static class WebtoonResponseDTO {
        String id;
        String title;
        String provider;
        List<String> updateDays;
        String url;
        List<String> thumbnail;
        boolean isEnd;
        boolean isFree;
        boolean isUpdated;
        int ageGrade;
        Integer freeWaitHour;
        List<String> authors;

        public static WebtoonResponseDTO fromWebtoonDTO(WebtoonResult.WebtoonDTO webtoon) {
            return new WebtoonResponseDTO(
                    webtoon.getId(),
                    webtoon.getTitle(),
                    webtoon.getProvider(),
                    webtoon.getUpdateDays(),
                    webtoon.getUrl(),
                    webtoon.getThumbnail(),
                    webtoon.isEnd(),
                    webtoon.isFree(),
                    webtoon.isUpdated(),
                    webtoon.getAgeGrade(),
                    webtoon.getFreeWaitHour(),
                    webtoon.getAuthors()
            );
        }
    }
}
