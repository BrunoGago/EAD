package com.ead.authuser.services.impl;

import com.ead.authuser.services.UtilsServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServicesImpl implements UtilsServices {

    public String createUrlGetAllCoursesByUser(UUID userId, Pageable pageable){
        return "/courses?userId=" + userId + "&page=" + pageable.getPageNumber() + "&size="
            + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replaceAll(": ", ",");
    }
}
