package com.samsamohoh.webtoonsearch.application.domain;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SearchableWebtoon {
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

    public boolean isForAdults() {
        return ageGrade >= 19;
    }

    public boolean isFreeToRead() {
        return isFree || (freeWaitHour != null && freeWaitHour == 0);
    }

    public boolean isRecentlyUpdated() {
        return isUpdated && !isEnd;
    }
}
