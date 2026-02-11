package com.odcloud.adapter.in.controller.group.update_group;

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
import com.odcloud.application.group.port.in.UpdateGroupUseCase;
import com.odcloud.application.group.service.update_group.UpdateGroupServiceResponse;
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

class UpdateGroupControllerDocsTest extends RestDocsSupport {

    private final UpdateGroupUseCase useCase = mock(UpdateGroupUseCase.class);
    private final String apiName = "그룹 수정 API";

    @Override
    protected Object initController() {
        return new UpdateGroupController(useCase);
    }

    @Nested
    @DisplayName("[update] 그룹 정보를 수정하는 API")
    class Describe_update {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API를 호출한 경우 401 코드와 에러 메시지를 응답한다")
        void error_unauthorized() throws Exception {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .name("New Group Name")
                .build();
            String authorization = "error token";
            given(useCase.update(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when & then
            performErrorDocument(1L, request, authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] ownerEmail만 변경한다")
        void success_updateOwnerEmail() throws Exception {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail("newowner@example.com")
                .build();

            UpdateGroupServiceResponse serviceResponse = new UpdateGroupServiceResponse(
                Boolean.TRUE
            );

            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument(1L, request, "Bearer test", status().isOk(),
                "ownerEmail만 변경 성공", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[success] name만 변경한다")
        void success_updateName() throws Exception {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .name("New Group Name")
                .build();

            UpdateGroupServiceResponse serviceResponse = new UpdateGroupServiceResponse(
                Boolean.TRUE
            );

            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument(1L, request, "Bearer test", status().isOk(),
                "name만 변경 성공", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[success] ownerEmail과 name 모두 변경한다")
        void success_updateBoth() throws Exception {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail("newowner@example.com")
                .name("New Group Name")
                .build();

            UpdateGroupServiceResponse serviceResponse = new UpdateGroupServiceResponse(
                Boolean.TRUE
            );

            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument(1L, request, "Bearer test", status().isOk(),
                "ownerEmail과 name 모두 변경 성공", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 그룹 소유자가 아닌 경우 500 에러를 반환한다")
        void error_notGroupOwner() throws Exception {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .name("New Group Name")
                .build();

            given(useCase.update(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_INVALID_GROUP_OWNER));

            // when & then
            performErrorDocument(1L, request, "Bearer test",
                status().isInternalServerError(), "그룹 소유자가 아님");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 이메일로 변경 시도 시 500 에러를 반환한다")
        void error_ownerEmailNotFound() throws Exception {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail("nonexistent@example.com")
                .build();

            given(useCase.update(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_NOT_FOUND_ACCOUNT));

            // when & then
            performErrorDocument(1L, request, "Bearer test",
                status().isInternalServerError(), "존재하지 않는 이메일");
        }

        @Test
        @DisplayName("[error] 새 소유자가 이미 3개 그룹 소유 시 500 에러를 반환한다")
        void error_groupLimitExceeded() throws Exception {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail("newowner@example.com")
                .build();

            given(useCase.update(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_GROUP_LIMIT_EXCEEDED));

            // when & then
            performErrorDocument(1L, request, "Bearer test",
                status().isInternalServerError(), "그룹 개수 제한 초과");
        }

        @Test
        @DisplayName("[error] 이미 존재하는 그룹명으로 변경 시 500 에러를 반환한다")
        void error_duplicateGroupName() throws Exception {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .name("Existing Group")
                .build();

            given(useCase.update(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_SAVED_GROUP));

            // when & then
            performErrorDocument(1L, request, "Bearer test",
                status().isInternalServerError(), "이미 존재하는 그룹명");
        }
    }

    private void performDocument(
        Long groupId,
        UpdateGroupRequest request,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        JsonFieldType ownerEmailType = request.getOwnerEmail() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType nameType = request.getName() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(patch("/groups/{groupId}", groupId)
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
                    .summary("그룹 정보 수정 API")
                    .description("그룹의 소유자(ownerEmail) 또는 그룹명(name)을 수정하는 API입니다. 두 값 모두 선택적(optional)이며, 변경하고자 하는 값만 전달하면 됩니다.")
                    .pathParameters(
                        parameterWithName("groupId").description("수정할 그룹 ID")
                    )
                    .requestFields(
                        fieldWithPath("ownerEmail").type(ownerEmailType)
                            .description("변경할 소유자 이메일 (선택)").optional(),
                        fieldWithPath("name").type(nameType)
                            .description("변경할 그룹명 (선택)").optional()
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
        Long groupId,
        UpdateGroupRequest request,
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(groupId, request, authorization, status, identifier, "error",
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
