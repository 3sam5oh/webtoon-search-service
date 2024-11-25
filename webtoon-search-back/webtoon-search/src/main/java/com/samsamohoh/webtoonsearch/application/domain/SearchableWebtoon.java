package com.samsamohoh.webtoonsearch.application.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(access = AccessLevel.PRIVATE)
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

    // 검색 성능 모니터링을 위한 메타데이터
    SearchMetadata searchMetadata;

    @Value
    @Builder
    public static class SearchMetadata {
        float score;         // OpenSearch 검색 점수
        long latency;        // 검색 소요시간 (ms)
        String matchedOn;    // 매칭된 필드 (title, authors 등)
    }

    // 팩토리 메서드 - 모든 필요한 데이터를 파라미터로 받도록 수정
    public static SearchableWebtoon createFrom(
            String id,
            String title,
            String provider,
            List<String> updateDays,
            String url,
            List<String> thumbnail,
            boolean isEnd,
            boolean isFree,
            boolean isUpdated,
            int ageGrade,
            Integer freeWaitHour,
            List<String> authors,
            float score,
            long latency,
            String matchedField) {

        return builder()
                .id(id)
                .title(title)
                .provider(provider)
                .updateDays(updateDays)
                .url(url)
                .thumbnail(thumbnail)
                .isEnd(isEnd)
                .isFree(isFree)
                .isUpdated(isUpdated)
                .ageGrade(ageGrade)
                .freeWaitHour(freeWaitHour)
                .authors(authors)
                .searchMetadata(SearchMetadata.builder()
                        .score(score)
                        .latency(latency)
                        .matchedOn(matchedField)
                        .build())
                .build();
    }

    // 선택적으로 검색 메타데이터만 따로 생성할 수 있는 팩토리 메서드 추가
    public SearchableWebtoon withSearchMetadata(float score, long latency, String matchedOn) {
        return builder()
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
                .searchMetadata(SearchMetadata.builder()
                        .score(score)
                        .latency(latency)
                        .matchedOn(matchedOn)
                        .build())
                .build();
    }
}
