package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Region {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("cities")
    @Expose
    private List<City> cities;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<City> getCities() {
        return cities;
    }
}
