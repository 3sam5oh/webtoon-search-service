#!/bin/bash

set -e

# DEBUG 모드 설정
if [ "$DEBUG" = "true" ]; then
    set -x  # 디버그 모드 활성화 (Dockerfile에 false로 비활성화 상태)
fi

API_URL="https://korea-webtoon-api-cc7dda2f0d77.herokuapp.com/webtoons"
OPENSEARCH_URL="http://localhost:9200"
INDEX_NAME="webtoon-kr"
PAGE_SIZE=100
TEMP_FILE="/tmp/webtoon_data.json"

echo "웹툰 데이터 가져오기 및 인덱싱 프로세스 시작..."

# API에서 데이터 가져오기 및 인덱싱
echo "API에서 데이터 가져오기 시작..."
PAGE=1
: > $TEMP_FILE
TOTAL_DOCUMENTS=0

while true; do
    echo "페이지 ${PAGE} 가져오는 중..."
    RESPONSE=$(curl -s "${API_URL}?page=${PAGE}&perPage=${PAGE_SIZE}")

    DATA=$(echo $RESPONSE | jq '.webtoons')
    IS_LAST=$(echo $RESPONSE | jq '.isLastPage')

    DOCS_COUNT=$(echo $DATA | jq '. | length')
    TOTAL_DOCUMENTS=$((TOTAL_DOCUMENTS + DOCS_COUNT))
    echo "현재 페이지 문서 수: $DOCS_COUNT, 총 문서 수: $TOTAL_DOCUMENTS"

    echo $DATA | jq -c '.[]' >> $TEMP_FILE

    if [ "$(echo $IS_LAST)" = "true" ]; then
        echo "마지막 페이지 도달. 데이터 가져오기 완료."
        break
    fi

    PAGE=$((PAGE + 1))
done

echo "모든 데이터 가져오기 완료. 인덱싱 시작..."

TOTAL_LINES=$(wc -l < $TEMP_FILE)
CURRENT_LINE=0

while read -r LINE; do
    CURRENT_LINE=$((CURRENT_LINE + 1))
    ID=$(echo $LINE | jq -r '.id')

    INDEX_RESPONSE=$(curl -X POST "$OPENSEARCH_URL/$INDEX_NAME/_doc/$ID" \
         -H "Content-Type: application/json" \
         -d "$LINE")

    if [ $((CURRENT_LINE % 100)) -eq 0 ]; then
        echo "진행 상황: $CURRENT_LINE / $TOTAL_LINES ($(( (CURRENT_LINE * 100) / TOTAL_LINES ))%)"
    fi
done < $TEMP_FILE

rm $TEMP_FILE

echo "인덱싱 완료. 최종 인덱스 상태 확인 중..."
INDEX_STATUS=$(curl -s "$OPENSEARCH_URL/_cat/indices/$INDEX_NAME?v")
echo "최종 인덱스 상태:"
echo "$INDEX_STATUS"

echo "설정 완료."

# DEBUG 모드 비활성화
if [ "$DEBUG" = "true" ]; then
    set +x
fi
