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

    private Context context;

    public InOutBoardArrayAdapter(Context context) {
        super(context, R.layout.listitem_inout);
        this.context = context;
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
            if (listItem.getStatus().status == DeviceStatus.Status.IN) {
                drawable.mutate().setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.SRC_IN);
                if (listItem.getStatus().distance.isEmpty()) {
                    holder.distanceTextView.setText("-  ");
                } else {
                    holder.distanceTextView.setText(listItem.getStatus().distance);
                }
            } else if (listItem.getStatus().status == DeviceStatus.Status.OUT){
                drawable.mutate().setColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.SRC_IN);
                holder.distanceTextView.setText("-  ");
            } else if (listItem.getStatus().status == DeviceStatus.Status.HIDDEN){
                drawable.mutate().setColorFilter(Color.parseColor("#D3D3D3"), PorterDuff.Mode.SRC_IN);
                holder.distanceTextView.setText("-  ");
            }
            holder.statusImageView.setImageDrawable(drawable);
            holder.locationTextView.setText(listItem.getStatus().location);
            holder.userNameTextView.setText(listItem.getStatus().username);
        }
        return rowView;
    }

    static class ViewHolder {
        TextView locationTextView;
        TextView distanceTextView;
        TextView userNameTextView;
        ImageView statusImageView;
    }
}
