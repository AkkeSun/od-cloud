# 그룹 파일 구글 드라이브 백업 API - 개발 계획서

---

## 1. 개요

### 기능 설명
백업 활성화된 모든 그룹의 미백업 파일 이력을 조회하여 Google Drive에 증분 백업하는 API

### 개발 목적 및 배경
현재 핵심 백업 로직은 구현 완료 상태이나, 두 가지 미완성 사항이 존재한다.

1. `BackupGroupFilesResponse`에 `skipCount` 필드가 누락되어 미백업 이력이 없어 건너뛴 그룹 수를 응답하지 못하는 상태
2. `BackupGroupFilesService`에 `skipCount` 추적 로직이 없어 `pendingHistories.isEmpty()` 분기에서 카운트가 누락되는 상태
3. 컨트롤러 레이어의 RestDocs 테스트가 미작성 상태

위 세 가지를 해소하여 기능을 완성한다.

---

## 2. API 설계

### HTTP Method + URL
```
POST /files/backup
```

### 요청 스펙
| 항목 | 내용 |
|------|------|
| 인증 | 불필요 (스케줄러 또는 관리자 직접 호출) |
| Content-Type | 불필요 (Request Body 없음) |
| Request Body | 없음 |

### 응답 스펙
`ApiResponse<BackupGroupFilesResponse>`

| 필드명 | 타입 | 설명 |
|--------|------|------|
| httpStatus | int | HTTP 상태 코드 (200) |
| message | String | 응답 메시지 ("OK") |
| data.totalGroups | int | 백업 활성화(backupYn=Y)된 전체 그룹 수 |
| data.successCount | int | 미백업 이력이 존재하고 백업 처리가 완료된 그룹 수 |
| data.failCount | int | 하나 이상의 파일 백업 실패가 발생한 그룹 수 |
| data.skipCount | int | 미백업 이력이 없어 건너뛴 그룹 수 |

### 에러 케이스

| 에러코드 | 메시지 | 발생 조건 |
|----------|--------|-----------|
| 4030 | Google Drive 폴더 생성/조회 중 오류가 발생했습니다 | `ensureFolder` 또는 `ensureSubFolder` 호출 실패 시 해당 그룹 failCount 증가 후 continue |
| 4031 | Google Drive 파일 업로드 중 오류가 발생했습니다 | `uploadFile` 호출 실패 시 해당 이력 failCount 증가 후 continue |
| 4032 | Google Drive 파일 삭제 중 오류가 발생했습니다 | `deleteFile` 호출 실패 시 warn 로그 후 계속 진행 (MOVE/RENAME의 이전 파일 삭제는 non-critical) |

> 위 에러는 API 레벨에서 500을 반환하지 않는다. 그룹/이력 단위로 격리 처리하며 최종 카운트를 응답에 반영한다.

---

## 3. 생성할 클래스 목록

### 수정 대상 (기존 파일)

| 패키지 경로 | 클래스명 | 유형 | 역할 |
|------------|----------|------|------|
| `application/file/service/backup_group_files` | `BackupGroupFilesResponse` | Record (수정) | `skipCount` 필드 추가 |
| `application/file/service/backup_group_files` | `BackupGroupFilesService` | Service (수정) | `skipCount` 추적 변수 선언 및 `continue` 분기에 `skipCount++` 추가, 응답 빌더에 `skipCount` 포함 |

### 신규 작성 대상

| 패키지 경로 | 클래스명 | 유형 | 역할 |
|------------|----------|------|------|
| `src/test/.../controller/file/backup_group_files` | `BackupGroupFilesControllerDocsTest` | RestDocs 테스트 | `POST /files/backup` 컨트롤러의 요청/응답 문서화 |

---

## 4. 도메인 모델 설계

신규 도메인 모델 없음. 기존 모델을 그대로 사용한다.

### 사용 도메인 모델

| 도메인 모델 | 주요 사용 필드 | 역할 |
|------------|--------------|------|
| `Group` | `id`, `name`, `driveFolderId`, `backupYn` | 백업 대상 그룹 식별 및 Drive 폴더 연결 |
| `FileHistory` | `id`, `groupId`, `actionType`, `beforeFileName`, `afterFileName`, `beforeFolderId`, `afterFolderId`, `fileLoc`, `fileSize`, `backupDt` | 백업 처리 단위 이력 |
| `FolderInfo` | `id`, `parentId`, `name` | Drive 폴더 계층 재현을 위한 앱 폴더 구조 참조 |

