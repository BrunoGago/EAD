package com.ead.course.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CourseUserDto {

    private UUID courseId;
    private UUID userId;
}
