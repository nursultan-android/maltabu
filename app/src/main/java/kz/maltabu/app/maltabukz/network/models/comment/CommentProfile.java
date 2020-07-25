package kz.maltabu.app.maltabukz.network.models.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentProfile implements Serializable{

        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("user_id")
        @Expose
        private int userId;

        @SerializedName("first_name")
        @Expose
        private String firstName;

        @SerializedName("last_name")
        @Expose
        private String lastName;

        @SerializedName("sur_name")
        @Expose
        private String surName;

        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        @SerializedName("created_at")
        @Expose
        private String createdAt;

        @SerializedName("region_id")
        @Expose
        private String regionId;

        @SerializedName("city_id")
        @Expose
        private String cityId;

        public int getId() {
                return id;
        }

        public int getUserId() {
                return userId;
        }

        public String getFirstName() {
                return firstName;
        }

        public String getLastName() {
                return lastName;
        }

        public String getSurName() {
                return surName;
        }

        public String getUpdatedAt() {
                return updatedAt;
        }

        public String getCreatedAt() {
                return createdAt;
        }

        public String getRegionId() {
                return regionId;
        }

        public String getCityId() {
                return cityId;
        }
}
