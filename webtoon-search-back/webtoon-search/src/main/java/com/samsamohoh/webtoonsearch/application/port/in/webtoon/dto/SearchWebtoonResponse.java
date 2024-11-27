package com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto;

import lombok.Value;

import java.util.List;

@Value
public class SearchWebtoonResponse {

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
