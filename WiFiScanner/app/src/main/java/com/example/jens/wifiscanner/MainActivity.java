package com.example.jens.wifiscanner;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, MainActivity.class.getName());
        txtLocation = (EditText) findViewById(R.id.txtLocation);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
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

    public void btnScan_Clicked(View view)
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
            long timestamp = scan.timestamp;
            appendToCsv(location, timestamp+"", apMac, localMac, ss+"");
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

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

            }else{
                List<ScanResult> scanResults = wifiManager.getScanResults();

                scanWiFi(txtLocation.getText().toString(), scanResults);
                //do something, permission was previously granted; or legacy device
            }

        }
    }

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            List<ScanResult> scanResults = wifiManager.getScanResults();

            scanWiFi(txtLocation.getText().toString(), scanResults);        }
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

        String baseFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

        File file = new File(baseFolder + "/wifimeasurements.csv");
        if(!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file,true);
            fos.write(csvString.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
