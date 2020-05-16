package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResponseBanner implements Serializable {

    @SerializedName("side-bar")
    @Expose
    private Banner sideBarBanner;

    @SerializedName("main")
    @Expose
    private Banner mainBanner;

    public Banner getSideBarBanner() {
        return sideBarBanner;
    }

    public Banner getMainBanner() {
        return mainBanner;
    }
}
