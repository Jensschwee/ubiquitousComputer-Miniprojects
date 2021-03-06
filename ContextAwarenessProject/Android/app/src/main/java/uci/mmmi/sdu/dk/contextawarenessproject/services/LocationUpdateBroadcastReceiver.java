package uci.mmmi.sdu.dk.contextawarenessproject.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.DeviceStatus;
import uci.mmmi.sdu.dk.contextawarenessproject.net.NetworkManager;

/**
 * Created by peter on 02-05-17.
 */

public class LocationUpdateBroadcastReceiver extends BroadcastReceiver {

    public static final String LOCATION_UPDATED = "locationupdated";

    @Override
    public void onReceive(Context context, Intent intent) {
        String provider = intent.getStringExtra("provider");
        double lat = intent.getDoubleExtra("lat", 0.0);
        double lng = intent.getDoubleExtra("lng", 0.0);
        String location = intent.getStringExtra("location");
        String roomId = intent.getStringExtra("roomId");

        DeviceStatus.Status boardstatus = null;
        switch (provider) {
            case "KontaktBLE":
                boardstatus = DeviceStatus.Status.IN;
                break;

            case "Geofencing":
                if(location.equals("Outside OU44")) {
                    boardstatus = DeviceStatus.Status.OUT;
                }
                else {
                    boardstatus = DeviceStatus.Status.IN;
                }
                break;

        }

        String uuid = PreferenceManager.getDefaultSharedPreferences(context).getString("deviceUUID", "");
        boolean isHidden = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("hidden", false);
        String name = PreferenceManager.getDefaultSharedPreferences(context).getString("name", "Unnamed");

        System.out.println("Update: " + provider);

        // Send location update to backend.
        DeviceStatus status = null;
        if(isHidden) {
            status = new DeviceStatus(UUID.fromString(uuid), name, DeviceStatus.Status.HIDDEN, "-", "");
        }
        else {
            status = new DeviceStatus(UUID.fromString(uuid), name, boardstatus, location, roomId);
        }
        NetworkManager.getInstance(context).getUbicomService().sendDeviceStatus(status.deviceId.toString(), status).enqueue(new Callback<DeviceStatus>() {
            @Override
            public void onResponse(Call<DeviceStatus> call, Response<DeviceStatus> response) {

            }

            @Override
            public void onFailure(Call<DeviceStatus> call, Throwable t) {

            }
        });
    }
}