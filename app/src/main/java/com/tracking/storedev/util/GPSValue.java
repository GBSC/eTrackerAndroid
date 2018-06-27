package com.tracking.storedev.util;

import im.delight.android.location.SimpleLocation;

/**
 * Created by ZASS on 4/13/2018.
 */

public class GPSValue {
    private static final GPSValue ourInstance = new GPSValue();

    public static GPSValue getInstance() {
        return ourInstance;
    }

    SimpleLocation location;

    public SimpleLocation getLocation() {
        return location;
    }

    public void setLocation(SimpleLocation location) {
        this.location = location;
    }
}

