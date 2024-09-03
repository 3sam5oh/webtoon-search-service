import http from 'k6/http';
import { sleep, check } from 'k6';
import { URL } from 'https://jslib.k6.io/url/1.0.0/index.js';

// export let options = {
//   stages: [
//     { duration: '10s', target: 1000 }, // 30초 동안 20명의 가상 사용자를 증가
//     { duration: '10s', target: 1000 }, // 1분 동안 20명의 가상 사용자 유지
//     { duration: '10s', target: 0 }, // 10초 동안 가상 사용자 수를 0으로 줄임
//   ],
// };

// export const options = {
//   scenarios: {
//     contacts: {
//       executor: 'ramping-vus',
//       startVUs: 0,
//       stages: [
//         { duration: '10s', target: 100 },
//         { duration: '10s', target: 200 },
//         { duration: '10s', target: 300 },
//         { duration: '10s', target: 400 },
//         { duration: '10s', target: 500 },
//         { duration: '10m', target: 500 },
//         { duration: '10s', target: 0 },
//       ],
//       gracefulRampDown: '0s',
//     },
//   },
//   thresholds: {
//     http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: true }], // http errors should be less than 1%, otherwise abort the test
//     http_req_duration: ['p(99)<1000'], // 99% of requests should be below 1s
//   },
// }

export let options = {
  thresholds: {
    http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: true }], // http errors should be less than 1%, otherwise abort the test
    http_req_duration: ['p(99)<1000'], // 99% of requests should be below 1s
  },
  scenarios: {
    smoke_test: { // smoke test 시나리오 정의
      executor: 'shared-iterations', // 모든 VU가 설정된 횟수만큼 실행
      vus: 1, // 가상 사용자 1명
      iterations: 1,  // 1번 실행
      tags: { test_tag: 'smoke' }, // 태그를 통해 테스트 유형 지정.. 테스트 후 grafana에서 해당 태그로 필터링 가능
      // exec: 'smoke', // smoke 시나리오를 실행
    },
    load_test: {
      executor: 'constant-arrival-rate',
      duration: '3m', // 3분 동안
      timeUnit: '1s', // 1초당
      rate: 100, // 100개의 요청
      preAllocatedVUs: 50, // 사전 할당 가상 사용자
      maxVUs: 100, // 최대 사용자 100
      tags: { test_tag: 'load' },
      startTime: '1m', // 1분에 시작
      // exec: 'load', // load 시나리오를 실행
    },
    stress_test: {
      executor: 'constant-arrival-rate',
      duration: '3m', // 3분 동안
      timeUnit: '1s', // 1초당
      rate: 1500, // 1500개의 요청
      preAllocatedVUs: 100,
      maxVUs: 700,
      tags: { test_tag: 'stress' },
      startTime: '5m', // 5분에 시작
      // exec: 'stress', // stress 시나리오를 실행
    },
  },
};

export default function () {
    const url = new URL('http://spring-svc.spring-app.svc.cluster.local:8080/webtoons/search');
    url.searchParams.append('query', '화산');

    const res = http.get(url.toString());
    check(res, {
        'http response status code is 200': res.status === 200,
      });

    // sleep(1);
}
