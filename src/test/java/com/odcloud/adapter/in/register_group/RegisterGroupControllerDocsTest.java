package com.odcloud.adapter.in.register_group;

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
import com.odcloud.application.port.in.RegisterGroupUseCase;
import com.odcloud.application.service.register_group.RegisterGroupServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class RegisterGroupControllerDocsTest extends RestDocsSupport {

    private final RegisterGroupUseCase useCase = mock(RegisterGroupUseCase.class);
    private final String apiName = "그룹 등록 API";

    @Override
    protected Object initController() {
        return new RegisterGroupController(useCase);
    }


    @Nested
    @DisplayName("[register] 그룹을 등록하는 API")
    class Describe_register {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("group-123")
                .description("테스트 그룹")
                .build();
            String authorization = "error token";
            given(useCase.register(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument(request, authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 유효한 정보로 그룹을 등록한다")
        void success() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("group-123")
                .description("테스트 그룹")
                .build();

            RegisterGroupServiceResponse serviceResponse =
                RegisterGroupServiceResponse.ofSuccess();

            given(useCase.register(any())).willReturn(serviceResponse);

            // when & then
            performDocument(request, "Bearer test", status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("등록 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 그룹 아이디를 입력하지 않은 경우 400 에러를 반환한다")
        void error_idIsBlank() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .description("테스트 그룹")
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(), "그룹 아이디 미입력");
        }

        @Test
        @DisplayName("[error] 그룹 설명이 빈 문자열인 경우 400 에러를 반환한다")
        void error_descriptionIsBlank() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("group-123")
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(), "그룹 설명 미입력");
        }

        @Test
        @DisplayName("[error] 이미 등록된 그룹 ID인 경우 500 에러를 반환한다")
        void error_savedGroup() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("existing-group-123")
                .description("테스트 그룹")
                .build();

            given(useCase.register(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_GROUP));

            // when & then
            performErrorDocument(request, "Bearer test", status().isInternalServerError(),
                "이미 등록된 그룹 정보 입력");
        }
    }

    private void performDocument(
        RegisterGroupRequest request,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        JsonFieldType idType = request.id() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType descriptionType = request.description() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Account")
                    .summary("그룹 등록 API")
                    .description("새로운 그룹을 생성하는 API 입니다")
                    .requestFields(
                        fieldWithPath("id").type(idType)
                            .description("그룹 ID"),
                        fieldWithPath("description").type(descriptionType)
                            .description("그룹 설명")
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
        RegisterGroupRequest request,
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(request, authorization, status, identifier, "error",
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
