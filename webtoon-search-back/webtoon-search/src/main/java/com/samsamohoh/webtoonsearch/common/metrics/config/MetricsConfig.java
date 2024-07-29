package com.samsamohoh.webtoonsearch.common.metrics.config;

import com.samsamohoh.webtoonsearch.common.metrics.CustomMetrics;
import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
   메트릭 수집에 필요한 어노테이션 @Counted, @Timed 를 사용하기 위한 설정 클래스.
   SearchEngineConfig와 달리 어댑터에서만 사용되는 config가 아니라 전 클래스에 걸쳐 메트릭 수집을 위한 config 이므로 common 아래의
   별도의 패키지로 관리함.

   mircrometer에서 사용자가 정의할 수 있는 메트릭에는 세 종류가 있음.
   1. Counter : 절대로 감소 하지 않는 수(ex: 메소드 요청 요청 수)등을 기록
   2. Gauge   : 감소할 수 있는 수 기록. 주로 현재의 수치를 기록 하는데 사용.(ex: CPU, DISK 사용률)
   3. Timer   : 메소드 / 클래스 등의 호출 시간을 메트릭화 시킬 때 사용할 수 있다. (ex: 웹툰 조회 메소드의 응답 시간)

   Gauge는 별도로 관리할 수치가 없고 actuator 의 기본 메트릭으로 처리할 수 있을 것 같아서
   @Counted 와 @Timed 만으로 메트릭 정의
 */
@Configuration
public class MetricsConfig {

    @Bean
    public CountedAspect countedAspect(MeterRegistry meterRegistry) {
        return new CountedAspect(meterRegistry);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry);
    }

    @Bean
    public CustomMetrics customMetrics(MeterRegistry meterRegistry) {
        return new CustomMetrics(meterRegistry);
    }
}
