# Google Drive 백업 기능 배포 체크리스트

## 1. GCP 사전 준비

### 1-1. Google Drive API 활성화

1. [GCP Console](https://console.cloud.google.com) → 프로젝트 선택
2. `API 및 서비스` → `라이브러리` 검색: **Google Drive API** → 사용 설정

### 1-2. Service Account 생성

1. `IAM 및 관리자` → `서비스 계정` → `서비스 계정 만들기`
2. 이름: `od-cloud-backup` (또는 원하는 이름)
3. 역할: **없음(None)** — Drive 접근은 IAM 역할이 아닌 OAuth 스코프(`DriveScopes.DRIVE`)로 제어됨
4. 완료 후 `키` 탭 → `키 추가` → `새 키 만들기` → **JSON** 선택 → 다운로드

### 1-3. Drive 공유 폴더 설정 (선택)

- Service Account 이메일(`...@....iam.gserviceaccount.com`)에 백업 대상 Drive 폴더를 공유하거나,
- 별도 공유 없이 Service Account 소유 Drive에 직접 저장 가능 (기본 동작)

---

## 2. 서버 준비

### 2-1. CI/CD 시크릿 등록

JSON 키 파일을 서버에 두지 않고, **JSON 내용 전체를 환경변수로 주입**한다.

1. 다운로드한 JSON 파일을 열어 내용 전체를 복사
2. CI/CD 시크릿에 `GOOGLE_DRIVE_SERVICE_ACCOUNT_KEY_JSON` 이름으로 등록
3. 배포 시 환경변수로 주입

```yaml
# GitHub Actions 예시
- name: Deploy
  env:
    GOOGLE_DRIVE_SERVICE_ACCOUNT_KEY_JSON: ${{ secrets.GOOGLE_DRIVE_SERVICE_ACCOUNT_KEY_JSON }}
```

`application.yaml` 기준:

```yaml
google-drive:
  service-account-key-json: ${GOOGLE_DRIVE_SERVICE_ACCOUNT_KEY_JSON}
```

### 2-2. .gitignore 등록

JSON 키 파일을 로컬에서 테스트 목적으로 사용할 경우 git에 올라가지 않도록 등록한다.

```
# Google Drive Service Account Key
**/google-drive-service-account.json
**/service-account*.json
```

---

## 3. DB 마이그레이션

```sql
-- group 테이블: 그룹별 Drive 루트 폴더 ID, 백업 활성화 여부
ALTER TABLE "group"
    ADD COLUMN drive_folder_id VARCHAR(255),
    ADD COLUMN backup_yn      VARCHAR(1) NOT NULL DEFAULT 'N';

-- 백업 대상 그룹 활성화 (필요한 그룹만 Y로 변경)
-- UPDATE "group" SET backup_yn = 'Y' WHERE id IN (...);
```

> `drive_folder_id`는 nullable. 최초 백업 실행 시 없으면 Drive에서 생성 후 저장됨.  
> `backup_yn` 기본값은 `'N'`. 백업을 원하는 그룹만 `'Y'`로 수동 변경해야 한다.  
> 서브폴더 ID는 DB에 저장하지 않음 — 매 실행 시 Drive API(`ensureSubFolder`)가 name+parent로 찾거나 생성하며, 런타임 캐시로 같은 폴더의 중복 호출을 방지한다.

---

## 4. 백업 흐름

```
[스케줄러] 매일 새벽 02:00
    │
    ▼
BackupScheduler.backupGroupFiles()
    │
    ▼
BackupGroupFilesService.backup()
    │
    ├─ GroupStoragePort.findAll()          ← 전체 그룹 조회
    │
    └─ [그룹별 반복]
        │
        ├─ FileHistoryStoragePort
        │    .findByGroupIdAndBackupDtIsNull(groupId)
        │                                  ← 미백업 이력 조회
        │
        ├─ [미백업 이력 없음] → skip (skipCount++)
        │
        ├─ [driveFolderId 없음]
        │    └─ GoogleDrivePort.ensureFolder("groupId_groupName")
        │         └─ Drive API: 폴더 검색 → 없으면 생성
        │    └─ GroupStoragePort.updateDriveFolderId() → DB 저장
        │
        └─ [이력별 반복]
             │
             ├─ [DELETE 이력] → Drive 업로드 skip, backupIds에만 추가
             │
             ├─ [fileLoc/afterFileName null] → log.warn, failCount++, skip
             │
             ├─ [UPLOAD/RENAME/MOVE 이력]
             │    ├─ FilePort.readFile()   ← 로컬 디스크에서 파일 읽기
             │    ├─ GoogleDrivePort.uploadFile(folderId, fileName, stream, size)
             │    └─ backupIds에 추가 (성공)
             │
             └─ [실패] → log.warn, failCount++, 다음 항목 계속
             
        └─ FileHistoryStoragePort.updateBackupDt(backupIds, now())
             ← 성공한 이력의 backupDt 일괄 갱신
```

### 수동 실행 (관리자 API)

```
POST /files/backup
Authorization: Bearer {ADMIN_JWT_TOKEN}
```

---

## 5. 수동 실행 API

관리자 JWT 토큰으로 즉시 백업을 트리거할 수 있다.

```bash
curl -X POST https://{서버주소}/files/backup \
     -H "Authorization: Bearer {ADMIN_TOKEN}"
```

**응답 예시:**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalGroups": 5,
    "successCount": 3,
    "failCount": 0,
    "skipCount": 2
  }
}
```

| 필드             | 설명                     |
|----------------|------------------------|
| `totalGroups`  | 전체 그룹 수                |
| `successCount` | 백업 완료된 그룹 수            |
| `failCount`    | 백업 실패 건 수 (Drive 오류 등) |
| `skipCount`    | 미백업 이력 없어 건너뛴 그룹 수     |

---

## 6. 확인 사항 체크리스트

- [ ] GCP Drive API 활성화
- [ ] Service Account 생성 및 JSON 키 발급
- [ ] JSON 키 파일 서버 배치 (`chmod 400`)
- [ ] 환경변수 `GOOGLE_DRIVE_SERVICE_ACCOUNT_KEY_PATH` 설정
- [ ] `.gitignore`에 JSON 키 파일 패턴 등록
- [ ] DB 마이그레이션 실행 (`group` 테이블에 `drive_folder_id`, `backup_yn` 컬럼 추가)
- [ ] 수동 실행 API로 1회 테스트 (`POST /files/backup`)
- [ ] GCP Console → Drive API 사용량 모니터링 확인
