package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by yegoryeriomin on 11/8/17.
 */

@AllArgsConstructor public class DeleteUserRequest {
  @Getter @Setter @SerializedName("member_id") String memberId;
}
