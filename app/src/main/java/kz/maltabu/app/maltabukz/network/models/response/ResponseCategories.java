package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ResponseCategories {

    @SerializedName("data")
    @Expose
    private List<MenuCategory> categoriesList;

    public List<MenuCategory> getCategoriesList() {
        return categoriesList;
    }
}
