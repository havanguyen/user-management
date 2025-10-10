package com.hanguyen.demo_spring_bai1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyScheduleResponse {
    private String studentCode;
    private String studentName;
    private String semesterName;
    private List<ScheduleItem> scheduleItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleItem {
        private String courseCode;
        private String subjectName;
        private int credits;
        private String lecturerName;
        private String scheduleInfo;
    }
}