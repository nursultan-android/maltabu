package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class MenuCategory implements Serializable {
    public MenuCategory(int id, String name){
        this.id = id;
        this.name=name;
    }

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("slug")
    @Expose
    private String slug;

    @SerializedName("order")
    @Expose
    private int order;

    @SerializedName("child")
    @Expose
    private List<CategoryChild> categoryChildList;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getSlug() {
        return slug;
    }

    public int getOrder() {
        return order;
    }

    public List<CategoryChild> getCategoryChildList() {
        return categoryChildList;
    }
}
