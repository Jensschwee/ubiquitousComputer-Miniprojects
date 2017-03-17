package uci.mmmi.sdu.dk.androidpositioningproject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.ProximityManagerImpl;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.SpaceListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleSpaceListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;

import java.util.List;

/**
 * Created by peter on 17-03-17.
 */

public class BLEService extends Service {

    public static final int SCAN_INTERVAL = 30000; // milliseconds
    public static final int SCAN_TIME = 5000; // milliseconds

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KontaktSDK.initialize("YOUR_API_KEY");

        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setIBeaconListener(createIBeaconListener());
        proximityManager.setSpaceListener(createSpaceListener());
        startScanning();
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

    private IBeaconListener createIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                Log.i("Sample", "IBeacon discovered: " + ibeacon.toString());
            }

            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> ibeacons, IBeaconRegion region) {
                super.onIBeaconsUpdated(ibeacons, region);
                Log.i("Sample", "IBeacons: " + ibeacons.size());
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
                Log.i("Sample", "Region entered: " + region.toString());
            }

            @Override
            public void onRegionAbandoned(IBeaconRegion region) {
                super.onRegionAbandoned(region);
                Log.i("Sample", "Region abandoned: " + region.toString());
            }
        };
    }
}
