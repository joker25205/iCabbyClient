package ua.com.icabbyclient.icabbyclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothServer;
import ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothServerMeterTunnel;
import ua.com.icabbyclient.icabbyclient.fragments.BluetoothClientFragment;
import ua.com.icabbyclient.icabbyclient.fragments.ICabbyClientFragment;
import ua.com.icabbyclient.icabbyclient.fragments.ICabbyConnectionFragment;
import ua.com.icabbyclient.icabbyclient.fragments.MeterCommunicationMessageFragment;
import ua.com.icabbyclient.icabbyclient.model.Args;
import ua.com.icabbyclient.icabbyclient.model.TripStatusRequest;
import ua.com.icabbyclient.icabbyclient.utils.MeterCommand;

import static ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothServer.PIM_MESSAGE;

public class MainActivity extends AppCompatActivity implements ConnectedThreadListener, ICabbyClientFragment.MeterFunctions {

    public static UUID sUUID_PIM = UUID.fromString("c90c5b86-536f-11e8-9c2d-fa7ae01bbebc");
    public static final String DEVICE_NAME = "PimServer";

    public static UUID sUUID_Tunnel = UUID.fromString("c90c5b86-536f-11e8-9c2d-fa7ae01bbebc");
    private static final String TUNNEL_SEVER_NAME = "TunnelServer";


    private static final String TRIP_ID = "A2514F2";

    private BluetoothServer mBluetoothServer;
    private BluetoothServerMeterTunnel mBluetoothServerMeter;

    Handler mHandlerPim;
    Handler mHandlerMeterTunel;

    private MeterCommunicationMessageFragment meterCommunicationMessageFragment;
    private ICabbyClientFragment mICabbyClientFragment;
    List<String> mMeterMessage = new ArrayList<>();
    List<String> mICabbyMessage = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandlerPim = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                if (msg.what == BluetoothServer.MSG_ID) {
                    log("< " + new String(((byte[]) msg.obj), 0, msg.arg1));
                }
                if (msg.what == PIM_MESSAGE) {
                    log((String) msg.obj);
                }
            }
        };


        mHandlerMeterTunel = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                logMeter((String) msg.obj);
            }
        };

        mBluetoothServer = new BluetoothServer(DEVICE_NAME, sUUID_PIM, mHandlerPim, this);

        mBluetoothServerMeter = new BluetoothServerMeterTunnel(TUNNEL_SEVER_NAME, sUUID_Tunnel, mHandlerMeterTunel, this);

        meterCommunicationMessageFragment = new MeterCommunicationMessageFragment();
        mICabbyClientFragment = new ICabbyClientFragment(this);

        if (savedInstanceState == null) {
            addFragment(new BluetoothClientFragment(mBluetoothServer));
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_pim_client_connect:
                    replaceFragment(new BluetoothClientFragment(mBluetoothServer));
                    return true;
                case R.id.navigation_tunnel_client_connect:
                    replaceFragment(new ICabbyConnectionFragment(mBluetoothServerMeter));
                    return true;
                case R.id.navigation_send_data_to_pim:
                    replaceFragment(mICabbyClientFragment);
                    return true;
                case R.id.navigation_tunnel_communicate:
                    replaceFragment(meterCommunicationMessageFragment);
                    return true;

            }
            return false;
        }
    };

    protected void addFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_frame, fragment, null)
                .disallowAddToBackStack()
                .commit();
    }

    protected void replaceFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit();
    }

    void log(String log) {
        setPimMessage("<--- Pim: " + log);
        Log.d("iCabbyLogs: ", log);
    }

    private void setPimMessage(final String log) {
        mICabbyMessage.add(log);
        mICabbyClientFragment.updateList(mICabbyMessage);
    }

    void logMeter(String log) {
        if (meterCommunicationMessageFragment != null) {
            mMeterMessage.add(log);
            meterCommunicationMessageFragment.updateList(mMeterMessage);
        }
        Log.d("MeterTunnelLogs: ", log);
    }

    @Override
    public void onIncomingBtByte(final byte[] b) {
        mHandlerPim.sendMessage(mHandlerPim.obtainMessage(PIM_MESSAGE, new String(b)));
    }

    @Override
    public void onIncomingBtTunnelByte(final String meterInfo) {
        mHandlerMeterTunel.sendMessage(mHandlerPim.obtainMessage(PIM_MESSAGE, meterInfo));
    }

    @Override
    public void onConnectedThreadError(final Throwable reason) {

    }

    @Override
    public void setMeterCommand(final MeterCommand mMeterCommand) {
        switch (mMeterCommand) {
            case START_RIDE:
                String sendMessageMeterOn = getICabbyData("TRIP_STATUS", 320, 0, false, 320, TRIP_ID, "StartRide", false);
                mBluetoothServer.send(sendMessageMeterOn.getBytes());
                setPimMessage("---> Pim: " + sendMessageMeterOn);
                break;
            case EXTRAS:
                String sendMessageUpdateTrip = getICabbyData("TRIP_STATUS", 600, 800, false, 1400, TRIP_ID, "UpdateRide", false);
                mBluetoothServer.send(sendMessageUpdateTrip.getBytes());
                setPimMessage("---> Pim: " + sendMessageUpdateTrip);
                break;
            case STOP_RIDE:
                String sendMessageStopRide = getICabbyData("TRIP_STATUS", 600, 400, false, 1000, TRIP_ID, "StopRide", false);
                mBluetoothServer.send(sendMessageStopRide.getBytes());
                setPimMessage("---> Pim: " + sendMessageStopRide);
                break;
            case FINISH_RIDE:
                String sendMessageFinishRide = getICabbyData("TRIP_STATUS", 600, 800, false, 1400, TRIP_ID, "FinishRide", false);
                mBluetoothServer.send(sendMessageFinishRide.getBytes());
                setPimMessage("---> Pim: " + sendMessageFinishRide);
                break;
        }
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

}
