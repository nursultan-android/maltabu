package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseRegion {

    @SerializedName("data")
    @Expose
    private List<Region> regions;

    public List<Region> getRegions() {
        return regions;
    }
}
