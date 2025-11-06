package com.odcloud.adapter.in.register_group;

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
import com.odcloud.application.port.in.RegisterGroupUseCase;
import com.odcloud.application.service.register_group.RegisterGroupServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class RegisterGroupControllerDocsTest extends RestDocsSupport {

    private final RegisterGroupUseCase registerGroupUseCase = mock(RegisterGroupUseCase.class);
    private final String apiName = "registerGroup";

    @Override
    protected Object initController() {
        return new RegisterGroupController(registerGroupUseCase);
    }

    @Nested
    @DisplayName("[registerGroup] 그룹을 등록하는 API")
    class Describe_registerGroup {

        @Test
        @DisplayName("[error] id가 null일 때 400 에러를 응답한다.")
        void error_id_null() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id(null)
                .description("테스트 그룹")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "id null",
                "그룹 아이디 미입력");
        }

        @Test
        @DisplayName("[error] id가 빈 문자열일 때 400 에러를 응답한다.")
        void error_id_blank() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("   ")
                .description("테스트 그룹")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "id blank",
                "그룹 아이디 미입력");
        }

        @Test
        @DisplayName("[error] description이 null일 때 400 에러를 응답한다.")
        void error_description_null() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("test-group")
                .description(null)
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "description null",
                "그룹 설명 미입력");
        }

        @Test
        @DisplayName("[error] description이 빈 문자열일 때 400 에러를 응답한다.")
        void error_description_blank() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("test-group")
                .description("   ")
                .build();

            // when, then
            performErrorDocument(request, status().isBadRequest(), "description blank",
                "그룹 설명 미입력");
        }

        @Test
        @DisplayName("[error] 이미 등록된 그룹일 때 500 에러를 응답한다.")
        void error_duplicateGroup() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("test-group")
                .description("테스트 그룹")
                .build();

            given(registerGroupUseCase.register(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_GROUP));

            // when, then
            performErrorDocument(request, status().isInternalServerError(),
                "duplicate group", "이미 등록된 그룹");
        }

        @Test
        @DisplayName("[success] 그룹 등록에 성공한다.")
        void success() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("test-group")
                .description("테스트 그룹")
                .build();

            RegisterGroupServiceResponse serviceResponse = RegisterGroupServiceResponse.ofSuccess();

            given(registerGroupUseCase.register(any())).willReturn(serviceResponse);

            // when, then
            performSuccessDocument(request, status().isOk(), "success",
                "그룹 등록 성공",
                fieldWithPath("result").type(JsonFieldType.BOOLEAN)
                    .description("등록 성공 여부")
            );
        }
    }

    private void performErrorDocument(RegisterGroupRequest request,
        ResultMatcher status, String docIdentifier, String responseMessage) throws Exception {

        JsonFieldType idType = request.id() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType descriptionType = request.description() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(post("/accounts/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Group")
                    .summary("그룹 등록 API")
                    .description("새로운 그룹을 등록합니다.")
                    .requestFields(
                        fieldWithPath("id").type(idType)
                            .description("그룹 아이디 (필수)"),
                        fieldWithPath("description").type(descriptionType)
                            .description("그룹 설명 (필수)")
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

    private void performSuccessDocument(RegisterGroupRequest request,
        ResultMatcher status, String docIdentifier, String responseSchema,
        FieldDescriptor... dataFields) throws Exception {

        JsonFieldType idType = request.id() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType descriptionType = request.description() == null ?
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

        mockMvc.perform(post("/accounts/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Account")
                    .summary("그룹 등록 API")
                    .description("새로운 그룹을 등록합니다.")
                    .requestFields(
                        fieldWithPath("id").type(idType)
                            .description("그룹 아이디 (필수)"),
                        fieldWithPath("description").type(descriptionType)
                            .description("그룹 설명 (필수)")
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }
}
