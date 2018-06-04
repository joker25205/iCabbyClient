package ua.com.icabbyclient.icabbyclient.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import ua.com.icabbyclient.icabbyclient.R;
import ua.com.icabbyclient.icabbyclient.bluetooth_helper.BluetoothHelperService;
import ua.com.icabbyclient.icabbyclient.bluetooth_helper.BluetoothMessageSender;
import ua.com.icabbyclient.icabbyclient.bluetooth_helper.BluetoothServer;
import ua.com.icabbyclient.icabbyclient.model.Args;
import ua.com.icabbyclient.icabbyclient.model.ICabbyData;


@SuppressLint("ValidFragment")
public class ICabbyClientFragment extends Fragment implements BluetoothMessageSender, BluetoothHelperService.Listener, View.OnClickListener {

    private static final String TRIP_ID = "A2514F2";
    Button btnMiterOn;
    Button btnMiterAddExtras;
    Button btnMiterTimeOff;
    Button btnMiterOff;
    Button btnMiterUpdateTripInformation;

    private BluetoothServer mBluetoothServer;

    public ICabbyClientFragment(final BluetoothServer bluetoothServer) {
        mBluetoothServer = bluetoothServer;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icabby_client, container, false);

        btnMiterOn = view.findViewById(R.id.miter_on);
        btnMiterOn.setOnClickListener(this);
        btnMiterTimeOff = view.findViewById(R.id.miter_time_off);
        btnMiterTimeOff.setOnClickListener(this);
        btnMiterAddExtras = view.findViewById(R.id.miter_add_extras);
        btnMiterAddExtras.setOnClickListener(this);
        btnMiterOff = view.findViewById(R.id.miter_off);
        btnMiterOff.setOnClickListener(this);
        btnMiterUpdateTripInformation = view.findViewById(R.id.update_trip);
        btnMiterUpdateTripInformation.setOnClickListener(this);
        return view;
    }

    @Override
    public void onBtSendRawMessage(final int message, final String rawMsg) {

    }

    @Override
    public void onBtStateChanged(final int previous, final String current) {

    }

    @Override
    public void onBtRawMessage(final int message, final String rawMsg) {

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.miter_on:
                Toast.makeText(getContext(), "Click Miter On", Toast.LENGTH_SHORT).show();
                String sendMessageMeterOn = getICabbyData("TRIP_STATUS", 320, 0, false, 320, TRIP_ID, "MeterOn");
                mBluetoothServer.send(sendMessageMeterOn.getBytes());
                break;
            case R.id.miter_time_off:
                Toast.makeText(getContext(), "Click Miter Time Off", Toast.LENGTH_SHORT).show();
                String sendMessageMeterOff = getICabbyData("TRIP_STATUS", 600, 400, false, 1000, TRIP_ID, "MeterTimeOff");
                mBluetoothServer.send(sendMessageMeterOff.getBytes());
                break;
            case R.id.miter_add_extras:
                Toast.makeText(getContext(), "Click Miter Add Extras", Toast.LENGTH_SHORT).show();
                String sendMessageMeterAddExtras = getICabbyData("TRIP_STATUS", 600, 800, false, 1400, TRIP_ID, "MeterExtras");
                mBluetoothServer.send(sendMessageMeterAddExtras.getBytes());
                break;
            case R.id.miter_off:
                Toast.makeText(getContext(), "Click Miter Off", Toast.LENGTH_SHORT).show();
                String sendMessageMeterMeterOff = getICabbyData("TRIP_STATUS", 600, 800, false, 1400, TRIP_ID, "MeterOff");
                mBluetoothServer.send(sendMessageMeterMeterOff.getBytes());
                break;
            case R.id.update_trip:
                Toast.makeText(getContext(), "Click Update Trip information", Toast.LENGTH_SHORT).show();
                String sendMessageMeterMeterUpdateTrip = getICabbyData("TRIP_STATUS", 600, 800, false, 1400, TRIP_ID, "UpdateTrip");
                mBluetoothServer.send(sendMessageMeterMeterUpdateTrip.getBytes());
                break;
        }
    }

    @NonNull
    private String getICabbyData(final String cmd, final int fare, final int extras, final boolean flatRate, final int totalAmount, final String tripID, final String status) {
        ICabbyData iCabbyData = new ICabbyData();
        iCabbyData.setCmd(cmd);
        Args args = new Args();
        args.setFare(fare);
        args.setExtras(extras);
        args.setFlatRate(false);
        args.setStatus(status);
        args.setTotalAmount(totalAmount);
        args.setTripId(tripID);
        iCabbyData.setArgs(args);
        return new Gson().toJson(iCabbyData);
    }
}
