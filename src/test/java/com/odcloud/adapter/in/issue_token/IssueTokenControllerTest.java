package com.odcloud.adapter.in.issue_token;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odcloud.application.port.in.IssueTokenUseCase;
import com.odcloud.application.service.issue_token.IssueTokenServiceResponse;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class IssueTokenControllerTest {

    private MockMvc mockMvc;
    private IssueTokenUseCase useCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        useCase = mock(IssueTokenUseCase.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(new IssueTokenController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Nested
    @DisplayName("[issue] 토큰 발급")
    class Describe_issue {

        @Test
        @DisplayName("[success] 유효한 구글 인증으로 토큰을 발급한다")
        void success() throws Exception {
            // given
            String googleAuthorization = "Bearer google-access-token";
            IssueTokenServiceResponse serviceResponse = new IssueTokenServiceResponse(
                "access-token-123",
                "refresh-token-456"
            );

            given(useCase.issue(googleAuthorization)).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(post("/auth")
                    .header("googleAuthorization", googleAuthorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token-456"));
        }

        @Test
        @DisplayName("[failure] googleAuthorization 헤더가 없는 경우 400 에러를 반환한다")
        void failure_missingHeader() throws Exception {
            // when & then
            mockMvc.perform(post("/auth"))
                .andExpect(status().isBadRequest());
        }
    }
}
