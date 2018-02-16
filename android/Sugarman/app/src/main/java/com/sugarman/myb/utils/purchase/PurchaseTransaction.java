package com.sugarman.myb.utils.purchase;

import com.sugarman.myb.api.models.responses.Tracking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nikita on 16.02.2018.
 */
@AllArgsConstructor @NoArgsConstructor @Builder public class PurchaseTransaction {
  @Getter @Setter private Tracking tracking;
}
