package com.odcloud.adapter.in.controller.account.delete_account;

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
import com.odcloud.application.account.port.in.DeleteAccountUseCase;
import com.odcloud.application.account.service.delete_account.DeleteAccountServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class DeleteAccountControllerDocsTest extends RestDocsSupport {

    private final DeleteAccountUseCase useCase = mock(DeleteAccountUseCase.class);
    private final String apiName = "계정 삭제 API";

    @Override
    protected Object initController() {
        return new DeleteAccountController(useCase);
    }

    @Nested
    @DisplayName("[delete] 계정을 삭제하는 API")
    class Describe_delete {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API를 호출한 경우 401 코드와 에러 메시지를 응답한다")
        void error_unauthorized() throws Exception {
            // given
            String authorization = "error token";
            given(useCase.delete(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when & then
            performErrorDocument(authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 계정을 삭제한다")
        void success_deleteAccount() throws Exception {
            // given
            DeleteAccountServiceResponse serviceResponse = DeleteAccountServiceResponse.ofSuccess();
            given(useCase.delete(any())).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", status().isOk(),
                "계정 삭제 성공", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("삭제 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 존재하지 않는 계정인 경우 500 에러를 반환한다")
        void error_accountNotFound() throws Exception {
            // given
            given(useCase.delete(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_NOT_FOUND_ACCOUNT));

            // when & then
            performErrorDocument("Bearer test",
                status().isInternalServerError(), "존재하지 않는 계정");
        }
    }

    private void performDocument(
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(delete("/accounts")
                .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Account")
                    .summary("계정 삭제 API")
                    .description("계정과 관련된 모든 디바이스, 개인 스케줄, 소유 그룹(파일/폴더/그룹 스케줄 포함)을 삭제하는 API입니다.")
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .responseFields(responseFields)
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(authorization, status, identifier, "error",
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