---

## 5. DB 설계

신규 테이블 및 컬럼 변경 없음. 기존 `file_history` 테이블의 `backup_dt` 컬럼을 배치 UPDATE한다.

### 관련 쿼리 (기존)

| 쿼리 | 조건 |
|------|------|
| `findAllEnabledForBackup()` | `backup_yn = 'Y'` |
| `findByGroupIdAndBackupDtIsNull(groupId)` | `group_id = ?` AND `backup_dt IS NULL` |
| `updateBackupDt(ids, backupDt)` | `id IN (...)` SET `backup_dt = ?` |

---

## 6. 핵심 비즈니스 로직 흐름

```
1. [Controller] POST /files/backup 요청 수신
   → useCase.backup() 호출
   → ApiResponse.ok(response) 응답

2. [BackupGroupFilesService] backup() 실행
   → groupStoragePort.findAllEnabledForBackup() : backupYn='Y' 그룹 전체 조회
   → totalGroups = groups.size()
   → successCount = 0, failCount = 0, skipCount = 0 초기화

3. [BackupGroupFilesService] 그룹별 반복 처리
   3-1. fileHistoryStoragePort.findByGroupIdAndBackupDtIsNull(groupId) : 미백업 이력 조회
   3-2. pendingHistories.isEmpty() == true → skipCount++ → continue (신규 추가)
   3-3. group.getDriveFolderId() == null
        → googleDrivePort.ensureFolder(groupName) : Drive 루트 폴더 생성/조회
        → groupStoragePort.updateDriveFolderId(groupId, folderId) : DB 갱신
        → 실패 시 failCount++ → continue

4. [BackupGroupFilesService] 이력 단위 반복 처리
   4-1. FileHistoryActionType.DELETE
        → resolveTargetFolder(beforeFolderId) : 이전 폴더의 Drive ID 결정
        → googleDrivePort.deleteFile(driveFolderId, beforeFileName)
        → 실패 시 warn 로그 후 failCount++, backupId는 추가하지 않고 continue

   4-2. fileLoc 또는 afterFileName이 null → warn 로그, failCount++ → continue

   4-3. UPLOAD / MOVE / RENAME
        → resolveTargetFolder(afterFolderId) : 대상 폴더 Drive ID 결정
        → filePort.readFile(fileInfo) : 로컬 파일 스트림 획득
        → googleDrivePort.uploadFile(targetFolderId, afterFileName, inputStream, fileSize)
        → backupIds에 이력 ID 추가
        → MOVE/RENAME이면 googleDrivePort.deleteFile(oldFolderId, beforeFileName) : 이전 파일 삭제
        → 업로드 실패 시 failCount++

5. [BackupGroupFilesService] resolveTargetFolder (재귀)
   → appFolderId == null → groupFolderId 반환 (루트)
   → subFolderIdCache 캐시 히트 → 캐시값 반환
   → folderInfoStoragePort.findById(appFolderId) : 앱 폴더 정보 조회
   → 부모 폴더 ID로 재귀 호출
   → googleDrivePort.ensureSubFolder(parentDriveFolderId, folderName) : Drive 서브폴더 생성/조회
   → 실패 시 null 반환 (호출부에서 failCount++ 처리)

6. [BackupGroupFilesService] 그룹 완료 처리
   → backupIds.isEmpty() == false → fileHistoryStoragePort.updateBackupDt(backupIds, now()) 배치 갱신
   → successCount++

7. [BackupGroupFilesService] 응답 구성
   → BackupGroupFilesResponse { totalGroups, successCount, failCount, skipCount } 반환
```

---

## 7. 성능 및 예외 처리 고려사항

### 성능

