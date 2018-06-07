package ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper;

public interface BluetoothMessageListener {
    void onBtRawMessage(int message, String rawMsg);
}
