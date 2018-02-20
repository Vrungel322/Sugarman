package com.sugarman.myb.models.mentor;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 20.02.2018.
 */
@Builder public class StringAbstraction {
  @Getter @Setter @SerializedName("mentor_data") String data;
}
