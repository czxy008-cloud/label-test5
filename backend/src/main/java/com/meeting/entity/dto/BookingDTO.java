package com.meeting.entity.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    @NotNull(message = "会议室ID不能为空")
    private Long roomId;

    @NotBlank(message = "会议主题不能为空")
    private String title;

    @NotBlank(message = "用途说明不能为空")
    private String purpose;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    private Integer attendeeCount;
}
