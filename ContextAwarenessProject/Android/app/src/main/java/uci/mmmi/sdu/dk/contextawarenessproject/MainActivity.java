package uci.mmmi.sdu.dk.contextawarenessproject;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import uci.mmmi.sdu.dk.contextawarenessproject.services.GeofenceTransitionService;
import uci.mmmi.sdu.dk.contextawarenessproject.services.KontaktBLEService;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (handlePermissions()) {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        Geofence geofence = new Geofence.Builder()
                .setRequestId("OU44")
                .setCircularRegion(
                        // Middle of the building
                        55.3674083780001,
                        10.4307825390001,
                        // 100 meters radius
                        100
                )
                //.setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        ArrayList<Geofence> geofences = new ArrayList<>();
        geofences.add(geofence);

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(geofences);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Makes sure that all of our permission requests have been handled
     * LOCATION HANDLERS ARE FOR API-24 AND UP
     * Source https://github.com/LennartOlsen/ble-sense/blob/master/app/src/main/java/net/lennartolsen/blescanner/MainActivity.java#L60
     */
    protected boolean handlePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN
                    },
                    1);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Source https://github.com/LennartOlsen/ble-sense/blob/master/app/src/main/java/net/lennartolsen/blescanner/MainActivity.java#L60
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        ArrayList<String> allowed = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] <= 0) {
                allowed.add(permissions[i]);
            }
        }
        //Call out location service starter if all is good thank fuck for api-24 n' up
        if (allowed.contains(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                allowed.contains(Manifest.permission.ACCESS_FINE_LOCATION) &&
                allowed.contains(Manifest.permission.BLUETOOTH) &&
                allowed.contains(Manifest.permission.BLUETOOTH_ADMIN)) {
            //startService(new Intent(this, KontaktBLEService.class));
            //startService(new Intent(this, GPSService.class));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull Status status) {

    }
}
