package ua.com.icabbyclient.icabbyclient.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ua.com.icabbyclient.icabbyclient.MainActivity;
import ua.com.icabbyclient.icabbyclient.R;
import ua.com.icabbyclient.icabbyclient.adapters.DeviceListAdapter;
import ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothServerMeterTunnel;
import ua.com.icabbyclient.icabbyclient.utils.SeparatorDecoration;

@SuppressLint("ValidFragment")
public class ICabbyConnectionFragment extends Fragment implements DeviceListAdapter.OnPairButtonClickListener{
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mListDevice = new ArrayList<>();
    private DeviceListAdapter mDeviceListAdapter;

    RecyclerView mRecyclerView;

    private BluetoothServerMeterTunnel mBluetoothServer;


    public ICabbyConnectionFragment(final BluetoothServerMeterTunnel bluetoothServer) {
        mBluetoothServer = bluetoothServer;
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
            // We need to enable the Bluetooth, so we ask the user
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // REQUEST_ENABLE_BT es un valor entero que vale 1
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            discoverableDevice();
            startScanDevice();
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SeparatorDecoration decoration = new SeparatorDecoration(getActivity(), Color.GRAY, 1.5f);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDeviceListAdapter = new DeviceListAdapter(getActivity(), mListDevice, this);
        mRecyclerView.setAdapter(mDeviceListAdapter);
        getActivity().registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

        return view;

    }

    private void startScanDevice() {
        mListDevice.clear();

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mReceiverSearchDevice, discoverDevicesIntent);
        }
        if (!mBluetoothAdapter.isDiscovering()) {
            //check BT permissions in manifest
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                checkBTPermissions();
            }
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mReceiverSearchDevice, discoverDevicesIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == MainActivity.RESULT_OK) {
                    startScanDevice();
                }
            case 1001:
                startScanDevice();
                break;
        }
    }

    private final BroadcastReceiver mReceiverSearchDevice = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // A Bluetooth device was found
                // Getting device information from the intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mListDevice.add(device);
                mDeviceListAdapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiverSearchDevice);
        getActivity().unregisterReceiver(mPairReceiver);
        mListDevice.clear();
    }

    @Override
    public void onPairButtonClick(final int position) {
        BluetoothDevice device = mListDevice.get(position);

        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            unpairDevice(device);
        } else {
            Toast.makeText(getContext(), "Pairing...", Toast.LENGTH_SHORT).show();
            pairDevice(device);
        }
    }

    @Override
    public void onSelectConnectionDevice(final int position) {
        BluetoothDevice bluetoothDevice = mListDevice.get(position);
        mBluetoothServer.connectToServer(bluetoothDevice);

        bluetoothDevice.getUuids();
        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            mBluetoothServer.connectToServer(bluetoothDevice);
        } else {
            Toast.makeText(getContext(), "Pairing...", Toast.LENGTH_SHORT).show();
            pairDevice(bluetoothDevice);
        }
    }


    private void pairDevice(final BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpairDevice(final BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    Toast.makeText(context, "Unpaired", Toast.LENGTH_SHORT).show();

                }

                mDeviceListAdapter.notifyDataSetChanged();
            }
        }
    };

    private void discoverableDevice() {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000);
        startActivity(discoverableIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d("", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    void log(String log) {
        Log.d("TAG ", log);
    }
}
