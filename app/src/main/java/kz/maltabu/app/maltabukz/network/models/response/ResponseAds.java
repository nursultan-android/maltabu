package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class ResponseAds implements Serializable {

    @SerializedName("data")
    @Expose
    private List<Ad> adList;

    public List<Ad> getAdList() {
        return adList;
    }
}
