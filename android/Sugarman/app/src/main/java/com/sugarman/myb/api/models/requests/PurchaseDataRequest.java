package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

/**
 * Created by nikita on 25.09.17.
 */
@AllArgsConstructor
public class PurchaseDataRequest {
  @SerializedName("country") private String country;
  @SerializedName("city") private String city;
  @SerializedName("street") private String street;
  @SerializedName("zipcode") private String zipcode;
  @SerializedName("fullname") private String fullname;
  @SerializedName("amount") private String amountPrice;
  @SerializedName("product_name") private String product_name;
  @SerializedName("quantity") private String count;
  @SerializedName("price") private String productPrice;
  @SerializedName("payment_type") private String paymentType;
  @SerializedName("phone_number") private String phone_number;


}
