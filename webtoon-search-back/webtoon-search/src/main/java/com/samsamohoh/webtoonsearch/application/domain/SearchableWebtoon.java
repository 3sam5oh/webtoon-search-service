package com.samsamohoh.webtoonsearch.application.domain;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Value
@Builder
@Getter
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

    public static SearchableWebtoon create(String id, String title, String provider, List<String> updateDays,
                                        String url, List<String> thumbnail, boolean isEnd, boolean isFree,
                                        boolean isUpdated, int ageGrade, Integer freeWaitHour, List<String> authors) {
        return builder()
                .id(id)
                .title(title)
                .provider(provider)
                .updateDays(Collections.unmodifiableList(updateDays))
                .url(url)
                .thumbnail(Collections.unmodifiableList(thumbnail))
                .isEnd(isEnd)
                .isFree(isFree)
                .isUpdated(isUpdated)
                .ageGrade(ageGrade)
                .freeWaitHour(freeWaitHour)
                .authors(Collections.unmodifiableList(authors))
                .build();
    }
}
