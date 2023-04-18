package com.ead.course.services.impl;

import com.ead.course.services.UtilsServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServicesImpl implements UtilsServices {

    public String createUrlGetAllUsersByCourse(UUID courseId, Pageable pageable){
        return "/users?courseId=" + courseId + "&page=" + pageable.getPageNumber() + "&size="
            + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replaceAll(": ", ",");
    }
}
