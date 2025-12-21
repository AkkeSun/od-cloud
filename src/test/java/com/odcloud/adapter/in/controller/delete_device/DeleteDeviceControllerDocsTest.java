package com.odcloud.adapter.in.controller.delete_device;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.port.in.DeleteDeviceUseCase;
import com.odcloud.application.service.delete_device.DeleteDeviceServiceResponse;
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

class DeleteDeviceControllerDocsTest extends RestDocsSupport {

    private final DeleteDeviceUseCase useCase = mock(DeleteDeviceUseCase.class);
    private final String apiName = "디바이스 삭제 API";

    @Override
    protected Object initController() {
        return new DeleteDeviceController(useCase);
    }

    @Nested
    @DisplayName("[deleteDevice] 디바이스를 삭제하는 API")
    class Describe_deleteDevice {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API를 호출한 경우 401 코드와 에러 메시지를 응답한다")
        void error_unauthorized() throws Exception {
            // given
            DeleteDeviceRequest request = DeleteDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-123")
                .build();

            given(useCase.delete(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when & then
            performErrorDocument("error token", request,
                status().isUnauthorized(), "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 디바이스를 정상적으로 삭제한다")
        void success() throws Exception {
            // given
            DeleteDeviceRequest request = DeleteDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-123")
                .build();

            DeleteDeviceServiceResponse serviceResponse = DeleteDeviceServiceResponse.ofSuccess();
            given(useCase.delete(any())).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", request, status().isOk(), "success",
                "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("디바이스 삭제 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 존재하지 않는 디바이스 삭제 시 500 에러를 반환한다")
        void error_deviceNotFound() throws Exception {
            // given
            DeleteDeviceRequest request = DeleteDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-999")
                .build();

            given(useCase.delete(any())).willThrow(
                new CustomBusinessException(ErrorCode.Business_DoesNotExists_DEVICE));

            // when & then
            performErrorDocument("Bearer test", request,
                status().isInternalServerError(), "존재하지 않는 디바이스");
        }

        @Test
        @DisplayName("[error] 다른 사용자의 디바이스 삭제 시 500 에러를 반환한다")
        void error_notOwnDevice() throws Exception {
            // given
            DeleteDeviceRequest request = DeleteDeviceRequest.builder()
                .osType("iOS")
                .deviceId("device-123")
                .build();

            given(useCase.delete(any())).willThrow(
                new CustomBusinessException(ErrorCode.ACCESS_DENIED));

            // when & then
            performErrorDocument("Bearer test", request,
                status().isInternalServerError(), "다른 사용자의 디바이스");
        }
    }

    private void performDocument(
        String authorization,
        DeleteDeviceRequest request,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(delete("/devices")
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
                    .summary("디바이스 삭제 API")
                    .description("로그인한 사용자의 디바이스 정보를 삭제합니다.<br><br>"
                        + "- accountId, osType, deviceId 조합으로 디바이스를 찾아 삭제합니다.<br>"
                        + "- 로그인한 사용자의 디바이스만 삭제할 수 있습니다.<br>"
                        + "- 존재하지 않는 디바이스를 삭제하려고 하면 에러가 발생합니다.")
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .requestFields(
                        fieldWithPath("osType").type(JsonFieldType.STRING)
                            .description("OS 타입 (iOS, Android 등)"),
                        fieldWithPath("deviceId").type(JsonFieldType.STRING)
                            .description("디바이스 고유 ID")
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
        DeleteDeviceRequest request,
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
