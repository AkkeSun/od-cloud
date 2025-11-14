package com.odcloud.adapter.in.register_account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odcloud.application.port.in.RegisterAccountUseCase;
import com.odcloud.application.service.register_account.RegisterAccountServiceResponse;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RegisterAccountControllerTest {

    private MockMvc mockMvc;
    private RegisterAccountUseCase useCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        useCase = mock(RegisterAccountUseCase.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(new RegisterAccountController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Nested
    @DisplayName("[registerAccount] 계정 등록")
    class Describe_registerAccount {

        @Test
        @DisplayName("[success] 유효한 요청으로 계정을 등록한다")
        void success() throws Exception {
            // given
            String googleAuthorization = "Bearer google-access-token";
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("Test User")
                .groupId("test-group-id")
                .build();

            RegisterAccountServiceResponse serviceResponse = new RegisterAccountServiceResponse(true);
            given(useCase.register(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(post("/accounts")
                    .header("googleAuthorization", googleAuthorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.result").value(true));
        }

        @Test
        @DisplayName("[failure] name이 null인 경우 400 에러를 반환한다")
        void failure_nameIsNull() throws Exception {
            // given
            String googleAuthorization = "Bearer google-access-token";
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name(null)
                .groupId("test-group-id")
                .build();

            // when & then
            mockMvc.perform(post("/accounts")
                    .header("googleAuthorization", googleAuthorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] groupId가 null인 경우 400 에러를 반환한다")
        void failure_groupIdIsNull() throws Exception {
            // given
            String googleAuthorization = "Bearer google-access-token";
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("Test User")
                .groupId(null)
                .build();

            // when & then
            mockMvc.perform(post("/accounts")
                    .header("googleAuthorization", googleAuthorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] name이 빈 문자열인 경우 400 에러를 반환한다")
        void failure_nameIsBlank() throws Exception {
            // given
            String googleAuthorization = "Bearer google-access-token";
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("")
                .groupId("test-group-id")
                .build();

            // when & then
            mockMvc.perform(post("/accounts")
                    .header("googleAuthorization", googleAuthorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }
    }
}