| 항목 | 현황 | 대응 |
|------|------|------|
| Drive API 호출 횟수 | 폴더당 1회, 파일당 1~2회 | `subFolderIdCache` Map으로 동일 폴더 중복 호출 방지 (이미 구현됨) |
| backupDt 갱신 쿼리 | 이력 건수만큼 개별 UPDATE 가능성 | 그룹 단위로 backupIds 누적 후 `IN(...)` 배치 UPDATE (이미 구현됨) |
| 대용량 그룹 처리 | 그룹이 많아질 경우 단일 트랜잭션 미사용 | 그룹별 격리 처리로 부분 실패가 전체에 영향 없음 |

### 예외 처리 시나리오

| 시나리오 | 처리 방식 |
|----------|-----------|
| Drive 루트 폴더 생성/조회 실패 | 해당 그룹 건너뜀 (failCount++), 나머지 그룹은 계속 처리 |
| Drive 서브폴더 생성/조회 실패 | 해당 이력만 건너뜀 (failCount++), 다음 이력 계속 처리 |
| 로컬 파일 읽기 실패 (IOException) | 해당 이력만 건너뜀 (failCount++), 다음 이력 계속 처리 |
| Drive 파일 업로드 실패 | 해당 이력만 건너뜀 (failCount++), 다음 이력 계속 처리 |
| MOVE/RENAME 이전 파일 삭제 실패 | warn 로그만 기록, failCount 증가 없이 계속 진행 (비정상 삭제는 non-critical) |
| fileLoc 또는 afterFileName이 null | 데이터 이상으로 판단, warn 로그, failCount++ |
| 미백업 이력이 없는 그룹 | skipCount++, 다음 그룹으로 continue |

---

## 8. 수정/신규 파일 전체 코드

### 8-1. BackupGroupFilesResponse (수정)

파일 경로: `src/main/java/com/odcloud/application/file/service/backup_group_files/BackupGroupFilesResponse.java`

현재 `skipCount` 필드가 없어 `BackupGroupFilesResponseTest`와 `BackupGroupFilesServiceTest`가 컴파일 오류 상태이다.
`skipCount` 필드를 추가한다.

```java
package com.odcloud.application.file.service.backup_group_files;

import lombok.Builder;

@Builder
public record BackupGroupFilesResponse(
    int totalGroups,
    int successCount,
    int failCount,
    int skipCount
) {

}
```

---

### 8-2. BackupGroupFilesService (수정)

파일 경로: `src/main/java/com/odcloud/application/file/service/backup_group_files/BackupGroupFilesService.java`

변경 포인트 세 곳:
1. `int skipCount = 0;` 초기화 변수 추가 (successCount, failCount 선언과 함께)
2. `pendingHistories.isEmpty()` 분기에 `skipCount++;` 추가
3. 응답 빌더에 `.skipCount(skipCount)` 추가

