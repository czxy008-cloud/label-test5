package com.meeting.entity.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ApprovalDTO {
    @NotNull(message = "预订ID不能为空")
    private Long bookingId;

    @NotBlank(message = "审批状态不能为空")
    private String approvalStatus;

    private String approvalComment;
}
