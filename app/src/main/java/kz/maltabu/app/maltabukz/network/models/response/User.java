package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        if(phone!=null && !phone.isEmpty())
            return "7"+phone;
        else
            return null;
    }

    public String getEmail() {
        return email;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }
}
