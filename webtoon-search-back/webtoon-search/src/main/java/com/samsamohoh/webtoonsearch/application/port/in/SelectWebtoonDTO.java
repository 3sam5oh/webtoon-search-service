package com.samsamohoh.webtoonsearch.application.port.in;

import lombok.Value;

@Value
public class SelectWebtoonDTO {

    String id;
    String url;
    String title;
    String platform;
}
