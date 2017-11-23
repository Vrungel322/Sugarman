package com.sugarman.myb.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by yegoryeriomin on 11/22/17.
 */

@AllArgsConstructor @NoArgsConstructor public class ContactForServer {
  @Setter @Getter @SerializedName("phone1") String phoneNumber;
  @Setter @Getter @SerializedName("phone2") String phoneNumber2;
  @Setter @Getter @SerializedName("phone3") String phoneNumber3;
  @Setter @Getter @SerializedName("name") String name;
  @Setter @Getter @SerializedName("email") String email;
}
