package com.odcloud.adapter.in.controller.group.update_notice;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record UpdateNoticeRequest(
    @NotBlank(message = "제목은 필수값 입니다")
    String title,
    @NotBlank(message = "내용은 필수값 입니다")
    String content
) {

}
