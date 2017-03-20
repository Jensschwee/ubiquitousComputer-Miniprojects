package uci.mmmi.sdu.dk.androidpositioningproject.pojos;

/**
 * This class is used for parsing a JSON file containing location information
 * for the building's iBeacons
 * Created by bullari on 3/14/16.
 *
 * Source: Halldór's and Peter's bachelor project.
 */
public class Beacons {

    public beaconData[] beacons;

    public class beaconData {
        public String alias, UUID, instanceid, room, level, roomName;
        public int major, minor;

    }
}
