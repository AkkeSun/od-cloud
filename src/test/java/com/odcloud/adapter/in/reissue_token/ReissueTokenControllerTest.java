package com.odcloud.adapter.in.reissue_token;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odcloud.application.port.in.ReissueTokenUseCase;
import com.odcloud.application.service.reissue_token.ReissueTokenServiceResponse;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ReissueTokenControllerTest {

    private MockMvc mockMvc;
    private ReissueTokenUseCase useCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        useCase = mock(ReissueTokenUseCase.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(new ReissueTokenController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Nested
    @DisplayName("[update] 토큰 재발급")
    class Describe_update {

        @Test
        @DisplayName("[success] 유효한 refreshToken으로 토큰을 재발급한다")
        void success() throws Exception {
            // given
            String refreshToken = "valid-refresh-token";
            ReissueTokenServiceResponse serviceResponse = new ReissueTokenServiceResponse(
                "new-access-token-123",
                "new-refresh-token-456"
            );

            given(useCase.reissueToken(refreshToken)).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(put("/auth")
                    .header("refreshToken", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token-123"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token-456"));
        }

        @Test
        @DisplayName("[failure] refreshToken 헤더가 없는 경우 400 에러를 반환한다")
        void failure_missingHeader() throws Exception {
            // when & then
            mockMvc.perform(put("/auth"))
                .andExpect(status().isBadRequest());
        }
    }
}
