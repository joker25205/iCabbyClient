package ua.com.icabbyclient.icabbyclient;

public interface ConnectedThreadListener {
    void onIncomingBtByte(byte[] b);

    void onIncomingBtTunnelByte(String meterData);
}
