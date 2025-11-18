package com.odcloud.adapter.in.find_files;

import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.validation.Contains;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindFilesRequest {

    @Contains(
        values = {"NAME_ASC", "NAME_DESC", "REG_DT_ASC", "REG_DT_DESC"},
        message = "유효하지 않은 정렬 타입 입니다")
    private String sortType;

    private Long folderId;

    private String groupId;

    private String keyword;

    public FindFilesCommand toCommand(Account account) {
        return FindFilesCommand.builder()
            .account(account)
            .sortType(sortType)
            .folderId(folderId)
            .groupId(groupId)
            .keyword(keyword)
            .build();
    }
}
