package com.example.jens.wifiscanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName();

    private static final long WIFI_SCAN_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(1);

    private WifiManager wifiManager;
    private WifiScanBroadcastReceiver wifiScanBroadcastReceiver = new WifiScanBroadcastReceiver();
    private WifiManager.WifiLock wifiLock;


    private volatile boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, MainActivity.class.getName());
    }

    @Override
    protected void onResume() {
        running = true;

        wifiLock.acquire();

        registerReceiver(wifiScanBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        final Handler wifiScanHandler = new Handler();
        Runnable wifiScanRunnable = new Runnable() {

            @Override
            public void run() {
                if (!running) {
                    return;
                }

                if (!wifiManager.startScan()){
                    Log.w(TAG, "Couldn't start Wi-fi scan!");
                }

                wifiScanHandler.postDelayed(this, WIFI_SCAN_DELAY_MILLIS);
            }

        };
        wifiScanHandler.post(wifiScanRunnable);
    }

    @Override
    protected void onPause() {
        running = false;

        unregisterReceiver(wifiScanBroadcastReceiver);

        wifiLock.release();
    }

    private final class WifiScanBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!running || !WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                return;
            }

            List<ScanResult> scanResults = wifiManager.getScanResults();

            // Do something with your scanResults
        }

    }



    private void appendToCsv(String... fields) {
        String csvString = "";
        for(String field : fields) {
            csvString += field + ",";
        }

        String baseFolder = this.getFilesDir().getAbsolutePath();
        File file = new File(baseFolder + "wifimeasurements.csv");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(csvString.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
