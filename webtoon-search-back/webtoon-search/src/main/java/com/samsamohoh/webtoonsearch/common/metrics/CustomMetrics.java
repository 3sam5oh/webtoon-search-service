package com.samsamohoh.webtoonsearch.common.metrics;

import io.micrometer.core.instrument.*;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// 전역적으로 사용될 Metric 등록 메소드를 정의하는 클래스
@RequiredArgsConstructor
public class CustomMetrics {

    // 상수 정의
    private final MeterRegistry meterRegistry;
    private static final String NAME_PATTERN = "^[a-z][a-z0-9_]*(\\.([a-z0-9_])+)*$";
    private static final int MAX_TAG_LENGTH = 50;

    // 성능 측정을 위한 SLO(Service level Objectives) 정의
    private static final Duration SLO_FAST = Duration.ofMillis(100);    // 빠른 응답 100ms 이하
    private static final Duration SLO_MEDIUM = Duration.ofMillis(500);  // 중간 응답 500ms 이하
    private static final Duration SLO_SLOW = Duration.ofSeconds(1);     // 느린 응답 1s 이하
    private static final double[] PERCENTILES = {0.5, 0.75, 0.95, 0.99};     // 성능 분포 측정 구간

    // 검색 품질 관련 상수
    private static final double SCORE_THRESHOLD_EXCELLENT = 2.0;
    private static final double SCORE_THRESHOLD_GOOD = 1.0;
    private static final double SCORE_THRESHOLD_FAIR = 0.5;

    /**
     * <li> 카운터 메트릭 (Counter metric) </li>
     * <li> 이벤트 발생 횟수를 측정 (예: API 호출 횟수, 에러 발생 횟수) </li>
     */
    public Counter getCounter(String metricName, String metricDescribe, List<Tag> tags) {
        return Counter.builder(metricName)
                .description(metricDescribe)
                .tags(validateTags(tags))
                .register(meterRegistry);
    }

    /**
     * <li> 타이머 메트릭 (Timer metric) </li>
     * <li> 작업 수행 시간을 측정하고 SLO 기준으로 분포 기록 </li>
     */
    public Timer getTimer(String metricName, String metricDescribe, List<Tag> tags) {
        return Timer.builder(validateName(metricName))
                .description(metricDescribe)
                .tags(validateTags(tags))
                .publishPercentiles(PERCENTILES)
                .serviceLevelObjectives(SLO_FAST, SLO_MEDIUM, SLO_SLOW)
                .register(meterRegistry);
    }

    /**
     * Distribution Summary 메트릭
     */
    public DistributionSummary getDistributionSummary(
            String metricName,
            String metricDescribe,
            List<Tag> tags,
            String baseUnit) {
        return DistributionSummary.builder(validateName(metricName))
                .description(metricDescribe)
                .tags(validateTags(tags))
                .publishPercentiles(PERCENTILES)
                .baseUnit(baseUnit)
                .register(meterRegistry);
    }

    /**
     * 검색 품질 메트릭 수집
     */
    public <T> void recordSearchPerformanceMetrics(SearchResponse<T> response, String searchTerm) {
        // 검색 소요 시간 측정
        getTimer(
                "search.execution.time",
                "Time taken for search execution",
                List.of(Tag.of("term_length", String.valueOf(searchTerm.length())))
        ).record(Duration.ofMillis(response.took()));

        // 검색 결과 수 분포
        getDistributionSummary(
                "search.results.count",
                "Distribution of search results count",
                Collections.emptyList(),
                "results"
        ).record(response.hits().total().value());

        // 매칭 스코어 품질 측정
        if (response.hits().maxScore() != null) {
            recordSearchScoreMetrics(response.hits().hits());
        }
    }

    /**
     * 검색어 관련 메트릭 수집
     * <li> 검색어 길이 분포 측정 </li>
     * <li> 검색어 패턴 분석 및 기록 </li>
     */
    public void recordSearchTermMetrics(String searchTerm) {
        // 검색어 길이 분포
        getDistributionSummary(
                "search.term.length",
                "Distribution of search term lengths",
                Collections.emptyList(),
                "characters"
        ).record(searchTerm.length());

        // 검색어 패턴 분석 및 기록
        String pattern = searchTerm.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣].*") ? "korean" :
                         searchTerm.matches(".*[a-zA-Z].*") ? "english" :
                         searchTerm.matches(".*\\d.*") ? "numeric" :
                         searchTerm.contains(" ") ? "complex" : "mixed";

        getCounter(
                "search.term.pattern",
                "Search term pattern types",
                List.of(Tag.of("pattern", pattern))
        ).increment();
    }

    // ******************************************************* //
    // ************** Private Method ************** //
    // ******************************************************* //
    /**
     * 검색 결과 스코어 메트릭
     * 검색 결과의 관련성 점수를 분석하고 기록
     */
    private <T> void recordSearchScoreMetrics(List<Hit<T>> hits) {
        // 스코어 분포 측정
        DistributionSummary scoreDistribution = getDistributionSummary(
                "search.score.distribution",
                "Distribution of search result scores",
                Collections.emptyList(),
                "score"
        );

        // 품질 등급 카운터
        Counter excellentMatches = getCounter(
                "search.quality.excellent",
                "Matches with score >= 2.0",
                Collections.emptyList()
        );
        Counter goodMatches = getCounter(
                "search.quality.good",
                "Matches with score >= 1.0",
                Collections.emptyList()
        );
        Counter fairMatches = getCounter(
                "search.quality.fair",
                "Matches with score >= 0.5",
                Collections.emptyList()
        );

        // 각 결과의 스코어 분석
        for (Hit<T> hit : hits) {
            if (hit.score() != null) {  // null safe 체크
                double score = hit.score();
                scoreDistribution.record(score);

                // 품질 등급 카운팅
                if (score >= SCORE_THRESHOLD_EXCELLENT) {
                    excellentMatches.increment();
                } else if (score >= SCORE_THRESHOLD_GOOD) {
                    goodMatches.increment();
                } else if (score >= SCORE_THRESHOLD_FAIR) {
                    fairMatches.increment();
                }
            }
        }
    }

    // 메트릭 이름 유효성 검사
    private String validateName(String metricName) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty");
        }
        if (!Pattern.matches(NAME_PATTERN, metricName)) {
            throw new IllegalArgumentException(
                    "Metric name must start with a letter and contain only lowercase letters, numbers, and underscores"
            );
        }
        return metricName;
    }

    // 메트릭 태그 유효성 검사
    private List<Tag> validateTags(List<Tag> tags) {
        if (tags == null) {
            return Collections.emptyList();
        }

        return tags.stream()
                .map(tag -> {
                    String key = tag.getKey();
                    String value = tag.getValue();

                    if (key.isEmpty()) {
                        throw new IllegalArgumentException("Tag key cannot be null or empty");
                    }
                    if (value.isEmpty()) {
                        throw new IllegalArgumentException("Tag value cannot be null or empty");
                    }
                    if (key.length() > MAX_TAG_LENGTH) {
                        throw new IllegalArgumentException(
                                "Tag key and value must not exceed " + MAX_TAG_LENGTH + " characters"
                        );
                    }
                    return tag;
                })
                .collect(Collectors.toList());
    }

}
