package ua.com.icabbyclient.icabbyclient.utils;

public class MeterData {
    private String status;
    private int fare;
    private int extras;

    public MeterData(final String status, final int fare, final int extras) {
        this.status = status;
        this.fare = fare;
        this.extras = extras;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(final int fare) {
        this.fare = fare;
    }

    public int getExtras() {
        return extras;
    }

    public void setExtras(final int extras) {
        this.extras = extras;
    }
}
