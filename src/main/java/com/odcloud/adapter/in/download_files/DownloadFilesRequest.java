package com.odcloud.adapter.in.download_files;

import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadFilesRequest {

    @NotEmpty(message = "파일 아이디 목록은 필수값 입니다")
    List<Long> fileIds;

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
