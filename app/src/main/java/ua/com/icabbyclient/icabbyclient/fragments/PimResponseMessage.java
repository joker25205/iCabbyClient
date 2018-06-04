package ua.com.icabbyclient.icabbyclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.com.icabbyclient.icabbyclient.R;
import ua.com.icabbyclient.icabbyclient.bluetooth_helper.BluetoothMessageListener;

public class PimResponseMessage  extends Fragment implements BluetoothMessageListener {

    public static PimResponseMessage newInstance() {
        return new PimResponseMessage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pim_client, container, false);
    }

    @Override
    public void onBtRawMessage(final int message, final String rawMsg) {
        switch (message){

        }
    }
}
