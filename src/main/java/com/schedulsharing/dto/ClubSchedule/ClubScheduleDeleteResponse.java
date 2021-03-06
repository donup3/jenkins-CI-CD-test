package com.schedulsharing.dto.ClubSchedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubScheduleDeleteResponse {
    private boolean success;
    private String message;
}
