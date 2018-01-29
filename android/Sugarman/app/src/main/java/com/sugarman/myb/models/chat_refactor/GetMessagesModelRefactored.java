package com.sugarman.myb.models.chat_refactor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetMessagesModelRefactored {

  @SerializedName("code") @Expose private Integer code;
  @SerializedName("data") @Expose private Data data;

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }
}
