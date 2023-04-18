package com.ead.course.services;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UtilsServices {

    String createUrlGetAllUsersByCourse(UUID courseId, Pageable pageable);
}
