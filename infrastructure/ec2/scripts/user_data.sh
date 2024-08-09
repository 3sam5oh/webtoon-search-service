#!/bin/bash
#cloud-config

# 로그 파일 설정
LOG_FILE="/var/log/user_data.log"

# 로깅 함수
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a $LOG_FILE
}

# 오류 처리 함수
handle_error() {
    log "라인 $1에서 오류가 발생했습니다."
    exit 1
}

# 오류 발생 시 handle_error 함수 호출
trap 'handle_error $LINENO' ERR

log "사용자 데이터 스크립트 실행을 시작합니다."

# 네트워크 연결 대기
until ping -c1 8.8.8.8 &>/dev/null; do
    log "네트워크 연결을 기다리는 중..."
    sleep 1
done
log "네트워크 연결이 확인되었습니다."

# 재시도 로직
retry_command() {
    local retries=5
    local count=0
    until "$@"; do
        exit=$?
        wait=$((2 ** count))
        count=$((count + 1))
        if [ $count -lt $retries ]; then
            log "Retry $count/$retries exited $exit, retrying in $wait seconds..."
            sleep $wait
        else
            log "Retry $count/$retries exited $exit, no more retries left."
            return $exit
        fi
    done
    return 0
}

log "시스템을 업데이트합니다."
retry_command sudo apt-get update -y

log "필수 패키지를 설치합니다."
retry_command sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common

log "Docker GPG 키를 추가합니다."
retry_command curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

log "Docker 저장소를 추가합니다."
retry_command sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

log "시스템을 다시 업데이트합니다."
retry_command sudo apt-get update -y

log "Docker를 설치합니다."
retry_command sudo apt-get install -y docker-ce
retry_command sudo systemctl start docker
retry_command sudo systemctl enable docker

log "ubuntu 사용자를 docker 그룹에 추가합니다."
retry_command sudo usermod -aG docker ubuntu

log "애플리케이션 Docker 이미지를 가져오고 실행합니다."
if retry_command sudo docker pull ${docker_image}; then
  retry_command sudo docker run -d -p 80:80 ${docker_image}
else
  log "Docker 이미지를 가져오는 데 실패했습니다."
  exit 1
fi

log "사용자 데이터 스크립트 실행이 성공적으로 완료되었습니다."
