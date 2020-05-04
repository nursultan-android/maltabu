package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResponseAmount implements Serializable {

    @SerializedName("data")
    @Expose
    private List<AmountType> regions;

    public List<AmountType> getRegions() {
        return regions;
    }
}
