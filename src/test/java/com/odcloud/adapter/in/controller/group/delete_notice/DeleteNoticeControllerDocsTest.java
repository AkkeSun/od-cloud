package com.odcloud.adapter.in.controller.group.delete_notice;

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
import com.odcloud.application.port.in.DeleteNoticeUseCase;
import com.odcloud.application.service.delete_notice.DeleteNoticeServiceResponse;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class DeleteNoticeControllerDocsTest extends RestDocsSupport {

    private final DeleteNoticeUseCase useCase = mock(DeleteNoticeUseCase.class);
    private final String apiName = "공지사항 삭제 API";

    @Override
    protected Object initController() {
        return new DeleteNoticeController(useCase);
    }

    @Nested
    @DisplayName("[delete] 공지사항 삭제 API")
    class Describe_delete {

        @Test
        @DisplayName("[success] 그룹장이 공지사항을 삭제한다")
        void success() throws Exception {
            // given
            String groupId = "test-group";
            Long noticeId = 1L;

            DeleteNoticeServiceResponse response = DeleteNoticeServiceResponse.ofSuccess();
            given(useCase.delete(any())).willReturn(response);

            // when & then
            performDocument(groupId, noticeId, "공지사항 삭제 성공", "success", status().isOk(),
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.result")
                    .type(JsonFieldType.BOOLEAN).description("삭제 결과")
            );
        }

        @Test
        @DisplayName("[error] 그룹장이 아닌 사용자가 삭제를 시도하면 500 에러를 반환한다")
        void error_notGroupOwner() throws Exception {
            // given
            String groupId = "test-group";
            Long noticeId = 1L;

            given(useCase.delete(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_INVALID_GROUP_OWNER));

            // when & then
            performErrorDocument(groupId, noticeId, "그룹장이 아님",
                status().isInternalServerError());
        }

        @Test
        @DisplayName("[error] 존재하지 않는 공지사항 ID인 경우 500 에러를 반환한다")
        void error_noticeNotFound() throws Exception {
            // given
            String groupId = "test-group";
            Long noticeId = 999L;

            given(useCase.delete(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_NOT_FOUND_NOTICE));

            // when & then
            performErrorDocument(groupId, noticeId, "존재하지 않는 공지사항",
                status().isInternalServerError());
        }
    }

    private void performDocument(
        String groupId,
        Long noticeId,
        String docIdentifier,
        String responseSchema,
        ResultMatcher status,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete(
                "/groups/{groupId}/notices/{noticeId}", groupId, noticeId))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Group")
                    .summary("공지사항 삭제 API")
                    .description("그룹장이 공지사항을 삭제합니다.")
                    .pathParameters(
                        parameterWithName("groupId").description("그룹 ID"),
                        parameterWithName("noticeId").description("공지사항 ID")
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        String groupId,
        Long noticeId,
        String identifier,
        ResultMatcher status
    ) throws Exception {
        performDocument(groupId, noticeId, identifier, "error", status,
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
