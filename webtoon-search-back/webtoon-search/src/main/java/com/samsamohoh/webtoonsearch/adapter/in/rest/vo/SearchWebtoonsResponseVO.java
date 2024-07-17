package com.samsamohoh.webtoonsearch.adapter.in.rest.vo;

import com.samsamohoh.webtoonsearch.adapter.in.rest.dto.SearchableWebtoonDTO;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SearchWebtoonsResponseVO {
    List<SearchableWebtoonDTO> webtoonlist;
}
