package uci.mmmi.sdu.dk.contextawarenessproject.pojos;

import java.util.UUID;

/**
 * Created by peter on 28-04-17.
 */

public class DeviceStatus {

    public enum Status {
        IN,
        OUT
    }

    public UUID deviceId;
    public String username;
    public Status status;
    public String location;
    public String roomId;

    public transient String distance;

    public DeviceStatus(UUID deviceId, String username, Status status, String location, String roomId) {
        this.deviceId = deviceId;
        this.username = username;
        this.status = status;
        this.location = location;
        this.roomId = roomId;
        distance = "";
    }
}
