package com.samsamohoh.webtoonsearch.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchWebtoonResultDTO {
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
}
