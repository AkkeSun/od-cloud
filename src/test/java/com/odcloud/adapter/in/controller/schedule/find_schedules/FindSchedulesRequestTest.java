package com.odcloud.adapter.in.controller.schedule.find_schedules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.odcloud.application.schedule.port.in.command.FindSchedulesCommand;
import com.odcloud.domain.model.Account;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindSchedulesRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] FindSchedulesRequest를 Command로 변환한다")
        void success() {
            // given
            FindSchedulesRequest request = FindSchedulesRequest.builder()
                .baseDate("2025-01-15")
                .filterType("PRIVATE")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("test@example.com");

            // when
            FindSchedulesCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.baseDate()).isEqualTo(LocalDate.of(2025, 1, 15));
            assertThat(command.filterType()).isEqualTo("PRIVATE");
        }
    }
}
