package com.meeting.controller;

import com.meeting.entity.dto.BookingDTO;
import com.meeting.service.BookingService;
import com.meeting.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public Result<?> create(@Valid @RequestBody BookingDTO bookingDTO) {
        return bookingService.createBooking(bookingDTO);
    }

    @PostMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

    @GetMapping("/today")
    public Result<?> todayBookings() {
        return bookingService.getTodayBookings();
    }

    @GetMapping("/today/room/{roomId}")
    public Result<?> todayBookingsByRoom(@PathVariable Long roomId) {
        return bookingService.getTodayBookingsByRoom(roomId);
    }

    @GetMapping("/my")
    public Result<?> myBookings() {
        return bookingService.getMyBookings();
    }

    @GetMapping("/pending")
    public Result<?> pendingBookings() {
        return bookingService.getPendingBookings();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(bookingService.getById(id));
    }
}
