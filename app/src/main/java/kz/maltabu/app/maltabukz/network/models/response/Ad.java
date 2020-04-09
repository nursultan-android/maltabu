package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Ad implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("images")
    @Expose
    private List<String> images;

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("amount")
    @Expose
    private long amount;

    @SerializedName("region")
    @Expose
    private String region;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phones")
    @Expose
    private List<String> phones;

    @SerializedName("visited")
    @Expose
    private int visited;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("currency")
    @Expose
    private String currency;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public List<String> getImages() {
        return images;
    }

    public String getCategory() {
        return category;
    }

    public long getAmount() {
        return amount;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getPhones() {
        return phones;
    }

    public int getVisited() {
        return visited;
    }

    public String getDate() {
        return date;
    }

    public String getCurrency() {
        return currency;
    }
}
