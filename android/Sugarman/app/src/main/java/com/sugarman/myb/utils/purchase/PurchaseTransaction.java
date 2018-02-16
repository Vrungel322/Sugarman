package com.sugarman.myb.utils.purchase;

import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.utils.inapp.Purchase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nikita on 16.02.2018.
 */
@AllArgsConstructor @NoArgsConstructor @Builder public class PurchaseTransaction {
  @Getter @Setter private String freeSku;
  @Getter @Setter private Tracking tracking;
  @Getter @Setter private String vendor;
  @Getter @Setter private String productTitle;
  @Getter @Setter private String mentorId;
  @Getter @Setter private Purchase purchase;
}
