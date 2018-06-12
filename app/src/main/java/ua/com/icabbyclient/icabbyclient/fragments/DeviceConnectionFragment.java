package ua.com.icabbyclient.icabbyclient.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ua.com.icabbyclient.icabbyclient.MainActivity;
import ua.com.icabbyclient.icabbyclient.R;
import ua.com.icabbyclient.icabbyclient.adapters.DeviceListAdapter;
import ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothServerConnecting;
import ua.com.icabbyclient.icabbyclient.utils.SeparatorDecoration;

public class DeviceConnectionFragment extends Fragment implements DeviceListAdapter.OnPairButtonClickListener {
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mListDevice = new ArrayList<>();
    private DeviceListAdapter mDeviceListAdapter;
    private RecyclerView mRecyclerView;

    private BluetoothServerConnecting mBluetoothServer;
    private BluetoothServerConnecting mBluetoothServerMeterTunnel;

    public void setBluetoothServerAPI(BluetoothServerConnecting bluetoothServer) {
        mBluetoothServer = bluetoothServer;
    }

    public void setBluetoothServerTunnel(BluetoothServerConnecting bluetoothServer) {
        mBluetoothServerMeterTunnel = bluetoothServer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icabby_connection, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mListDevice.addAll(mBluetoothAdapter.getBondedDevices());
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SeparatorDecoration decoration = new SeparatorDecoration(getActivity(), Color.GRAY, 1.5f);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDeviceListAdapter = new DeviceListAdapter(getActivity(), mListDevice, this);
        mRecyclerView.setAdapter(mDeviceListAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListDevice.clear();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == MainActivity.RESULT_OK) {
                    mListDevice.addAll(mBluetoothAdapter.getBondedDevices());
                    mDeviceListAdapter.notifyDataSetChanged();
                }
        }
    }

    @Override
    public void onConnectionToPim(final int position) {
        BluetoothDevice bluetoothDevice = mListDevice.get(position);
        mBluetoothServer.connectToServer(bluetoothDevice);
    }

    @Override
    public void onConnectionToMeterTunnel(final int position) {
        BluetoothDevice bluetoothDevice = mListDevice.get(position);
        mBluetoothServerMeterTunnel.connectToServer(bluetoothDevice);
    }
}
