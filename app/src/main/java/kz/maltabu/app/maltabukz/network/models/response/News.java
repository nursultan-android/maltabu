package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class News implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("slug")
    @Expose
    private String slug;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("locale")
    @Expose
    private String locale;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("visited")
    @Expose
    private int visited;

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getImage() {
        return image;
    }

    public String getLocale() {
        return locale;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public int getVisited() {
        return visited;
    }
}
