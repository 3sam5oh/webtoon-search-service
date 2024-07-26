package com.samsamohoh.webtoonsearch.adapter.persistence;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Document(indexName = "webtoon-kr")
public class SearchWebtoonEntity {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword)
    private String provider;

    @Field(type = FieldType.Keyword)
    private List<String> updateDays;

    @Field(type = FieldType.Keyword)
    private String url;

    @Field(type = FieldType.Keyword)
    private List<String> thumbnail;

    @Field(type = FieldType.Boolean)
    private boolean isEnd;

    @Field(type = FieldType.Boolean)
    private boolean isFree;

    @Field(type = FieldType.Boolean)
    private boolean isUpdated;

    @Field(type = FieldType.Integer)
    private int ageGrade;

    @Field(type = FieldType.Integer)
    private Integer freeWaitHour;

    @Field(type = FieldType.Keyword)
    private List<String> authors;
}
