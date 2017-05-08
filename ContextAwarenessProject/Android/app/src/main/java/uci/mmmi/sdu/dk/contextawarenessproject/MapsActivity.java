package uci.mmmi.sdu.dk.contextawarenessproject;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.UUID;

import uci.mmmi.sdu.dk.contextawarenessproject.adapters.MapsViewPagerAdapter;
import uci.mmmi.sdu.dk.contextawarenessproject.services.GeofenceTransitionService;

// Location Manager code based on following link:
// http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent;

    private MapsViewPagerAdapter mapsViewPagerAdapter;
    private ViewPager mapsViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handlePermissions();

        setContentView(R.layout.activity_maps);

        if (!PreferenceManager.getDefaultSharedPreferences(this).contains("deviceUUID")) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("deviceUUID", UUID.randomUUID().toString()).commit();
        }

        mapsViewPagerAdapter = new MapsViewPagerAdapter(getSupportFragmentManager());
        mapsViewPager = (ViewPager) findViewById(R.id.fragment_maps_viewpager);
        mapsViewPager.setAdapter(mapsViewPagerAdapter);

        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
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
                .setExpirationDuration(120000) // Two minute update-rate.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        ArrayList<Geofence> geofences = new ArrayList<>();
        geofences.add(geofence);

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
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
            if(!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
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
        mGoogleApiClient.connect();
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
        System.out.println(status.isSuccess());
    }
}
