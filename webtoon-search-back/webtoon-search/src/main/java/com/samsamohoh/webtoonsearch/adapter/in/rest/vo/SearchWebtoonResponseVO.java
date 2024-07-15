package com.samsamohoh.webtoonsearch.adapter.in.rest.vo;

import com.samsamohoh.webtoonsearch.application.dto.SearchWebtoonResultDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchWebtoonResponseVO {
    private final String id;
    private final String title;
    private final String provider;
    private final List<String> updateDays;
    private final String url;
    private final List<String> thumbnail;
    private final boolean isEnd;
    private final boolean isFree;
    private final boolean isUpdated;
    private final int ageGrade;
    private final Integer freeWaitHour;
    private final List<String> authors;

    public static SearchWebtoonResponseVO fromDTO(SearchWebtoonResultDTO dto) {
        return SearchWebtoonResponseVO.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .provider(dto.getProvider())
                .updateDays(dto.getUpdateDays())
                .url(dto.getUrl())
                .thumbnail(dto.getThumbnail())
                .isEnd(dto.isEnd())
                .isFree(dto.isFree())
                .isUpdated(dto.isUpdated())
                .ageGrade(dto.getAgeGrade())
                .freeWaitHour(dto.getFreeWaitHour())
                .authors(dto.getAuthors())
                .build();
    }
}
