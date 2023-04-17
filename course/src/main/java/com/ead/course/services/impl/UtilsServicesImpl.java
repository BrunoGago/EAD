package com.ead.course.services.impl;

import com.ead.course.services.UtilsServices;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServicesImpl implements UtilsServices {

    String REQUEST_URI = "http://localhost:8082";

    public String createUrl(UUID courseId, Pageable pageable){
        return REQUEST_URI + "/users?courseId=" + courseId + "&page=" + pageable.getPageNumber() + "&size="
            + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replaceAll(": ", ",");
    }
}
