package com.odcloud.adapter.in.controller.question.register_question;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.port.in.RegisterQuestionUseCase;
import com.odcloud.application.service.register_question.RegisterQuestionServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class RegisterQuestionControllerDocsTest extends RestDocsSupport {

    private final RegisterQuestionUseCase useCase = mock(RegisterQuestionUseCase.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected Object initController() {
        return new RegisterQuestionController(useCase);
    }

    @Test
    @DisplayName("[success] 문의 게시글을 등록한다")
    void success() throws Exception {
        // given
        RegisterQuestionRequest request = RegisterQuestionRequest.builder()
            .title("API 사용 문의")
            .content("API 사용 방법을 알고 싶습니다.")
            .build();

        RegisterQuestionServiceResponse serviceResponse = RegisterQuestionServiceResponse.ofSuccess();
        given(useCase.registerQuestion(any())).willReturn(serviceResponse);

        // when & then
        mockMvc.perform(post("/questions")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("문의 게시글 등록 API",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Question")
                    .description("문의 게시글을 등록합니다")
                    .requestHeaders(
                        headerWithName("Authorization").description("인증 토큰")
                    )
                    .requestSchema(Schema.schema("RegisterQuestionRequest"))
                    .requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING)
                            .description("문의 제목"),
                        fieldWithPath("content").type(JsonFieldType.STRING)
                            .description("문의 내용")
                    )
                    .responseSchema(Schema.schema("RegisterQuestionResponse"))
                    .responseFields(
                        fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                            .description("상태 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("상태 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("응답 데이터"),
                        fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                            .description("등록 성공 여부")
                    )
                    .build()
                )
            ));
    }
}
