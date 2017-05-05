package uci.mmmi.sdu.dk.contextawarenessproject;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

import uci.mmmi.sdu.dk.contextawarenessproject.adapters.MapsViewPagerAdapter;
import uci.mmmi.sdu.dk.contextawarenessproject.services.KontaktBLEService;
import uci.mmmi.sdu.dk.contextawarenessproject.services.GPSService;

import static uci.mmmi.sdu.dk.contextawarenessproject.R.id.status;

// Location Manager code based on following link:
// http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/

public class MapsActivity extends FragmentActivity {

    private MapsViewPagerAdapter mapsViewPagerAdapter;
    private ViewPager mapsViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if(handlePermissions() ) {
            startService(new Intent(this, KontaktBLEService.class));
            startService(new Intent(this, GPSService.class));
        //}

        setContentView(R.layout.activity_maps);

        if(!PreferenceManager.getDefaultSharedPreferences(this).contains("deviceUUID")) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("deviceUUID", UUID.randomUUID().toString()).commit();
        }


        mapsViewPagerAdapter = new MapsViewPagerAdapter(getSupportFragmentManager());
        mapsViewPager = (ViewPager) findViewById(R.id.fragment_maps_viewpager);
        mapsViewPager.setAdapter(mapsViewPagerAdapter);

//        latText = (TextView) findViewById(R.id.latTextView);
//        longText = (TextView) findViewById(R.id.longTextView);
//        sensorText = (TextView) findViewById(R.id.sensorTextView);
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }



    /**
     * Makes sure that all of our permission requests have been handled
     * LOCATION HANDLERS ARE FOR API-24 AND UP
     * Source https://github.com/LennartOlsen/ble-sense/blob/master/app/src/main/java/net/lennartolsen/blescanner/MainActivity.java#L60
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
    protected void onDestroy() {
        super.onDestroy();
    }

//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        LatLng odense = new LatLng(55.368225, 10.426634);
//        mMap.addMarker(new MarkerOptions().position(odense).title("Marker in Odense"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(odense));
//        mMap.moveCamera(CameraUpdateFactory.zoomTo(50));
//    }


//    public void positioningChanged(String provider, double lat, double lng, String location, String roomId) {
//        LatLng currentLocation = new LatLng(lat, lng);
//        mMap.clear();
//        mMap.addMarker(new MarkerOptions().position(currentLocation).title(location));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
//        latText.setText("" + lat);
//        longText.setText("" + lng);
//        sensorText.setText(provider);
//    }
}
