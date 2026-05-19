package com.meeting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_approval")
public class Approval {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookingId;
    private Long approverId;
    private String approvalStatus;
    private String approvalComment;
    private LocalDateTime approvalTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
