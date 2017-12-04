package com.sugarman.myb.models.iab;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by yegoryeriomin on 12/1/17.
 */

@AllArgsConstructor public class InAppSinglePurchase {
  @Getter @Setter @SerializedName("product_name") String productName;
  @Getter @Setter @SerializedName("product_id") String productId;
  @Getter @Setter @SerializedName("purchase_token") String purchaseToken;
  @Getter @Setter @SerializedName("android_slot") String slot;
}
