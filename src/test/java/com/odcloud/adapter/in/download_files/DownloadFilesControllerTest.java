package com.odcloud.adapter.in.download_files;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odcloud.application.port.in.DownloadFileUseCase;
import com.odcloud.application.service.download_file.DownloadFileServiceResponse;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DownloadFilesControllerTest {

    private MockMvc mockMvc;
    private DownloadFileUseCase useCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        useCase = mock(DownloadFileUseCase.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(new DownloadFilesController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Nested
    @DisplayName("[downloadFiles] 복수 파일 다운로드")
    class Describe_downloadFiles {

        @Test
        @DisplayName("[success] 여러 파일을 ZIP으로 압축하여 다운로드한다")
        void success() throws Exception {
            // given
            byte[] zipContent = "ZIP content".getBytes();
            DownloadFileServiceResponse serviceResponse = DownloadFileServiceResponse.of(
                "files.zip",
                zipContent,
                "application/zip"
            );

            given(useCase.downloadFiles(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(get("/files/download")
                    .param("fileIds", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                    "attachment; filename=\"files.zip\""))
                .andExpect(header().string("Content-Type", "application/zip"))
                .andExpect(content().bytes(zipContent));
        }

        @Test
        @DisplayName("[success] 단일 파일도 쿼리 파라미터로 다운로드할 수 있다")
        void success_singleFile() throws Exception {
            // given
            byte[] zipContent = "ZIP content".getBytes();
            DownloadFileServiceResponse serviceResponse = DownloadFileServiceResponse.of(
                "files.zip",
                zipContent,
                "application/zip"
            );

            given(useCase.downloadFiles(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(get("/files/download")
                    .param("fileIds", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                    "attachment; filename=\"files.zip\""))
                .andExpect(header().string("Content-Type", "application/zip"))
                .andExpect(content().bytes(zipContent));
        }
    }
}
