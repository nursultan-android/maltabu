package kz.maltabu.app.maltabukz.network.models.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentPhonePivot implements Serializable{

        @SerializedName("phonegable_id")
        @Expose
        private int phoneGableId;

        @SerializedName("phone_id")
        @Expose
        private int phoneId;

        @SerializedName("phonegable_type")
        @Expose
        private String phoneGableType;

        public int getPhoneGableId() {
                return phoneGableId;
        }

        public int getPhoneId() {
                return phoneId;
        }

        public String getPhoneGableType() {
                return phoneGableType;
        }
}
