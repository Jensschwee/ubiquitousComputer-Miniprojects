package uci.mmmi.sdu.dk.contextawarenessproject.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.BeaconRegion;
import com.kontakt.sdk.android.ble.exception.ScanError;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.ScanStatusListener;
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
import java.util.UUID;

import uci.mmmi.sdu.dk.contextawarenessproject.MapsActivity;
import uci.mmmi.sdu.dk.contextawarenessproject.R;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.Beacons;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.OU44Feature;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.OU44GeoJSONDoc;

/**
 * Created by peter on 17-03-17.
 */

public class KontaktBLEService extends Service implements ScanStatusListener {

    public static final int SCAN_INTERVAL = 5001; // milliseconds
    public static final int SCAN_TIME = 5000; // milliseconds

    private ProximityManager proximityManager;
    private IBeaconRegion beaconRegion = BeaconRegion.builder().identifier("lala").proximity(UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e")).build();

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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("KontaktBLEService", "Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        InputStream inStream = getBaseContext().getResources().openRawResource(R.raw.beacons);
        Reader rd = new BufferedReader(new InputStreamReader(inStream));
        Gson gson = new Gson();
        Beacons obj = gson.fromJson(rd, Beacons.class);
        JSONbeacons = new LinkedList<>(Arrays.asList(obj.beacons));

        InputStream inStream2 = getBaseContext().getResources().openRawResource(R.raw.ou44_geometry);
        Reader rd2 = new BufferedReader(new InputStreamReader(inStream2));
        Gson gson2 = new Gson();
        ou44GeoJSONDoc = gson2.fromJson(rd2, OU44GeoJSONDoc.class);

        Log.d("KontaktBLEService", "Start command");

        System.out.println(JSONbeacons.size());

        enableBLE();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        proximityManager.stopScanning();

        Log.d("KontaktBLEService", "Stopping");
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        proximityManager.disconnect();
        proximityManager = null;

        Log.d("KontaktBLEService", "Destroyed");
        super.onDestroy();
    }

    public void enableBLE() {
        // Setup of the Kontakt.io API inspired by:
        // https://github.com/kontaktio/kontakt-beacon-admin-sample-app/blob/master/sample-app/src/main/java/com/kontakt/sample/samples/ScanRegionsActivity.java
        KontaktSDK.initialize("YOUR_API_KEY");
        proximityManager = ProximityManagerFactory.create(getBaseContext());
        proximityManager.configuration()
                .scanPeriod(ScanPeriod.RANGING)
                .scanMode(ScanMode.BALANCED)
                .deviceUpdateCallbackInterval(SCAN_INTERVAL);
        proximityManager.spaces().iBeaconRegion(beaconRegion);
        proximityManager.setIBeaconListener(createIBeaconListener());
        proximityManager.setSpaceListener(createSpaceListener());
        proximityManager.setScanStatusListener(this);
        startScanning();
    }

    private void startScanning() {
        if(!proximityManager.isConnected()) {
            System.out.println("SCAN CONNECT");
            proximityManager.connect(new OnServiceReadyListener() {
                @Override
                public void onServiceReady() {
                    proximityManager.startScanning();
                }
            });
        }
        else {

            System.out.println("SCAN RESTART");
            proximityManager.restartScanning();
        }
    }

    private void stopScanning() {
        Log.d("KontaktBLEService", "Stop scanning");
        proximityManager.stopScanning();
    }

    public void disableBLE() {
        stopScanning();
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
                    LatLng latLng = findRoomPosition(beacon);

                    Intent intent = new Intent(LocationUpdateBroadcastReceiver.LOCATION_UPDATED);
                    intent.putExtra("provider", "KontaktBLE");
                    intent.putExtra("lat", latLng.latitude);
                    intent.putExtra("lng", latLng.longitude);
                    intent.putExtra("location", beacon.roomName);
                    intent.putExtra("roomId", beacon.room);
                    getApplicationContext().sendBroadcast(intent);
                    Log.d("KontaktBLEService", "Sending: " + intent.toString());
                    break;
                }
            }


            // Hardcoded test-beacon
            if(device.getUniqueId().equals("VLqb")) {
                Intent intent = new Intent(LocationUpdateBroadcastReceiver.LOCATION_UPDATED);
                intent.putExtra("provider", "KontaktBLE");
                intent.putExtra("lat", 0);
                intent.putExtra("lng", 0);
                intent.putExtra("location", "Studiezone");
                intent.putExtra("roomId", "Ø22-508-0");
                getApplicationContext().sendBroadcast(intent);
                Log.d("KontaktBLEService", "Sending: " + intent.toString());
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
                    System.out.println(ibeacons.size());
                    startService(new Intent(getApplicationContext(), GPSService.class));
                    Log.i("LocationUpdater", "BLE region abandoned, starting GPS service.");
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
                stopService(new Intent(getApplicationContext(), GPSService.class));
                Log.i("LocationUpdater", "BLE region entered, stopping GPS service.");
            }

            @Override
            public void onRegionAbandoned(IBeaconRegion region) {
                super.onRegionAbandoned(region);
                startService(new Intent(getApplicationContext(), GPSService.class));
                Log.i("LocationUpdater", "BLE region abandoned, starting GPS service.");
            }
        };
    }

    @Override
    public void onScanStart() {
        Log.d("KontaktBLEService", "Starting scanning.");
    }

    @Override
    public void onScanStop() {
        Log.d("KontaktBLEService", "Stopping scanning.");
    }

    @Override
    public void onScanError(ScanError scanError) {
        Log.d("KontaktBLEService", "Scanning error.");
        Log.e("KontaktBLEService", scanError.getMessage());
    }

    @Override
    public void onMonitoringCycleStart() {
        Log.d("KontaktBLEService", "Cycle start.");
    }

    @Override
    public void onMonitoringCycleStop() {
        Log.d("KontaktBLEService", "Cycle stop.");
    }
}
