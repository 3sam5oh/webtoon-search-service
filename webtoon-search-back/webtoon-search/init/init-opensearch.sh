#!/bin/bash

set -e

# DEBUG 모드 설정
if [ "$DEBUG" = "true" ]; then
    set -x  # 디버그 모드 활성화
fi

INDEX_NAME="webtoon-kr"
OPENSEARCH_URL="http://localhost:9200"
OPENSEARCH_HOME="/usr/share/opensearch"

# OpenSearch가 준비될 때까지 대기
until curl -s -f $OPENSEARCH_URL > /dev/null; do
    echo "OpenSearch 아직 준비되지 않음. 10초 후 재시도..."
    sleep 10
done

echo "OpenSearch 준비 완료. 초기화 스크립트 실행"

# 인덱스 존재 여부 확인
INDEX_EXISTS=$(curl -s -o /dev/null -w "%{http_code}" "$OPENSEARCH_URL/$INDEX_NAME")

if [ "$INDEX_EXISTS" = "404" ]; then
    echo "인덱스 생성 및 매핑 설정 중..."
    MAPPING_RESPONSE=$(curl -X PUT "$OPENSEARCH_URL/$INDEX_NAME" -H "Content-Type: application/json" -d @${OPENSEARCH_HOME}/init/webtoon-mapping.json)
    echo "매핑 설정 응답: $MAPPING_RESPONSE"

    if [[ "$MAPPING_RESPONSE" == *'"acknowledged":false'* ]]; then
        echo "인덱스 생성 실패: $MAPPING_RESPONSE"
        exit 1
    fi

    # 데이터 인덱싱 실행
    ${OPENSEARCH_HOME}/init/setup-opensearch.sh
else
    echo "인덱스가 이미 존재합니다. 초기화 작업을 건너뜁니다."
fi

echo "Initialization completed"

# DEBUG 모드 비활성화
if [ "$DEBUG" = "true" ]; then
    set +x
fi
