# CI/CD 설정 가이드

이 문서는 GitHub Actions를 통한 Production 배포를 위해 준비해야 할 항목들을 설명합니다.

## 1. GitHub Secrets 설정

GitHub Repository > Settings > Secrets and variables > Actions에서 아래 secrets를 등록해야 합니다.

### SSH 연결 정보
| Secret Name | 설명 | 예시 |
|-------------|------|------|
| `SSH_HOST` | 배포 서버 IP 또는 도메인 | `123.456.789.0` |
| `SSH_PORT` | SSH 포트 | `22` |
| `SSH_USERNAME` | SSH 접속 사용자명 | `od` |
| `SSH_PRIVATE_KEY` | SSH 개인키 (전체 내용) | `-----BEGIN OPENSSH PRIVATE KEY-----...` |

### 데이터베이스 (PostgreSQL)
| Secret Name | 설명 | 예시 |
|-------------|------|------|
| `PROD_RDB_HOST` | DB 호스트 주소 | `localhost:5432/dbname` |
| `PROD_RDB_USERNAME` | DB 사용자명 | `postgres` |
| `PROD_RDB_PASSWORD` | DB 비밀번호 | `your-password` |

### Redis
| Secret Name | 설명 | 예시 |
|-------------|------|------|
| `PROD_REDIS_HOST` | Redis 호스트 | `localhost` |
| `PROD_REDIS_PORT` | Redis 포트 | `6379` |
| `PROD_REDIS_PASSWORD` | Redis 비밀번호 | `your-redis-password` |

### 메일 서버 (SMTP)
| Secret Name | 설명 | 예시 |
|-------------|------|------|
| `MAIL_HOST` | SMTP 서버 주소 | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP 포트 | `587` |
| `MAIL_USERNAME` | 메일 계정 | `your-email@gmail.com` |
| `MAIL_PASSWORD` | 메일 앱 비밀번호 | `your-app-password` |

### 인증 관련
| Secret Name | 설명 | 예시 |
|-------------|------|------|
| `PROD_JWT_SECRET` | JWT 서명 시크릿 키 | `your-jwt-secret-key` |
| `PROD_AES_SECRET` | AES 암호화 키 | `your-aes-secret-key` |
| `GOOGLE_CLIENT_ID` | Google OAuth 클라이언트 ID | `xxx.apps.googleusercontent.com` |
| `GOOGLE_CLIENT_SECRET` | Google OAuth 클라이언트 시크릿 | `GOCSPX-xxx` |

### Firebase
| Secret Name | 설명 |
|-------------|------|
| `FIREBASE_SERVICE_ACCOUNT` | Firebase 서비스 계정 JSON (전체 내용) |

Firebase 서비스 계정 JSON은 Firebase Console > 프로젝트 설정 > 서비스 계정 > 새 비공개 키 생성에서 다운로드할 수 있습니다.

---

## 2. 배포 서버 준비사항

### 디렉토리 구조
```bash
# 서비스 디렉토리 생성
mkdir -p /home/od/moimism
```

### OpenTelemetry Java Agent 다운로드 (SigNoz 연동)
```bash
# 서비스 디렉토리에 OpenTelemetry Java Agent 다운로드
cd /home/od/moimism
wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
```

### deploy.sh 스크립트
`/home/od/moimism/deploy.sh` 파일을 생성하고 배포 로직을 작성해야 합니다.

```bash
#!/bin/bash
# 예시 deploy.sh (SigNoz/OpenTelemetry 연동 포함)

APP_NAME=od-cloud
JAR_FILE=od-cloud.jar
OTEL_AGENT=opentelemetry-javaagent.jar
SERVICE_NAME=od-cloud
OTEL_ENDPOINT=http://127.0.0.1:4318

# 기존 프로세스 종료
PID=$(pgrep -f $JAR_FILE)
if [ -n "$PID" ]; then
    echo "Stopping existing application (PID: $PID)"
    kill -15 $PID
    sleep 5
fi

# 새 애플리케이션 시작 (OpenTelemetry Agent 포함)
echo "Starting new application with OpenTelemetry..."
nohup java \
    -javaagent:$OTEL_AGENT \
    -Dotel.service.name=$SERVICE_NAME \
    -Dotel.exporter.otlp.endpoint=$OTEL_ENDPOINT \
    -Dotel.exporter.otlp.protocol=http/protobuf \
    -Dotel.metrics.exporter=otlp \
    -Dotel.traces.exporter=otlp \
    -Dotel.logs.exporter=otlp \
    -Dotel.instrumentation.logging.experimental.enabled=true \
    -jar $JAR_FILE \
    --spring.profiles.active=prod \
    > app.log 2>&1 &

echo "Application started with PID: $!"
```

### JDK 21 설치
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# 버전 확인
java -version
```

### 필수 서비스 설치 및 실행
- **PostgreSQL**: 데이터베이스 서버
- **Redis**: 캐시/세션 서버
- **SigNoz**: 모니터링/APM (127.0.0.1:4318에서 OTLP 수신 대기)

---

## 3. 워크플로우 트리거

배포는 다음 조건에서 자동 실행됩니다:
- `master` 브랜치에 push할 때
- GitHub Actions에서 수동 실행 (workflow_dispatch)

---

## 4. 체크리스트

배포 전 확인사항:

- [ ] 모든 GitHub Secrets 등록 완료
- [ ] 배포 서버 SSH 접속 테스트 완료
- [ ] `/home/od/moimism` 디렉토리 생성
- [ ] `deploy.sh` 스크립트 작성 및 실행 권한 부여
- [ ] JDK 21 설치 확인
- [ ] PostgreSQL 설치 및 데이터베이스 생성
- [ ] Redis 설치 및 실행
- [ ] 방화벽에서 필요한 포트 오픈 (애플리케이션 포트, SSH 등)
- [ ] Firebase 프로젝트 설정 및 서비스 계정 JSON 생성
- [ ] Google OAuth 클라이언트 설정 (Google Cloud Console)
- [ ] OpenTelemetry Java Agent 다운로드 (`/home/od/moimism/opentelemetry-javaagent.jar`)
- [ ] SigNoz 설치 및 OTLP 수신 포트(4318) 확인