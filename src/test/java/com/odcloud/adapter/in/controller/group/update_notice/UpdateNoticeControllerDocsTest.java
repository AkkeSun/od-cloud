package com.odcloud.adapter.in.controller.group.update_notice;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.group.port.in.UpdateNoticeUseCase;
import com.odcloud.application.group.service.update_notice.UpdateNoticeServiceResponse;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class UpdateNoticeControllerDocsTest extends RestDocsSupport {

    private final UpdateNoticeUseCase useCase = mock(UpdateNoticeUseCase.class);
    private final String apiName = "공지사항 수정 API";

    @Override
    protected Object initController() {
        return new UpdateNoticeController(useCase);
    }

    @Nested
    @DisplayName("[update] 공지사항 수정 API")
    class Describe_update {

        @Test
        @DisplayName("[success] 그룹장이 공지사항을 수정한다")
        void success() throws Exception {
            // given
            Long groupId = 1L;
            Long noticeId = 1L;
            String requestBody = """
                {
                    "title": "수정된 제목",
                    "content": "수정된 내용"
                }
                """;

            UpdateNoticeServiceResponse response = UpdateNoticeServiceResponse.ofSuccess();
            given(useCase.update(any())).willReturn(response);

            // when & then
            performDocument(groupId, noticeId, requestBody, "공지사항 수정 성공", "success",
                status().isOk(),
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.result")
                    .type(JsonFieldType.BOOLEAN).description("수정 결과")
            );
        }

        @Test
        @DisplayName("[error] 그룹장이 아닌 사용자가 수정을 시도하면 500 에러를 반환한다")
        void error_notGroupOwner() throws Exception {
            // given
            Long groupId = 1L;
            Long noticeId = 1L;
            String requestBody = """
                {
                    "title": "수정된 제목",
                    "content": "수정된 내용"
                }
                """;

            given(useCase.update(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_INVALID_GROUP_OWNER));

            // when & then
            performErrorDocument(groupId, noticeId, requestBody, "그룹장이 아님",
                status().isInternalServerError());
        }

        @Test
        @DisplayName("[error] 존재하지 않는 공지사항 ID인 경우 500 에러를 반환한다")
        void error_noticeNotFound() throws Exception {
            // given
            Long groupId = 1L;
            Long noticeId = 999L;
            String requestBody = """
                {
                    "title": "수정된 제목",
                    "content": "수정된 내용"
                }
                """;

            given(useCase.update(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_NOT_FOUND_NOTICE));

            // when & then
            performErrorDocument(groupId, noticeId, requestBody, "존재하지 않는 공지사항",
                status().isInternalServerError());
        }
    }

    private void performDocument(
        Long groupId,
        Long noticeId,
        String requestBody,
        String docIdentifier,
        String responseSchema,
        ResultMatcher status,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.put(
                    "/groups/{groupId}/notices/{noticeId}", groupId, noticeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Group")
                    .summary("공지사항 수정 API")
                    .description("그룹장이 공지사항을 수정합니다.")
                    .pathParameters(
                        parameterWithName("groupId").description("그룹 ID"),
                        parameterWithName("noticeId").description("공지사항 ID")
                    )
                    .requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING)
                            .description("수정할 제목"),
                        fieldWithPath("content").type(JsonFieldType.STRING)
                            .description("수정할 내용")
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        Long groupId,
        Long noticeId,
        String requestBody,
        String identifier,
        ResultMatcher status
    ) throws Exception {
        performDocument(groupId, noticeId, requestBody, identifier, "error", status,
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
