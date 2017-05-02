package uci.mmmi.sdu.dk.contextawarenessproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

import uci.mmmi.sdu.dk.contextawarenessproject.common.AbstractHostActivity;
import uci.mmmi.sdu.dk.contextawarenessproject.fragments.MainFragment;
import uci.mmmi.sdu.dk.contextawarenessproject.services.GPSService;
import uci.mmmi.sdu.dk.contextawarenessproject.services.KontaktBLEService;

public class MainActivity extends AbstractHostActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(handlePermissions() ) {
            startService(new Intent(this, KontaktBLEService.class));
            startService(new Intent(this, GPSService.class));
        }

        addFragment(new MainFragment());
    }

    @Override
    protected int getBaseFragmentFrameResource() {
        return R.id.activity_main_basefragmentframe;
    }

    @Nullable
    @Override
    protected Integer getActionbarIconResource() {
        return null;
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
            startService(new Intent(this, KontaktBLEService.class));
            startService(new Intent(this, GPSService.class));
        }
    }
}
