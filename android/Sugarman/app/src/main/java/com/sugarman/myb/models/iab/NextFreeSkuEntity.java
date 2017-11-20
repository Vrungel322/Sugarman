package com.sugarman.myb.models.iab;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 20.11.2017.
 */
@AllArgsConstructor
public class NextFreeSkuEntity {
  @Getter @Setter @SerializedName("next_slot") String freeSku;

}
