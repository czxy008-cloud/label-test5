package com.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.entity.MeetingRoom;
import com.meeting.common.Result;

public interface MeetingRoomService extends IService<MeetingRoom> {
    Result<?> listAll();
    Result<?> add(MeetingRoom meetingRoom);
    Result<?> update(MeetingRoom meetingRoom);
    Result<?> delete(Long id);
}
