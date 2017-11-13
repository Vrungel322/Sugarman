package com.sugarman.myb.models.iab;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

/**
 * Created by nikita on 13.11.2017.
 */
@AllArgsConstructor public class InAppBilling {
  @SerializedName("receipt") PurchaseForServer purchase;
  @SerializedName("signature") String signature;
}
