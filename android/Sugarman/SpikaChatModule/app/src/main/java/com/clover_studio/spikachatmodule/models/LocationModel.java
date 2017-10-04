package com.clover_studio.spikachatmodule.models;

import java.io.Serializable;

/**
 * Created by ubuntu_ivo on 21.07.15..
 */
public class LocationModel implements Serializable{

    public double lat;
    public double lng;

    @Override
    public String toString() {
        return "LocationModel{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
