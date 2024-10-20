package com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class WebtoonResult {
    List<WebtoonDTO> webtoons;

    public WebtoonResult(List<WebtoonDTO> webtoons) {
        this.webtoons = webtoons;
    }

    @Value
    @Builder
    public static class WebtoonDTO {
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
    }
}
