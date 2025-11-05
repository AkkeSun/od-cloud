package com.odcloud.adapter.in.register_account;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import com.odcloud.application.port.in.RegisterAccountUseCase;
import com.odcloud.application.service.register_account.RegisterAccountServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class RegisterAccountControllerDocsTest extends RestDocsSupport {

    private final RegisterAccountUseCase registerAccountUseCase = mock(RegisterAccountUseCase.class);
    private final String apiName = "registerAccount";

    @Override
    protected Object initController() {
        return new RegisterAccountController(registerAccountUseCase);
    }

    @Nested
    @DisplayName("[registerAccount] 계정을 등록하는 API")
    class Describe_registerAccount {

        @Test
        @DisplayName("[error] username이 null일 때 400 에러를 응답한다.")
        void error_username_null() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username(null)
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "username null",
                "접속 계정 미입력");
        }

        @Test
        @DisplayName("[error] username이 빈 문자열일 때 400 에러를 응답한다.")
        void error_username_blank() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("   ")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "username blank",
                "접속 계정 미입력");
        }

        @Test
        @DisplayName("[error] username이 30자를 초과할 때 400 에러를 응답한다.")
        void error_username_sizeExceed() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("a".repeat(31))
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "username size exceed",
                "접속 계정 입력 사이즈 초과");
        }

        @Test
        @DisplayName("[error] password가 null일 때 400 에러를 응답한다.")
        void error_password_null() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password(null)
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "password null",
                "비밀번호 미입력");
        }

        @Test
        @DisplayName("[error] password가 빈 문자열일 때 400 에러를 응답한다.")
        void error_password_blank() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password("   ")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "password blank",
                "비밀번호 미입력");
        }

        @Test
        @DisplayName("[error] name이 null일 때 400 에러를 응답한다.")
        void error_name_null() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password("password123")
                .name(null)
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "name null",
                "이름 미입력");
        }

        @Test
        @DisplayName("[error] email이 null일 때 400 에러를 응답한다.")
        void error_email_null() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email(null)
                .role("ROLE_USER")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "email null",
                "이메일 미입력");
        }

        @Test
        @DisplayName("[error] email이 30자를 초과할 때 400 에러를 응답한다.")
        void error_email_sizeExceed() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("a".repeat(31))
                .role("ROLE_USER")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "email size exceed",
                "이메일 입력 사이즈 초과");
        }

        @Test
        @DisplayName("[error] role이 null일 때 400 에러를 응답한다.")
        void error_role_null() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role(null)
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "role null",
                "권한 미입력");
        }

        @Test
        @DisplayName("[error] role이 유효하지 않은 값일 때 400 에러를 응답한다.")
        void error_role_invalid() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("INVALID_ROLE")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "role invalid",
                "유효하지 않은 권한");
        }

        @Test
        @DisplayName("[error] 이미 등록된 사용자명일 때 500 에러를 응답한다.")
        void error_duplicateUsername() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            given(registerAccountUseCase.register(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_USER));

            // when, then
            performErrorDocument(request, status().isInternalServerError(),
                "duplicate username", "이미 등록된 사용자");
        }

        @Test
        @DisplayName("[success] 계정 등록에 성공한다.")
        void success() throws Exception {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            RegisterAccountServiceResponse serviceResponse = RegisterAccountServiceResponse.builder()
                .result(true)
                .otpUrl("otpauth://totp/testuser?secret=ABCD1234")
                .build();

            given(registerAccountUseCase.register(any())).willReturn(serviceResponse);

            // when, then
            performSuccessDocument(request, status().isOk(), "success",
                "계정 등록 성공",
                fieldWithPath("result").type(JsonFieldType.BOOLEAN)
                    .description("등록 성공 여부"),
                fieldWithPath("optUrl").type(JsonFieldType.STRING)
                    .description("OTP URL")
            );
        }
    }

    private void performErrorDocument(RegisterAccountRequest request,
        ResultMatcher status, String docIdentifier, String responseMessage) throws Exception {

        JsonFieldType usernameType = request.username() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType passwordType = request.password() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType nameType = request.name() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType emailType = request.email() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType roleType = request.role() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Account")
                    .summary("계정 등록 API")
                    .description("새로운 계정을 등록합니다.")
                    .requestFields(
                        fieldWithPath("username").type(usernameType)
                            .description("접속 계정 (필수, 30자 이하)"),
                        fieldWithPath("password").type(passwordType)
                            .description("비밀번호 (필수)"),
                        fieldWithPath("name").type(nameType)
                            .description("이름 (필수)"),
                        fieldWithPath("email").type(emailType)
                            .description("이메일 (필수, 30자 이하)"),
                        fieldWithPath("role").type(roleType)
                            .description("권한 (필수, ROLE_ADMIN 또는 ROLE_USER)")
                    )
                    .responseFields(
                        fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                            .description("HTTP 상태 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("에러 데이터"),
                        fieldWithPath("data.errorCode").type(JsonFieldType.NUMBER)
                            .description("에러 코드"),
                        fieldWithPath("data.errorMessage").type(JsonFieldType.STRING)
                            .description("에러 메시지")
                    )
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseMessage))
                    .build()
                )
            ));
    }

    private void performSuccessDocument(RegisterAccountRequest request,
        ResultMatcher status, String docIdentifier, String responseSchema,
        FieldDescriptor... dataFields) throws Exception {

        // Request 필드가 null인 경우 JsonFieldType.NULL, 아니면 실제 타입
        JsonFieldType usernameType = request.username() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType passwordType = request.password() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType nameType = request.name() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType emailType = request.email() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType roleType = request.role() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        FieldDescriptor[] responseFields = new FieldDescriptor[dataFields.length + 3];
        responseFields[0] = fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
            .description("HTTP 상태 코드");
        responseFields[1] = fieldWithPath("message").type(JsonFieldType.STRING)
            .description("응답 메시지");
        responseFields[2] = fieldWithPath("data").type(JsonFieldType.OBJECT)
            .description("응답 데이터");

        for (int i = 0; i < dataFields.length; i++) {
            FieldDescriptor dataField = dataFields[i];
            String path = dataField.getPath();
            if (!path.startsWith("data.")) {
                path = "data." + path;
            }
            responseFields[i + 3] = fieldWithPath(path)
                .type(dataField.getType())
                .description(dataField.getDescription());
        }

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Account")
                    .summary("계정 등록 API")
                    .description("새로운 계정을 등록합니다.")
                    .requestFields(
                        fieldWithPath("username").type(usernameType)
                            .description("접속 계정 (필수, 30자 이하)"),
                        fieldWithPath("password").type(passwordType)
                            .description("비밀번호 (필수)"),
                        fieldWithPath("name").type(nameType)
                            .description("이름 (필수)"),
                        fieldWithPath("email").type(emailType)
                            .description("이메일 (필수, 30자 이하)"),
                        fieldWithPath("role").type(roleType)
                            .description("권한 (필수, ROLE_ADMIN 또는 ROLE_USER)")
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }
}