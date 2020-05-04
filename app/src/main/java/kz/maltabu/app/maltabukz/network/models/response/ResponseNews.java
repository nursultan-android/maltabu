package kz.maltabu.app.maltabukz.network.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class ResponseNews implements Serializable {

    @SerializedName("data")
    @Expose
    private List<News> newsList;

    public List<News> getNewsList() {
        return newsList;
    }
}
