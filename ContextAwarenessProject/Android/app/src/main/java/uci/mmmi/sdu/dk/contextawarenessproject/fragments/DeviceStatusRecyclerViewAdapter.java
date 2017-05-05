package uci.mmmi.sdu.dk.contextawarenessproject.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import uci.mmmi.sdu.dk.contextawarenessproject.R;
import uci.mmmi.sdu.dk.contextawarenessproject.entities.DeviceLocation;
import uci.mmmi.sdu.dk.contextawarenessproject.entities.DeviceStatus;
import uci.mmmi.sdu.dk.contextawarenessproject.fragments.InOutBoard.OnListFragmentInteractionListener;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.Beacons;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.OU44Feature;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.OU44Location;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DeviceStatus} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DeviceStatusRecyclerViewAdapter extends RecyclerView.Adapter<DeviceStatusRecyclerViewAdapter.ViewHolder> {

    private final List<DeviceStatus> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context context;
    private Drawable drawable;
    private LinkedList<OU44Location> locations;



    public DeviceStatusRecyclerViewAdapter(List<DeviceStatus> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;

        InputStream inStream = context.getResources().openRawResource(R.raw.all_rooms_sdu);
        Reader rd = new BufferedReader(new InputStreamReader(inStream));
        Gson gson = new Gson();
        OU44Location obj = gson.fromJson(rd, OU44Location.class);
        locations = new LinkedList<>(Arrays.asList(obj));
    }

    public void calculateDistance()
    {
        String floor = "1";
        for (DeviceStatus device:mValues) {
            DeviceLocation deviceLocation = findLocation(device);
            if(deviceLocation.floor.equals(floor)) {
                Location me = new Location("");
                Location dest = new Location("");

                //me.setLatitude(myLat);
                //me.setLongitude(myLong);

                dest.setLatitude(deviceLocation.lat);
                dest.setLongitude(deviceLocation.lng);

                float dist = me.distanceTo(dest);
                device.distance = String.valueOf(Math.round(dist));
            }
            else
                device.distance = deviceLocation.floor;
        }
    }

    public DeviceLocation findLocation(DeviceStatus device)
    {
        for(OU44Location l : locations) {

            if(device.roomId.equals(l.getProperties().getRoomId())) {
                DeviceLocation deviceLocation = new DeviceLocation(l.getProperties().getFloor(), l.getGeometry().getCoordinates().get(0), l.getGeometry().getCoordinates().get(1));
                return deviceLocation;
            }
        }
        return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_devicestatus2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUsername.setText(mValues.get(position).username);
        holder.mLocation.setText(mValues.get(position).location);
        holder.mdistance.setText(mValues.get(position).distance);

        drawable = context.getResources().getDrawable(R.drawable.dot);
        if(mValues.get(position).status == DeviceStatus.Status.IN)
            drawable.mutate().setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.SRC_IN);
        else
            drawable.mutate().setColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.SRC_IN);

        holder.mStatus.setImageDrawable(drawable);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mUsername;
        public final ImageView mStatus;
        public final TextView mLocation;
        public final TextView mdistance;

        public DeviceStatus mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mStatus = (ImageView) view.findViewById(R.id.status);
            mUsername = (TextView) view.findViewById(R.id.id);
            mLocation = (TextView) view.findViewById(R.id.location);
            mdistance = (TextView) view.findViewById(R.id.distance);
        }
        @Override
        public String toString() {
            return super.toString() + " '" + mUsername.getText() + "'";
        }
    }
}
