package com.sugarman.myb.models.iab;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 13.11.2017.
 */
@AllArgsConstructor public class PurchaseForServer {
  //@Getter @Setter @SerializedName("orderId") String mOrderId;
  //@Getter @Setter @SerializedName("productId") String mSku;
  //@Getter @Setter @SerializedName("purchaseTime") long mPurchaseTime;

  @Getter @Setter @SerializedName("orderId") String mOrderId;
  @Getter @Setter@SerializedName("packageName") String mPackageName;
  @Getter @Setter@SerializedName("productId") String mSku;
  @Getter @Setter@SerializedName("purchaseTime") long mPurchaseTime;
  @Getter @Setter@SerializedName("purchaseState") int mPurchaseState;
  @Getter @Setter@SerializedName("developerPayload") String mDeveloperPayload;
  @Getter @Setter@SerializedName("purchaseToken") String mToken;
}
