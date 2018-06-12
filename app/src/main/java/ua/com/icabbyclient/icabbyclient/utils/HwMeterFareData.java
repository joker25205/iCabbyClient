package ua.com.icabbyclient.icabbyclient.utils;

import java.io.Serializable;
import java.math.BigDecimal;

public class HwMeterFareData implements Serializable {
    private static final long serialVersionUID = 2L;

    //******** intermediate values ********//

    //all values are in dollars: 10.25, 1.34, etc
    private BigDecimal mFare = new BigDecimal("0"); // base fare
    private BigDecimal mExtras = new BigDecimal("0"); // extras


    //******** final (or sum) values*********//
    private BigDecimal mFinalTax = new BigDecimal("0");
    //***************************************//

    private byte mStatus;
    private int mRate;

    private double mTripAboveThresholdDistance; // in meters
    private double mTripTotalDistance; // in meters

    private BigDecimal mMtaTax = new BigDecimal("0");
    private BigDecimal mNetTotal = new BigDecimal("0");

    private int unit = 9;
    private long totalTripCount;


    public HwMeterFareData() {
    }


    // ********************** fare ***********************

    public BigDecimal getFare() {
        return mFare;
    }

    public void setFare(BigDecimal fare) {
        mFare = fare;
    }

    public double getFareDouble() {
        return mFare.doubleValue();
    }

    public void setFare(double fare) {
        mFare = DecimalUtils.createBigDecimal(fare);
    }

    // ********************** extras ***********************

    public BigDecimal getExtras() {
        return mExtras;
    }

    public void setExtras(BigDecimal extras) {
        mExtras = extras;
    }

    public double getExtrasDouble() {
        return mExtras.doubleValue();
    }

    public void setExtras(double extras) {
        mExtras = DecimalUtils.createBigDecimal(extras);
    }


    public byte getStatus() {
        return mStatus;
    }

    public void setStatus(byte status) {
        mStatus = status;
    }

    public int getRate() {
        return mRate;
    }

    public void setRate(int rate) {
        mRate = rate;
    }


    //************************* final fax ******************************

    public double getFinalFax() {
        return mFinalTax.doubleValue();
    }

    public void setFinalFax(double finalFax) {
        mFinalTax = DecimalUtils.createBigDecimal(finalFax);
    }

    //************************* Mta tax ******************************

    public double getMtaTax() {
        return mMtaTax.doubleValue();
    }

    public void setMtaTax(double mtaTax) {
        mMtaTax = DecimalUtils.createBigDecimal(mtaTax);
    }

    //************************* net total ******************************

    public double getNetTotal() {
        return mNetTotal.doubleValue();
    }

    public void setNetTotal(double netTotal) {
        mNetTotal = DecimalUtils.createBigDecimal(netTotal);
    }


    public double getTripAboveThresholdDistance() {
        return mTripAboveThresholdDistance;
    }

    public void setTripAboveThresholdDistance(double tripAboveThresholdDistance) {
        mTripAboveThresholdDistance = tripAboveThresholdDistance;
    }

    public double getTripTotalDistance() {
        return mTripTotalDistance;
    }

    public void setTripTotalDistance(double tripTotalDistance) {
        mTripTotalDistance = tripTotalDistance;
    }


    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public long getTotalTripCount() {
        return totalTripCount;
    }

    public void setTotalTripCount(long totalTripCount) {
        this.totalTripCount = totalTripCount;
    }

    public boolean populateWithDiffValue(HwMeterFareData current,
                                         HwMeterFareData initial) {
        if (initial == null || current == null) {
            return false;
        }
        mFare = current.mFare.subtract(initial.mFare);
        mFinalTax = current.mFinalTax.subtract(initial.mFinalTax);
        mExtras = current.mExtras.subtract(initial.mExtras);
        mTripTotalDistance = current.mTripTotalDistance - initial.mTripTotalDistance;
        mTripAboveThresholdDistance = current.mTripAboveThresholdDistance - initial.mTripAboveThresholdDistance;
        unit = current.unit;
        return true;
    }


}
