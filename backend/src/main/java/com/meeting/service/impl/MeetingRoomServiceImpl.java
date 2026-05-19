package com.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.entity.MeetingRoom;
import com.meeting.mapper.MeetingRoomMapper;
import com.meeting.service.MeetingRoomService;
import com.meeting.common.Result;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoom> implements MeetingRoomService {

    @Override
    public Result<?> listAll() {
        LambdaQueryWrapper<MeetingRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeetingRoom::getRoomStatus, 1);
        wrapper.orderByAsc(MeetingRoom::getId);
        List<MeetingRoom> list = list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<?> add(MeetingRoom meetingRoom) {
        meetingRoom.setRoomStatus(1);
        save(meetingRoom);
        return Result.success(meetingRoom);
    }

    @Override
    public Result<?> update(MeetingRoom meetingRoom) {
        updateById(meetingRoom);
        return Result.success(meetingRoom);
    }

    @Override
    public Result<?> delete(Long id) {
        MeetingRoom room = getById(id);
        if (room == null) {
            return Result.error("会议室不存在");
        }
        room.setRoomStatus(0);
        updateById(room);
        return Result.success();
    }
}
