package ua.com.icabbyclient.icabbyclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.UUID;

import ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothServer;
import ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothServerMeterTunnel;
import ua.com.icabbyclient.icabbyclient.fragments.ApiCommunicationClientFragment;
import ua.com.icabbyclient.icabbyclient.fragments.BluetoothClientFragment;
import ua.com.icabbyclient.icabbyclient.fragments.ICabbyConnectionFragment;
import ua.com.icabbyclient.icabbyclient.fragments.TunnelCommunicationFragment;

public class MainActivity extends AppCompatActivity implements ApiCommunicationClientFragment.MeterFunctions {

    public static final String DEVICE_NAME = "PimServer";
    private static final String TUNNEL_SEVER_NAME = "TunnelServer";
    public static UUID sUUID_PIM = UUID.fromString("895a86e2-6a31-11e8-adc0-fa7ae01bbebc");
    public static UUID sUUID_Tunnel = UUID.fromString("c90c5b86-536f-11e8-9c2d-fa7ae01bbebc");

    private BluetoothServer mBluetoothServer;
    private BluetoothServerMeterTunnel mBluetoothServerMeter;

    private TunnelCommunicationFragment mTunnelCommunicationFragment;
    private ApiCommunicationClientFragment mApiCommunicationClientFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTunnelCommunicationFragment = new TunnelCommunicationFragment();
        mApiCommunicationClientFragment = new ApiCommunicationClientFragment();
        mApiCommunicationClientFragment.setClickComand(this);

        mBluetoothServer = new BluetoothServer(DEVICE_NAME, sUUID_PIM, mApiCommunicationClientFragment);
        mBluetoothServerMeter = new BluetoothServerMeterTunnel(TUNNEL_SEVER_NAME, sUUID_Tunnel, mTunnelCommunicationFragment);

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
                    replaceFragment(mApiCommunicationClientFragment);
                    return true;
                case R.id.navigation_tunnel_communicate:
                    replaceFragment(mTunnelCommunicationFragment);
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

    @Override
    public void setMeterCommand(final String message) {
        if (mBluetoothServer != null)
            mBluetoothServer.send(message.getBytes());
    }
}
