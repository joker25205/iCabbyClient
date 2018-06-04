package ua.com.icabbyclient.icabbyclient.bluetooth_helper;

public interface BluetoothMessageSender {

    public void onBtSendRawMessage(int message, String rawMsg);
}
