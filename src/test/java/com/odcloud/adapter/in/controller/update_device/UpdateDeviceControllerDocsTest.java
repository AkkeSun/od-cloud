package com.odcloud.adapter.in.controller.update_device;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.port.in.UpdateDeviceUseCase;
import com.odcloud.application.service.update_device.UpdateDeviceServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class UpdateDeviceControllerDocsTest extends RestDocsSupport {

    private final UpdateDeviceUseCase useCase = mock(UpdateDeviceUseCase.class);
    private final String apiName = "디바이스 정보 수정 API";

    @Override
    protected Object initController() {
        return new UpdateDeviceController(useCase);
    }

    @Nested
    @DisplayName("[updateDevice] 디바이스 정보를 수정하는 API")
    class Describe_updateDevice {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API를 호출한 경우 401 코드와 에러 메시지를 응답한다")
        void error_unauthorized() throws Exception {
            // given
            UpdateDeviceRequest request = UpdateDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-123")
                .pushYn("N")
                .fcmToken("new-fcm-token")
                .build();

            given(useCase.update(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when & then
            performErrorDocument("error token", request, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] pushYn과 fcmToken을 모두 업데이트한다")
        void success_updateBoth() throws Exception {
            // given
            UpdateDeviceRequest request = UpdateDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-123")
                .pushYn("N")
                .fcmToken("new-fcm-token")
                .build();

            UpdateDeviceServiceResponse serviceResponse =
                UpdateDeviceServiceResponse.ofSuccess();

            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", request, status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("디바이스 정보 수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[success] pushYn만 업데이트한다")
        void success_updatePushYnOnly() throws Exception {
            // given
            UpdateDeviceRequest request = UpdateDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-123")
                .pushYn("N")
                .fcmToken(null)
                .build();

            UpdateDeviceServiceResponse serviceResponse =
                UpdateDeviceServiceResponse.ofSuccess();

            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", request, status().isOk(),
                "success pushYn only", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("디바이스 정보 수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[success] fcmToken만 업데이트한다")
        void success_updateFcmTokenOnly() throws Exception {
            // given
            UpdateDeviceRequest request = UpdateDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-123")
                .pushYn(null)
                .fcmToken("new-fcm-token")
                .build();

            UpdateDeviceServiceResponse serviceResponse =
                UpdateDeviceServiceResponse.ofSuccess();

            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", request, status().isOk(),
                "success fcmToken only", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("디바이스 정보 수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] osType을 입력하지 않은 경우 400 에러를 반환한다")
        void error_osTypeIsBlank() throws Exception {
            // given
            UpdateDeviceRequest request = UpdateDeviceRequest.builder()
                .osType(null)
                .deviceId("device-123")
                .pushYn("N")
                .fcmToken("new-fcm-token")
                .build();

            // when & then
            performErrorDocument("Bearer test", request, status().isBadRequest(),
                "osType 미입력");
        }

        @Test
        @DisplayName("[error] deviceId를 입력하지 않은 경우 400 에러를 반환한다")
        void error_deviceIdIsBlank() throws Exception {
            // given
            UpdateDeviceRequest request = UpdateDeviceRequest.builder()
                .osType("iOS")
                .deviceId(null)
                .pushYn("N")
                .fcmToken("new-fcm-token")
                .build();

            // when & then
            performErrorDocument("Bearer test", request, status().isBadRequest(),
                "deviceId 미입력");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 디바이스를 업데이트하려 하면 404 에러를 반환한다")
        void error_deviceNotFound() throws Exception {
            // given
            UpdateDeviceRequest request = UpdateDeviceRequest.builder()
                .osType("iOS")
                .deviceId("non-existent-device")
                .pushYn("N")
                .fcmToken("new-fcm-token")
                .build();

            given(useCase.update(any())).willThrow(
                new CustomBusinessException(ErrorCode.Business_DoesNotExists_DEVICE));

            // when & then
            performErrorDocument("Bearer test", request, status().isInternalServerError(),
                "디바이스 존재하지 않음");
        }
    }

    private void performDocument(
        String authorization,
        UpdateDeviceRequest request,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(patch("/devices")
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
                    .summary("디바이스 정보 수정 API")
                    .description("사용자의 디바이스 정보를 수정합니다.<br><br>"
                        + "- pushYn: 푸시 알림 수신 여부 (Y/N)<br>"
                        + "- fcmToken: FCM 푸시 토큰<br>"
                        + "- 두 필드 모두 선택적이며, 제공된 필드만 업데이트됩니다.<br>"
                        + "- osType과 deviceId로 대상 디바이스를 식별합니다.")
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .requestFields(
                        fieldWithPath("osType").type(JsonFieldType.STRING)
                            .description("OS 타입 (iOS, Android 등)").optional(),
                        fieldWithPath("deviceId").type(JsonFieldType.STRING)
                            .description("디바이스 고유 ID").optional(),
                        fieldWithPath("pushYn").type(JsonFieldType.STRING)
                            .description("푸시 알림 수신 여부 (Y/N)").optional(),
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
        UpdateDeviceRequest request,
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
