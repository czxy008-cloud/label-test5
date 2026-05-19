package com.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.entity.Booking;
import com.meeting.entity.dto.BookingDTO;
import com.meeting.common.Result;

public interface BookingService extends IService<Booking> {
    Result<?> createBooking(BookingDTO bookingDTO);
    Result<?> cancelBooking(Long id);
    Result<?> getTodayBookings();
    Result<?> getTodayBookingsByRoom(Long roomId);
    Result<?> getMyBookings();
    Result<?> getPendingBookings();
}
