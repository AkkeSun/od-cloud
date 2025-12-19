package com.odcloud.adapter.in.register_device;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
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
import com.odcloud.application.port.in.RegisterDeviceUseCase;
import com.odcloud.application.service.register_device.RegisterDeviceServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class RegisterDeviceControllerDocsTest extends RestDocsSupport {

    private final RegisterDeviceUseCase useCase = mock(RegisterDeviceUseCase.class);
    private final String apiName = "디바이스 등록 API";

    @Override
    protected Object initController() {
        return new RegisterDeviceController(useCase);
    }

    @Nested
    @DisplayName("[registerDevice] 디바이스를 등록하는 API")
    class Describe_registerDevice {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API를 호출한 경우 401 코드와 에러 메시지를 응답한다")
        void error_unauthorized() throws Exception {
            // given
            RegisterDeviceRequest request = RegisterDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-123")
                .appVersion("1.0.0")
                .fcmToken("fcm-token-123")
                .build();

            given(useCase.register(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when & then
            performErrorDocument("error token", request, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 디바이스를 정상적으로 등록한다")
        void success() throws Exception {
            // given
            RegisterDeviceRequest request = RegisterDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-123")
                .appVersion("1.0.0")
                .fcmToken("fcm-token-123")
                .build();

            RegisterDeviceServiceResponse serviceResponse =
                RegisterDeviceServiceResponse.ofSuccess();

            given(useCase.register(any())).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", request, status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("디바이스 등록 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] osType을 입력하지 않은 경우 400 에러를 반환한다")
        void error_osTypeIsBlank() throws Exception {
            // given
            RegisterDeviceRequest request = RegisterDeviceRequest.builder()
                .osType(null)
                .deviceId("device-123")
                .appVersion("1.0.0")
                .fcmToken("fcm-token-123")
                .build();

            // when & then
            performErrorDocument("Bearer test", request, status().isBadRequest(),
                "osType 미입력");
        }

        @Test
        @DisplayName("[error] deviceId를 입력하지 않은 경우 400 에러를 반환한다")
        void error_deviceIdIsBlank() throws Exception {
            // given
            RegisterDeviceRequest request = RegisterDeviceRequest.builder()
                .osType("iOS")
                .deviceId(null)
                .appVersion("1.0.0")
                .fcmToken("fcm-token-123")
                .build();

            // when & then
            performErrorDocument("Bearer test", request, status().isBadRequest(),
                "deviceId 미입력");
        }

        @Test
        @DisplayName("[error] appVersion을 입력하지 않은 경우 400 에러를 반환한다")
        void error_appVersionIsBlank() throws Exception {
            // given
            RegisterDeviceRequest request = RegisterDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-123")
                .appVersion(null)
                .fcmToken("fcm-token-123")
                .build();

            // when & then
            performErrorDocument("Bearer test", request, status().isBadRequest(),
                "appVersion 미입력");
        }

        @Test
        @DisplayName("[error] fcmToken을 입력하지 않은 경우 400 에러를 반환한다")
        void error_fcmTokenIsBlank() throws Exception {
            // given
            RegisterDeviceRequest request = RegisterDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-123")
                .appVersion("1.0.0")
                .fcmToken(null)
                .build();

            // when & then
            performErrorDocument("Bearer test", request, status().isBadRequest(),
                "fcmToken 미입력");
        }
    }

    private void performDocument(
        String authorization,
        RegisterDeviceRequest request,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(post("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Device")
                    .summary("디바이스 등록 API")
                    .description("사용자의 디바이스 정보를 등록하거나 업데이트합니다.<br><br>"
                        + "- 동일한 accountId, osType, deviceId 조합이 존재하면 정보를 업데이트합니다.<br>"
                        + "- FCM 토큰이나 앱 버전이 변경된 경우 업데이트됩니다.<br>"
                        + "- 마지막 로그인 시간(lastLoginDt)은 항상 갱신됩니다.")
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .requestFields(
                        fieldWithPath("osType").type(JsonFieldType.STRING)
                            .description("OS 타입 (iOS, Android 등)").optional(),
                        fieldWithPath("deviceId").type(JsonFieldType.STRING)
                            .description("디바이스 고유 ID").optional(),
                        fieldWithPath("appVersion").type(JsonFieldType.STRING)
                            .description("앱 버전").optional(),
                        fieldWithPath("fcmToken").type(JsonFieldType.STRING)
                            .description("FCM 푸시 토큰").optional()
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        String authorization,
        RegisterDeviceRequest request,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(authorization, request, status, identifier, "error",
            fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                .description("상태 코드"),
            fieldWithPath("message").type(JsonFieldType.STRING)
                .description("상태 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT)
                .description("응답 데이터"),
            fieldWithPath("data.errorCode").type(JsonFieldType.NUMBER)
                .description("에러 코드"),
            fieldWithPath("data.errorMessage").type(JsonFieldType.STRING)
                .description("에러 메시지")
        );
    }
}
