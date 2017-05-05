package uci.mmmi.sdu.dk.contextawarenessproject.entities;

/**
 * Created by jens on 05-05-2017.
 */

public class DeviceLocation {
    public String floor;
    public float lat;
    public float lng;

    public DeviceLocation(String floor, float lat, float lng) {
        this.lat = lat;
        this.floor = floor;
        this.lng = lng;
    }
}
