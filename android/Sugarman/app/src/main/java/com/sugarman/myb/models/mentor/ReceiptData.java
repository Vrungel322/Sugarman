package com.sugarman.myb.models.mentor;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by nikita on 20.02.2018.
 */
@ToString public class ReceiptData {
  @Getter @Setter @SerializedName("id_transaction") String transactionId;
  @Getter @Setter @SerializedName("code") int code;
  @Getter @Setter @SerializedName("status") String status;
}
