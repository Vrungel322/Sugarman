package com.sugarman.myb.models.mentor;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 20.02.2018.
 */
@Builder
public class MentorFreeSomeLayer {
  @Getter @Setter @SerializedName("receipt_data") ReceiptData receiptData;
  @Getter @Setter @SerializedName("slot") private String freeSku;
  @Getter @Setter @SerializedName("server") private String vendor;
  @Getter @Setter @SerializedName("id_mentor") private String mentorId;

}
