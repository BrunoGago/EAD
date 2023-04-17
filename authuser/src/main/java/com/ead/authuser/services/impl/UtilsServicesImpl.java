package com.ead.authuser.services.impl;

import com.ead.authuser.services.UtilsServices;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServicesImpl implements UtilsServices {

    String REQUEST_URI = "http://localhost:8083";

    public String createUrl(UUID userId, Pageable pageable){
        return REQUEST_URI + "/courses?userId=" + userId + "&page=" + pageable.getPageNumber() + "&size="
            + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replaceAll(": ", ",");
    }
}
