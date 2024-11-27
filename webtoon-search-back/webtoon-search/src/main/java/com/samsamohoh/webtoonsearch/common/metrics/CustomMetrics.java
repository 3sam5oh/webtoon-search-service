package com.samsamohoh.webtoonsearch.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

import java.util.List;

// 전역적으로 사용될 Metric 등록 메소드를 정의하는 클래스
public class CustomMetrics {

    private final MeterRegistry meterRegistry;

    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // 호출되는 메소드에 counter metric 등록이 필요한 경우 호출
    public Counter getCounter(String metricName, String metricDescribe, List<Tag> tags) {
        return Counter.builder(metricName)
                .description(metricDescribe)
                .tags(tags)
                .register(meterRegistry);
    }
}
