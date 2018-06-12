package ua.com.icabbyclient.icabbyclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.UUID;

import ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper.BluetoothClientServer;
import ua.com.icabbyclient.icabbyclient.fragments.ApiCommunicationClientFragment;
import ua.com.icabbyclient.icabbyclient.fragments.DeviceConnectionFragment;
import ua.com.icabbyclient.icabbyclient.fragments.TunnelCommunicationFragment;

public class MainActivity extends AppCompatActivity {

    public static final String DEVICE_NAME = "PimServer";
    private static final String TUNNEL_SEVER_NAME = "TunnelServer";
    public static UUID sUUID_PIM = UUID.fromString("895a86e2-6a31-11e8-adc0-fa7ae01bbebc");
    public static UUID sUUID_Tunnel = UUID.fromString("c90c5b86-536f-11e8-9c2d-fa7ae01bbebc");

    private DeviceConnectionFragment mDeviceConnectionFragment;
    private ApiCommunicationClientFragment mApiCommunicationClientFragment;
    private TunnelCommunicationFragment mTunnelCommunicationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDeviceConnectionFragment = new DeviceConnectionFragment();

        mApiCommunicationClientFragment = new ApiCommunicationClientFragment();

        mTunnelCommunicationFragment = new TunnelCommunicationFragment();


        final BluetoothClientServer mBluetoothServer = new BluetoothClientServer(DEVICE_NAME, sUUID_PIM, mApiCommunicationClientFragment, BluetoothClientServer.TypeConnection.PIM_APP);
        final BluetoothClientServer mBluetoothServerMeter = new BluetoothClientServer(TUNNEL_SEVER_NAME, sUUID_Tunnel, mTunnelCommunicationFragment, BluetoothClientServer.TypeConnection.TUNNEL_APP);

        mDeviceConnectionFragment.setBluetoothServerTunnel(mBluetoothServerMeter);
        mDeviceConnectionFragment.setBluetoothServerAPI(mBluetoothServer);

        mApiCommunicationClientFragment.setBluetoothConnection(mBluetoothServer);

        mTunnelCommunicationFragment.setBluetoothServerSendData(mBluetoothServerMeter);

        if (savedInstanceState == null) {
            addFragment(mDeviceConnectionFragment);
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_bluetooth_deviceconnection:
                    replaceFragment(mDeviceConnectionFragment);
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
}
