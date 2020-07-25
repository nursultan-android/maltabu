package kz.maltabu.app.maltabukz.network.models.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentPhone implements Serializable{

        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("value")
        @Expose
        private String value;

        @SerializedName("pivot")
        @Expose
        private CommentPhonePivot pivot;

        public int getId() {
                return id;
        }

        public String getValue() {
                return value;
        }

        public CommentPhonePivot getPivot() {
                return pivot;
        }
}
