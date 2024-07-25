package com.samsamohoh.webtoonsearch.adapter.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity(name = "record")
@AllArgsConstructor
@NoArgsConstructor
public class SelectRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String uid;
    @Column
    private String url;
    @Column
    private String title;
    @Column
    private String platform;

    public SelectRecordEntity(String uid, String url, String title, String platform) {
        this.uid = uid;
        this.url = url;
        this.title = title;
        this.platform = platform;
    }
}
