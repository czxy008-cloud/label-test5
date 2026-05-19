package com.meeting.controller;

import com.meeting.entity.dto.ApprovalDTO;
import com.meeting.service.ApprovalService;
import com.meeting.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/approval")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @PostMapping
    public Result<?> approve(@Valid @RequestBody ApprovalDTO approvalDTO) {
        return approvalService.approve(approvalDTO);
    }
}
