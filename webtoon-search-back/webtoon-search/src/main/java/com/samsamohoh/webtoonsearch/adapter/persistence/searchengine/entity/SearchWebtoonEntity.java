package com.samsamohoh.webtoonsearch.adapter.persistence.searchengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * openSearch의 webtoon-kr 인덱스와 매핑되는 엔티티
 * 매핑 설정은 webtoon-mapping.json 참조
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchWebtoonEntity {

    /**
     * OpenSearch 인덱스 설정
     */
    public static final String INDEX_NAME = "webtoon-kr";

    /**
     * 검색 결과 필드
     */
    private String id;                // 웹툰 고유 식별자
    private String title;             // 제목
    private String provider;          // 제공자 (naver, kakao 등)
    private List<String> updateDays;  // 연재 요일
    private String url;               // 웹툰 URL
    private List<String> thumbnail;   // 썸네일 이미지 URL 목록
    @JsonProperty("isEnd")
    private boolean isEnd;            // 완결 여부 JSON의 "isEnd" 필드를 매핑
    @JsonProperty("isFree")
    private boolean isFree;           // 무료 여부 JSON의 "isFree" 필드를 매핑
    @JsonProperty("isUpdated")
    private boolean isUpdated;        // 최근 업데이트 여부 JSON의 "isUpdated" 필드를 매핑
    private int ageGrade;            // 연령 등급
    private Integer freeWaitHour;     // 무료 공개 대기 시간
    private List<String> authors;     // 작가 목록
}
