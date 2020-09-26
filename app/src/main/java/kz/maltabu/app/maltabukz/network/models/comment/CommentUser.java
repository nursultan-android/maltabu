package kz.maltabu.app.maltabukz.network.models.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CommentUser implements Serializable{

        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("email")
        @Expose
        private String email;

        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        @SerializedName("created_at")
        @Expose
        private String createdAt;

        @SerializedName("phones")
        @Expose
        private List<CommentPhone> phones;

        @SerializedName("profile")
        @Expose
        private CommentProfile profile;

        public int getId() {
                return id;
        }

        public String getName() {
                return name;
        }

        public String getEmail() {
                return email;
        }

        public String getUpdatedAt() {
                return updatedAt;
        }

        public String getCreatedAt() {
                return createdAt;
        }

        public List<CommentPhone> getPhones() {
                return phones;
        }

        public CommentProfile getProfile() {
                return profile;
        }
}
