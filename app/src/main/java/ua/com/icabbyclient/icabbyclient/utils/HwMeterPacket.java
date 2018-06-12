package ua.com.icabbyclient.icabbyclient.utils;

import com.google.gson.Gson;

            /*  case ID_REPORT_CURRENT_RUNNING_FARE:
              byte status = getData()[0];
//                b.append("FARE:");
//                b.append(" fare: ");
//                b.append("STATUS: ");
              MeterData meterOff = new MeterData("STATUS_TRIP_UPDATE", formatCentsAmount(0, 8), formatCentsAmount(8, 8));
              b.append(new Gson().toJson(meterOff));
              //                b.append(formatCentsAmount(0, 8));
//                b.append(" extras: ");
//                b.append(formatCentsAmount(8, 8));

              break;*/


public class HwMeterPacket {

    private static final String TAG = "HwMeterPacket";

    /*******
     * METER ON/OFF STATE CHANGE
     *********************************/
    public static final byte METER_STATE_OFF = 0x30;
    public static final byte METER_STATE_ON = 0x31;
    public static final byte METER_STATE_TIME_OFF = 0x32;
    public static final byte METER_STATE_HIRED_TIME_OFF_PAYMENT = 0x33;

    /*******
     * Meter error
     **********************************************/
    public static final byte METER_ERROR_NONE = 0x00;
    public static final byte METER_ERROR_INTERNAL_NVRAM = 0x01;
    public static final byte METER_ERROR_INTERNAL_NVRAM_2 = 0x02;
    public static final byte METER_ERROR_EXTERNAL_NVRAM = 0x03;
    public static final byte METER_ERROR_PRINTER = 0x04;
    public static final byte METER_ERROR_ROM_CHECKSUM = 0x05;
    public static final byte METER_ERROR_EXTERNAL_NVRAM_2 = 0x06;
    public static final byte METER_ERROR_UNDETERMINED_ERROR = 0x09;


    /*******
     * Meter status
     **********************************************/
    public static final byte METER_STATUS_VACANT = 0x30;
    public static final byte METER_STATUS_HIRED = 0x31;
    public static final byte METER_STATUS_TIMEOFF = 0x32;
    public static final byte METER_STATUS_STATISTICS = 0x33;

    /**********
     * printer status
     ******************************************/

    public static final byte PRINTER_STATUS_FAILED = 0x30;
    public static final byte PRINTER_STATUS_IN_USE = 0x31;
    public static final byte PRINTER_STATUS_NOT_IN_USE = 0x32;

    /********************************************************************/

