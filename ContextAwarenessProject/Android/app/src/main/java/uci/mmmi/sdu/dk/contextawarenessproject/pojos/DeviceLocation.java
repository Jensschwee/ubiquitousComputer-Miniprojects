package uci.mmmi.sdu.dk.contextawarenessproject.pojos;

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
