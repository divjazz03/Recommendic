package com.divjazz.recommendic.location.service;

import com.google.maps.GeoApiContext;
import org.springframework.stereotype.Service;


public class LocationService {

    private final GeoApiContext geoApiContext;

    public LocationService(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }
}
