package ua.com.icabbyclient.icabbyclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ua.com.icabbyclient.icabbyclient.adapters.DeviceListAdapter;
import ua.com.icabbyclient.icabbyclient.bluetooth_helper.BluetoothHelperService;
import ua.com.icabbyclient.icabbyclient.bluetooth_helper.BluetoothServer;
import ua.com.icabbyclient.icabbyclient.fragments.BluetoothClientFragment;
import ua.com.icabbyclient.icabbyclient.fragments.ICabbyClientFragment;
import ua.com.icabbyclient.icabbyclient.fragments.PimResponseMessage;

public class MainActivity extends AppCompatActivity {

    public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static final String TAG = "AppsBluetooth";
    public static final String DEVICE_NAME = "BluetoothName";
    public static final String TOAST = "BluetoothService";

    private static final int REQUEST_ENABLE_BT = 1;
    public static final int MESSAGE_DEVICE_NAME = 2;
    public static final int MESSAGE_READ = 3;
    public static final int MESSAGE_WRITE = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_STATE_CHANGE = 6;
    public static final int PICK_IMAGE_REQUEST = 7;
    public static final int MESSAGE_IMAGE_READ = 8;

    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mListDevice = new ArrayList<>();
    private DeviceListAdapter mDeviceListAdapter;

    private BluetoothServer mBluetoothServer;
    private UUID mId = UUID.fromString("c90c5b86-536f-11e8-9c2d-fa7ae01bbebc");

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                if (msg.what == BluetoothServer.MSG_ID) {
                    log("< " + new String(((byte[]) msg.obj), 0, msg.arg1));
                }
                if (msg.what == BluetoothServer.LOG_ID) {
                    log((String) msg.obj);
                }
            }
        };

            mBluetoothServer = new BluetoothServer("MyServer", mId, mHandler);

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
                    replaceFragment(new BluetoothClientFragment(mBluetoothServer));
                    return true;
                case R.id.navigation_send_data_to_pim:
                    replaceFragment(new ICabbyClientFragment(mBluetoothServer));
                    return true;
                case R.id.navigation_pim_communicate:
                    replaceFragment(PimResponseMessage.newInstance());
                    return true;
                    case R.id.navigation_tunnel_communicate:
                    replaceFragment(PimResponseMessage.newInstance());
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
        Log.d("iCabbyLogs: ", log);
    }

}
