package ua.com.icabbyclient.icabbyclient.bluetooth_pim_helper;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import ua.com.icabbyclient.icabbyclient.ConnectedThreadListener;

public class BluetoothServer {
    public static final int MSG_ID = 1;
    public static final int PIM_MESSAGE = 2;
    private final String TAG = "BluetoothController";
    private final String mServerName;
    private final UUID mServerId;
    private final BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    private int mState;
    private final int STATE_DISCONNECTED = 0;
    private final int STATE_LISTENING = 1;
    private final int STATE_CONNECTING = 2;
    private final int STATE_CONNECTED = 3;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private final ConnectedThreadListener mConnectedThreadListener;


    public BluetoothServer(final String serverName, final UUID serverId, Handler handler, final  ConnectedThreadListener connectedThreadListener) {
        this.mServerName = serverName;
        mServerId = serverId;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
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
        // destroy currently server if it exists
        if (mAcceptThread != null) {
            mAcceptThread.interrupt();
            mAcceptThread = null;
        }
        // starting listen for new entry connection
        mAcceptThread = new AcceptThread();
        mAcceptThread.start();
    }

    public synchronized void connectToServer(BluetoothDevice device) {
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

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

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

    }

    class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(mServerName, mServerId);
            } catch (IOException e) {
                Log.e(TAG, "> ACCEPT THREAD >" + e);
            }
            mmServerSocket = tmp;
            if (mmServerSocket != null) {
                setState(STATE_LISTENING);
            } else
                setState(STATE_DISCONNECTED);

        }

        public void run() {
            BluetoothSocket socket;
            while (mState == STATE_LISTENING) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

                if (socket != null) {
                    onConnect(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        Log.d(TAG, "code 3");
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "code 4");
            }
        }
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
            mBluetoothAdapter.cancelDiscovery();

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

            synchronized (BluetoothServer.this) {
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
                    numBytes = mmInStream.read(mmBuffer);

                    byte[] b1 = new byte[numBytes];

                    System.arraycopy(mmBuffer, 0, b1, 0, numBytes);


                    mConnectedThreadListener.onIncomingBtByte(b1);
                } catch (IOException e) {
                    startServer();
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

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
        Message m = mHandler.obtainMessage(PIM_MESSAGE, log);
        mHandler.sendMessage(m);
        Log.d(TAG, log);
    }

}
