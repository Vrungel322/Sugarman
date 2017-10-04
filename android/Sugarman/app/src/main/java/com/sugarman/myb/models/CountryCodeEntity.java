package com.sugarman.myb.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nikita on 20.09.17.
 */

public class CountryCodeEntity {
  @SerializedName("countryName") private String countryName;
  @SerializedName("code") private String code;

  public CountryCodeEntity(String countryName, String code) {
    this.countryName = countryName;
    this.code = code;
  }

  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
