package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SearchWebtoonCommand;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.WebtoonResult;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonPort;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.LoadWebtoonQuery;
import com.samsamohoh.webtoonsearch.common.metrics.CustomMetrics;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/* 필기.
    메트릭: 어플리케이션 관점에서의 수집 메트릭이 필요. ex) 조회 실패, 요청 건수, 로그로 추천 -> 자주 사용하는 단어?
    메트릭으로 봐야 할 내용과 로그로 확인해야 하는 데이터를 구분 할 것
    Metric cardinality 검색 해볼 것
    다 알고 싶으면 트레이스 -> 전체 요청 중에 일부만 남긴다. -> 가능 하면 전체 요청의 0.1% 추천
        트레이스는 직접 구현해야함 -> 요청부터 반환까지 어떤 일이 발생하는지 모두 알고 싶다.
        -> 로직적으로 중요한 순간에만 트레이스를 보내도록 설계를 해야한다.
        Static instrumentation - User Statically Defined Tracing (USDT)
* */

@Service
@RequiredArgsConstructor
public class SearchWebtoonService implements SearchWebtoonUseCase {
    private final LoadWebtoonPort loadWebtoonPort;
    private final CustomMetrics customMetrics;

    @Override
    public WebtoonResult searchWebtoons(SearchWebtoonCommand command) {

        if (isNull(command, "search.condition.null.count"
                , "title is null"
                , Arrays.asList(Tag.of("class", "search-webtoon-service"),
                        Tag.of("method", "search-webtoons"),
                        Tag.of("endpoint", "/webtoons/search")))
        )
            throw new IllegalArgumentException("검색 조건이 존재 하지 않음.");

        return loadWebtoonPort.loadWebtoons(new LoadWebtoonQuery(command.getQuery()));
    }

    // 검색어 전달 객체가 null이나 빈 값을 가질 경우 메트릭 에러 카운트 메트릭 등록
    private boolean isNull(SearchWebtoonCommand data
            , String metricName
            , String description
            , List<Tag> tags) {

        if (data == null || data.getQuery() == null || data.getQuery().isEmpty()) {
            customMetrics.getCounter(metricName, description, tags).increment();

            return true;
        }
        return false;
    }
}
