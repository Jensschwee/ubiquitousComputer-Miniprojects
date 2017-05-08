package uci.mmmi.sdu.dk.contextawarenessproject.pojos;

/**
 * Created by bullari on 5/8/17.
 */

public class InOutBoardListItem {

    private String location;
    private int distance;
    private boolean in;
    private DeviceStatus status;
    private String username;

    public InOutBoardListItem(){}

    public InOutBoardListItem(String username, String location, int distance, boolean in) {
        this.location = location;
        this.distance = distance;
        this.username = username;
        this.in = in;
    }

    public InOutBoardListItem(DeviceStatus status) {
        this.status = status;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isIn() {
        return in;
    }

    public void setIn(boolean in) {
        this.in = in;
    }
}
