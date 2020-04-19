package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class News {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

//    @SerializedName("description")
//    @Expose
//    private String description;

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

//    public String getDescription() {
//        return description;
//    }

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
