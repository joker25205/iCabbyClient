package ua.com.icabbyclient.icabbyclient.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ua.com.icabbyclient.icabbyclient.R;


public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<BluetoothDevice> mList;
    private Context mContext;
    private OnPairButtonClickListener mListener;


    public DeviceListAdapter(final Context context, final List<BluetoothDevice> list, final OnPairButtonClickListener pairButtonClickListener) {
        mContext = context;
        mList = list;
        mListener = pairButtonClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BluetoothDevice device = mList.get(position);
        holder.title.setText(device.getName());
        holder.macAddress.setText(device.getAddress());

        holder.connectToPimApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mListener.onConnectionToPim(position);
            }
        });

        holder.connectToTunnelApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mListener.onConnectionToMeterTunnel(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView macAddress;
        public Button connectToPimApp;
        public Button connectToTunnelApp;


        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            macAddress = (TextView) view.findViewById(R.id.tv_mac_address);
            connectToPimApp = (Button) view.findViewById(R.id.connect_to_pim_app);
            connectToTunnelApp = (Button) view.findViewById(R.id.connect_to_tunnel_app);
        }
    }

    public interface OnPairButtonClickListener {

        void onConnectionToPim(int position);

        void onConnectionToMeterTunnel(int position);
    }
}
