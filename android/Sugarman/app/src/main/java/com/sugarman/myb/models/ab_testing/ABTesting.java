package com.sugarman.myb.models.ab_testing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nikita on 18.12.2017.
 */
@AllArgsConstructor @NoArgsConstructor public class ABTesting {
  public static int A = 0;
  public static int B = 1;
  @Getter @Setter @SerializedName("testing") @Expose private Integer aOrB;
}
