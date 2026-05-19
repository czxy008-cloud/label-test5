package com.meeting.controller;

import com.meeting.entity.MeetingRoom;
import com.meeting.service.MeetingRoomService;
import com.meeting.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room")
public class MeetingRoomController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    @GetMapping("/list")
    public Result<?> list() {
        return meetingRoomService.listAll();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(meetingRoomService.getById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody MeetingRoom meetingRoom) {
        return meetingRoomService.add(meetingRoom);
    }

    @PutMapping
    public Result<?> update(@RequestBody MeetingRoom meetingRoom) {
        return meetingRoomService.update(meetingRoom);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return meetingRoomService.delete(id);
    }
}
