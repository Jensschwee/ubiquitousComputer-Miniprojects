package com.example.jens.wifiscanner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {


    private static final String TAG = MainActivity.class.getSimpleName();

    private static final long WIFI_SCAN_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(1);

    private WifiManager wifiManager;
    private WifiScanBroadcastReceiver wifiScanBroadcastReceiver = new WifiScanBroadcastReceiver();
    private WifiManager.WifiLock wifiLock;


    private volatile boolean running;

    private EditText txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, MainActivity.class.getName());
        txtLocation = (EditText) findViewById(R.id.txtLocation);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    final Handler handler = new Handler();

    protected void btnScan_Clicked(View view)
    {
        running = true;

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
                else
                {
                    Log.w(TAG, "Stated Wi-fi scan!");
                }

                wifiScanHandler.postDelayed(this, WIFI_SCAN_DELAY_MILLIS);
            }
        };
        wifiScanHandler.post(wifiScanRunnable);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                running = false;
            }
        }, 20000);
    }

    private void scanWiFi(String location, List<ScanResult> scanResults)
    {
        String localMac = wifiManager.getConnectionInfo().getMacAddress();
        for (ScanResult scan : scanResults) {
            int ss = scan.level;
            String apMac = scan.BSSID;
            Date timestamp = new Date(scan.timestamp);
            appendToCsv(location, timestamp.toString(), apMac, localMac, getString(ss));
            System.out.print("location");
            Log.w("sdgsdg", location);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

            scanWiFi(txtLocation.getText().toString(), scanResults);
        }

    }



    private void appendToCsv(String... fields) {
        String csvString = "";
        csvString += fields[0];

        int count = 0;
        for(String field : fields) {
            if(count != 0) {
                csvString += "," + field;
            }
            count++;
        }
        String baseFolder = this.getExternalFilesDir("csv").getAbsolutePath();
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
