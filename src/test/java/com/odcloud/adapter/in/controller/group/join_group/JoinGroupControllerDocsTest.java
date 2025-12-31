package com.odcloud.adapter.in.controller.group.join_group;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.odcloud.application.group.port.in.JoinGroupUseCase;
import com.odcloud.application.group.service.join_group.JoinGroupServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class JoinGroupControllerDocsTest extends RestDocsSupport {

    private final JoinGroupUseCase useCase = mock(JoinGroupUseCase.class);
    private final String apiName = "그룹 가입 요청 API";

    @Override
    protected Object initController() {
        return new JoinGroupController(useCase);
    }

    @Nested
    @DisplayName("[join] 그룹에 가입 요청하는 API")
    class Describe_join {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            String groupId = "test-group";
            String authorization = "error token";
            given(useCase.join(anyString(), any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performDocument(groupId, authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력", "error",
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

        @Test
        @DisplayName("[success] 유효한 정보로 그룹 가입 요청을 생성한다")
        void success() throws Exception {
            // given
            String groupId = "test-group";
            JoinGroupServiceResponse serviceResponse = JoinGroupServiceResponse.ofSuccess();
            given(useCase.join(anyString(), any())).willReturn(serviceResponse);

            // when & then
            performDocument(groupId, "Bearer test", status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("가입 요청 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 존재하지 않는 그룹에 가입 요청하면 500 에러를 반환한다")
        void error_nonExistentGroup() throws Exception {
            // given
            String groupId = "non-existent-group";
            given(useCase.join(anyString(), any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_DoesNotExists_GROUP));

            // when & then
            performDocument(groupId, "Bearer test", status().isInternalServerError(),
                "존재하지 않는 그룹 입력", "error",
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

    private void performDocument(
        String groupId,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        org.springframework.restdocs.payload.FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(post("/groups/{groupId}/join", groupId)
                .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Group")
                    .summary("그룹 가입 요청 API")
                    .description("그룹에 가입 요청을 생성하는 API 입니다. GroupAccount 상태가 PENDING으로 등록됩니다.")
                    .pathParameters(
                        parameterWithName("groupId").description("가입 요청할 그룹 ID")
                    )
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }
}
