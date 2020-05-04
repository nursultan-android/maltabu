package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MessageStatus implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }
}
