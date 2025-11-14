package com.odcloud.adapter.in.find_group_account_list;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odcloud.application.port.in.FindGroupAccountListUseCase;
import com.odcloud.application.service.find_group_account_list.FindGroupAccountListServiceResponse;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class FindGroupAccountListControllerTest {

    private MockMvc mockMvc;
    private FindGroupAccountListUseCase useCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        useCase = mock(FindGroupAccountListUseCase.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(new FindGroupAccountListController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Nested
    @DisplayName("[findGroupAccountList] 그룹 계정 목록 조회")
    class Describe_findGroupAccountList {

        @Test
        @DisplayName("[success] 유효한 groupId로 요청시 그룹 계정 목록을 반환한다")
        void success() throws Exception {
            // given
            String groupId = "test-group-id";
            LocalDateTime now = LocalDateTime.now();

            GroupAccount groupAccount = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(100L)
                .name("Test User")
                .nickName("testnick")
                .email("test@example.com")
                .status("APPROVED")
                .regDt(now)
                .modDt(now)
                .build();

            FindGroupAccountListServiceResponse serviceResponse =
                new FindGroupAccountListServiceResponse(List.of(groupAccount));

            given(useCase.findGroupAccountList(groupId)).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(get("/groups/{groupId}/accounts", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.groupAccounts").isArray())
                .andExpect(jsonPath("$.data.groupAccounts[0].id").value(1))
                .andExpect(jsonPath("$.data.groupAccounts[0].groupId").value(groupId))
                .andExpect(jsonPath("$.data.groupAccounts[0].accountId").value(100))
                .andExpect(jsonPath("$.data.groupAccounts[0].name").value("Test User"))
                .andExpect(jsonPath("$.data.groupAccounts[0].email").value("test@example.com"))
                .andExpect(jsonPath("$.data.groupAccounts[0].status").value("APPROVED"));
        }

        @Test
        @DisplayName("[success] 빈 목록을 반환할 수 있다")
        void success_emptyList() throws Exception {
            // given
            String groupId = "empty-group-id";
            FindGroupAccountListServiceResponse serviceResponse =
                new FindGroupAccountListServiceResponse(List.of());

            given(useCase.findGroupAccountList(groupId)).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(get("/groups/{groupId}/accounts", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.groupAccounts").isArray())
                .andExpect(jsonPath("$.data.groupAccounts").isEmpty());
        }
    }
}
