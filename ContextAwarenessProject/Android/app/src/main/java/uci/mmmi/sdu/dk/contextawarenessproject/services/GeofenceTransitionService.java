package uci.mmmi.sdu.dk.contextawarenessproject.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by peter on 08-05-17.
 */
public class GeofenceTransitionService extends IntentService {

    public GeofenceTransitionService() {
        super("GeofencingTransitionService");
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // TODO: Send locationupdated intent.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Intent locationIntent = new Intent(LocationUpdateBroadcastReceiver.LOCATION_UPDATED);
            locationIntent.putExtra("provider", "Geofencing");
            locationIntent.putExtra("lat", geofencingEvent.getTriggeringLocation().getLatitude());
            locationIntent.putExtra("lng", geofencingEvent.getTriggeringLocation().getLongitude());
            locationIntent.putExtra("location", "Inside OU44");
            locationIntent.putExtra("roomId", "");
            getApplicationContext().sendBroadcast(locationIntent);

            startService(new Intent(this, KontaktBLEService.class));

        } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Intent locationIntent = new Intent(LocationUpdateBroadcastReceiver.LOCATION_UPDATED);
            locationIntent.putExtra("provider", "Geofencing");
            locationIntent.putExtra("lat", geofencingEvent.getTriggeringLocation().getLatitude());
            locationIntent.putExtra("lng", geofencingEvent.getTriggeringLocation().getLongitude());
            locationIntent.putExtra("location", "Outside OU44");
            locationIntent.putExtra("roomId", "");
            getApplicationContext().sendBroadcast(locationIntent);

            stopService(new Intent(this, KontaktBLEService.class));
        }
    }
}
