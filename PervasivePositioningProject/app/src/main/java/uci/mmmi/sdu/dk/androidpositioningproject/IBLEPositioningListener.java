package uci.mmmi.sdu.dk.androidpositioningproject;

/**
 * Created by peter on 20-03-17.
 */

public interface IBLEPositioningListener extends IPositioningListener {
    void enteredBLEBuildingZone();
    void abandonedBLEBuildingZone();
}
