package ua.com.icabbyclient.icabbyclient.utils;

import com.google.gson.Gson;

public class HwMeterPacket {

    private static final String TAG = "HwMeterPacket";

    /*******
     * METER ON/OFF STATE CHANGE
     *********************************/
    public static final byte METER_STATE_OFF = 0x30;
    public static final byte METER_STATE_ON = 0x31;
    public static final byte METER_STATE_TIME_OFF = 0x32;
    public static final byte METER_STATE_HIRED_TIME_OFF_PAYMENT = 0x33;


    // <-- means Meter To MDT, --> means MDT to Meter, <--> means both ways
    public static final byte ID_UNSUPPORTED = 0x59;
    public static final byte ID_ACK = 0x5A; // <-->
    public static final byte ID_METER_ON_OFF_STATE_CHANGE = 0x41; // <--
    public static final byte ID_REQUEST_METER_TRIP_DATA = 0x44;  // -->
    public static final byte ID_PRINT_BLOCK = 0x45;  // -->
    public static final byte ID_ENABLE_DISABLE_METER = 0x47;  // -->
    public static final byte ID_REQUEST_PRINTER_STATUS = 0x48;  // -->
    public static final byte ID_REQUEST_METER_STATUS = 0x49;  // -->
    public static final byte ID_REPORT_METER_STATUS = 0x4A;  // <--
    public static final byte ID_REPORT_METER_TRIP_DATA = 0x4B;  // <--
    public static final byte ID_REQUEST_METER_STATISTICS = 0x4E;  // -->
    public static final byte ID_REPORT_METER_STATISTICS = 0x4F;  // <--
    public static final byte ID_REPORT_METER_TIMED_OUT = 0x54;  // <--
    public static final byte ID_REQUEST_CURRENT_RATE = 0x61;  // -->
    public static final byte ID_REPORT_CURRENT_RATE = 0x62;  // <--
    public static final byte ID_SET_METER_CONFIGURATION = 0x67;  // -->
    public static final byte ID_REPORT_CURRENT_RUNNING_FARE = 0x68;  // <--
    public static final byte ID_PING_MDT = 0x70;  // <--
    public static final byte ID_SET_NEGOTIATED_FARE = 0x69;  // -->
    public static final byte ID_SET_TOLLS_EXTRAS = (byte) 0xC4;  // -->
    public static final byte ID_SET_NEGOTIATED_FARE_AND_PASSENGERS = (byte) 0xC5;  // -->

    public static final byte ID_MIN_FARE_AMOUNT = (byte) 0x81;  // -->
    public static final byte ID_ICABBI_JOB_TYPE = (byte) 0x82;  // -->
    public static final byte ID_ICABBI_TRIP_ID_DRIVER_STATUS = (byte) 0x83;  // -->


    public static final byte STX = 0x02;
    private byte mId;
    private int mDataLen;
    private byte[] mData;
    private int mBcc;
    public static final byte ETX = 0x03;


    public HwMeterPacket() {
        mId = 0;
        mDataLen = 0;
        mData = null;
        mBcc = 0;
    }

    public HwMeterPacket(byte id) {
        mId = id;
        mDataLen = 0;
        mData = null;
        mBcc = 0;
    }

    public byte getId() {
        return mId;
    }

    public int getDataLen() {
        return mDataLen;
    }

    public byte[] getData() {
        return mData;
    }


    public boolean fromBytesLongData(byte[] array, int dataLen) {
        if (array[0] != STX) {
            return false;
        }

        mId = array[1];
        mDataLen = dataLen;
        if (mDataLen != 0) {
            mData = new byte[mDataLen];
            for (int i = 0; i < mDataLen; i++) {
                mData[i] = array[3 + i];
            }
        } else {
            mData = null;
        }
        mBcc = array[3 + mDataLen];
        if (array[3 + mDataLen + 1] != HwMeterPacket.ETX) {
            return false;
        }
        return true;
    }

    public int getLenBytes() {
        return 5 + mDataLen;
    }

    public byte[] toBytes() {
        final byte[] buffer = new byte[getLenBytes()];
        toBytes(buffer);
        return buffer;
    }

    public int toBytes(byte[] array) {
        if (array.length < 5 + mDataLen) {
            return -1;
        }

        array[0] = STX;
        array[1] = mId;
        array[2] = (byte) mDataLen;
        for (int i = 0; i < mDataLen; i++) {
            array[3 + i] = mData[i];
        }
        mBcc = 0;
        for (int i = 0; i < 3 + mDataLen; i++) {
            mBcc ^= array[i];
        }
        array[3 + mDataLen] = (byte) mBcc;
        array[3 + mDataLen + 1] = ETX;

        return 5 + mDataLen;
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append(String.format("%02X %02X %02X ", HwMeterPacket.STX, mId, mDataLen));
        for (int i = 0; i < mDataLen; i++) {
            b.append(String.format("%02X ", mData[i]));
        }
        b.append(String.format("%02X %02X", (byte) mBcc, HwMeterPacket.ETX));
        return b.toString();
    }

