# OpenSearch 2.15.0 버전을 기본 이미지로 사용
FROM opensearchproject/opensearch:2.15.0

# 환경 변수 설정
ENV OPENSEARCH_HOME=/usr/share/opensearch \
    PLUGIN_NAME=analysis-nori \
    DEBUG=false

# root 사용자로 전환 (플러그인 설치 및 파일 권한 변경을 위해)
USER root

# 필요한 도구 설치 및 Nori 한국어 형태소 분석기 플러그인 설치
RUN yum update -y && \
    yum install -y jq file && \
    yum clean all && \
    rm -rf /var/cache/yum && \
    ${OPENSEARCH_HOME}/bin/opensearch-plugin install --batch ${PLUGIN_NAME}

# DEBUG 모드 설정
RUN if [ "$DEBUG" = "true" ]; then \
        echo "logger.level: DEBUG" >> ${OPENSEARCH_HOME}/config/opensearch.yml; \
    fi

# 초기화 스크립트 복사 및 권한 설정
COPY init/ ${OPENSEARCH_HOME}/init/
RUN chmod +x ${OPENSEARCH_HOME}/init/*.sh && \
    chown -R opensearch:opensearch ${OPENSEARCH_HOME}

# OpenSearch 사용자로 다시 전환
USER opensearch

# 작업 디렉토리 설정
WORKDIR ${OPENSEARCH_HOME}

# 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:9200/_cluster/health || exit 1
