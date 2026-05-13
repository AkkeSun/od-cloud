# Google Drive Refresh Token 발급 가이드

Google Drive 업로드 기능에 필요한 Refresh Token이 만료되거나 재발급이 필요할 때 사용합니다.

## 사전 준비

### 1. Google Cloud Console 설정
- [Google Cloud Console](https://console.cloud.google.com) 접속
- APIs & Services → Credentials → OAuth 2.0 Client IDs
- 데스크톱 앱 타입 Client ID 생성 (없으면 `+ CREATE CREDENTIALS → OAuth client ID → 데스크톱 앱`)
- 생성된 Client ID, Client Secret 복사

### 2. Python 라이브러리 설치
```bash
pip3 install google-auth-oauthlib
```

---

## Refresh Token 발급

```bash
python3 get_drive_token.py
```

1. Client ID 입력
2. Client Secret 입력
3. 브라우저에서 구글 계정 로그인 및 Drive 권한 허용
4. 터미널에 출력된 `GOOGLE_DRIVE_USER_REFRESH_TOKEN=...` 값 복사

---

## 발급 후 적용

### 로컬
`src/main/resources/.env` 파일에서 기존 값 교체:
```
GOOGLE_DRIVE_USER_REFRESH_TOKEN=새로발급받은토큰값
```

### 운영 (GitHub Secrets)
GitHub → Settings → Secrets and variables → Actions → `GOOGLE_DRIVE_USER_REFRESH_TOKEN` 업데이트

---

## Refresh Token 만료 조건

아래 경우에만 무효화되므로 평소에는 재발급 불필요합니다.

| 조건 | 설명 |
|------|------|
| 앱 권한 수동 취소 | Google 계정 → 보안 → 앱 접근 권한에서 직접 취소한 경우 |
| 비밀번호 변경 | Google 계정 비밀번호 변경 시 |
| 6개월 미사용 | Drive 업로드가 6개월 이상 호출되지 않은 경우 |