package uci.mmmi.sdu.dk.contextawarenessproject.services;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationResult;

import java.util.List;

/**
 * Created by peter on 08-05-17.
 */
public class GeofenceTransitionService extends IntentService {

    public GeofenceTransitionService() {
        super("GeofencingTransitionService");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

    }

    protected void onHandleIntent(Intent intent) {
        System.out.println("LELELELE");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        LocationResult locationResult = LocationResult.extractResult(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        Intent locationIntent = new Intent(LocationUpdateBroadcastReceiver.LOCATION_UPDATED);
        locationIntent.putExtra("provider", "Geofencing");
        locationIntent.putExtra("roomId", "");
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            System.out.println("GEOFENCE ENTER");
            startService(new Intent(this, KontaktBLEService.class));
            locationIntent.putExtra("lat", geofencingEvent.getTriggeringLocation().getLatitude());
            locationIntent.putExtra("lng", geofencingEvent.getTriggeringLocation().getLongitude());
            locationIntent.putExtra("location", "Inside OU44");
            getApplicationContext().sendBroadcast(locationIntent);
        } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            System.out.println("GEOFENCE EXIT");
            stopService(new Intent(this, KontaktBLEService.class));
            locationIntent.putExtra("lat", geofencingEvent.getTriggeringLocation().getLatitude());
            locationIntent.putExtra("lng", geofencingEvent.getTriggeringLocation().getLongitude());
            locationIntent.putExtra("location", "Outside OU44");
            getApplicationContext().sendBroadcast(locationIntent);
        }

        if(locationResult != null) {
            Location location = locationResult.getLastLocation();
            if(location != null && !isMyServiceRunning(KontaktBLEService.class)) {
                locationIntent.putExtra("lat", location.getLatitude());
                locationIntent.putExtra("lng", location.getLongitude());
                locationIntent.putExtra("location", "Outside OU44");
                getApplicationContext().sendBroadcast(locationIntent);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(getApplicationContext().ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
