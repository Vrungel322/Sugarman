package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nikita on 25.09.17.
 */

public class PurchaseDataRequest {
  @SerializedName("user_id") private String userId;
  @SerializedName("country") private String country;
  @SerializedName("city") private String city;
  @SerializedName("street") private String street;
  @SerializedName("fullname") private String fullname;
  @SerializedName("phone_number") private String phone_number;
  @SerializedName("amount") private String amount;
  @SerializedName("count") private String count;
  @SerializedName("product_name") private String product_name;
  @SerializedName("zipcode") private String zipcode;

  public PurchaseDataRequest(String userId, String country, String city, String street,
      String fullname, String phone_number, String amount, String count, String product_name,
      String zipcode) {
    this.userId = userId;
    this.country = country;
    this.city = city;
    this.street = street;
    this.fullname = fullname;
    this.phone_number = phone_number;
    this.amount = amount;
    this.count = count;
    this.product_name = product_name;
    this.zipcode = zipcode;
  }
}
