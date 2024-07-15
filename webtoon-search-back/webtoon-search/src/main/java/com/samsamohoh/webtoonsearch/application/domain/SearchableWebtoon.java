package com.samsamohoh.webtoonsearch.application.domain;

import com.samsamohoh.webtoonsearch.application.dto.SearchWebtoonResultDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchableWebtoon {
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

    public static SearchableWebtoon create(String id, String title, String provider, List<String> updateDays,
                                        String url, List<String> thumbnail, boolean isEnd, boolean isFree,
                                        boolean isUpdated, int ageGrade, Integer freeWaitHour, List<String> authors) {
        return new SearchableWebtoon(id, title, provider, updateDays, url, thumbnail, isEnd, isFree,
                isUpdated, ageGrade, freeWaitHour, authors);
    }

    public SearchWebtoonResultDTO toDTO() {
        return SearchWebtoonResultDTO.builder()
                .id(this.id)
                .title(this.title)
                .provider(this.provider)
                .updateDays(this.updateDays)
                .url(this.url)
                .thumbnail(this.thumbnail)
                .isEnd(this.isEnd)
                .isFree(this.isFree)
                .isUpdated(this.isUpdated)
                .ageGrade(this.ageGrade)
                .freeWaitHour(this.freeWaitHour)
                .authors(this.authors)
                .build();
    }


}
