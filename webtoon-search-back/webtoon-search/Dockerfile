FROM opensearchproject/opensearch:2.15.0

# root 사용자로 전환
USER root

# 플러그인 설치
RUN /usr/share/opensearch/bin/opensearch-plugin install analysis-nori

# 권한 설정
RUN chown -R opensearch:opensearch /usr/share/opensearch/plugins/analysis-nori

# 원래 사용자로 돌아가기
USER opensearch
