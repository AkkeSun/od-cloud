package com.odcloud.adapter.in.update_group_account_status;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odcloud.application.port.in.UpdateGroupAccountStatusUseCase;
import com.odcloud.application.service.update_group_account_status.UpdateGroupAccountStatusServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

class UpdateGroupAccountStatusControllerTest {

    private MockMvc mockMvc;
    private UpdateGroupAccountStatusUseCase useCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        useCase = mock(UpdateGroupAccountStatusUseCase.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Mock LoginAccountResolver
        HandlerMethodArgumentResolver loginAccountResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().equals(Account.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                ModelAndViewContainer mavContainer,
                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return Account.builder()
                    .id(1L)
                    .email("owner@example.com")
                    .name("Group Owner")
                    .nickname("ownernick")
                    .build();
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(new UpdateGroupAccountStatusController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setCustomArgumentResolvers(loginAccountResolver)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Nested
    @DisplayName("[updateStatus] 그룹 계정 상태 변경")
    class Describe_updateStatus {

        @Test
        @DisplayName("[success] 유효한 요청으로 그룹 계정 상태를 변경한다")
        void success() throws Exception {
            // given
            String groupId = "test-group-id";
            Long accountId = 100L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("APPROVED")
                .build();

            UpdateGroupAccountStatusServiceResponse serviceResponse =
                new UpdateGroupAccountStatusServiceResponse(true);
            given(useCase.updateStatus(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(patch("/groups/{groupId}/accounts/{accountId}/status", groupId,
                    accountId)
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.result").value(true));
        }

        @Test
        @DisplayName("[success] PENDING 상태로 변경할 수 있다")
        void success_pendingStatus() throws Exception {
            // given
            String groupId = "test-group-id";
            Long accountId = 100L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("PENDING")
                .build();

            UpdateGroupAccountStatusServiceResponse serviceResponse =
                new UpdateGroupAccountStatusServiceResponse(true);
            given(useCase.updateStatus(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(patch("/groups/{groupId}/accounts/{accountId}/status", groupId,
                    accountId)
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.result").value(true));
        }

        @Test
        @DisplayName("[success] BLOCK 상태로 변경할 수 있다")
        void success_blockStatus() throws Exception {
            // given
            String groupId = "test-group-id";
            Long accountId = 100L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("BLOCK")
                .build();

            UpdateGroupAccountStatusServiceResponse serviceResponse =
                new UpdateGroupAccountStatusServiceResponse(true);
            given(useCase.updateStatus(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(patch("/groups/{groupId}/accounts/{accountId}/status", groupId,
                    accountId)
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.result").value(true));
        }

        @Test
        @DisplayName("[failure] status가 null인 경우 400 에러를 반환한다")
        void failure_statusIsNull() throws Exception {
            // given
            String groupId = "test-group-id";
            Long accountId = 100L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status(null)
                .build();

            // when & then
            mockMvc.perform(patch("/groups/{groupId}/accounts/{accountId}/status", groupId,
                    accountId)
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] status가 빈 문자열인 경우 400 에러를 반환한다")
        void failure_statusIsBlank() throws Exception {
            // given
            String groupId = "test-group-id";
            Long accountId = 100L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("")
                .build();

            // when & then
            mockMvc.perform(patch("/groups/{groupId}/accounts/{accountId}/status", groupId,
                    accountId)
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] status가 유효하지 않은 값인 경우 400 에러를 반환한다")
        void failure_statusIsInvalid() throws Exception {
            // given
            String groupId = "test-group-id";
            Long accountId = 100L;
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("INVALID_STATUS")
                .build();

            // when & then
            mockMvc.perform(patch("/groups/{groupId}/accounts/{accountId}/status", groupId,
                    accountId)
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }
    }
}
