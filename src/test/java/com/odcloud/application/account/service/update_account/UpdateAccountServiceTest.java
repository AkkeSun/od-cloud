package com.odcloud.application.account.service.update_account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.account.port.in.command.UpdateAccountCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeAccountStoragePort;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class UpdateAccountServiceTest {

    private FakeAccountStoragePort fakeAccountStoragePort;
    private FakeFilePort fakeFilePort;
    private ProfileConstant profileConstant;
    private UpdateAccountService updateAccountService;

    @BeforeEach
    void setUp() {
        fakeAccountStoragePort = new FakeAccountStoragePort();
        fakeFilePort = new FakeFilePort();
        profileConstant = ProfileConstant.builder()
            .webServerHost("http://localhost:8080")
            .fileUpload(ProfileConstant.FileUpload.builder()
                .basePath("/picture/")
                .build())
            .build();
        updateAccountService = new UpdateAccountService(fakeFilePort, profileConstant,
            fakeAccountStoragePort);
    }

    @Nested
    @DisplayName("[update] 사용자 정보 수정")
    class Describe_update {

        @Test
        @DisplayName("[success] 닉네임과 프로필 사진을 수정한다")
        void success_updateBoth() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("oldNickname")
                .name("사용자")
                .picture("http://localhost:8080/picture/old.jpg")
                .groups(List.of(Group.of("group1")))
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            fakeAccountStoragePort.database.add(account);

            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "new-profile.jpg",
                "image/jpeg",
                "test image content".getBytes()
            );

            UpdateAccountCommand command = UpdateAccountCommand.builder()
                .account(account)
                .nickname("newNickname")
                .pictureFile(pictureFile)
                .build();

            // when
            UpdateAccountServiceResponse response = updateAccountService.update(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(response.nickname()).isEqualTo("newNickname");
            assertThat(response.picture()).isNotNull().startsWith("http://localhost:8080");

            // DB에 저장된 값 검증
            Account updatedAccount = fakeAccountStoragePort.findByEmail("user@example.com");
            assertThat(updatedAccount.getNickname()).isEqualTo("newNickname");
            assertThat(updatedAccount.getModDt()).isNotNull();

            // 파일 업로드 검증
            assertThat(fakeFilePort.uploadFileCallCount).isEqualTo(1);

            // 기존 파일 삭제 검증
            assertThat(fakeFilePort.deletedFiles).contains("/picture/old.jpg");
        }

        @Test
        @DisplayName("[success] 닉네임만 수정한다")
        void success_updateNicknameOnly() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("oldNickname")
                .name("사용자")
                .picture("http://localhost:8080/picture/old.jpg")
                .groups(List.of(Group.of("group1")))
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            fakeAccountStoragePort.database.add(account);

            UpdateAccountCommand command = UpdateAccountCommand.builder()
                .account(account)
                .nickname("updatedNickname")
                .pictureFile(null)
                .build();

            // when
            UpdateAccountServiceResponse response = updateAccountService.update(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.nickname()).isEqualTo("updatedNickname");
            assertThat(response.picture()).isEqualTo("http://localhost:8080/picture/old.jpg");

            Account updatedAccount = fakeAccountStoragePort.findByEmail("user@example.com");
            assertThat(updatedAccount.getNickname()).isEqualTo("updatedNickname");
            assertThat(updatedAccount.getPicture()).isEqualTo(
                "http://localhost:8080/picture/old.jpg");

            // 파일 업로드 및 삭제 호출되지 않음
            assertThat(fakeFilePort.uploadFileCallCount).isEqualTo(0);
            assertThat(fakeFilePort.deletedFiles).isEmpty();
        }

        @Test
        @DisplayName("[success] 프로필 사진만 수정한다")
        void success_updatePictureOnly() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("myNickname")
                .name("사용자")
                .picture("http://localhost:8080/picture/old.jpg")
                .groups(List.of(Group.of("group1")))
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            fakeAccountStoragePort.database.add(account);

            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "brand-new.jpg",
                "image/jpeg",
                "test image content".getBytes()
            );

            UpdateAccountCommand command = UpdateAccountCommand.builder()
                .account(account)
                .nickname(null)
                .pictureFile(pictureFile)
                .build();

            // when
            UpdateAccountServiceResponse response = updateAccountService.update(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.nickname()).isEqualTo("myNickname");
            assertThat(response.picture()).isNotNull().startsWith("http://localhost:8080");

            Account updatedAccount = fakeAccountStoragePort.findByEmail("user@example.com");
            assertThat(updatedAccount.getNickname()).isEqualTo("myNickname");

            // 파일 업로드 검증
            assertThat(fakeFilePort.uploadFileCallCount).isEqualTo(1);

            // 기존 파일 삭제 검증
            assertThat(fakeFilePort.deletedFiles).contains("/picture/old.jpg");
        }

        @Test
        @DisplayName("[success] 외부 URL 프로필 사진은 삭제하지 않는다")
        void success_externalUrlNotDeleted() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("nickname")
                .name("사용자")
                .picture("https://example.com/external-profile.jpg")
                .groups(List.of(Group.of("group1")))
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            fakeAccountStoragePort.database.add(account);

            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "new.jpg",
                "image/jpeg",
                "test image content".getBytes()
            );

            UpdateAccountCommand command = UpdateAccountCommand.builder()
                .account(account)
                .nickname("nickname")
                .pictureFile(pictureFile)
                .build();

            // when
            UpdateAccountServiceResponse response = updateAccountService.update(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.nickname()).isEqualTo("nickname");
            assertThat(response.picture()).isNotNull().startsWith("http");

            // 파일 업로드는 호출됨
            assertThat(fakeFilePort.uploadFileCallCount).isEqualTo(1);

            // 외부 URL이므로 삭제되지 않음
            assertThat(fakeFilePort.deletedFiles).isEmpty();
        }

        @Test
        @DisplayName("[success] 수정 후 modDt가 업데이트된다")
        void success_modDtUpdated() {
            // given
            LocalDateTime originalRegDt = LocalDateTime.of(2024, 1, 1, 12, 0);
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("oldNickname")
                .name("사용자")
                .picture("http://localhost:8080/picture/old.jpg")
                .groups(List.of(Group.of("group1")))
                .regDt(originalRegDt)
                .modDt(null)
                .build();

            fakeAccountStoragePort.database.add(account);

            UpdateAccountCommand command = UpdateAccountCommand.builder()
                .account(account)
                .nickname("newNickname")
                .pictureFile(null)
                .build();

            // when
            UpdateAccountServiceResponse response = updateAccountService.update(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.nickname()).isEqualTo("newNickname");

            Account updatedAccount = fakeAccountStoragePort.findByEmail("user@example.com");
            assertThat(updatedAccount.getModDt()).isNotNull();
            assertThat(updatedAccount.getRegDt()).isEqualTo(originalRegDt);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 사용자를 수정하려고 하면 예외가 발생한다")
        void failure_accountNotFound() {
            // given
            Account nonExistentAccount = Account.builder()
                .id(999L)
                .email("nonexistent@example.com")
                .nickname("oldNickname")
                .name("사용자")
                .picture("/picture/old.jpg")
                .groups(List.of())
                .build();

            UpdateAccountCommand command = UpdateAccountCommand.builder()
                .account(nonExistentAccount)
                .nickname("newNickname")
                .pictureFile(null)
                .build();

            // when & then
            assertThatThrownBy(() -> updateAccountService.update(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_NOT_FOUND_ACCOUNT);
        }
    }
}
