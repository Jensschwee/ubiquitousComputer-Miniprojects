package uci.mmmi.sdu.dk.contextawarenessproject.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uci.mmmi.sdu.dk.contextawarenessproject.entities.DeviceStatus;
import uci.mmmi.sdu.dk.contextawarenessproject.net.NetworkManager;

/**
 * Created by peter on 02-05-17.
 */

public class LocationUpdateBroadcastReceiver extends BroadcastReceiver {

    public static final String LOCATION_UPDATED = "locationupdated";

    @Override
    public void onReceive(Context context, Intent intent) {
        String provider = intent.getStringExtra("provider");
        double lat = intent.getDoubleExtra("lat", 0);
        double lng = intent.getDoubleExtra("lng", 0);
        String location = intent.getStringExtra("location");
        String roomId = intent.getStringExtra("roomId");

        DeviceStatus status = null;
        switch (provider) {
            case "KontaktBLE":
        }

        // Send location update to backend.
        DeviceStatus status = new DeviceStatus(UUID.randomUUID(), , DeviceStatus.Status.valueOf(), location, "ELELELE");
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
