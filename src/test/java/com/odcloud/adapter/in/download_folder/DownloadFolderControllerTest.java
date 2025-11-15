package com.odcloud.adapter.in.download_folder;

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

class DownloadFolderControllerTest {

    private MockMvc mockMvc;
    private DownloadFileUseCase useCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        useCase = mock(DownloadFileUseCase.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(new DownloadFolderController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Nested
    @DisplayName("[downloadFolder] 폴더 다운로드")
    class Describe_downloadFolder {

        @Test
        @DisplayName("[success] 폴더 내 모든 파일을 ZIP으로 압축하여 다운로드한다")
        void success() throws Exception {
            // given
            Long folderId = 1L;
            byte[] zipContent = "ZIP folder content".getBytes();
            DownloadFileServiceResponse serviceResponse = DownloadFileServiceResponse.of(
                "MyFolder.zip",
                zipContent,
                "application/zip"
            );

            given(useCase.downloadFolder(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(get("/folders/{folderId}/download", folderId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                    "attachment; filename=\"MyFolder.zip\""))
                .andExpect(header().string("Content-Type", "application/zip"))
                .andExpect(content().bytes(zipContent));
        }

        @Test
        @DisplayName("[success] 한글 이름의 폴더도 다운로드할 수 있다")
        void success_koreanFolderName() throws Exception {
            // given
            Long folderId = 2L;
            byte[] zipContent = "ZIP content".getBytes();
            DownloadFileServiceResponse serviceResponse = DownloadFileServiceResponse.of(
                "내폴더.zip",
                zipContent,
                "application/zip"
            );

            given(useCase.downloadFolder(any())).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(get("/folders/{folderId}/download", folderId))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Type", "application/zip"))
                .andExpect(content().bytes(zipContent));
        }
    }
}
