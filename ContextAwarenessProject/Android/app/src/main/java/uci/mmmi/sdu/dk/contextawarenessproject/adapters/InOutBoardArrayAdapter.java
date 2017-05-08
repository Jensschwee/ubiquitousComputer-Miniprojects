package uci.mmmi.sdu.dk.contextawarenessproject.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uci.mmmi.sdu.dk.contextawarenessproject.R;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.DeviceLocation;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.DeviceStatus;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.Beacons;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.InOutBoardListItem;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.OU44Location;

/**
 * Created by bullari on 5/8/17.
 */

public class InOutBoardArrayAdapter extends ArrayAdapter<InOutBoardListItem> {

    private List<DeviceStatus> mValues;
    private DeviceLocation localPhoneLocation;
    private Collection<OU44Location> locations;
    private LinkedList<Beacons.beaconData> JSONbeacons;
    private Context context;

    public InOutBoardArrayAdapter(Context context) {
        super(context, R.layout.listitem_inout);
        this.context = context;

        InputStream inStream = context.getResources().openRawResource(R.raw.all_rooms_sdu);
        Reader rd = new BufferedReader(new InputStreamReader(inStream));
        Gson gson = new Gson();
//        locations = gson.fromJson(rd, OU44LocationRoot.class);
        Type collectionType = new TypeToken<Collection<OU44Location>>(){}.getType();
        locations = gson.fromJson(rd, collectionType);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.listitem_inout, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.locationTextView = (TextView) rowView.findViewById(R.id.list_item_inout_location_textview);
            viewHolder.distanceTextView = (TextView) rowView.findViewById(R.id.list_item_inout_distance_textview);
            viewHolder.statusImageView = (ImageView) rowView.findViewById(R.id.list_item_inout_imageview);
            viewHolder.userNameTextView = (TextView) rowView.findViewById(R.id.list_item_inout_username_textview);
            rowView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        InOutBoardListItem listItem = getItem(position);

        if (listItem != null) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.dot);
            if (listItem.isIn()) {
                drawable.mutate().setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.SRC_IN);
            } else {
                drawable.mutate().setColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.SRC_IN);
            }
            holder.statusImageView.setImageDrawable(drawable);
            holder.locationTextView.setText(listItem.getLocation());
            holder.distanceTextView.setText(Integer.toString(listItem.getDistance()));
            holder.userNameTextView.setText(listItem.getUsername());
        }
        return rowView;
    }

    static class ViewHolder {
        TextView locationTextView;
        TextView distanceTextView;
        TextView userNameTextView;
        ImageView statusImageView;
    }

    public void calculateDistance()
    {
        for (DeviceStatus device:mValues) {
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
            else
                device.distance = deviceLocation.floor;
        }
    }

    public DeviceStatus findLocalPhone(String deviceId)
    {
        for(DeviceStatus device : mValues) {
            if(device.deviceId.equals(deviceId))
            {
                return device;
            }
        }
        return null;
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
}
