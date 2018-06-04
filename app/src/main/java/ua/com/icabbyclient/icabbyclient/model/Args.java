package ua.com.icabbyclient.icabbyclient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Args {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("fare")
    @Expose
    private Integer fare;
    @SerializedName("extras")
    @Expose
    private Integer extras;
    @SerializedName("total_amount")
    @Expose
    private Integer totalAmount;
    @SerializedName("flat_rate")
    @Expose
    private Boolean flatRate;
    @SerializedName("trip_id")
    @Expose
    private String tripId;
    @SerializedName("account")
    @Expose
    private Boolean account;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFare() {
        return fare;
    }

    public void setFare(Integer fare) {
        this.fare = fare;
    }

    public Integer getExtras() {
        return extras;
    }

    public void setExtras(Integer extras) {
        this.extras = extras;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Boolean getFlatRate() {
        return flatRate;
    }

    public void setFlatRate(Boolean flatRate) {
        this.flatRate = flatRate;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public Boolean getAccount() {
        return account;
    }

    public void setAccount(Boolean account) {
        this.account = account;
    }
}
