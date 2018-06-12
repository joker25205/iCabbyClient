package ua.com.icabbyclient.icabbyclient.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ua.com.icabbyclient.icabbyclient.ConnectedThreadListener;
import ua.com.icabbyclient.icabbyclient.R;
import ua.com.icabbyclient.icabbyclient.adapters.LogsAdapter;
import ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothClientServer;
import ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothServerSendData;
import ua.com.icabbyclient.icabbyclient.utils.HwMeterPacket;

public class TunnelCommunicationFragment extends Fragment implements ConnectedThreadListener {

    private List<String> mLogsList = new ArrayList<>();
    private LogsAdapter mLogsAdapter;
    private RecyclerView mRecyclerView;
    private Handler mHandler;

    private BluetoothServerSendData mBluetoothServerSendData;

    public void setBluetoothServerSendData(BluetoothServerSendData bluetoothServerSendData) {
        mBluetoothServerSendData = bluetoothServerSendData;
    }

    public TunnelCommunicationFragment() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                mLogsList.add((String) msg.obj);
                if (mLogsAdapter != null)
                    mLogsAdapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meter_comunication, container, false);
        mRecyclerView = view.findViewById(R.id.logs_recycler_view);
        mLogsAdapter = new LogsAdapter(mLogsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mLogsAdapter);
        return view;
    }

    @Override
    public void onMessageReceived(final byte[] b) {
        Message m = mHandler.obtainMessage(BluetoothClientServer.TUNNEL_MESSAGE, new String(b));
        mHandler.sendMessage(m);

        if (mBluetoothServerSendData != null)
            mBluetoothServerSendData.send(new HwMeterPacket(HwMeterPacket.ID_ACK).toBytes());
    }

}
