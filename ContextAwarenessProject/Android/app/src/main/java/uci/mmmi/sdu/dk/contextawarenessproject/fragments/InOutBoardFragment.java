package uci.mmmi.sdu.dk.contextawarenessproject.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static uci.mmmi.sdu.dk.contextawarenessproject.R.id.status;

public class InOutBoardFragment extends ListFragment {

    private ArrayAdapter<InOutBoardListItem> adapter;
    private List<InOutBoardListItem> listData;

    private List<DeviceStatus> deviceList;
    private DeviceLocation localPhoneLocation;
    private Collection<OU44Location> locations;
    private LinkedList<Beacons.beaconData> JSONbeacons;



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

        NetworkManager.getInstance(getActivity()).getUbicomService().getAllDeviceStatuses().enqueue(new Callback<List<RemoteDeviceStatus>>() {
            @Override
            public void onResponse(Call<List<RemoteDeviceStatus>> call, Response<List<RemoteDeviceStatus>> response) {
                ArrayList<RemoteDeviceStatus> networkList = (ArrayList<RemoteDeviceStatus>) response.body();
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
                for (DeviceStatus status : deviceList) {
                    listData.add(new InOutBoardListItem(status));
                }
            }

            @Override
            public void onFailure(Call<List<RemoteDeviceStatus>> call, Throwable t) {
                Log.e("NETWORK", "Request to get all data failed");
            }
        });
//        List<InOutBoardListItem> testList = new LinkedList<>();
//        testList.add(new InOutBoardListItem("Peter", "U175", 1, true));
//        testList.add(new InOutBoardListItem("Hal", "U176", 2, true));
//        testList.add(new InOutBoardListItem("Jens", "U174", 1232131, false));

        if(adapter == null) {
            adapter = new InOutBoardArrayAdapter(getContext());
        }
        adapter.clear();
        adapter.addAll(listData);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setListShown(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setAdapter(adapter);
        setListShown(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void calculateDistance() {
        for (DeviceStatus device : deviceList) {
            DeviceLocation deviceLocation = findLocation(device);
            if(deviceLocation.floor.equals(localPhoneLocation.floor)) {
                Location me = new Location("");
                Location dest = new Location("");

                me.setLatitude(localPhoneLocation.lat);
                me.setLongitude(localPhoneLocation.lng);

                dest.setLatitude(deviceLocation.lat);
                dest.setLongitude(deviceLocation.lng);

                float dist = me.distanceTo(dest);
                device.distance = String.valueOf(Math.round(dist));
            }
            else {
                device.distance = deviceLocation.floor;
            }
        }
    }

    public DeviceStatus findLocalPhone(String deviceId) {
        for(DeviceStatus device : deviceList) {
            if(device.deviceId.equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    public DeviceLocation findLocation(DeviceStatus device) {
        for(OU44Location l : locations) {
            if(device.roomId.equals(l.getProperties().getRoomId())) {
                DeviceLocation deviceLocation = new DeviceLocation(l.getProperties().getFloor(), l.getGeometry().getCoordinates().get(0), l.getGeometry().getCoordinates().get(1));
                return deviceLocation;
            }
        }
        return null;
    }
}
