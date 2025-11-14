package com.odcloud.adapter.in.callback_google_oauth2;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odcloud.application.port.in.CallbackGoogleOAuth2UseCase;
import com.odcloud.application.service.callback_google_oauth2.CallbackGoogleOAuth2ServiceResponse;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CallbackGoogleOAuth2ControllerTest {

    private MockMvc mockMvc;
    private CallbackGoogleOAuth2UseCase useCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        useCase = mock(CallbackGoogleOAuth2UseCase.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(new CallbackGoogleOAuth2Controller(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Nested
    @DisplayName("[callback] 구글 OAuth2 콜백 처리")
    class Describe_callback {

        @Test
        @DisplayName("[success] 유효한 code로 요청시 구글 액세스 토큰을 반환한다")
        void success() throws Exception {
            // given
            String code = "valid-google-auth-code";
            CallbackGoogleOAuth2ServiceResponse serviceResponse =
                new CallbackGoogleOAuth2ServiceResponse("google-access-token-123");

            given(useCase.callback(code)).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(get("/auth/google")
                    .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.googleAccessToken").value("google-access-token-123"));
        }

        @Test
        @DisplayName("[failure] code가 null인 경우 400 에러를 반환한다")
        void failure_codeIsNull() throws Exception {
            // when & then
            mockMvc.perform(get("/auth/google"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] code가 빈 문자열인 경우 400 에러를 반환한다")
        void failure_codeIsBlank() throws Exception {
            // when & then
            mockMvc.perform(get("/auth/google")
                    .param("code", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }
    }
}
