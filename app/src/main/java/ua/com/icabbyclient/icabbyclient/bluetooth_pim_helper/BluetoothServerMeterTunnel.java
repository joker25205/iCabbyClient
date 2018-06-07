package ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import ua.com.icabbyclient.icabbyclient.ConnectedThreadListener;
import ua.com.icabbyclient.icabbyclient.utils.HwMeterPacket;

public class BluetoothServerMeterTunnel implements BluetoothServerConnecting, BluetoothServerSendData {
    private final String TAG = "BluetoothController";
    private final String mServerName;
    private final UUID mServerId;


    private int mState;
    private final int STATE_DISCONNECTED = 0;
    private final int STATE_LISTENING = 1;
    private final int STATE_CONNECTING = 2;
    private final int STATE_CONNECTED = 3;

    private BluetoothServerMeterTunnel.ConnectThread mConnectThread;
    private BluetoothServerMeterTunnel.ConnectedThread mConnectedThread;

    private final ConnectedThreadListener mConnectedThreadListener;


    public BluetoothServerMeterTunnel(final String serverName, final UUID serverId, final ConnectedThreadListener connectedThreadListener) {
        mServerName = serverName;
        mServerId = serverId;
        mConnectedThreadListener = connectedThreadListener;
        setState(STATE_DISCONNECTED);
    }


    public synchronized void startServer() {
        // destroy currently connection if it exists
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // destroy currently connect if it exists
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

    }
    @Override
    public synchronized void connectToServer(BluetoothDevice device) {
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        mConnectThread = new BluetoothServerMeterTunnel.ConnectThread(device);
        mConnectThread.start();
    }

    @Override
    public void send(byte[] b) {
        mConnectedThread.write(b);
    }

    public synchronized void onConnect(BluetoothSocket socket) {

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }


        mConnectedThread = new BluetoothServerMeterTunnel.ConnectedThread(socket);
        mConnectedThread.start();

    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(mServerId);
            } catch (IOException e) {
                Log.d(TAG, "code 5");
            }
            mmSocket = tmp;
            setState(STATE_CONNECTING);
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.d(TAG, "code 5");
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.d(TAG, "code 6");
                }
                return;
            }

            synchronized (BluetoothServerMeterTunnel.this) {
                mConnectThread = null;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            onConnect(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "code 7");
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "> CONNECTED THREAD > Error to get in/out stream" + e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            setState(STATE_CONNECTED);
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes;

            while (mState == STATE_CONNECTED) {
                try {
                    final byte[] buffer = new byte[1024];

                    byte stx = (byte) mmInStream.read();

                    if (stx == HwMeterPacket.STX) {
                        buffer[0] = HwMeterPacket.STX;
                        byte id = (byte) mmInStream.read();
                        buffer[1] = id;
                        int len = mmInStream.read();

                        for (int i = 0; i < len; i++) {
                            buffer[3 + i] = (byte) mmInStream.read();
                        }
                        byte bcc = (byte) mmInStream.read();
                        buffer[3 + len] = bcc;
                        buffer[3 + len + 1] = (byte) mmInStream.read();
                        HwMeterPacket packet = new HwMeterPacket();
                        if (packet.fromBytesLongData(buffer, len)) {
                            Log.d("MeterData", packet.toHumanString());
                            mConnectedThreadListener.onIncomingBtTunnelByte(packet.toHumanString());
                            write(new HwMeterPacket(HwMeterPacket.ID_ACK).toBytes());
                        }
                    }
                } catch (IOException e) {
                    startServer();
                    break;
                }
            }
        }


        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                mmOutStream.flush();
                // Share the sent message with the UI activity.

            } catch (IOException e) {
                Log.d(TAG, "code 11");
                // Send a failure message back to the activity.
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setState(STATE_DISCONNECTED);
        }
    }

    private void setState(int state) {
        switch (state) {
            case STATE_DISCONNECTED:
                log("- STATE_DISCONNECTED");
                break;
            case STATE_LISTENING:
                log("- STATE_LISTENING");
                break;
            case STATE_CONNECTING:
                log("- STATE_CONNECTING");
                break;
            case STATE_CONNECTED:
                log("- STATE_CONNECTED");
                break;
        }
        mState = state;
    }

    private void log(String log) {
        mConnectedThreadListener.onIncomingBtTunnelByte(log);
        Log.d(TAG, log);
    }

}

