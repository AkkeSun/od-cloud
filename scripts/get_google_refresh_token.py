"""
Google Drive OAuth2 Refresh Token 발급 스크립트 (표준 라이브러리만 사용)

사전 조건:
  1. Google Cloud Console > API & 서비스 > 사용자 인증 정보에서
     OAuth 2.0 클라이언트 ID(데스크톱 앱) 생성
  2. Google Drive API 활성화

사용법:
  python3 scripts/get_google_refresh_token.py
"""

import http.server
import json
import sys
import threading
import urllib.error
import urllib.parse
import urllib.request
import webbrowser

REDIRECT_URI = "http://localhost:8080"
SCOPE = "https://www.googleapis.com/auth/drive"
AUTH_URL = "https://accounts.google.com/o/oauth2/auth"
TOKEN_URL = "https://oauth2.googleapis.com/token"


def get_credentials() -> tuple[str, str]:
    print("=" * 55)
    print("  Google Drive OAuth2 Refresh Token 발급")
    print("=" * 55)
    client_id = input("Client ID     : ").strip()
    client_secret = input("Client Secret : ").strip()
    return client_id, client_secret


def build_auth_url(client_id: str) -> str:
    params = {
        "client_id": client_id,
        "redirect_uri": REDIRECT_URI,
        "response_type": "code",
        "scope": SCOPE,
        "access_type": "offline",
        "prompt": "consent",
    }
    return AUTH_URL + "?" + urllib.parse.urlencode(params)


def wait_for_auth_code() -> str:
    """localhost:8080 에서 인증 코드를 수신한다."""
    auth_code_holder: list[str] = []

    class CallbackHandler(http.server.BaseHTTPRequestHandler):
        def do_GET(self):
            parsed = urllib.parse.urlparse(self.path)
            params = urllib.parse.parse_qs(parsed.query)

            if "error" in params:
                error = params["error"][0]
                self.send_response(400)
                self.send_header("Content-Type", "text/html; charset=utf-8")
                self.end_headers()
                self.wfile.write(
                    f"<h2>인증 실패: {error}</h2><p>이 창을 닫고 다시 시도하세요.</p>".encode()
                )
                auth_code_holder.append("")
            elif "code" in params:
                auth_code_holder.append(params["code"][0])
                self.send_response(200)
                self.send_header("Content-Type", "text/html; charset=utf-8")
                self.end_headers()
                self.wfile.write(
                    "<h2>인증 완료!</h2><p>이 창을 닫고 터미널을 확인하세요.</p>".encode()
                )
            else:
                self.send_response(400)
                self.end_headers()

        def log_message(self, *_):
            pass

    server = http.server.HTTPServer(("localhost", 8080), CallbackHandler)

    thread = threading.Thread(target=server.handle_request, daemon=True)
    thread.start()
    thread.join(timeout=120)

    if not auth_code_holder:
        print("[오류] 120초 내에 인증 코드를 받지 못했습니다.")
        sys.exit(1)

    if not auth_code_holder[0]:
        print("[오류] 인증이 거부되었습니다.")
        sys.exit(1)

    return auth_code_holder[0]


def exchange_code_for_tokens(client_id: str, client_secret: str, code: str) -> dict:
    body = urllib.parse.urlencode({
        "code": code,
        "client_id": client_id,
        "client_secret": client_secret,
        "redirect_uri": REDIRECT_URI,
        "grant_type": "authorization_code",
    }).encode()

    req = urllib.request.Request(TOKEN_URL, data=body, method="POST")
    req.add_header("Content-Type", "application/x-www-form-urlencoded")

    try:
        with urllib.request.urlopen(req) as res:
            return json.loads(res.read())
    except urllib.error.HTTPError as e:
        error_body = e.read().decode()
        print(f"[오류] 토큰 교환 실패 (HTTP {e.code}): {error_body}")
        sys.exit(1)


def main():
    client_id, client_secret = get_credentials()

    auth_url = build_auth_url(client_id)
    print("\n브라우저에서 Google 계정 인증 페이지를 엽니다...")
    webbrowser.open(auth_url)
    print("(브라우저가 열리지 않으면 아래 URL을 직접 복사하세요)")
    print(f"\n{auth_url}\n")

    print("인증 완료를 기다리는 중... (최대 120초)")
    code = wait_for_auth_code()

    print("토큰 교환 중...")
    tokens = exchange_code_for_tokens(client_id, client_secret, code)

    refresh_token = tokens.get("refresh_token")
    if not refresh_token:
        print("[오류] Refresh Token이 응답에 없습니다.")
        print("Google Cloud Console > OAuth 동의 화면 > 테스트 사용자에 계정이 등록되어 있는지 확인하세요.")
        print(f"응답 내용: {tokens}")
        sys.exit(1)

    print("\n" + "=" * 55)
    print("  발급 완료")
    print("=" * 55)
    print(f"Refresh Token : {refresh_token}")
    print()
    print("아래 환경 변수에 설정하세요:")
    print(f"  GOOGLE_DRIVE_USER_REFRESH_TOKEN={refresh_token}")
    print(f"  GOOGLE_DRIVE_CLIENT_ID={client_id}")
    print(f"  GOOGLE_DRIVE_CLIENT_SECRET={client_secret}")
    print("=" * 55)


if __name__ == "__main__":
    main()
