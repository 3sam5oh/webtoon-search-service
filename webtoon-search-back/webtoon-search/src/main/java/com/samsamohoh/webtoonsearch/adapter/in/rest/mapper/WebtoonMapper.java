package com.samsamohoh.webtoonsearch.adapter.in.rest.mapper;

import com.samsamohoh.webtoonsearch.adapter.in.rest.dto.SearchableWebtoonDTO;
import com.samsamohoh.webtoonsearch.adapter.in.rest.vo.SearchWebtoonsResponseVO;
import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WebtoonMapper {
    public SearchableWebtoonDTO toDTO(SearchableWebtoon webtoon) {
        return SearchableWebtoonDTO.builder()
                .id(webtoon.getId())
                .title(webtoon.getTitle())
                .provider(webtoon.getProvider())
                .updateDays(webtoon.getUpdateDays())
                .url(webtoon.getUrl())
                .thumbnail(webtoon.getThumbnail())
                .isEnd(webtoon.isEnd())
                .isFree(webtoon.isFree())
                .isUpdated(webtoon.isUpdated())
                .ageGrade(webtoon.getAgeGrade())
                .freeWaitHour(webtoon.getFreeWaitHour())
                .authors(webtoon.getAuthors())
                .build();
    }

    public SearchWebtoonsResponseVO toResponseVO(List<SearchableWebtoon> webtoon) {
        List<SearchableWebtoonDTO> dtos = webtoon.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return SearchWebtoonsResponseVO.builder()
                .webtoonlist(dtos)
                .build();
    }
}
