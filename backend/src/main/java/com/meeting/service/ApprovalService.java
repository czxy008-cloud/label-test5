package com.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.entity.Approval;
import com.meeting.entity.dto.ApprovalDTO;
import com.meeting.common.Result;

public interface ApprovalService extends IService<Approval> {
    Result<?> approve(ApprovalDTO approvalDTO);
}
