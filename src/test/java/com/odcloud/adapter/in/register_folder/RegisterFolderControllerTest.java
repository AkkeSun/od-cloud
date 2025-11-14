package com.odcloud.adapter.in.register_folder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odcloud.application.port.in.RegisterFolderUseCase;
import com.odcloud.application.service.register_folder.RegisterFolderServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import com.odcloud.resolver.LoginAccountResolver;
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

class RegisterFolderControllerTest {

    private MockMvc mockMvc;
    private RegisterFolderUseCase useCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        useCase = mock(RegisterFolderUseCase.class);
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

        mockMvc = MockMvcBuilders.standaloneSetup(new RegisterFolderController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setCustomArgumentResolvers(loginAccountResolver)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Nested
    @DisplayName("[createFolder] 폴더 생성")
    class Describe_createFolder {

        @Test
        @DisplayName("[success] 유효한 요청으로 폴더를 생성한다")
        void success() throws Exception {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(1L)
                .groupId("test-group-id")
                .name("Test Folder")
                .accessLevel("PUBLIC")
                .build();

            RegisterFolderServiceResponse serviceResponse = new RegisterFolderServiceResponse(true);
            given(useCase.createFolder(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(post("/folders")
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.result").value(true));
        }

        @Test
        @DisplayName("[failure] parentId가 null인 경우 400 에러를 반환한다")
        void failure_parentIdIsNull() throws Exception {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(null)
                .groupId("test-group-id")
                .name("Test Folder")
                .accessLevel("PUBLIC")
                .build();

            // when & then
            mockMvc.perform(post("/folders")
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] groupId가 null인 경우 400 에러를 반환한다")
        void failure_groupIdIsNull() throws Exception {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(1L)
                .groupId(null)
                .name("Test Folder")
                .accessLevel("PUBLIC")
                .build();

            // when & then
            mockMvc.perform(post("/folders")
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] name이 null인 경우 400 에러를 반환한다")
        void failure_nameIsNull() throws Exception {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(1L)
                .groupId("test-group-id")
                .name(null)
                .accessLevel("PUBLIC")
                .build();

            // when & then
            mockMvc.perform(post("/folders")
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] accessLevel이 null인 경우 400 에러를 반환한다")
        void failure_accessLevelIsNull() throws Exception {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(1L)
                .groupId("test-group-id")
                .name("Test Folder")
                .accessLevel(null)
                .build();

            // when & then
            mockMvc.perform(post("/folders")
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }

        @Test
        @DisplayName("[failure] accessLevel이 유효하지 않은 값인 경우 400 에러를 반환한다")
        void failure_accessLevelIsInvalid() throws Exception {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(1L)
                .groupId("test-group-id")
                .name("Test Folder")
                .accessLevel("INVALID")
                .build();

            // when & then
            mockMvc.perform(post("/folders")
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
        }
    }
}
