package com.samsamohoh.webtoonsearch.adapter.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
public class SelectRecordRequest {

    String id;
    String gender;
    String age;
    String url;
    String title;
    String platform;
    String days;
    boolean isEnd;
    boolean isFree;


    /*
    * 웹으로 부터의 입력 값에 대한 유효성 검사
    */
    private SelectRecordRequest(String id, String gender, String age, String url, String title,
                               String platform, String days, boolean isEnd, boolean isFree) {

        if(id == null || id.isEmpty())
            throw new IllegalArgumentException("id cannot be null or empty");
        if(gender == null || gender.isEmpty())
            throw new IllegalArgumentException("gender cannot be null or empty");
        if(age == null || age.isEmpty())
            throw new IllegalArgumentException("age cannot be null or empty");
        if(url == null || url.isEmpty())
            throw new IllegalArgumentException("url cannot be null or empty");
        if(title == null || title.isEmpty())
            throw new IllegalArgumentException("title cannot be null or empty");
        if(platform == null || platform.isEmpty())
            throw new IllegalArgumentException("platform cannot be null or empty");
        if(days == null || days.isEmpty())
            throw new IllegalArgumentException("days cannot be null or empty");

        this.id = id;
        this.gender = gender;
        this.age = age;
        this.url = url;
        this.title = title;
        this.platform = platform;
        this.days = days;
        this.isEnd = isEnd;
        this.isFree = isFree;
    }
}