```java
package com.odcloud.application.file.service.backup_group_files;

import com.odcloud.application.file.port.in.BackupGroupFilesUseCase;
import com.odcloud.application.file.port.out.FileHistoryStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.file.port.out.GoogleDrivePort;
import com.odcloud.application.file.port.out.dto.FileResponse;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.FileHistory;
import com.odcloud.domain.model.FileHistoryActionType;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class BackupGroupFilesService implements BackupGroupFilesUseCase {

    private final GroupStoragePort groupStoragePort;
    private final FileHistoryStoragePort fileHistoryStoragePort;
    private final FilePort filePort;
    private final GoogleDrivePort googleDrivePort;
    private final FolderInfoStoragePort folderInfoStoragePort;

    @Override
    public BackupGroupFilesResponse backup() {
        List<Group> groups = groupStoragePort.findAllEnabledForBackup();

        int successCount = 0;
        int failCount = 0;
        int skipCount = 0;  // [추가]

        for (Group group : groups) {
            List<FileHistory> pendingHistories =
                fileHistoryStoragePort.findByGroupIdAndBackupDtIsNull(group.getId());

            if (pendingHistories.isEmpty()) {
                skipCount++;  // [추가]
                continue;
            }

            String groupFolderId = group.getDriveFolderId();
            if (groupFolderId == null) {
                try {
                    String folderName = group.getName();
                    groupFolderId = googleDrivePort.ensureFolder(folderName);
                    groupStoragePort.updateDriveFolderId(group.getId(), groupFolderId);
                } catch (Exception e) {
                    log.error(
                        "[BackupGroupFilesService] Drive 그룹 폴더 생성/조회 실패 - groupId={}, error={}",
                        group.getId(), e.getMessage());
                    failCount++;
                    continue;
                }
            }

            Map<Long, String> subFolderIdCache = new HashMap<>();

            List<Long> backupIds = new ArrayList<>();
            for (FileHistory history : pendingHistories) {
                if (history.getActionType() == FileHistoryActionType.DELETE) {
                    if (history.getBeforeFolderId() != null && history.getBeforeFileName() != null) {
                        String oldDriveFolderId = resolveTargetFolder(
                            history.getBeforeFolderId(), groupFolderId, subFolderIdCache
                        );
                        if (oldDriveFolderId != null) {
                            try {
                                googleDrivePort.deleteFile(oldDriveFolderId, history.getBeforeFileName());
                            } catch (Exception e) {
                                log.warn("[BackupGroupFilesService] Drive 파일 삭제 실패 - historyId={}, error={}",
                                    history.getId(), e.getMessage());
                                failCount++;
                                continue;
                            }
                        }
                    }
                    backupIds.add(history.getId());
                    continue;
                }

                if (history.getFileLoc() == null || history.getAfterFileName() == null) {
                    log.warn("[BackupGroupFilesService] 이력 데이터 이상 - historyId={}, actionType={}",
                        history.getId(), history.getActionType());
                    failCount++;
                    continue;
                }

                String targetFolderId = resolveTargetFolder(
                    history.getAfterFolderId(), groupFolderId, subFolderIdCache
                );
                if (targetFolderId == null) {
                    failCount++;
                    continue;
                }

                try {
                    FileInfo fileInfo = FileInfo.builder()
                        .fileLoc(history.getFileLoc())
                        .fileName(history.getAfterFileName())
                        .fileSize(history.getFileSize())
                        .build();

                    FileResponse fileResponse = filePort.readFile(fileInfo);
                    try (InputStream inputStream = fileResponse.resource().getInputStream()) {
                        googleDrivePort.uploadFile(
                            targetFolderId,
                            history.getAfterFileName(),
                            inputStream,
                            history.getFileSize() != null ? history.getFileSize() : 0L
                        );
                    }
                    backupIds.add(history.getId());

                    if ((history.getActionType() == FileHistoryActionType.MOVE
                        || history.getActionType() == FileHistoryActionType.RENAME)
                        && history.getBeforeFolderId() != null
                        && history.getBeforeFileName() != null) {
                        String oldDriveFolderId = resolveTargetFolder(
                            history.getBeforeFolderId(), groupFolderId, subFolderIdCache
                        );
                        if (oldDriveFolderId != null) {
                            try {
                                googleDrivePort.deleteFile(oldDriveFolderId, history.getBeforeFileName());
                            } catch (Exception e) {
                                log.warn("[BackupGroupFilesService] Drive 이전 폴더 파일 삭제 실패 - historyId={}, error={}",
                                    history.getId(), e.getMessage());
                            }
                        }
                    }
                } catch (IOException e) {
                    log.warn("[BackupGroupFilesService] 파일 스트림 처리 실패 - historyId={}, error={}",
                        history.getId(), e.getMessage());
                    failCount++;
                } catch (Exception e) {
                    log.warn("[BackupGroupFilesService] 파일 백업 실패 - historyId={}, error={}",
                        history.getId(), e.getMessage());
                    failCount++;
                }
            }

            if (!backupIds.isEmpty()) {
                fileHistoryStoragePort.updateBackupDt(backupIds, LocalDateTime.now());
                successCount++;
            }
        }

        return BackupGroupFilesResponse.builder()
            .totalGroups(groups.size())
            .successCount(successCount)
            .failCount(failCount)
            .skipCount(skipCount)  // [추가]
            .build();
    }

    private String resolveTargetFolder(Long appFolderId, String groupFolderId,
        Map<Long, String> subFolderIdCache) {
        if (appFolderId == null) {
            return groupFolderId;
        }

        if (subFolderIdCache.containsKey(appFolderId)) {
            return subFolderIdCache.get(appFolderId);
        }

        try {
            FolderInfo folderInfo = folderInfoStoragePort.findById(appFolderId);

            String parentDriveFolderId = resolveTargetFolder(
                folderInfo.getParentId(), groupFolderId, subFolderIdCache
            );

            String subFolderName = folderInfo.getName();
            String subFolderDriveId = googleDrivePort.ensureSubFolder(parentDriveFolderId,
                subFolderName);
            subFolderIdCache.put(appFolderId, subFolderDriveId);
            return subFolderDriveId;

        } catch (Exception e) {
            log.warn("[BackupGroupFilesService] Drive 서브폴더 생성/조회 실패 - folderId={}, error={}",
                appFolderId, e.getMessage());
            return null;
        }
    }
}
```

