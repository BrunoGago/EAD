package com.ead.course.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class SubscritionDto {

    @NotNull
    private UUID userId;
}
