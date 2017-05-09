package uci.mmmi.sdu.dk.contextawarenessproject.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uci.mmmi.sdu.dk.contextawarenessproject.R;
import uci.mmmi.sdu.dk.contextawarenessproject.adapters.InOutBoardArrayAdapter;
import uci.mmmi.sdu.dk.contextawarenessproject.net.NetworkManager;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.Beacons;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.DeviceLocation;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.DeviceStatus;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.InOutBoardListItem;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.OU44Location;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.RemoteDeviceStatus;
import uci.mmmi.sdu.dk.contextawarenessproject.services.LocationUpdateBroadcastReceiver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class InOutBoardFragment extends ListFragment {

    private ArrayAdapter<InOutBoardListItem> adapter;
    private List<InOutBoardListItem> listData;

    private List<DeviceStatus> deviceList;
    private DeviceLocation localPhoneLocation;
    private Collection<OU44Location> locations;
    private LinkedList<Beacons.beaconData> JSONbeacons;

    private BroadcastReceiver locationUpdatedReceiver;
    private Handler handler = new Handler();
    private Runnable dataPullRunnable;

    // Default is in the center of OU44.
    private double localLat = 55.3674083780001;
    private double localLng = 10.4307825390001;

    public InOutBoardFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceList = new ArrayList<>();
        InputStream inStream = getActivity().getResources().openRawResource(R.raw.all_rooms_sdu);
        Reader rd = new BufferedReader(new InputStreamReader(inStream));
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<OU44Location>>(){}.getType();
        locations = gson.fromJson(rd, collectionType);
        listData = new ArrayList<>();

        if(adapter == null) {
            adapter = new InOutBoardArrayAdapter(getContext());
        }
        adapter.clear();
        adapter.addAll(listData);
        adapter.notifyDataSetChanged();

        locationUpdatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                localLat = intent.getDoubleExtra("lat", 0d);
                localLng = intent.getDoubleExtra("lng", 0d);

                System.out.println(intent.getDoubleExtra("lat", 0d));
                System.out.println(intent.getDoubleExtra("lng", 0d));
            }
        };
        getActivity().registerReceiver(locationUpdatedReceiver, new IntentFilter(LocationUpdateBroadcastReceiver.LOCATION_UPDATED));

        pullDataWithInterval(5000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void pullDataWithInterval(final int millis) {
        dataPullRunnable = new Runnable() {
            @Override
            public void run() {
                try{
                    NetworkManager.getInstance(getActivity()).getUbicomService().getAllDeviceStatuses().enqueue(new Callback<List<RemoteDeviceStatus>>() {
                        @Override
                        public void onResponse(Call<List<RemoteDeviceStatus>> call, Response<List<RemoteDeviceStatus>> response) {
                            ArrayList<RemoteDeviceStatus> networkList = (ArrayList<RemoteDeviceStatus>) response.body();
                            deviceList.clear();
                            listData.clear();

                            for (RemoteDeviceStatus status : networkList) {
                                DeviceStatus.Status statusEnum = DeviceStatus.Status.IN;
                                if (status.status.equals("IN")) {
                                    statusEnum = DeviceStatus.Status.IN;
                                } else if (status.status.equals("OUT")) {
                                    statusEnum = DeviceStatus.Status.OUT;
                                } else if (status.status.equals("HIDDEN")){
                                    statusEnum = DeviceStatus.Status.HIDDEN;
                                }
                                deviceList.add(new DeviceStatus(UUID.fromString(status.deviceId), status.username, statusEnum, status.location, status.roomId));
                            }

                            String deviceId = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("deviceUUID", null);
                            DeviceStatus deviceStatus = findLocalPhone(deviceId);
                            localPhoneLocation = findLocation(deviceStatus);
                            deviceList.remove(deviceStatus);

                            
                            calculateDistance();

                            for (DeviceStatus status : deviceList) {
                                listData.add(new InOutBoardListItem(status));
                            }

                            adapter.clear();
                            adapter.addAll(listData);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<List<RemoteDeviceStatus>> call, Throwable t) {
                            Log.e("NETWORK", "Request to get all data failed");
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("HANDLER", "SOMETHING WENT WRONG!");
                }
                finally{
                    //also call the same runnable to call it at regular interval
                    handler.postDelayed(this, millis);
                }
            }
        };
        handler.post(dataPullRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        setListShown(true);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(locationUpdatedReceiver);
        handler.removeCallbacks(dataPullRunnable);
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setAdapter(adapter);
        setListShown(false);
    }

    public void calculateDistance() {
        for (DeviceStatus device : deviceList) {
            if(!device.status.equals(DeviceStatus.Status.IN) || device.roomId.isEmpty())
                continue;
            DeviceLocation deviceLocation = findLocation(device);
            device.floor = deviceLocation.floor;
            System.out.println(deviceLocation);
            System.out.println(deviceLocation.floor);
            System.out.println(localPhoneLocation);
            System.out.println(localPhoneLocation.floor);
            if(deviceLocation.floor.equals(localPhoneLocation.floor)) {
                Location me = new Location("");
                Location dest = new Location("");

                me.setLatitude(localPhoneLocation.lat);
                me.setLongitude(localPhoneLocation.lng);

                System.out.println(localLat);

                dest.setLatitude(deviceLocation.lat);
                dest.setLongitude(deviceLocation.lng);

                System.out.println(deviceLocation.lat);

                float dist = me.distanceTo(dest);
                device.distance = String.valueOf(Math.round(dist));
            }
            else {
                switch (deviceLocation.floor) {
                    case "0":
                        device.distance = "Parterre";
                        break;
                    case "1":
                        device.distance = "Ground floor";
                        break;
                    case "2":
                        device.distance = "1. floor";
                        break;
                }
            }
        }
        sortDeviceList();
    }

    private void sortDeviceList() {
        ArrayList<DeviceStatus> parterre = new ArrayList<>();
        ArrayList<DeviceStatus> ground = new ArrayList<>();
        ArrayList<DeviceStatus> first = new ArrayList<>();
        ArrayList<DeviceStatus> sameFloor = new ArrayList<>();
        ArrayList<DeviceStatus> out = new ArrayList<>();
        ArrayList<DeviceStatus> in = new ArrayList<>();
        ArrayList<DeviceStatus> rest = new ArrayList<>();

        if (localPhoneLocation.floor != null) {
            for (DeviceStatus device : deviceList) {
                if (device.floor != null) {
                    if (device.floor.equals(localPhoneLocation.floor)) {
                        sameFloor.add(device);
                    }
                }
            }
        }
        if (!sameFloor.isEmpty()) Collections.sort(sameFloor, new DeviceDistanceMeterComparator());
        
        for (DeviceStatus device : sameFloor) {
            if (deviceList.contains(device)) deviceList.remove(device);
        }

        for (DeviceStatus device : deviceList) {
            if (!(device.status == DeviceStatus.Status.IN) || (device.roomId == null || device.roomId.isEmpty())) {
                in.add(device);
            } else if (device.distance.equals("Parterre")) {
                parterre.add(device);
            } else if (device.distance.equals("Ground floor")) {
                ground.add(device);
            } else if (device.distance.equals("1. floor")) {
                first.add(device);
            } else if (device.status == DeviceStatus.Status.OUT) {
                out.add(device);
            } else if (device.status == DeviceStatus.Status.IN) {
                in.add(device);
            } else {
                rest.add(device);
            }
        }

        deviceList.clear();
        if (!sameFloor.isEmpty()) deviceList.addAll(sameFloor);
        if (!parterre.isEmpty()) deviceList.addAll(parterre);
        if (!ground.isEmpty()) deviceList.addAll(ground);
        if (!first.isEmpty()) deviceList.addAll(first);
        if (!in.isEmpty()) deviceList.addAll(in);
        if (!out.isEmpty()) deviceList.addAll(out);
        if (!rest.isEmpty()) deviceList.addAll(rest);
    }

    private class DeviceDistanceMeterComparator implements Comparator<DeviceStatus> {
        @Override
        public int compare(DeviceStatus lhs, DeviceStatus rhs) {
            if(Integer.parseInt(lhs.distance) < Integer.parseInt(rhs.distance)) {
                return -1;
            }
            else if (Integer.parseInt(lhs.distance) > Integer.parseInt(rhs.distance)){
                return 1;
            } else {
                return 0;
            }
        }
    }


    public DeviceStatus findLocalPhone(String deviceId) {
        for(DeviceStatus device : deviceList) {
            if(device.deviceId.toString().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    public DeviceLocation findLocation(DeviceStatus device) {
        if (device != null) {
            for (OU44Location l : locations) {
                if (device.roomId.equals(l.getProperties().getRoomId())) {
                    DeviceLocation deviceLocation = new DeviceLocation(l.getProperties().getFloor(), l.getGeometry().getCoordinates().get(1), l.getGeometry().getCoordinates().get(0));
                    return deviceLocation;
                }
            }
        }
        return new DeviceLocation("0", localLat, localLng);
    }
}