---

### 8-3. BackupGroupFilesControllerDocsTest (신규)

파일 경로: `src/test/java/com/odcloud/adapter/in/controller/file/backup_group_files/BackupGroupFilesControllerDocsTest.java`

Request Body가 없는 POST API이므로 `requestFields` 없이 `responseFields`만 문서화한다.
에러 케이스는 UseCase 레벨에서 예외가 전파되는 시나리오 1건을 문서화한다.

```java
package com.odcloud.adapter.in.controller.file.backup_group_files;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.file.port.in.BackupGroupFilesUseCase;
import com.odcloud.application.file.service.backup_group_files.BackupGroupFilesResponse;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

class BackupGroupFilesControllerDocsTest extends RestDocsSupport {

    private final BackupGroupFilesUseCase useCase = mock(BackupGroupFilesUseCase.class);
    private final String apiName = "그룹 파일 Google Drive 백업 API";

    @Override
    protected Object initController() {
        return new BackupGroupFilesController(useCase);
    }

    @Nested
    @DisplayName("[backup] 그룹 파일을 Google Drive에 백업하는 API")
    class Describe_backup {

        @Test
        @DisplayName("[success] 모든 그룹이 정상 백업되면 successCount를 응답한다")
        void success_allGroupsBackedUp() throws Exception {
            given(useCase.backup()).willReturn(BackupGroupFilesResponse.builder()
                .totalGroups(3)
                .successCount(2)
                .failCount(0)
                .skipCount(1)
                .build());

            mockMvc.perform(post("/files/backup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("[" + apiName + "] success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName)
                        .description(
                            "백업 활성화(backupYn=Y)된 모든 그룹의 미백업 파일 이력을 Google Drive에 증분 백업합니다. "
                                + "그룹 단위로 격리 처리되어 일부 그룹 실패 시에도 나머지 그룹은 계속 처리됩니다.")
                        .responseFields(
                            fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                                .description("HTTP 상태 코드"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT)
                                .description("응답 데이터"),
                            fieldWithPath("data.totalGroups").type(JsonFieldType.NUMBER)
                                .description("백업 활성화된 전체 그룹 수"),
                            fieldWithPath("data.successCount").type(JsonFieldType.NUMBER)
                                .description("백업 완료된 그룹 수"),
                            fieldWithPath("data.failCount").type(JsonFieldType.NUMBER)
                                .description("백업 실패가 발생한 그룹 수"),
                            fieldWithPath("data.skipCount").type(JsonFieldType.NUMBER)
                                .description("미백업 이력이 없어 건너뛴 그룹 수")
                        )
                        .responseSchema(Schema.schema("[response] " + apiName))
                        .build()
                    )
                ));
        }

        @Test
        @DisplayName("[success] 백업 대상 그룹이 없으면 모든 카운트가 0으로 응답한다")
        void success_noGroupsToBackup() throws Exception {
            given(useCase.backup()).willReturn(BackupGroupFilesResponse.builder()
                .totalGroups(0)
                .successCount(0)
                .failCount(0)
                .skipCount(0)
                .build());

            mockMvc.perform(post("/files/backup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("[" + apiName + "] success - no groups",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName + " - 백업 대상 없음")
                        .description("백업 활성화된 그룹이 없는 경우 모든 카운트가 0으로 응답됩니다.")
                        .responseFields(
                            fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                                .description("HTTP 상태 코드"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT)
                                .description("응답 데이터"),
                            fieldWithPath("data.totalGroups").type(JsonFieldType.NUMBER)
                                .description("백업 활성화된 전체 그룹 수"),
                            fieldWithPath("data.successCount").type(JsonFieldType.NUMBER)
                                .description("백업 완료된 그룹 수"),
                            fieldWithPath("data.failCount").type(JsonFieldType.NUMBER)
                                .description("백업 실패가 발생한 그룹 수"),
                            fieldWithPath("data.skipCount").type(JsonFieldType.NUMBER)
                                .description("미백업 이력이 없어 건너뛴 그룹 수")
                        )
                        .responseSchema(Schema.schema("[response] " + apiName))
                        .build()
                    )
                ));
        }

        @Test
        @DisplayName("[success] 일부 그룹에서 failCount가 발생해도 200을 응답한다")
        void success_partialFailure() throws Exception {
            given(useCase.backup()).willReturn(BackupGroupFilesResponse.builder()
                .totalGroups(5)
                .successCount(3)
                .failCount(2)
                .skipCount(0)
                .build());

            mockMvc.perform(post("/files/backup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("[" + apiName + "] success - partial failure",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName + " - 부분 실패")
                        .description(
                            "일부 그룹/이력에서 Drive API 실패가 발생해도 HTTP 200으로 응답합니다. "
                                + "failCount를 통해 실패 여부를 확인할 수 있습니다.")
                        .responseFields(
                            fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                                .description("HTTP 상태 코드"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT)
                                .description("응답 데이터"),
                            fieldWithPath("data.totalGroups").type(JsonFieldType.NUMBER)
                                .description("백업 활성화된 전체 그룹 수"),
                            fieldWithPath("data.successCount").type(JsonFieldType.NUMBER)
                                .description("백업 완료된 그룹 수"),
                            fieldWithPath("data.failCount").type(JsonFieldType.NUMBER)
                                .description("백업 실패가 발생한 그룹 수"),
                            fieldWithPath("data.skipCount").type(JsonFieldType.NUMBER)
                                .description("미백업 이력이 없어 건너뛴 그룹 수")
                        )
                        .responseSchema(Schema.schema("[response] " + apiName))
                        .build()
                    )
                ));
        }

        @Test
        @DisplayName("[error] UseCase에서 예상치 못한 예외 발생 시 500을 응답한다")
        void error_unexpectedExceptionReturns500() throws Exception {
            willThrow(new CustomBusinessException(ErrorCode.Business_GOOGLE_DRIVE_ENSURE_FOLDER_ERROR))
                .given(useCase).backup();

            mockMvc.perform(post("/files/backup"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andDo(document("[" + apiName + "] error - unexpected exception",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName + " - 서버 오류")
                        .description(
                            "UseCase 레벨에서 처리되지 않은 예외 발생 시 500을 반환합니다. "
                                + "정상적으로는 그룹 단위 격리 처리로 500이 발생하지 않습니다.")
                        .responseFields(
                            fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                                .description("HTTP 상태 코드"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT)
                                .description("에러 응답 데이터"),
                            fieldWithPath("data.errorCode").type(JsonFieldType.NUMBER)
                                .description("에러 코드"),
                            fieldWithPath("data.errorMessage").type(JsonFieldType.STRING)
                                .description("에러 메시지")
                        )
                        .responseSchema(Schema.schema("[response] error"))
                        .build()
                    )
                ));
        }
    }
}
```

---

## 9. 작업 순서 요약

| 순서 | 대상 파일 | 작업 유형 | 핵심 변경 내용 |
|------|-----------|-----------|---------------|
| 1 | `BackupGroupFilesResponse.java` | 수정 | `skipCount` 필드 추가 |
| 2 | `BackupGroupFilesService.java` | 수정 | `skipCount` 변수 초기화, `isEmpty()` 분기 카운트, 응답 빌더 포함 |
| 3 | `BackupGroupFilesControllerDocsTest.java` | 신규 | RestDocs 기반 컨트롤러 테스트 4케이스 작성 |

순서 1, 2 완료 후 기존 `BackupGroupFilesResponseTest` 및 `BackupGroupFilesServiceTest`의 컴파일 오류가 해소된다.
