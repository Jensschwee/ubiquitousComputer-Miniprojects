package uci.mmmi.sdu.dk.androidpositioningproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.SpaceListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleSpaceListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import uci.mmmi.sdu.dk.androidpositioningproject.pojos.Beacons;
import uci.mmmi.sdu.dk.androidpositioningproject.pojos.OU44Feature;
import uci.mmmi.sdu.dk.androidpositioningproject.pojos.OU44GeoJSONDoc;

/**
 * Created by peter on 17-03-17.
 */

public class BLEService extends Service {

    public static final int SCAN_INTERVAL = 30000; // milliseconds
    public static final int SCAN_TIME = 5000; // milliseconds

    private final IBLEPositioningListener blePositioningListener;
    private final Context context;

    private ProximityManager proximityManager;

    private Handler handler = new Handler();
    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            startScanning();
        }
    };
    private Runnable scanStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScanning();
        }
    };

    private LinkedList<Beacons.beaconData> JSONbeacons;
    private OU44GeoJSONDoc ou44GeoJSONDoc;

    public BLEService(Context context, IBLEPositioningListener blePositioningListener) {
        this.context = context;
        this.blePositioningListener = blePositioningListener;

        InputStream inStream = context.getResources().openRawResource(R.raw.beacons);
        Reader rd = new BufferedReader(new InputStreamReader(inStream));
        Gson gson = new Gson();
        Beacons obj = gson.fromJson(rd, Beacons.class);
        JSONbeacons = new LinkedList<>(Arrays.asList(obj.beacons));

        InputStream inStream2 = context.getResources().openRawResource(R.raw.ou44_geometry);
        Reader rd2 = new BufferedReader(new InputStreamReader(inStream2));
        Gson gson2 = new Gson();
        ou44GeoJSONDoc = gson2.fromJson(rd2, OU44GeoJSONDoc.class);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        proximityManager.stopScanning();
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(scanRunnable);
        proximityManager.disconnect();
        proximityManager = null;
        super.onDestroy();
    }

    public void enableBLE() {
        KontaktSDK.initialize("YOUR_API_KEY");
        proximityManager = ProximityManagerFactory.create(context);
        proximityManager.setIBeaconListener(createIBeaconListener());
        proximityManager.setSpaceListener(createSpaceListener());
        startScanning();
    }

    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });

        handler.postDelayed(scanRunnable, SCAN_INTERVAL);
        handler.postDelayed(scanStopRunnable, SCAN_TIME);
    }

    private void stopScanning() {
        proximityManager.stopScanning();
    }

    public void disableBLE() {
        stopScanning();
        handler.removeCallbacks(scanRunnable);
        handler.removeCallbacks(scanStopRunnable);
    }

    private void setClosestIBeacon(List<IBeaconDevice> ibeacons) {
        int highestRSSI = Integer.MIN_VALUE;
        IBeaconDevice closestDevice = null;

        for(IBeaconDevice device : ibeacons) {
            int rssi = device.getRssi();
            if(highestRSSI < rssi) {
                highestRSSI = rssi;
                closestDevice = device;
                System.out.println(highestRSSI);
            }
        }
        sendPosition(closestDevice);
    }

    private void sendPosition(IBeaconDevice device) {
        if(device != null) {
            for(Beacons.beaconData beacon : JSONbeacons) {
                if(beacon.alias.equals(device.getUniqueId())) {
                    Location location = new Location("KontaktBLE");

                    LatLng latLng = findRoomPosition(beacon);
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);

                    blePositioningListener.positioningChanged(beacon.roomName, location);
                    break;
                }
            }
        }
    }

    private LatLng findRoomPosition(Beacons.beaconData beaconData) {
        for(OU44Feature f : ou44GeoJSONDoc.features) {
            if(f.properties.RoomId != null && f.properties.RoomId.equals(beaconData.room)) {
                LatLngBounds.Builder b = LatLngBounds.builder();
                for(int i = 0; i < f.geometry.coordinates[0].length - 1; i++) {
                    double[] point = f.geometry.coordinates[0][i];
                    b.include(new LatLng(point[1], point[0]));
                }
                return b.build().getCenter();
            }
        }
        return new LatLng(55.3674083780001, 10.4307825390001);
    }

    private IBeaconListener createIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                Log.i("Sample", "IBeacon discovered: " + ibeacon.toString());
            }

            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> ibeacons, IBeaconRegion region) {
                super.onIBeaconsUpdated(ibeacons, region);
                setClosestIBeacon(ibeacons);
                if(ibeacons.size() == 0) {
                    blePositioningListener.abandonedBLEBuildingZone();
                }
                Log.i("Sample", "IBeacon list: " + ibeacons.size());
            }

            @Override
            public void onIBeaconLost(IBeaconDevice ibeacon, IBeaconRegion region) {
                super.onIBeaconLost(ibeacon, region);
                Log.i("Sample", "IBeacon lost: " + ibeacon.toString());
            }
        };
    }

    private SpaceListener createSpaceListener() {
        return new SimpleSpaceListener() {
            @Override
            public void onRegionEntered(IBeaconRegion region) {
                super.onRegionEntered(region);
                blePositioningListener.enteredBLEBuildingZone();
                Log.i("Sample", "Region entered");
            }

            @Override
            public void onRegionAbandoned(IBeaconRegion region) {
                super.onRegionAbandoned(region);
                blePositioningListener.abandonedBLEBuildingZone();
                Log.i("Sample", "Region ababa");
            }
        };
    }
}
