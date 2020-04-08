package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryChild {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("parent_id")
    @Expose
    private int parent_id;

    @SerializedName("order")
    @Expose
    private int order;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("slug")
    @Expose
    private String slug;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("name_kk")
    @Expose
    private String name_kk;

    @SerializedName("name_ru")
    @Expose
    private String name_ru;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("name")
    @Expose
    private String name;

    public int getId() {
        return id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public int getOrder() {
        return order;
    }

    public String getImage() {
        return image;
    }

    public String getSlug() {
        return slug;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getName_kk() {
        return name_kk;
    }

    public String getName_ru() {
        return name_ru;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }
}
