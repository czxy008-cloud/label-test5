package com.meeting.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.entity.Booking;
import com.meeting.entity.MeetingRoom;
import com.meeting.entity.dto.BookingDTO;
import com.meeting.entity.vo.BookingVO;
import com.meeting.mapper.BookingMapper;
import com.meeting.mapper.MeetingRoomMapper;
import com.meeting.service.BookingService;
import com.meeting.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl extends ServiceImpl<BookingMapper, Booking> implements BookingService {

    @Autowired
    private MeetingRoomMapper meetingRoomMapper;

    @Override
    public Result<?> createBooking(BookingDTO bookingDTO) {
        if (bookingDTO.getEndTime().isBefore(bookingDTO.getStartTime())) {
            return Result.error("结束时间不能早于开始时间");
        }

        if (bookingDTO.getStartTime().isBefore(LocalDateTime.now())) {
            return Result.error("预订时间不能早于当前时间");
        }

        MeetingRoom room = meetingRoomMapper.selectById(bookingDTO.getRoomId());
        if (room == null || room.getRoomStatus() != 1) {
            return Result.error("会议室不存在或不可用");
        }

        if (bookingDTO.getAttendeeCount() != null && bookingDTO.getAttendeeCount() > room.getCapacity()) {
            return Result.error("参会人数超过会议室容纳人数");
        }

        List<Booking> conflicts = baseMapper.findConflictingBookings(
                bookingDTO.getRoomId(),
                bookingDTO.getStartTime(),
                bookingDTO.getEndTime(),
                0L
        );

        if (!conflicts.isEmpty()) {
            return Result.error("该时间段会议室已被预订，请选择其他时间");
        }

        Booking booking = new Booking();
        booking.setRoomId(bookingDTO.getRoomId());
        booking.setUserId(StpUtil.getLoginIdAsLong());
        booking.setTitle(bookingDTO.getTitle());
        booking.setPurpose(bookingDTO.getPurpose());
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setAttendeeCount(bookingDTO.getAttendeeCount());
        booking.setBookingStatus("PENDING");
        save(booking);

        return Result.success(booking);
    }

    @Override
    public Result<?> cancelBooking(Long id) {
        Booking booking = getById(id);
        if (booking == null) {
            return Result.error("预订不存在");
        }

        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (!booking.getUserId().equals(currentUserId)) {
            return Result.error("只能取消自己的预订");
        }

        if ("APPROVED".equals(booking.getBookingStatus()) && booking.getStartTime().isBefore(LocalDateTime.now().plusHours(1))) {
            return Result.error("会议开始前1小时内不能取消已批准的预订");
        }

        booking.setBookingStatus("CANCELLED");
        updateById(booking);
        return Result.success();
    }

    @Override
    public Result<?> getTodayBookings() {
        List<BookingVO> bookings = baseMapper.findAllTodayBookings();
        return Result.success(bookings);
    }

    @Override
    public Result<?> getTodayBookingsByRoom(Long roomId) {
        List<BookingVO> bookings = baseMapper.findTodayBookingsByRoomId(roomId);
        return Result.success(bookings);
    }

    @Override
    public Result<?> getMyBookings() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Booking::getUserId, userId);
        wrapper.orderByDesc(Booking::getCreateTime);
        List<Booking> list = list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<?> getPendingBookings() {
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Booking::getBookingStatus, "PENDING");
        wrapper.orderByDesc(Booking::getCreateTime);
        List<Booking> list = list(wrapper);
        return Result.success(list);
    }
}
