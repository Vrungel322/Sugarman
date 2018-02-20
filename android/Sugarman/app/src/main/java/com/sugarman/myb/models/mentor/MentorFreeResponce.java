package com.sugarman.myb.models.mentor;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by nikita on 20.02.2018.
 */
@ToString public class MentorFreeResponce {
  @Getter @Setter @SerializedName("receipt_data") ReceiptData receiptData;
}

