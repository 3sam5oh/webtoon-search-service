#!/bin/bash

# 프로젝트 루트 디렉토리 설정
PROJECT_ROOT="../.."
TEST_IMAGE_NAME="webtoon-search-build-test-with-cache"

build_and_measure() {
    local cache_option=$1
    local tag=$2

    echo "Building $tag with cache option: $cache_option"
    start_time=$(date +%s)
    docker build $cache_option -t ${TEST_IMAGE_NAME}:$tag -f Dockerfile-spring-optimized $PROJECT_ROOT
    end_time=$(date +%s)
    build_time=$((end_time - start_time))

    echo "Build time for $tag: $build_time seconds"
    echo "-----------------------------------"

    eval "${tag}_build_time='$build_time'"
}

# 캐시 없이 빌드
build_and_measure "--no-cache" "no_cache"

# 캐시를 사용하여 빌드 (첫 번째 빌드 후 변경 없이)
build_and_measure "" "with_cache"

# 소스 코드 일부 변경 후 캐시를 사용하여 빌드
echo "// 테스트를 위한 주석 추가" >> $PROJECT_ROOT/src/main/java/com/samsamohoh/webtoonsearch/WebtoonSearchApplication.java
build_and_measure "" "with_cache_changed"

# 결과 출력
echo "Cache Efficiency Test Results"
echo "============================="
echo "Build time without cache: ${no_cache_build_time} seconds"
echo "Build time with cache (no changes): ${with_cache_build_time} seconds"
echo "Build time with cache (after changes): ${with_cache_changed_build_time} seconds"

# 개선율 계산
cache_improvement=$(( (no_cache_build_time - with_cache_build_time) * 100 / no_cache_build_time ))
change_impact=$(( (with_cache_changed_build_time - with_cache_build_time) * 100 / with_cache_build_time ))

echo
echo "Cache efficiency: ${cache_improvement}% faster build with cache"
echo "Change impact: ${change_impact}% increase in build time after changes"

# 결과를 파일로 저장
echo "Saving results to cache_test_results.txt"
{
    echo "Cache Efficiency Test Results"
    echo "============================="
    echo "Build time without cache: ${no_cache_build_time} seconds"
    echo "Build time with cache (no changes): ${with_cache_build_time} seconds"
    echo "Build time with cache (after changes): ${with_cache_changed_build_time} seconds"
    echo
    echo "Cache efficiency: ${cache_improvement}% faster build with cache"
    echo "Change impact: ${change_impact}% increase in build time after changes"
} > cache_test_results.txt

echo "Test completed. Results saved in cache_test_results.txt"
