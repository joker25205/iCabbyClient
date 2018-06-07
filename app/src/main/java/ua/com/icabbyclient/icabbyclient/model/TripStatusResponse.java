package ua.com.icabbyclient.icabbyclient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripStatusResponse {
    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("tip")
    @Expose
    public int tip;
    @SerializedName("error_code")
    @Expose
    public String errorCode;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(final int tip) {
        this.tip = tip;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }
}
