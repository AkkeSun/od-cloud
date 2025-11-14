package com.odcloud.adapter.in.register_file;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odcloud.application.port.in.RegisterFileUseCase;
import com.odcloud.application.service.register_file.RegisterFileServiceResponse;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RegisterFileControllerTest {

    private MockMvc mockMvc;
    private RegisterFileUseCase useCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        useCase = mock(RegisterFileUseCase.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(new RegisterFileController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Nested
    @DisplayName("[register] 파일 등록")
    class Describe_register {

        @Test
        @DisplayName("[success] 유효한 파일로 등록한다")
        void success() throws Exception {
            // given
            Long folderId = 1L;
            MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "test1.txt",
                "text/plain",
                "test file content 1".getBytes()
            );
            MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "test2.txt",
                "text/plain",
                "test file content 2".getBytes()
            );

            RegisterFileServiceResponse serviceResponse = new RegisterFileServiceResponse(true);
            given(useCase.register(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(multipart("/folders/{folderId}/files", folderId)
                    .file(file1)
                    .file(file2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.result").value(true));
        }

        @Test
        @DisplayName("[success] 단일 파일도 등록할 수 있다")
        void success_singleFile() throws Exception {
            // given
            Long folderId = 1L;
            MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.txt",
                "text/plain",
                "test file content".getBytes()
            );

            RegisterFileServiceResponse serviceResponse = new RegisterFileServiceResponse(true);
            given(useCase.register(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(multipart("/folders/{folderId}/files", folderId)
                    .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.result").value(true));
        }

        @Test
        @DisplayName("[failure] 파일이 없는 경우 400 에러를 반환한다")
        void failure_noFiles() throws Exception {
            // given
            Long folderId = 1L;

            // when & then
            mockMvc.perform(multipart("/folders/{folderId}/files", folderId))
                .andExpect(status().isBadRequest());
        }
    }
}
