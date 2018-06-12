package ua.com.icabbyclient.icabbyclient.utils;

import android.util.Log;

import java.util.Vector;


public class HwMeterPacketParserQueue extends Thread {

    private static final String TAG_CLASS = "HwMeter";

    public interface Listener {
        public void onHwMeterParserResult(int event, Object obj);

        public void onHwMeterDistanceValue(byte value);
    }


    private final Vector<HwMeterPacket> mPackets = new Vector<HwMeterPacket>();
    private final Object mLockObject = new Object();
    private boolean mInterrupted = false;

    private Listener mListener = null;


    public HwMeterPacketParserQueue(Listener resultListener) {
        mListener = resultListener;
        if (mListener == null) {
            throw new RuntimeException("HwMeterPacketParserQueue null listener");
        }
    }

    public void parsePacket(HwMeterPacket p) {
        if (p != null) {
            synchronized (mLockObject) {
                mPackets.add(p);
                mLockObject.notify();
            }
        }
    }

    private HwMeterPacket popPacket() {
        if (mPackets.size() > 0) {
            return mPackets.remove(0);
        }
        return null;
    }

    @Override
    public void run() {
        Log.d(TAG_CLASS, "HwMeterPacketParserQueue: start");
        if (mInterrupted) {
            return;
        }
        HwMeterPacket packet;
        while (!mInterrupted) {

            synchronized (mLockObject) {
                if (mInterrupted) {
                    break;
                }
                packet = popPacket();
                if (packet == null) {
                    try {
                        mLockObject.wait();
                        continue;
                    } catch (InterruptedException e) {
                        Log.e(TAG_CLASS, e.getMessage(), e);
                        break;
                    }
                }
            }
            if (mInterrupted) {
                return;
            }
            try {
                proceedPacket(packet);
            } catch (Exception e) {
                Log.e(TAG_CLASS, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Stop queue
     */

    @Override
    public void interrupt() {
        Log.d(TAG_CLASS, "HwMeterPacketParserQueue: stop");
        synchronized (mLockObject) {
            mInterrupted = true;
            mLockObject.notify();
            super.interrupt();
        }
    }

    private void proceedPacket(HwMeterPacket p) {
        Log.d(TAG_CLASS, "HwMeterPacketParserQueue: new packet" + p.getId());
        switch (p.getId()) {
            case HwMeterPacket.ID_REPORT_CURRENT_RATE:
                //this packet used for ping
                OnReportCurrentRate(p);
                break;
            case HwMeterPacket.ID_METER_ON_OFF_STATE_CHANGE:
                OnOffStateChange(p);
                break;

            case HwMeterPacket.ID_REPORT_METER_STATUS:
                OnReportMeterStatus(p);
                break;
            case HwMeterPacket.ID_REPORT_METER_TIMED_OUT:
                OnMeterTimedOut(p);
                break;
            case HwMeterPacket.ID_METER_FAILURE_STATE_CHANGE:
                OnMeterError(p);
                break;
            case HwMeterPacket.ID_REPORT_METER_TRIP_DATA:
                OnTripData(p);
                break;

            case HwMeterPacket.ID_REPORT_METER_TRIP_DATA_TLC:
                OnTripDataTcl(p);
                break;

            case HwMeterPacket.ID_REPORT_METER_STATISTICS:
                OnMeterStatistic(p);
                break;

            case HwMeterPacket.ID_REPORT_CURRENT_RUNNING_FARE:
                OnRunningFare(p);
                break;
            case HwMeterPacket.ID_MIN_FARE_AMOUNT:
                OnMinFare(p);
                break;
            case HwMeterPacket.ID_SET_NEGOTIATED_FARE:
            case HwMeterPacket.ID_SET_NEGOTIATED_FARE_AND_PASSENGERS:
                OnNegotiatedFare(p);
                break;
            case HwMeterPacket.ID_ICABBI_TRIP_ID_DRIVER_STATUS:
                OniCabbiTripIdDriverStatus(p);
                break;
            case HwMeterPacket.ID_ICABBI_JOB_TYPE:
                OniCabbiJobType(p);
                break;

            default:
                break;
        }
    }

    private void OnOffStateChange(HwMeterPacket p) {
        HwMeterFareData data = new HwMeterFareData();
        data.setStatus(p.getData()[0]);
        // condition below is specific to centrodyne meters,
        // so, basically you should request trip data
        if (p.getDataLen() > 1) {
            data.setRate(getInt(p.getData(), 1, 1));
            data.setFare(getDollarAmount(p.getData(), 2, 5));
            data.setExtras(getDollarAmount(p.getData(), 7, 4));
        }
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_METER_ON_OFF_STATE_CHANGE, data);
    }

    private void OnReportCurrentRate(HwMeterPacket p) {
        byte rate = p.getData()[0];
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_REPORT_CURRENT_RATE, Byte.valueOf(rate));
    }

    private void OnMeterTimedOut(HwMeterPacket p) {
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_METER_TIMED_OUT, null);
    }

    private void OnMeterError(HwMeterPacket p) {
        byte errorCode = p.getData()[0];
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_METER_FAILURE_STATE_CHANGE, Byte.valueOf(errorCode));
    }