    // <-- means Meter To MDT, --> means MDT to Meter, <--> means both ways
    public static final byte ID_UNSUPPORTED = 0x59;
    public static final byte ID_ACK = 0x5A; // <-->
    public static final byte ID_METER_ON_OFF_STATE_CHANGE = 0x41; // <--
    public static final byte ID_METER_FAILURE_STATE_CHANGE = 0x42;  // <--
    public static final byte ID_CREDIT_CARD_DATA = 0x43;  // <--
    public static final byte ID_REQUEST_METER_TRIP_DATA = 0x44;  // -->
    public static final byte ID_REQUEST_METER_TRIP_DATA_TLC = 0x26;  // -->
    public static final byte ID_PRINT_BLOCK = 0x45;  // -->
    public static final byte ID_REPORT_PRINTER_STATUS = 0x46;  // <--
    public static final byte ID_ENABLE_DISABLE_METER = 0x47;  // -->
    public static final byte ID_REQUEST_PRINTER_STATUS = 0x48;  // -->
    public static final byte ID_REQUEST_METER_STATUS = 0x49;  // -->
    public static final byte ID_REPORT_METER_STATUS = 0x4A;  // <--
    public static final byte ID_REPORT_METER_TRIP_DATA = 0x4B;  // <--
    public static final byte ID_REPORT_METER_TRIP_DATA_TLC = 0x26;  // <--
    public static final byte ID_METER_BUSY_NOT_BUSY = 0x4C;  // <--
    public static final byte ID_PRINT_CREDIT_CARD_RECEIPT = 0x4D;  // -->
    public static final byte ID_REQUEST_METER_STATISTICS = 0x4E;  // -->
    public static final byte ID_REPORT_METER_STATISTICS = 0x4F;  // <--
    public static final byte ID_REQUEST_METER_STATISTICS_TLC = 0x21;  // -->
    public static final byte ID_REPORT_METER_STATISTICS_TLC = 0x21;  // <--
    public static final byte ID_REPORT_METER_DAILY_STATISTICS = 0x52;  // <--
    public static final byte ID_REPORT_METER_DAILY_STATISTICS_TLC = 0x23;  // <--
    public static final byte ID_SET_METER_MILEPOST_ODOMETER = 0x4F;  // -->
    public static final byte ID_CLEAR_METER_STATISTICS = 0x50;  // -->
    public static final byte ID_REPORT_AUTHORIZATION_NUMBER = 0x51;  // -->
    public static final byte ID_REQUEST_METER_DAILY_STATISTICS = 0x52;  // -->
    public static final byte ID_REQUEST_METER_DAILY_STATISTICS_TLC = 0x23;  // -->
    public static final byte ID_CLEAR_METER_DAILY_STATISTICS = 0x53;  // -->
    public static final byte ID_REPORT_GPS_LOCATION = 0x53;  // <--
    public static final byte ID_REPORT_METER_TIMED_OUT = 0x54;  // <--
    public static final byte ID_REPORT_DRIVER_ID_AND_PIN = 0x56;  // <--
    public static final byte ID_REQUEST_SET_METHOD_TYPE = 0x57;  // -->
    public static final byte ID_REPORT_METHOD_TYPE = 0x57;  // <--
    public static final byte ID_REQUEST_GPS_POSITION_FIX = 0x58;  // -->
    public static final byte ID_REQUEST_CURRENT_RATE = 0x61;  // -->
    public static final byte ID_REPORT_CURRENT_RATE = 0x62;  // <--
    public static final byte ID_SET_METER_RATE = 0x62;  // -->
    public static final byte ID_REQUEST_METER_CONFIGURATION = 0x63;  // -->
    public static final byte ID_REPORT_METER_CONFIGURATION = 0x63;  // <--
    public static final byte ID_PRINT_LARGE_BLOCK = 0x64;  // -->
    public static final byte ID_SET_METER_CONFIGURATION = 0x67;  // -->
    public static final byte ID_REPORT_CURRENT_RUNNING_FARE = 0x68;  // <--
    public static final byte ID_REQUEST_METER_ODOMETER = 0x6F;  // -->
    public static final byte ID_REPORT_METER_ODOMETER = 0x6F;  // <--
    public static final byte ID_PING_MDT = 0x70;  // <--
    public static final byte ID_REQUEST_MULTIPLE_RATE_METER_TRIP_DATA = 0x71;  // -->
    public static final byte ID_REPORT_MULTIPLE_RATE_METER_TRIP_DATA = 0x71;  // <--
    public static final byte ID_REQUEST_MULTIPLE_RATE_METER_TRIP_DATA_TLC = 0x25;  // -->
    public static final byte ID_REPORT_MULTIPLE_RATE_METER_TRIP_DATA_TLC = 0x25;  // <--
    public static final byte ID_CREDIT_CARD_TRANSACTION = 0x74;  // <--
    public static final byte ID_REPORT_MISSED_MESSAGE = 0x75;  // <--
    public static final byte ID_SEND_LATITUDE_LONGITUDE = 0x6C;  // -->
    public static final byte ID_GO_VACANT = 0x76;  // -->
    public static final byte ID_GO_VACANT2 = (byte) 0xC7;  // -->
    public static final byte ID_SET_NEGOTIATED_FARE = 0x69;  // -->
    public static final byte ID_SET_EXTRAS_AMOUNT = 0x6A;  // -->
    public static final byte ID_CREDIT_CARD_DATA_TRACK2_AND_TRACK1 = 0x44;  // <--
    public static final byte ID_SET_DISABLE_METER_TIME_AND_DATE = 0x7B;  // -->
    public static final byte ID_SET_DRIVER_ID = 0x7C;  // -->
    public static final byte ID_REQUEST_DRIVER_ID = 0x7D;  // -->
    public static final byte ID_REPORT_DRIVER_ID = 0x7D;  // <--
    public static final byte ID_SET_CAB_ID = (byte) 0xEE;  // -->
    public static final byte ID_REQUEST_CAB_ID = (byte) 0xEC;  // -->
    public static final byte ID_REPORT_CAB_ID = (byte) 0xEC;  // <--
    public static final byte ID_SET_TOLLS_EXTRAS = (byte) 0xC4;  // -->
    public static final byte ID_SET_NEGOTIATED_FARE_AND_PASSENGERS = (byte) 0xC5;  // -->
    public static final byte ID_REQUEST_METER_TIME_AND_DATE = (byte) 0xC9;  // -->
    public static final byte ID_REPORT_METER_TIME_AND_DATE = (byte) 0xC9;  // <--
    public static final byte ID_REQUEST_EXPANDED_TRIP_DATA = (byte) 0xE6;  // -->
    public static final byte ID_REPORT_EXPANDED_TRIP_DATA = (byte) 0xE6;  // <--
    public static final byte ID_REQUEST_DISABLE_METER_TIME_AND_DATE = (byte) 0xC6;  // -->
    public static final byte ID_REPORT_DISABLE_METER_TIME_AND_DATE = (byte) 0xC6;  // <--
    public static final byte ID_METER_PRINTING_MESSAGE = 0x48;  // <--
    public static final byte ID_SET_METER_TIME_AND_DATE = (byte) 0xF4;  // -->
    public static final byte ID_REQUEST_USER_TEXT_DATA = (byte) 0xF6;  // -->
    public static final byte ID_REPORT_USER_TEXT_DATA = (byte) 0xF6;  // <--

