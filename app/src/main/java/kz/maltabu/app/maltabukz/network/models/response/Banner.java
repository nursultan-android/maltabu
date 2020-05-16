package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Banner implements Serializable {

    @SerializedName("picture")
    @Expose
    private String picture;

    @SerializedName("link")
    @Expose
    private String link;

    public String getPicture() {
        return picture;
    }

    public String getLink() {
        return link;
    }
}
