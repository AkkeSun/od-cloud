package com.odcloud.adapter.in.controller.account.update_account;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.account.port.in.command.UpdateAccountCommand;
import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class UpdateAccountRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] Request를 Command로 변환한다")
        void success() {
            // given
            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "profile.png",
                "image/png",
                "image-content".getBytes()
            );

            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .nickname("새닉네임")
                .pictureFile(pictureFile)
                .build();

            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .name("홍길동")
                .build();

            // when
            UpdateAccountCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.nickname()).isEqualTo("새닉네임");
            assertThat(command.pictureFile()).isEqualTo(pictureFile);
        }

        @Test
        @DisplayName("[success] 닉네임만 있는 Request를 Command로 변환한다")
        void success_nicknameOnly() {
            // given
            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .nickname("새닉네임")
                .pictureFile(null)
                .build();

            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .name("홍길동")
                .build();

            // when
            UpdateAccountCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.nickname()).isEqualTo("새닉네임");
            assertThat(command.pictureFile()).isNull();
        }

        @Test
        @DisplayName("[success] 프로필 사진만 있는 Request를 Command로 변환한다")
        void success_pictureOnly() {
            // given
            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "profile.png",
                "image/png",
                "image-content".getBytes()
            );

            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .nickname(null)
                .pictureFile(pictureFile)
                .build();

            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .name("홍길동")
                .build();

            // when
            UpdateAccountCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.nickname()).isNull();
            assertThat(command.pictureFile()).isEqualTo(pictureFile);
        }

        @Test
        @DisplayName("[success] null 값이 포함된 Request를 Command로 변환한다")
        void success_withNullValues() {
            // given
            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .nickname(null)
                .pictureFile(null)
                .build();

            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .name("홍길동")
                .build();

            // when
            UpdateAccountCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.nickname()).isNull();
            assertThat(command.pictureFile()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열이 포함된 Request를 Command로 변환한다")
        void success_withEmptyString() {
            // given
            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .nickname("")
                .pictureFile(null)
                .build();

            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .name("홍길동")
                .build();

            // when
            UpdateAccountCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.nickname()).isEmpty();
            assertThat(command.pictureFile()).isNull();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Request를 생성한다")
        void success() {
            // given
            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "profile.png",
                "image/png",
                "image-content".getBytes()
            );

            // when
            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .nickname("새닉네임")
                .pictureFile(pictureFile)
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getNickname()).isEqualTo("새닉네임");
            assertThat(request.getPictureFile()).isEqualTo(pictureFile);
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .nickname(null)
                .pictureFile(null)
                .build();

            // then
            assertThat(request.getNickname()).isNull();
            assertThat(request.getPictureFile()).isNull();
        }

        @Test
        @DisplayName("[success] 닉네임만으로 Request를 생성한다")
        void success_nicknameOnly() {
            // when
            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .nickname("새닉네임")
                .build();

            // then
            assertThat(request.getNickname()).isEqualTo("새닉네임");
            assertThat(request.getPictureFile()).isNull();
        }

        @Test
        @DisplayName("[success] 프로필 사진만으로 Request를 생성한다")
        void success_pictureOnly() {
            // given
            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "profile.png",
                "image/png",
                "image-content".getBytes()
            );

            // when
            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .pictureFile(pictureFile)
                .build();

            // then
            assertThat(request.getNickname()).isNull();
            assertThat(request.getPictureFile()).isEqualTo(pictureFile);
        }
    }

    @Nested
    @DisplayName("[accessor] Request accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] getNickname()으로 값을 조회한다")
        void success_getNickname() {
            // given
            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .nickname("새닉네임")
                .build();

            // when
            String nickname = request.getNickname();

            // then
            assertThat(nickname).isEqualTo("새닉네임");
        }

        @Test
        @DisplayName("[success] getPictureFile()로 값을 조회한다")
        void success_getPictureFile() {
            // given
            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "profile.png",
                "image/png",
                "image-content".getBytes()
            );

            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .pictureFile(pictureFile)
                .build();

            // when
            MultipartFile file = request.getPictureFile();

            // then
            assertThat(file).isEqualTo(pictureFile);
            assertThat(file.getOriginalFilename()).isEqualTo("profile.png");
            assertThat(file.getContentType()).isEqualTo("image/png");
        }
    }

    @Nested
    @DisplayName("[validation] 파일 타입 검증 테스트")
    class Describe_validation {

        @Test
        @DisplayName("[success] PNG 파일은 유효하다")
        void success_pngFile() {
            // given
            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "profile.png",
                "image/png",
                "image-content".getBytes()
            );

            // when
            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .pictureFile(pictureFile)
                .build();

            // then
            assertThat(request.getPictureFile()).isNotNull();
            assertThat(request.getPictureFile().getOriginalFilename()).endsWith(".png");
        }

        @Test
        @DisplayName("[success] JPG 파일은 유효하다")
        void success_jpgFile() {
            // given
            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "profile.jpg",
                "image/jpeg",
                "image-content".getBytes()
            );

            // when
            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .pictureFile(pictureFile)
                .build();

            // then
            assertThat(request.getPictureFile()).isNotNull();
            assertThat(request.getPictureFile().getOriginalFilename()).endsWith(".jpg");
        }

        @Test
        @DisplayName("[success] JPEG 파일은 유효하다")
        void success_jpegFile() {
            // given
            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "profile.jpeg",
                "image/jpeg",
                "image-content".getBytes()
            );

            // when
            UpdateAccountRequest request = UpdateAccountRequest.builder()
                .pictureFile(pictureFile)
                .build();

            // then
            assertThat(request.getPictureFile()).isNotNull();
            assertThat(request.getPictureFile().getOriginalFilename()).endsWith(".jpeg");
        }
    }
}