    public static final byte ID_MIN_FARE_AMOUNT = (byte) 0x81;  // -->
    public static final byte ID_ICABBI_JOB_TYPE = (byte) 0x82;  // -->
    public static final byte ID_ICABBI_TRIP_ID_DRIVER_STATUS = (byte) 0x83;  // -->


    public static final byte STX = 0x02;
    private byte mId;
    private int mDataLen;
    private byte[] mData;
    private int mBcc;
    public static final byte ETX = 0x03;

    private boolean mWaitACK = true;

    private boolean mInternal = true;

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

    public HwMeterPacket(byte id, byte[] data) {
        mId = id;
        mDataLen = data.length;
        mData = data.clone();
        mBcc = 0;
    }

    public HwMeterPacket(byte id, byte dataLen, byte[] data) {
        mId = id;
        mDataLen = dataLen;
        mData = data.clone();
        mBcc = 0;
    }

    public void setInternal(boolean value) {
        mInternal = value;
    }

    public boolean isInternal() {
        return mInternal;
    }

    public void setWaitACK(boolean value) {
        mWaitACK = value;
    }

    public boolean getWaitACK() {
        return mWaitACK;
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

    public boolean fromBytes(byte[] array) {
        return fromBytesLongData(array, array[2]);
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
                b.append(formatDollarAmount(0, 8));
                b.append(" extras: ");
                b.append(formatDollarAmount(16, 4));
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
                b.append("FARE:");
                b.append(" fare: ");
                b.append(formatDollarAmount(0, 8));
                b.append(" extras: ");
                b.append(formatDollarAmount(8, 8));
                break;
            case ID_PING_MDT:
                b.append("PING MDT");
                break;
            case ID_SET_NEGOTIATED_FARE:
                b.append("SET NEGOTIATED FARE: ");
                b.append(formatDollarAmount(0, 5));
                break;
            case ID_SET_TOLLS_EXTRAS:
                byte what = getData()[0]; // '0' extras, '1' tolls, '2' mta tax
                b.append("SET ");
                if (what == '0') {
                    b.append("EXTRAS: ");
                } else if (what == '1') {
                    b.append("TOLLS: ");
                } else if (what == '1') {
                    b.append("MTA TAX: ");
                }
                b.append(formatDollarAmount(1, 4));
                break;
            case ID_SET_NEGOTIATED_FARE_AND_PASSENGERS:
                b.append("SET NEGOTIATED FARE: ");
                b.append(formatDollarAmount(0, 4));
                b.append(" Passengers: ");
                b.append((char) getData()[4]);
                break;
            case ID_MIN_FARE_AMOUNT:
                b.append("MIN FARE: ");
                b.append(formatDollarAmount(0, 5));
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
                return "VACANT";
            case METER_STATE_ON:
                return "HIRED";
            case METER_STATE_TIME_OFF:
                return "TIME OFF";
            case METER_STATE_HIRED_TIME_OFF_PAYMENT:
                return "TIME OFF PAYMENT";
        }
        return "UNKNOWN";
    }

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

    private String formatDollarAmount(int from, int length) {
        return String.format("%.2f", HwMeterPacketParserQueue.getDollarAmount(getData(), from, length));
    }
}
