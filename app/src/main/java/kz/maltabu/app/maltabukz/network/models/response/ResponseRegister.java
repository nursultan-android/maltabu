package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResponseRegister implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("token")
    @Expose
    private Token token;

    @SerializedName("user")
    @Expose
    private User user;

    public String getStatus() {
        return status;
    }

    public Token getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}
