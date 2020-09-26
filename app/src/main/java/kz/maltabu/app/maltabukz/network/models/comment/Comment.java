package kz.maltabu.app.maltabukz.network.models.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Comment implements Serializable{

        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("text")
        @Expose
        private String text;

        @SerializedName("advertisement_id")
        @Expose
        private int advertisementId;

        @SerializedName("user_id")
        @Expose
        private int userId;

        @SerializedName("created_at")
        @Expose
        private String createdAt;

        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        @SerializedName("date")
        @Expose
        private String date;

        @SerializedName("user")
        @Expose
        private CommentUser user;

        public int getId() {
                return id;
        }

        public String getText() {
                return text;
        }

        public int getAdvertisementId() {
                return advertisementId;
        }

        public int getUserId() {
                return userId;
        }

        public String getCreatedAt() {
                return createdAt;
        }

        public String getUpdatedAt() {
                return updatedAt;
        }

        public String getDate() {
                return date;
        }

        public CommentUser getUser() {
                return user;
        }
}
