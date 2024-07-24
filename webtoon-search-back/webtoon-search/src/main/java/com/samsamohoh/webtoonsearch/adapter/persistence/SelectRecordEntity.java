package com.samsamohoh.webtoonsearch.adapter.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity(name = "")
@AllArgsConstructor
@NoArgsConstructor
public class SelectRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    private String uid;
    @Column
    private String url;
    @Column
    private String title;
    @Column
    private String platform;

}
