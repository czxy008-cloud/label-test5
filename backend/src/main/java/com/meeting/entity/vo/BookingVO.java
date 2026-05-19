package com.meeting.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingVO {
    private Long id;
    private Long roomId;
    private String roomName;
    private Long userId;
    private String userName;
    private String title;
    private String purpose;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer attendeeCount;
    private String bookingStatus;
    private LocalDateTime createTime;
}
