package uci.mmmi.sdu.dk.contextawarenessproject.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uci.mmmi.sdu.dk.contextawarenessproject.R;
import uci.mmmi.sdu.dk.contextawarenessproject.services.LocationUpdateBroadcastReceiver;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks{

    public static final String LOCATION_UPDATED = "locationupdated";

    private GoogleMap mMap;
    private TextView latText, longText, sensorText;
    private BroadcastReceiver locationUpdatedReceiver;


    public MapsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        latText = (TextView) rootView.findViewById(R.id.latTextView);
        longText = (TextView) rootView.findViewById(R.id.longTextView);
        sensorText = (TextView) rootView.findViewById(R.id.sensorTextView);

//        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This is for updating stuff in the UI. Location is sent to the server via LocationUpdateBroadcastReceiver.
        locationUpdatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String provider = intent.getStringExtra("provider");
                double lat = intent.getDoubleExtra("lat", 0);
                double lng = intent.getDoubleExtra("lng", 0);
                String location = intent.getStringExtra("location");
                String roomId = intent.getStringExtra("roomId");

                Log.d("Location Updated", provider + ": " + location);

                positioningChanged(provider, lat, lng, location, roomId);
            }
        };
        getActivity().registerReceiver(locationUpdatedReceiver, new IntentFilter(LocationUpdateBroadcastReceiver.LOCATION_UPDATED));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(locationUpdatedReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng odense = new LatLng(55.368225, 10.426634);
        mMap.addMarker(new MarkerOptions().position(odense).title("Marker in Odense"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(odense));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(50));
    }

    public void positioningChanged(String provider, double lat, double lng, String location, String roomId) {
        LatLng currentLocation = new LatLng(lat, lng);
        if(mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentLocation).title(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        }
        latText.setText("" + lat);
        longText.setText("" + lng);
        sensorText.setText(provider);
    }
}