    public String toHumanString() {
        StringBuffer b = new StringBuffer();
        switch (mId) {
            case ID_UNSUPPORTED:
                b.append("UNSUPPORTED");
                break;
            case ID_ACK:
                b.append("ACK");
                break;
            case ID_METER_ON_OFF_STATE_CHANGE:
                b.append(meterStatusToString(getData()[0]));
                break;
            case ID_REQUEST_METER_TRIP_DATA:
                b.append("GET TRIP DATA");
                break;
            case ID_PRINT_BLOCK:
                b.append("PRINT BLOCK: ");
                b.append(HwMeterPacketParserQueue.getStringData(getData(), 0, getDataLen()));
                break;
            case ID_ENABLE_DISABLE_METER:
                b.append("SET STATE: ");
                b.append(meterStateToString(getData()[0]));
                break;
            case ID_REQUEST_PRINTER_STATUS:
                b.append("GET PRINTER STATUS");
                break;
            case ID_REQUEST_METER_STATUS:
                b.append("GET METER STATUS");
                break;
            case ID_REPORT_METER_STATUS: {
                byte status = getData()[0];
                byte failure = getData()[1]; // '0' - not failure
                byte enabled = getData()[2]; // '0' - disabled
                b.append("STATUS: ");
                b.append(meterStatusToString(status));
                b.append(" ");
                b.append(meterStateToString(enabled));

                if (failure != '0') {
                    b.append(" ERROR:" + (char) failure);
                }
                break;
            }
            case ID_REPORT_METER_TRIP_DATA: {
                b.append("TRIP DATA:");
                b.append(" fare: ");
                b.append(formatCentsAmount(0, 8));
                b.append(" extras: ");
                b.append(formatCentsAmount(16, 4));
                break;
            }
            case ID_REQUEST_METER_STATISTICS:
                b.append("GET STATS");
                break;
            case ID_REPORT_METER_STATISTICS:
                b.append("STATS");
                break;
            case ID_REPORT_METER_TIMED_OUT:
                b.append("TIMED OUT");
                break;
            case ID_REQUEST_CURRENT_RATE:
                b.append("GET RATE");
                break;
            case ID_REPORT_CURRENT_RATE:
                b.append("RATE: ");
                byte rate = getData()[0];
                b.append((char) rate);
                break;
            case ID_SET_METER_CONFIGURATION:
                b.append("SET CONFIG:");
                byte mdtValidation = getData()[0]; // '0' disabled
                byte reportCurrentFare = getData()[1]; // '0' disabled
                byte pingOnHired = getData()[2]; // '0' disabled
                b.append(" Mdt Validation: ");
                b.append(meterConfigToString(mdtValidation));
                b.append(" Report Current Fare: ");
                b.append(meterConfigToString(reportCurrentFare));
                b.append(" Ping On Hired: ");
                b.append(meterConfigToString(pingOnHired));
                break;
            case ID_REPORT_CURRENT_RUNNING_FARE:
                byte status = getData()[0];
//                b.append("FARE:");
//                b.append(" fare: ");
//                b.append("STATUS: ");
                MeterData meterOff = new MeterData("STATUS_TRIP_UPDATE", formatCentsAmount(0, 8), formatCentsAmount(8, 8));
                b.append(new Gson().toJson(meterOff));
                //                b.append(formatCentsAmount(0, 8));
//                b.append(" extras: ");
//                b.append(formatCentsAmount(8, 8));

                break;
            case ID_SET_NEGOTIATED_FARE_AND_PASSENGERS:
                b.append("SET NEGOTIATED FARE: ");
                b.append(formatCentsAmount(0, 4));
                b.append(" Passengers: ");
                b.append((char) getData()[4]);
                break;
            case ID_MIN_FARE_AMOUNT:
                b.append("MIN FARE: ");
                b.append(formatCentsAmount(0, 5));
                break;
            case ID_ICABBI_JOB_TYPE:
                b.append("JOB TYPE: ");
                b.append(HwMeterPacketParserQueue.getStringData(getData(), 0, getDataLen()));
                break;
            case ID_ICABBI_TRIP_ID_DRIVER_STATUS:
                b.append("HEARTBEAT: ");
                b.append(HwMeterPacketParserQueue.getStringData(getData(), 0, getDataLen()));
                break;
            default:
                b.append(toString());
                break;
        }


        return b.toString();
    }

    private String meterStatusToString(byte status) {
        switch (status) {
            case METER_STATE_OFF:
                return "FINISH RIDE";
            case METER_STATE_ON:
                return "START RIDE";
            case METER_STATE_TIME_OFF:
                return "STOP RIDE";
            case METER_STATE_HIRED_TIME_OFF_PAYMENT:
                return "TIME OFF PAYMENT";
        }
        return "UNKNOWN";
    }

//    public String meterStatusToString(byte status) {
//        switch (status) {
//            case METER_STATE_OFF:
//                MeterData meterOff = new MeterData("VACANT", formatCentsAmount(0, 8), formatCentsAmount(8, 8));
//                return new Gson().toJson(meterOff);
//            case METER_STATE_ON:
//                MeterData meterOn = new MeterData("HIRED", formatCentsAmount(0, 8), formatCentsAmount(8, 8));
//                return new Gson().toJson(meterOn);
//            case METER_STATE_TIME_OFF:
//                int fare = formatCentsAmount(0, 8);
//                int extra = formatCentsAmount(8, 16);
//                MeterData meterTimeOff = new MeterData("TIME OFF", fare, extra);
//                return new Gson().toJson(meterTimeOff);
////                return "TIME OFF";
//            case METER_STATE_HIRED_TIME_OFF_PAYMENT:
//                return "TIME OFF PAYMENT";
//        }
//        return "UNKNOWN";
//    }



    private String meterStateToString(byte state) {
        switch (state) {
            case '0':
                return "LOCKED";
            case '1':
                return "UNLOCKED";
        }
        return "UNKNOWN";
    }

    private String meterConfigToString(byte state) {
        switch (state) {
            case '0':
                return "DISABLED";
            case '1':
                return "ENABLED";
        }
        return "UNKNOWN";
    }

    private int formatCentsAmount(int from, int length) {
        return (int) HwMeterPacketParserQueue.getCentsAmount(getData(), from, length);
    }
}
