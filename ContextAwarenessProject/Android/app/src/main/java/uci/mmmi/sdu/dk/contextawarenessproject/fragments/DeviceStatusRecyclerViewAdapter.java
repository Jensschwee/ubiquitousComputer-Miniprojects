package uci.mmmi.sdu.dk.contextawarenessproject.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uci.mmmi.sdu.dk.contextawarenessproject.R;
import uci.mmmi.sdu.dk.contextawarenessproject.entities.DeviceStatus;
import uci.mmmi.sdu.dk.contextawarenessproject.fragments.InOutBoard.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DeviceStatus} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DeviceStatusRecyclerViewAdapter extends RecyclerView.Adapter<DeviceStatusRecyclerViewAdapter.ViewHolder> {

    private final List<DeviceStatus> mValues;
    private final OnListFragmentInteractionListener mListener;

    public DeviceStatusRecyclerViewAdapter(List<DeviceStatus> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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
        holder.mdistance.setText("20");
        if(mValues.get(position).status == DeviceStatus.Status.IN)
            //holder.mStatus.setImageBitmap();
        //else
            //holder.mStatus.setImageBitmap();

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
