package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 16.02.2018.
 */

public class MentorsVendor {
  @Getter @Setter @SerializedName("server") private String vendor;
  @Getter @Setter @SerializedName("slot") private String slot;
  @Getter @Setter @SerializedName("is_available") private Boolean isAvailable;

  @Override public String toString() {
    return "MentorsVendor server: " + vendor + " slot: " + slot + " isAvailable: " + isAvailable;
  }
}