    private void OnMeterStatistic(HwMeterPacket p) {
        HwMeterFareData data = new HwMeterFareData();
        data.setUnit(getInt(p.getData(), 0, 1));
        data.setFare(getDollarAmount(p.getData(), 1, 10));
        data.setExtras(getDollarAmount(p.getData(), 11, 10));
        data.setFinalFax(getDollarAmount(p.getData(), 21, 10));
        data.setTripTotalDistance(getLong(p.getData(), 31, 10));
        data.setTripAboveThresholdDistance(getLong(p.getData(), 41, 10));
        data.setTotalTripCount(getLong(p.getData(), 51, 10));
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_REPORT_METER_STATISTICS, data);

//        mListener.onHwMeterDistanceValue((byte) (p.getData()[0] - 48));
    }

    private void OnReportMeterStatus(HwMeterPacket p) {
        byte status = p.getData()[0];
        byte failure = p.getData()[1]; // 0 - not failure
        byte enabled = p.getData()[2]; // 0 - disabled
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_REPORT_METER_STATUS_FAILURE, Byte.valueOf(failure));
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_REPORT_METER_STATUS_ENABLED, Byte.valueOf(enabled));
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_REPORT_METER_STATUS, Byte.valueOf(status));
    }

    private void OnTripData(HwMeterPacket p) {
        HwMeterFareData data = new HwMeterFareData();
        data.setFare(getDollarAmount(p.getData(), 0, 8));
        data.setFinalFax(getDollarAmount(p.getData(), 8, 8));
        data.setExtras(getDollarAmount(p.getData(), 16, 4));
        data.setNetTotal(getDollarAmount(p.getData(), 20, 8));
        data.setTripTotalDistance(getLong(p.getData(), 28, 8));
        data.setTripAboveThresholdDistance(getLong(p.getData(), 28, 8));
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_REPORT_METER_TRIP_DATA, data);
    }

    private void OnRunningFare(HwMeterPacket p) {
        HwMeterFareData data = new HwMeterFareData();
        data.setFare(getDollarAmount(p.getData(), 0, 8));
        data.setExtras(getDollarAmount(p.getData(), 8, 8));
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_REPORT_METER_RUNNING_FARE, data);

    }

    private void OnMinFare(HwMeterPacket p) {
        Double fare = Double.valueOf(getDollarAmount(p.getData(), 0, 5));
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_MIN_FARE, fare);
    }

    private void OnNegotiatedFare(HwMeterPacket p) {
        Double fare = Double.valueOf(getDollarAmount(p.getData(), 0, 5));
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_NEGOTIATED_FARE, fare);
    }

    private void OniCabbiTripIdDriverStatus(HwMeterPacket p) {
        String data = getStringData(p.getData(), 0, p.getDataLen());
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_ICABBI_TRIP_ID_DRIVER_STATUS, data);
    }

    private void OniCabbiJobType(HwMeterPacket p) {
        String data = getStringData(p.getData(), 0, p.getDataLen());
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_ICABBI_JOB_TYPE, data);
    }


    private void OnTripDataTcl(HwMeterPacket p) {
        HwMeterFareData data = new HwMeterFareData();
        data.setFare(getDollarAmount(p.getData(), 0, 8));
        data.setFinalFax(getDollarAmount(p.getData(), 8, 8));
        data.setExtras(getDollarAmount(p.getData(), 16, 4));
        data.setMtaTax(getDollarAmount(p.getData(), 20, 4));
        data.setNetTotal(getDollarAmount(p.getData(), 24, 8));
        data.setTripTotalDistance(getLong(p.getData(), 32, 8));
        data.setTripAboveThresholdDistance(getLong(p.getData(), 32, 8));
//        mListener.onHwMeterParserResult(HwMeterService.EVENT_REPORT_METER_TRIP_DATA_TLC, data);
    }

    static String getStringData(byte[] data, int from, int length) {
        try {
            return new String(data, from, length, "US-ASCII");
        } catch (Exception ex) {
            Log.e(TAG_CLASS, ex.getMessage(), ex);
        }
        return "";
    }

    static double getDollarAmount(byte[] data, int from, int length) {
        return ((double) getLong(data, from, length)) / 100.0;// convert cents to $
    }

    static int getInt(byte[] data, int from, int length) {
        int result = 0;
        if (data.length >= from + length) {
            byte[] newArray = new byte[length];
            System.arraycopy(data, from, newArray, 0, length);
            try {
                result = Integer.parseInt(new String(newArray, "US-ASCII"));
            } catch (Exception e) {
                Log.e(TAG_CLASS, e.getMessage(), e);
                StringBuffer b = new StringBuffer();
                for (int i = 0; i < data.length; i++) {
                    b.append(String.format("%02X ", data[i]));
                }
                Log.d(TAG_CLASS, "HwMeterPacketParserQueue: fail parse int. From:" + from + " Length: " + length
                        + " Data: " + b);
            }
        }
        return result;
    }

    static long getLong(byte[] data, int from, int length) {
        long result = 0;
        if (data.length >= from + length) {
            byte[] newArray = new byte[length];
            System.arraycopy(data, from, newArray, 0, length);
            try {
                result = Long.parseLong(new String(newArray, "US-ASCII"));
            } catch (Exception e) {
                Log.e(TAG_CLASS, e.getMessage(), e);
                StringBuffer b = new StringBuffer();
                for (int i = 0; i < data.length; i++) {
                    b.append(String.format("%02X ", data[i]));
                }
                Log.d(TAG_CLASS, "HwMeterPacketParserQueue: fail parse long. From:" + from + " Length: " + length
                        + " Data: " + b);
            }
        }
        return result;
    }
}
