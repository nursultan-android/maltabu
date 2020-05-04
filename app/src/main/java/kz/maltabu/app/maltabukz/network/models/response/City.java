package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class City implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("locale")
    @Expose
    private String locale;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("region_id")
    @Expose
    private int region_id;

    @SerializedName("country_id")
    @Expose
    private int country_id;

    @SerializedName("province_id")
    @Expose
    private int province_id;

    public int getId() {
        return id;
    }

    public String getLocale() {
        return locale;
    }

    public String getName() {
        return name;
    }

    public int getRegion_id() {
        return region_id;
    }

    public int getCountry_id() {
        return country_id;
    }

    public int getProvince_id() {
        return province_id;
    }
}
