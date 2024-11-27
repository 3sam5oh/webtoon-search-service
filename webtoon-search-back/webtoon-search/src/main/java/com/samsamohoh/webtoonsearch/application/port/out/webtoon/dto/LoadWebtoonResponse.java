package com.samsamohoh.webtoonsearch.application.port.out.webtoon.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
public class LoadWebtoonResponse {

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
