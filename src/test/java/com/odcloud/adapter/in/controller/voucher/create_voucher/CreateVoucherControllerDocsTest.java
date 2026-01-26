package com.odcloud.adapter.in.controller.voucher.create_voucher;

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
import com.odcloud.application.voucher.port.in.CreateVoucherUseCase;
import com.odcloud.application.voucher.service.create_voucher.CreateVoucherServiceResponse;
import com.odcloud.domain.model.StoreType;
import com.odcloud.domain.model.VoucherType;
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

class CreateVoucherControllerDocsTest extends RestDocsSupport {

    private final CreateVoucherUseCase useCase = mock(CreateVoucherUseCase.class);
    private final String apiName = "바우처 생성 API";

    @Override
    protected Object initController() {
        return new CreateVoucherController(useCase);
    }

    @Nested
    @DisplayName("[createVoucher] 바우처를 생성하는 API")
    class Describe_createVoucher {

        @Test
        @DisplayName("[success] 바우처를 생성한다")
        void success_storagePlus() throws Exception {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_apple_123")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:30:00")
                .voucherType(VoucherType.STORAGE_PLUS)
                .groupId(10L)
                .memo("프리미엄 플랜 구매")
                .build();

            CreateVoucherServiceResponse serviceResponse = CreateVoucherServiceResponse.ofSuccess();
            given(useCase.create(any())).willReturn(serviceResponse);

            // when & then
            performDocument(request, "Bearer test", status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("바우처 생성 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API를 호출한 경우 401 코드와 에러 메시지를 응답한다")
        void error_unauthorized() throws Exception {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_apple_123")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .build();

            String authorization = "error token";
            given(useCase.create(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument(request, authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[error] storeType이 null인 경우 400 에러를 반환한다")
        void error_nullStoreType() throws Exception {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(null)
                .subscriptionKey("sub_test")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(), "storeType 미입력");
        }

        @Test
        @DisplayName("[error] storeTxId가 blank인 경우 400 에러를 반환한다")
        void error_blankStoreTxId() throws Exception {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_test")
                .orderTxId("")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(), "storeTxId 미입력");
        }

        @Test
        @DisplayName("[error] purchasedAt이 blank인 경우 400 에러를 반환한다")
        void error_blankPurchasedAt() throws Exception {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_test")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(),
                "purchasedAt 미입력");
        }

        @Test
        @DisplayName("[error] voucherType이 null인 경우 400 에러를 반환한다")
        void error_nullVoucherType() throws Exception {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_test")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(null)
                .groupId(10L)
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(),
                "voucherType 미입력");
        }

        @Test
        @DisplayName("[error] STORAGE 바우처에 groupId가 없는 경우 400 에러를 반환한다")
        void error_storageBasicWithoutGroupId() throws Exception {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_basic")
                .orderTxId("APPLE_TX_BASIC")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(null)
                .memo("베이직 플랜")
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(),
                "스토리지 바우처 groupId 미입력");
        }

        @Test
        @DisplayName("[error] STORAGE_PLUS 바우처에 groupId가 없는 경우 400 에러를 반환한다")
        void error_storagePlusWithoutGroupId() throws Exception {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("sub_plus")
                .orderTxId("GOOGLE_TX_PLUS")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_PLUS)
                .groupId(null)
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(),
                "스토리지 바우처 groupId 미입력");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 그룹 ID를 입력한 경우 500 에러를 반환한다")
        void error_groupNotFound() throws Exception {
            // given
            CreateVoucherRequest request = CreateVoucherRequest.builder()
                .storeType(StoreType.APPLE)
                .subscriptionKey("sub_test")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt("2026-01-09 10:00:00")
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(9999L)
                .build();

            given(useCase.create(any())).willThrow(
                new CustomBusinessException(ErrorCode.Business_DoesNotExists_GROUP));

            // when & then
            performErrorDocument(request, "Bearer test", status().isInternalServerError(),
                "존재하지 않는 그룹 ID 입력");
        }
    }

    private void performDocument(
        CreateVoucherRequest request,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        JsonFieldType storeTypeType =
            request.storeType() == null ? JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType subscriptionKeyType =
            request.subscriptionKey() == null ? JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType orderTxIdType =
            request.orderTxId() == null ? JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType storeProcessDtType =
            request.storeProcessDt() == null ? JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType voucherTypeType =
            request.voucherType() == null ? JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType groupIdType =
            request.groupId() == null ? JsonFieldType.NULL : JsonFieldType.NUMBER;
        JsonFieldType memoType = request.memo() == null ? JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(post("/vouchers")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Voucher")
                    .summary("바우처 생성 API")
                    .description(
                        "바우처를 생성하는 API 입니다. 스토리지 바우처(STORAGE_BASIC, STORAGE_PLUS)인 경우 groupId가 필수입니다.")
                    .requestFields(
                        fieldWithPath("storeType").type(storeTypeType)
                            .description("스토어 타입 (APPLE, GOOGLE)"),
                        fieldWithPath("subscriptionKey").type(subscriptionKeyType)
                            .description("구독 고유키"),
                        fieldWithPath("orderTxId").type(orderTxIdType)
                            .description("결제 단위 트랜젝션 아이디"),
                        fieldWithPath("storeProcessDt").type(storeProcessDtType)
                            .description("스토어 처리 일시 (yyyy-MM-dd HH:mm:ss)"),
                        fieldWithPath("voucherType").type(voucherTypeType)
                            .description(
                                "바우처 타입 (STORAGE_BASIC, STORAGE_PLUS, ADVERTISE_30, ADVERTISE_90, ADVERTISE_365)"),
                        fieldWithPath("groupId").type(groupIdType)
                            .description("그룹 ID (스토리지 바우처인 경우 필수)").optional(),
                        fieldWithPath("memo").type(memoType)
                            .description("메모").optional()
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
        CreateVoucherRequest request,
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
