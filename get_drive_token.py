import os
from google_auth_oauthlib.flow import InstalledAppFlow
import json

client_id = input("Google Client ID 입력: ").strip()
client_secret = input("Google Client Secret 입력: ").strip()

client_config = {
    "installed": {
        "client_id": client_id,
        "client_secret": client_secret,
        "redirect_uris": ["http://localhost:8090"],
        "auth_uri": "https://accounts.google.com/o/oauth2/auth",
        "token_uri": "https://oauth2.googleapis.com/token"
    }
}

flow = InstalledAppFlow.from_client_config(
    client_config,
    scopes=["https://www.googleapis.com/auth/drive"]
)

creds = flow.run_local_server(port=8090)

print("\n✅ Refresh Token 발급 완료!")
print(f"\nGOOGLE_DRIVE_USER_REFRESH_TOKEN={creds.refresh_token}")