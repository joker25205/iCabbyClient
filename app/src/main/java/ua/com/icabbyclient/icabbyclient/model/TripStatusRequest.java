package ua.com.icabbyclient.icabbyclient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripStatusRequest {

    @SerializedName("cmd")
    @Expose
    private String cmd;
    @SerializedName("args")
    @Expose
    private Args args;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Args getArgs() {
        return args;
    }

    public void setArgs(Args args) {
        this.args = args;
    }



}
