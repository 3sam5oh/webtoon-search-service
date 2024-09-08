#!/bin/bash

chmod +x "$0"

# gradlew가 있는 디렉토리로 PROJECT_ROOT 설정
PROJECT_ROOT="../.."
TEST_IMAGE_NAME="webtoon-search-build-test"

# 빌드 컨텍스트 및 파일 확인을 위한 출력
echo "Building Docker with context: $PROJECT_ROOT"
echo "Checking for gradlew file at $PROJECT_ROOT/gradlew..."

if [ ! -f "$PROJECT_ROOT/gradlew" ]; then
    echo "Error: gradlew file not found at $PROJECT_ROOT/gradlew"
    exit 1
fi

build_and_measure() {
    local dockerfile=$1
    local tag=$2

    echo "Building $tag..."
    start_time=$(date +%s)
    docker build --no-cache -t ${TEST_IMAGE_NAME}:$tag -f $dockerfile $PROJECT_ROOT
    build_status=$?
    end_time=$(date +%s)
    build_time=$((end_time - start_time))

    if [ $build_status -eq 0 ]; then
        image_size=$(docker images ${TEST_IMAGE_NAME}:$tag --format "{{.Size}}")
        layers=$(docker history ${TEST_IMAGE_NAME}:$tag | wc -l)
    else
        image_size="N/A"
        layers="N/A"
    fi

    echo "Build time for $tag: $build_time seconds"
    echo "Image size for $tag: $image_size"
    echo "Number of layers for $tag: $layers"
    echo "-----------------------------------"

    eval "${tag}_build_time='$build_time'"
    eval "${tag}_image_size='$image_size'"
    eval "${tag}_layers='$layers'"
}

measure_runtime_performance() {
    local tag=$1
    local container_name="${TEST_IMAGE_NAME}-${tag}"

    echo "Measuring runtime performance for $tag..."

    # 컨테이너 시작 시간 측정
    start_time=$(date +%s%N)
    docker run -d --name $container_name -p 8080:8080 ${TEST_IMAGE_NAME}:$tag
    if [ $? -ne 0 ]; then
        echo "Failed to start container"
        return 1
    fi
    container_id=$(docker ps -q -f name=$container_name)

    # 컨테이너 로그 확인 (초기 로그)
    echo "Initial container logs:"
    docker logs $container_id

    # 애플리케이션 시작 완료 대기 (타임아웃 추가)
    echo "Waiting for application to start..."
    timeout=120
    while [ $timeout -gt 0 ]; do
        if docker logs $container_id 2>&1 | grep -q "Root WebApplicationContext: initialization completed"; then
            echo "Application started successfully"
            break
        fi
        sleep 1
        ((timeout--))
    done

    if [ $timeout -eq 0 ]; then
        echo "Timeout waiting for application to start. Full logs:"
        docker logs $container_id
        docker stop $container_name
        docker rm $container_name
        return 1
    fi

    end_time=$(date +%s%N)
    start_time_ms=$(( (end_time - start_time) / 1000000 ))
    echo "Container and application start time: ${start_time_ms}ms"

    # 메모리 사용량 측정
    echo "Measuring memory usage..."
    sleep 5  # 애플리케이션이 안정화될 때까지 대기
    memory_usage=$(docker stats --no-stream --format "{{.MemUsage}}" $container_name | awk '{print $1}')
    echo "Memory usage: $memory_usage"

    # 첫 번째 요청 응답 시간 측정
    echo "Measuring first request response time..."
    first_request_time=$(curl -o /dev/null -s -w "%{time_total}\n" http://localhost:8080/actuator/health)
    if [ $? -ne 0 ]; then
        echo "Failed to make request to /actuator/health"
        first_request_time="N/A"
    else
        echo "First request response time: ${first_request_time}s"
    fi

    # 컨테이너 정리
    echo "Cleaning up container..."
    docker stop $container_name
    docker rm $container_name

    # 결과 저장
    eval "${tag}_start_time='$start_time_ms'"
    eval "${tag}_memory_usage='$memory_usage'"
    eval "${tag}_first_request_time='$first_request_time'"

    echo "Performance measurement completed for $tag"
}

# 원본 버전 빌드 및 측정
build_and_measure Dockerfile-spring-original original
measure_runtime_performance original

# 최적화 버전 빌드 및 측정
build_and_measure Dockerfile-spring-optimized optimized
measure_runtime_performance optimized

# 캐시 효율성 테스트
echo "Testing cache efficiency..."
build_and_measure Dockerfile-spring-optimized optimized-cache

# 결과를 파일로 저장
echo "Saving results to build_results.txt"
{
    echo "Build Test Results"
    echo "=================="
    echo "Original Version:"
    echo "Build time: ${original_build_time} seconds"
    echo "Image size: ${original_image_size}"
    echo "Number of layers: ${original_layers}"
    echo
    echo "Optimized Version:"
    echo "Build time: ${optimized_build_time} seconds"
    echo "Image size: ${optimized_image_size}"
    echo "Number of layers: ${optimized_layers}"
    echo
    echo "Cache Efficiency Test:"
    echo "Build time: ${optimized_cache_build_time} seconds"
    echo "Image size: ${optimized_cache_image_size}"
    echo "Number of layers: ${optimized_cache_layers}"
    echo
    echo "Improvements:"
    if [ "$original_image_size" != "N/A" ] && [ "$optimized_image_size" != "N/A" ]; then
        original_size=$(echo ${original_image_size} | cut -d'M' -f1)
        optimized_size=$(echo ${optimized_image_size} | cut -d'M' -f1)
        size_reduction=$(( (original_size - optimized_size) * 100 / original_size ))
        echo "Image size reduction: ${size_reduction}%"
    else
        echo "Image size reduction: N/A"
    fi
    if [ "$original_layers" != "N/A" ] && [ "$optimized_layers" != "N/A" ]; then
        layer_reduction=$(( (original_layers - optimized_layers) * 100 / original_layers ))
        echo "Layer reduction: ${layer_reduction}%"
    else
        echo "Layer reduction: N/A"
    fi

    echo
    echo "Runtime Performance:"
    echo "Original Version:"
    echo "Container and application start time: ${original_start_time}ms"
    echo "Memory usage: ${original_memory_usage}"
    echo "First request response time: ${original_first_request_time}s"
    echo
    echo "Optimized Version:"
    echo "Container and application start time: ${optimized_start_time}ms"
    echo "Memory usage: ${optimized_memory_usage}"
    echo "First request response time: ${optimized_first_request_time}s"

    # 성능 개선 계산
    start_time_improvement=$(( (original_start_time - optimized_start_time) * 100 / original_start_time ))
    echo "Start time improvement: ${start_time_improvement}%"

    original_memory=$(echo ${original_memory_usage} | sed 's/MiB//')
    optimized_memory=$(echo ${optimized_memory_usage} | sed 's/MiB//')
    memory_improvement=$(( (original_memory - optimized_memory) * 100 / original_memory ))
    echo "Memory usage improvement: ${memory_improvement}%"

    request_time_improvement=$(awk "BEGIN {print (${original_first_request_time} - ${optimized_first_request_time}) / ${original_first_request_time} * 100}")
    echo "First request time improvement: ${request_time_improvement}%"

} > build_results.txt

echo "Test completed. Results saved in build_results.txt"

# 테스트 완료 후 이미지 정리
echo "Cleaning up test images..."
docker rmi ${TEST_IMAGE_NAME}:original ${TEST_IMAGE_NAME}:optimized ${TEST_IMAGE_NAME}:optimized-cache
