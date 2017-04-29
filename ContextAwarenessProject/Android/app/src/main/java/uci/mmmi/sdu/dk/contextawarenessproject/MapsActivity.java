package uci.mmmi.sdu.dk.contextawarenessproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
// Location Manager code based on following link:
// http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, IGPSPositioningListener, IBLEPositioningListener {

    // ubicom.herokuapp.com



    private GoogleMap mMap;
    private GPSService gpsService;
    private BLEService bleService;
    private TextView latText, longText, sensorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(handlePermissions() ) {
            bleService = new BLEService(this, this);
            bleService.enableBLE();
            gpsService = new GPSService(this, this);
            gpsService.startUsingGps();
        }

        setContentView(R.layout.activity_maps);
        latText = (TextView) findViewById(R.id.latTextView);
        longText = (TextView) findViewById(R.id.longTextView);
        sensorText = (TextView) findViewById(R.id.sensorTextView);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // TODO: Generate and save UUID in SharedPreferences.
        /*DeviceStatus s = new DeviceStatus(UUID.randomUUID(), "U165", DeviceStatus.Status.IN, "testy test");
        NetworkManager.getInstance(this).getUbicomService().sendDeviceStatus(s.deviceId.toString(), s).enqueue(new Callback<DeviceStatus>() {
            @Override
            public void onResponse(Call<DeviceStatus> call, Response<DeviceStatus> response) {

            }

            @Override
            public void onFailure(Call<DeviceStatus> call, Throwable t) {

            }
        });*/
    }

    /**
     * Makes sure that all of our permission requests have been handled
     * LOCATION HANDLERS ARE FOR API-24 AND UP
     * Soruce https://github.com/LennartOlsen/ble-sense/blob/master/app/src/main/java/net/lennartolsen/blescanner/MainActivity.java#L60
     */
    protected boolean handlePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED||
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
     * Soruce https://github.com/LennartOlsen/ble-sense/blob/master/app/src/main/java/net/lennartolsen/blescanner/MainActivity.java#L60
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
            bleService = new BLEService(this, this);
            bleService.enableBLE();
            gpsService = new GPSService(this, this);
            gpsService.startUsingGps();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng odense = new LatLng(55.368225, 10.426634);
        mMap.addMarker(new MarkerOptions().position(odense).title("Marker in Odense"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(odense));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(50));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void enteredBLEBuildingZone() {
        gpsService.stopUsingGPS();
    }

    @Override
    public void abandonedBLEBuildingZone() {
        gpsService.startUsingGps();
    }

    @Override
    public void positioningChanged(String positionName, Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(currentLocation).title(positionName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        latText.setText("" + location.getLatitude());
        longText.setText("" + location.getLongitude());
        sensorText.setText(location.getProvider());
    }
}
