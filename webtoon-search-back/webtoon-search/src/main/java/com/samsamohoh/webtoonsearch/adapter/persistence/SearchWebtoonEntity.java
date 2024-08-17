package com.samsamohoh.webtoonsearch.adapter.persistence;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchWebtoonEntity {

    private String id;

    private String title;

    private String provider;

    private List<String> updateDays;

    private String url;

    private List<String> thumbnail;

    private boolean isEnd;

    private boolean isFree;

    private boolean isUpdated;

    private int ageGrade;

    private Integer freeWaitHour;

    private List<String> authors;
}
