package com.meeting.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.entity.Approval;
import com.meeting.entity.Booking;
import com.meeting.entity.User;
import com.meeting.entity.dto.ApprovalDTO;
import com.meeting.mapper.ApprovalMapper;
import com.meeting.mapper.BookingMapper;
import com.meeting.mapper.UserMapper;
import com.meeting.service.ApprovalService;
import com.meeting.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class ApprovalServiceImpl extends ServiceImpl<ApprovalMapper, Approval> implements ApprovalService {

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> approve(ApprovalDTO approvalDTO) {
        Long approverId = StpUtil.getLoginIdAsLong();
        User approver = userMapper.selectById(approverId);

        if (approver == null) {
            return Result.error("审批人不存在");
        }

        if (!"MANAGER".equals(approver.getRoleType()) && !"ADMIN".equals(approver.getRoleType())) {
            return Result.error("没有审批权限，需要部门经理或管理员角色");
        }

        Booking booking = bookingMapper.selectById(approvalDTO.getBookingId());
        if (booking == null) {
            return Result.error("预订记录不存在");
        }

        if (!"PENDING".equals(booking.getBookingStatus())) {
            return Result.error("该预订已处理，无需重复审批");
        }

        if ("APPROVED".equals(approvalDTO.getApprovalStatus())) {
            booking.setBookingStatus("APPROVED");
        } else if ("REJECTED".equals(approvalDTO.getApprovalStatus())) {
            booking.setBookingStatus("REJECTED");
        } else {
            return Result.error("无效的审批状态");
        }
        bookingMapper.updateById(booking);

        Approval approval = new Approval();
        approval.setBookingId(approvalDTO.getBookingId());
        approval.setApproverId(approverId);
        approval.setApprovalStatus(approvalDTO.getApprovalStatus());
        approval.setApprovalComment(approvalDTO.getApprovalComment());
        approval.setApprovalTime(LocalDateTime.now());
        save(approval);

        return Result.success();
    }
}
