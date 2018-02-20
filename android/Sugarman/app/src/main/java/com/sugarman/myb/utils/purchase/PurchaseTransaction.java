package com.sugarman.myb.utils.purchase;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.utils.inapp.Purchase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nikita on 16.02.2018.
 */
@AllArgsConstructor @NoArgsConstructor @Builder public class PurchaseTransaction {
  @Getter @Setter @SerializedName("slot") private String freeSku;
  @Getter @Setter private Tracking tracking;
  @Getter @Setter @SerializedName("provider") private String vendor;
  @Getter @Setter private String productTitle;
  @Getter @Setter @SerializedName("id_mentor") private String mentorId;
  @Getter @Setter private Purchase purchase;
  @Getter @Setter @SerializedName("purchase_token") private String purchaseToken;
}
