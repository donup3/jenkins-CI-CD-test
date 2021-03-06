package com.schedulsharing.dto.MySchedule;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyScheduleUpdateResponse {
    private Long myScheduleId;
    private String name;
    private String contents;
    private LocalDateTime scheduleStartDate;
    private LocalDateTime scheduleEndDate;
}
