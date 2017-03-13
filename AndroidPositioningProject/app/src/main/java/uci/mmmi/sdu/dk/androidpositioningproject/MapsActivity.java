package uci.mmmi.sdu.dk.androidpositioningproject;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kontakt.sdk.android.common.KontaktSDK;
// Location Manager code based on following link:
// http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GPSService gpsService;
    private TextView latText, longText, sensorText;
    private Button testGPSButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        latText = (TextView) findViewById(R.id.latTextView);
        longText = (TextView) findViewById(R.id.longTextView);
        sensorText = (TextView) findViewById(R.id.sensorTextView);
        testGPSButton = (Button) findViewById(R.id.gpsTestButton);
        testGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsService = new GPSService(MapsActivity.this);

                if(gpsService.canGetLocation()){

                    double latitude = gpsService.getLatitude();
                    double longitude = gpsService.getLongitude();
                    latText.setText("" + latitude);
                    longText.setText("" + longitude);
                    // \n is for new line
                    //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    //gpsService.showSettingsAlert();
                }
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        KontaktSDK.initialize(this);

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
