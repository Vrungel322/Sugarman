package com.sugarman.myb.models.iab;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 21.11.2017.
 */

public class SubscriptionEntity {
  @SerializedName("id_mentor") @Getter @Setter String mentorId;
  @SerializedName("android_slot") @Getter @Setter String slot;

}
