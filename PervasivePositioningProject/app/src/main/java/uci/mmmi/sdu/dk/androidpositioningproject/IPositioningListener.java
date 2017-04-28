package uci.mmmi.sdu.dk.androidpositioningproject;

import android.location.Location;

/**
 * Created by peter on 20-03-17.
 */
public interface IPositioningListener {
    void positioningChanged(String positionName, Location location);
}
