package com.odcloud.adapter.in.register_group;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odcloud.application.port.in.RegisterGroupUseCase;
import com.odcloud.application.service.register_group.RegisterGroupServiceResponse;
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

class RegisterGroupControllerTest {

    private MockMvc mockMvc;
    private RegisterGroupUseCase useCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        useCase = mock(RegisterGroupUseCase.class);
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
                    .email("test@example.com")
                    .name("Test User")
                    .nickname("testnick")
                    .build();
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(new RegisterGroupController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setCustomArgumentResolvers(loginAccountResolver)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Nested
    @DisplayName("[register] 그룹 등록")
    class Describe_register {

        @Test
        @DisplayName("[success] 유효한 요청으로 그룹을 등록한다")
        void success() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("test-group-id")
                .description("Test Group Description")
                .build();

            RegisterGroupServiceResponse serviceResponse = new RegisterGroupServiceResponse(true);
            given(useCase.register(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(post("/groups")
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.result").value(true));
        }

        @Test
        @DisplayName("[failure] id가 null인 경우 400 에러를 반환한다")
        void failure_idIsNull() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id(null)
                .description("Test Group Description")
                .build();

            // when & then
            mockMvc.perform(post("/groups")
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] description이 null인 경우 400 에러를 반환한다")
        void failure_descriptionIsNull() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("test-group-id")
                .description(null)
                .build();

            // when & then
            mockMvc.perform(post("/groups")
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] id가 빈 문자열인 경우 400 에러를 반환한다")
        void failure_idIsBlank() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("")
                .description("Test Group Description")
                .build();

            // when & then
            mockMvc.perform(post("/groups")
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] description이 빈 문자열인 경우 400 에러를 반환한다")
        void failure_descriptionIsBlank() throws Exception {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("test-group-id")
                .description("")
                .build();

            // when & then
            mockMvc.perform(post("/groups")
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }
    }
}
