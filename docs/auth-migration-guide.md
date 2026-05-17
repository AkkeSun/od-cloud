# 인증 API 변경 사항 (클라이언트 가이드)

## 변경 요약

토큰(accessToken, refreshToken)을 응답 바디 대신 **httpOnly 쿠키**로 관리하도록 변경되었습니다.  
클라이언트는 토큰을 직접 저장하거나 헤더에 담을 필요 없이, 모든 요청에 `credentials: 'include'` 만 추가하면 됩니다.

---

## 1. 로그인 (토큰 발급)

**POST /auth**

### 기존
```json
// Response Body
{
  "data": {
    "accessToken": "Bearer eyJhbGc...",
    "refreshToken": "Bearer eyJhbGc..."
  }
}
```
클라이언트가 토큰을 받아 `localStorage` 등에 직접 저장해야 했음.

### 변경 후
```json
// Response Body
{
  "data": {
    "result": true
  }
}
```
```
// Response Header (브라우저가 자동 저장)
Set-Cookie: accessToken=Bearer eyJhbGc...; HttpOnly; Path=/; SameSite=Lax
Set-Cookie: refreshToken=Bearer eyJhbGc...; HttpOnly; Path=/; SameSite=Lax
```
토큰은 httpOnly 쿠키로 브라우저가 자동 저장. JS에서 접근 불가.

### 클라이언트 코드
```js
// 변경 전
const res = await fetch('/auth', { method: 'POST', ... });
const { accessToken, refreshToken } = res.data;
localStorage.setItem('accessToken', accessToken);

// 변경 후
const res = await fetch('/auth', {
  method: 'POST',
  credentials: 'include',  // 추가
  headers: {
    'Content-Type': 'application/json',
    'googleAuthorization': googleToken,
  },
  body: JSON.stringify({ deviceId: 'web-browser' }),
});
// 토큰 저장 코드 불필요 — 브라우저가 자동 처리
```

---

## 2. 토큰 재발급

**PUT /auth**

### 기존
```
// Request Header
refreshToken: Bearer eyJhbGc...
```

### 변경 후
별도 헤더 불필요. 쿠키가 자동으로 전송됨.

### 클라이언트 코드
```js
// 변경 전
await fetch('/auth', {
  method: 'PUT',
  headers: { refreshToken: localStorage.getItem('refreshToken') },
});

// 변경 후
await fetch('/auth', {
  method: 'PUT',
  credentials: 'include',  // 쿠키 자동 전송
});
```

---

## 3. 인증이 필요한 모든 API

### 기존
```js
fetch('/some-api', {
  headers: { Authorization: localStorage.getItem('accessToken') },
});
```

### 변경 후
```js
fetch('/some-api', {
  credentials: 'include',  // 쿠키 자동 전송
});
```
`Authorization` 헤더 제거, `credentials: 'include'` 추가.

---

## 4. 신규 API — 내 계정 정보 조회

**GET /accounts/self** (인증 필요)

기존에 accessToken을 디코딩해서 클라이언트에서 직접 꺼내 쓰던 사용자 정보를 이 API로 조회합니다.

### Response
```json
{
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "홍길동",
    "picture": "https://...",
    "groups": [
      { "id": 1, "name": "그룹명" }
    ],
    "vouchers": ["VOUCHER_TYPE_1"]
  }
}
```

### 클라이언트 코드
```js
// 변경 전 — 토큰 디코딩으로 직접 파싱
import { jwtDecode } from 'jwt-decode';
const token = localStorage.getItem('accessToken');
const { id, email, nickname } = jwtDecode(token);

// 변경 후 — API 호출
const res = await fetch('/accounts/self', {
  credentials: 'include',
});
const { id, email, nickname, picture, groups, vouchers } = res.data;
```

---

## 5. 마이그레이션 체크리스트

- [ ] 모든 fetch/axios 요청에 `credentials: 'include'` 추가
- [ ] `localStorage`에서 토큰 저장/읽기 코드 제거
- [ ] `Authorization` 헤더에 토큰 직접 세팅하는 코드 제거
- [ ] 토큰 디코딩으로 사용자 정보 파싱하던 코드 → `GET /accounts/self` 로 대체
- [ ] axios 사용 시 전역 설정: `axios.defaults.withCredentials = true`
