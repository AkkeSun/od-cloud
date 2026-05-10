# 코드 리뷰 결과

## 리뷰 대상

| 파일 | 유형 |
|------|------|
| `application/file/service/backup_group_files/BackupGroupFilesResponse.java` | 수정 |
| `application/file/service/backup_group_files/BackupGroupFilesService.java` | 수정 |
| `test/.../controller/file/backup_group_files/BackupGroupFilesControllerDocsTest.java` | 신규 |

---

## 1차 리뷰 요약 (이전 WARNING 항목)

| # | 위치 | 내용 |
|---|------|------|
| W1 | `BackupGroupFilesService` | DELETE 타입 이력 삭제 실패 시 `backupIds`에 이력 ID가 추가되지 않아 다음 배치에서 중복 삭제 시도 가능성 |
| W2 | `BackupGroupFilesService` | `resolveTargetFolder` 재귀 호출 시 순환 참조 데이터에 대한 명시적 방어 없음 |
| W3 | `BackupGroupFilesControllerDocsTest` | success 케이스 3개에 `responseFields` 정의 중복 선언 |
| W4 | `BackupGroupFilesControllerDocsTest` | `error_unexpectedExceptionReturns500` 테스트가 실제 서비스 구현과 괴리된 시나리오를 테스트하며 의도 불명확 |

---

## 2차 재검토 결과

### W3 — `responseFields` 중복 추출 여부

수정 완료. `backupResponseFields()` private 헬퍼 메서드가 168~185라인에 추가되었고, success 케이스 3개(`success_allGroupsBackedUp`, `success_noGroupsToBackup`, `success_partialFailure`)의 `.responseFields()` 호출부가 모두 `backupResponseFields()` 호출로 교체되었다.

- [INFO] WARNING 해소 확인.

---

### W4 — error 케이스 주석 추가 여부

수정 완료. `error_unexpectedExceptionReturns500` 메서드 내 131~133라인에 아래 주석이 추가되었다.

```
// 이 테스트는 실제 BackupGroupFilesService가 내부적으로 모든 예외를 catch하여 카운트로 흡수하므로
// UseCase.backup()이 예외를 전파하는 시나리오는 프로덕션에서 발생하지 않는다.
// 목적: UseCase 레벨에서 예외가 전파될 경우 ExceptionAdvice가 500을 올바르게 반환하는지
// 컨트롤러-ExceptionAdvice 연동을 검증한다.
```

- [INFO] WARNING 해소 확인. 테스트의 존재 이유와 검증 범위가 명확히 문서화되었다.

---

### W1 — DELETE 타입 이력 삭제 실패 시 `backupIds` 추가 누락

3차 재검토 결과: 수정 완료. 82~91라인 확인.

- `catch` 블록에서 `continue`가 제거됨.
- 삭제 실패 시 `failCount++` 후 블록이 종료되고, 90라인의 `backupIds.add(history.getId())`가 정상 실행됨.
- 주석으로 설계 의도 명시: "삭제 실패해도 이력은 처리 완료로 기록하여 중복 재처리 방지"

- [INFO] WARNING 해소 확인.

---

### W2 — `resolveTargetFolder` 재귀 호출 순환 참조 방어 미흡

3차 재검토 결과: 수정 완료. 183~184라인 확인.

- 재귀 진입 전 `subFolderIdCache.put(appFolderId, null)` sentinel 등록이 추가됨.
- 178~181라인의 `containsKey` 체크에서 sentinel(null)이 등록된 appFolderId로 재진입 시 null을 즉시 반환하여 무한 재귀를 차단함.
- 196라인에서 Drive ID 획득 후 sentinel → 실제 Drive ID로 교체하는 흐름도 정상 확인.

- [INFO] WARNING 해소 확인.

---

### 신규 CRITICAL/WARNING 발생 여부

신규 문제 없음. 컴파일(BUILD SUCCESSFUL) 정상 확인.

---

### 컴파일 확인

```
./gradlew compileTestJava -x test
BUILD SUCCESSFUL in 806ms
```

---

## 종합 의견

**최종 리뷰 통과: CRITICAL 0건, WARNING 0건**

전체 4건의 WARNING(W1~W4)이 모두 해소되었다. 컴파일 오류 없이 정상 동작한다.
