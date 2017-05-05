package uci.mmmi.sdu.dk.contextawarenessproject.entities;

/**
 * Created by jens on 05-05-2017.
 */

public class DeviceLocation {
    public String floor;
    public Double lat;
    public Double lng;

    public DeviceLocation(String floor, Double lat, Double lng) {
        this.lat = lat;
        this.floor = floor;
        this.lng = lng;
    }
}
