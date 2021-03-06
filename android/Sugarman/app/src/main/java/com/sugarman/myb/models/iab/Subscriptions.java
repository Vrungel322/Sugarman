package com.sugarman.myb.models.iab;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.Tracking;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 21.11.2017.
 */

public class Subscriptions {
  @SerializedName("subscriptions") @Getter @Setter List<SubscriptionEntity> subscriptionEntities;
  @SerializedName("tracking") @Getter @Setter Tracking tracking;
}
