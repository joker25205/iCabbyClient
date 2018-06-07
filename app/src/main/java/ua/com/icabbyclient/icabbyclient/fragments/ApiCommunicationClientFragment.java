package ua.com.icabbyclient.icabbyclient.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ua.com.icabbyclient.icabbyclient.ConnectedThreadListener;
import ua.com.icabbyclient.icabbyclient.R;
import ua.com.icabbyclient.icabbyclient.adapters.LogsAdapter;
import ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothServer;
import ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothServerSendData;
import ua.com.icabbyclient.icabbyclient.model.Args;
import ua.com.icabbyclient.icabbyclient.model.TripStatusRequest;


@SuppressLint("ValidFragment")
public class ApiCommunicationClientFragment extends Fragment implements View.OnClickListener, ConnectedThreadListener {

    private static final String TRIP_ID = "A2514F2";

    private Button btnStartRide, btnStopRide, btnFinishRide, btnMiterUpdateTripInformation;
    private RecyclerView mRecyclerView;

    private List<String> mLogsList = new ArrayList<>();
    private LogsAdapter mLogsAdapter;
    private BluetoothServerSendData mBluetoothConnect;

    private Handler mHandler;

    public ApiCommunicationClientFragment() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                setPimMessage("<--- Pim: " + msg.obj);
            }
        };
    }

    public void setBluetoothConnection(BluetoothServerSendData bluetoothConnect) {
        mBluetoothConnect = bluetoothConnect;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icabby_client, container, false);

        btnStartRide = view.findViewById(R.id.start_ride);
        btnStartRide.setOnClickListener(this);
        btnStopRide = view.findViewById(R.id.stop_ride);
        btnStopRide.setOnClickListener(this);
        btnFinishRide = view.findViewById(R.id.finish_ride);
        btnFinishRide.setOnClickListener(this);
        btnMiterUpdateTripInformation = view.findViewById(R.id.update_trip);
        btnMiterUpdateTripInformation.setOnClickListener(this);

        mRecyclerView = view.findViewById(R.id.logs_recycler_view);
        mLogsAdapter = new LogsAdapter(mLogsList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mLogsAdapter);

        return view;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.start_ride:
                String sendMessageMeterOn = getICabbyData("TRIP_STATUS", 320, 0, false, 320, TRIP_ID, "StartRide", false);
                setPimMessage("---> Pim: " + sendMessageMeterOn);
                mBluetoothConnect.send(sendMessageMeterOn.getBytes());
                break;
            case R.id.stop_ride:
                String sendMessageUpdateTrip = getICabbyData("TRIP_STATUS", 600, 800, false, 1400, TRIP_ID, "UpdateRide", false);
                setPimMessage("---> Pim: " + sendMessageUpdateTrip);
                mBluetoothConnect.send(sendMessageUpdateTrip.getBytes());
                break;
            case R.id.finish_ride:
                String sendMessageStopRide = getICabbyData("TRIP_STATUS", 600, 400, false, 1000, TRIP_ID, "StopRide", false);
                setPimMessage("---> Pim: " + sendMessageStopRide);
                mBluetoothConnect.send(sendMessageStopRide.getBytes());

                break;
            case R.id.update_trip:
                String sendMessageFinishRide = getICabbyData("TRIP_STATUS", 600, 800, false, 1400, TRIP_ID, "FinishRide", false);
                setPimMessage("---> Pim: " + sendMessageFinishRide);
                mBluetoothConnect.send(sendMessageFinishRide.getBytes());
                break;
        }
    }

    private void setPimMessage(final String s) {
        mLogsList.add(s);
        if (mLogsAdapter != null)
            mLogsAdapter.notifyDataSetChanged();
    }

    private String getICabbyData(final String cmd, final int fare, final int extras, final boolean flatRate, final int totalAmount, final String tripID, final String status, final boolean account) {
        TripStatusRequest iCabbyData = new TripStatusRequest();
        iCabbyData.setCmd(cmd);
        Args args = new Args();
        args.setFare(fare);
        args.setExtras(extras);
        args.setFlatRate(false);
        args.setStatus(status);
        args.setTotalAmount(totalAmount);
        args.setTripId(tripID);
        args.setAccount(account);
        iCabbyData.setArgs(args);
        return new Gson().toJson(iCabbyData);
    }

    @Override
    public void onIncomingBtByte(final byte[] b) {
        String s = new String(b);
        Message m = mHandler.obtainMessage(BluetoothServer.PIM_MESSAGE, s);
        mHandler.sendMessage(m);
    }

    @Override
    public void onIncomingBtTunnelByte(final String meterData) {
        Message m = mHandler.obtainMessage(BluetoothServer.PIM_MESSAGE, meterData);
        mHandler.sendMessage(m);
    }

}
