package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.port.in.SearchWebtoonCommand;
import com.samsamohoh.webtoonsearch.application.port.in.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.WebtoonResult;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonPort;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonQuery;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final MeterRegistry meterRegistry;

    @Override
    public WebtoonResult searchWebtoons(SearchWebtoonCommand command) {

        if(isNull(command, "search.condition.null.count","title is null"))
            throw new IllegalArgumentException("검색 조건이 존재하지 않음.");

        return loadWebtoonPort.loadWebtoons(new LoadWebtoonQuery(command.getQuery()));
    }

    private boolean isNull(SearchWebtoonCommand command, String metricName, String description){

        if (command == null || command.getQuery() == null || command.getQuery().isEmpty()) {
            Counter counter = Counter.builder(metricName)
                    .description(description)
                    .register(meterRegistry);
        }

        return false;
    }
}
