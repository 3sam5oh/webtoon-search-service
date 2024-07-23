# OpenSearch 2.15.0 버전을 기본 이미지로 사용
FROM opensearchproject/opensearch:2.15.0

# 환경 변수 설정
ENV OPENSEARCH_HOME=/usr/share/opensearch \
    PLUGIN_NAME=analysis-nori \
    DEBUG=true

# root 사용자로 전환 (플러그인 설치 및 파일 권한 변경을 위해)
USER root

# 필요한 도구 설치 및 Nori 한국어 형태소 분석기 플러그인 설치
RUN yum update -y && \
    yum install -y jq && \
    yum clean all && \
    rm -rf /var/cache/yum && \
    ${OPENSEARCH_HOME}/bin/opensearch-plugin install --batch ${PLUGIN_NAME} && \
    chown -R opensearch:opensearch ${OPENSEARCH_HOME}

# OpenSearch 사용자로 다시 전환
USER opensearch

# 작업 디렉토리 설정
WORKDIR ${OPENSEARCH_HOME}

COPY --chown=opensearch:opensearch init/ ${OPENSEARCH_HOME}/init/
RUN chmod +x ${OPENSEARCH_HOME}/init/*.sh

# 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:9200/_cluster/health || exit 1