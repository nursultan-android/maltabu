package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResponseContest implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("code")
    @Expose
    private long code;

    public String getStatus() {
        return status;
    }

    public long getCode() {
        return code;
    }
}
