package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResponseAuth implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("user")
    @Expose
    private User user;

    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}
