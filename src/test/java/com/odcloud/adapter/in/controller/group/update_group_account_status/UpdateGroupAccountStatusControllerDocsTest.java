package com.odcloud.adapter.in.controller.group.update_group_account_status;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.group.port.in.UpdateGroupAccountStatusUseCase;
import com.odcloud.application.group.service.update_group_account_status.UpdateGroupAccountStatusServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class UpdateGroupAccountStatusControllerDocsTest extends RestDocsSupport {

    private final UpdateGroupAccountStatusUseCase useCase = mock(
        UpdateGroupAccountStatusUseCase.class);
    private final String apiName = "그룹 계정 상태 변경 API";

    @Override
    protected Object initController() {
        return new UpdateGroupAccountStatusController(useCase);
    }

    @Nested
    @DisplayName("[updateStatus] 그룹 계정 상태를 변경하는 API")
    class Describe_updateStatus {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error() throws Exception {
            // given
            Long groupId = 1L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("ACTIVE")
                .memo(null)
                .build();
            given(useCase.updateStatus(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument("Bearer test", groupId, 2L, request,
                status().isUnauthorized(), "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 유효한 상태값(ACTIVE)으로 변경한다")
        void success_active() throws Exception {
            // given
            Long groupId = 1L;
            Long accountId = 1L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("ACTIVE")
                .memo(null)
                .build();

            UpdateGroupAccountStatusServiceResponse serviceResponse =
                UpdateGroupAccountStatusServiceResponse.ofSuccess();

            given(useCase.updateStatus(any())).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", groupId, accountId, request, status().isOk(),
                "success-active", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("상태 변경 성공 여부")
            );
        }

        @Test
        @DisplayName("[success] 유효한 상태값(DENIED)으로 변경하고 메모를 입력한다")
        void success_denied() throws Exception {
            // given
            Long groupId = 1L;
            Long accountId = 1L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("DENIED")
                .memo("그룹 가입 요건을 충족하지 못했습니다.")
                .build();

            UpdateGroupAccountStatusServiceResponse serviceResponse =
                UpdateGroupAccountStatusServiceResponse.ofSuccess();

            given(useCase.updateStatus(any())).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", groupId, accountId, request, status().isOk(),
                "success-denied", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("상태 변경 성공 여부")
            );
        }

        @Test
        @DisplayName("[success] 유효한 상태값(BLOCK)으로 변경하고 메모를 입력한다")
        void success_block() throws Exception {
            // given
            Long groupId = 1L;
            Long accountId = 1L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("BLOCK")
                .memo("부적절한 행동으로 차단되었습니다.")
                .build();

            UpdateGroupAccountStatusServiceResponse serviceResponse =
                UpdateGroupAccountStatusServiceResponse.ofSuccess();

            given(useCase.updateStatus(any())).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", groupId, accountId, request, status().isOk(),
                "success-block", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("상태 변경 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] status 를 입력핮 않은 경우 400 에러를 반환한다")
        void error_statusIsBlank() throws Exception {
            // given
            Long groupId = 1L;
            Long accountId = 1L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .memo(null)
                .build();

            // when & then
            performErrorDocument("Bearer test", groupId, accountId, request,
                status().isBadRequest(), "상태값 미입력");
        }

        @Test
        @DisplayName("[error] status가 유효하지 않은 값(INVALID)인 경우 400 에러를 반환한다")
        void error_statusIsInvalid() throws Exception {
            // given
            Long groupId = 1L;
            Long accountId = 1L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("INVALID")
                .memo(null)
                .build();

            // when & then
            performErrorDocument("Bearer test", groupId, accountId, request,
                status().isBadRequest(), "유효하지 않은 상태값 입력");
        }

        @Test
        @DisplayName("[error] 등록되지 않은 그룹 사용자인 경우 500 에러를 반환한다")
        void error_groupAccountDoesNotExist() throws Exception {
            // given
            Long groupId = 1L;
            Long accountId = 999L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("ACTIVE")
                .memo(null)
                .build();

            given(useCase.updateStatus(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_GROUP_ACCOUNT));

            // when & then
            performErrorDocument("Bearer test", groupId, accountId, request,
                status().isInternalServerError(), "등록되지 않은 그룹 사용자");
        }

        @Test
        @DisplayName("[error] 그룹 소유자의 요청이 아닌경우 500 에러를 반환한다")
        void error2() throws Exception {
            // given
            Long groupId = 1L;
            Long accountId = 999L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("ACTIVE")
                .memo(null)
                .build();

            given(useCase.updateStatus(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    ErrorCode.Business_INVALID_GROUP_OWNER));

            // when & then
            performErrorDocument("Bearer test", groupId, accountId, request,
                status().isInternalServerError(), "그룹 소유자의 요청이 아님");
        }
    }

    private void performDocument(
        String authorization,
        Long groupId,
        Long accountId,
        UpdateGroupAccountStatusRequest request,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        JsonFieldType statusType = request.status() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(patch("/groups/{groupId}/accounts/{accountId}/status", groupId, accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Group")
                    .summary("그룹 계정 상태 변경 API")
                    .description("그룹 내 PENDING 상태인 계정의 상태를 변경합니다. (ACTIVE, DENIED, BLOCK)")
                    .pathParameters(
                        parameterWithName("groupId")
                            .description("그룹 ID"),
                        parameterWithName("accountId")
                            .description("계정 ID")
                    )
                    .requestFields(
                        fieldWithPath("status").type(statusType)
                            .description(
                                "계정 상태 (ACTIVE / DENIED / BLOCK)"),
                        fieldWithPath("memo").type(JsonFieldType.STRING)
                            .description("메모 (status가 DENIED 또는 BLOCK인 경우 사용)").optional()
                    )
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        String authorization,
        Long groupId,
        Long accountId,
        UpdateGroupAccountStatusRequest request,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(authorization, groupId, accountId, request, status, identifier, "error",
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
