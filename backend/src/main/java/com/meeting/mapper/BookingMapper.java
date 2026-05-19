package com.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meeting.entity.Booking;
import com.meeting.entity.vo.BookingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BookingMapper extends BaseMapper<Booking> {

    @Select("SELECT b.*, mr.room_name, u.real_name as user_name " +
            "FROM t_booking b " +
            "LEFT JOIN t_meeting_room mr ON b.room_id = mr.id " +
            "LEFT JOIN t_user u ON b.user_id = u.id " +
            "WHERE b.room_id = #{roomId} " +
            "AND b.booking_status IN ('APPROVED', 'PENDING')")
    List<BookingVO> findBookingsByRoomId(@Param("roomId") Long roomId);

    @Select("SELECT b.*, mr.room_name, u.real_name as user_name " +
            "FROM t_booking b " +
            "LEFT JOIN t_meeting_room mr ON b.room_id = mr.id " +
            "LEFT JOIN t_user u ON b.user_id = u.id " +
            "WHERE b.room_id = #{roomId} " +
            "AND b.booking_status IN ('APPROVED', 'PENDING') " +
            "AND b.start_time &lt; #{endTime} " +
            "AND b.end_time &gt; #{startTime} " +
            "AND b.id != #{excludeId}")
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("excludeId") Long excludeId);

    @Select("SELECT b.*, mr.room_name, u.real_name as user_name " +
            "FROM t_booking b " +
            "LEFT JOIN t_meeting_room mr ON b.room_id = mr.id " +
            "LEFT JOIN t_user u ON b.user_id = u.id " +
            "WHERE b.room_id = #{roomId} " +
            "AND DATE(b.start_time) = CURRENT_DATE " +
            "AND b.booking_status IN ('APPROVED', 'PENDING') " +
            "ORDER BY b.start_time ASC")
    List<BookingVO> findTodayBookingsByRoomId(@Param("roomId") Long roomId);

    @Select("SELECT b.*, mr.room_name, u.real_name as user_name " +
            "FROM t_booking b " +
            "LEFT JOIN t_meeting_room mr ON b.room_id = mr.id " +
            "LEFT JOIN t_user u ON b.user_id = u.id " +
            "WHERE DATE(b.start_time) = CURRENT_DATE " +
            "AND b.booking_status IN ('APPROVED', 'PENDING') " +
            "ORDER BY b.start_time ASC")
    List<BookingVO> findAllTodayBookings();
}
