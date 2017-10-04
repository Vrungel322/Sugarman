package com.sugarman.myb.api.error;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Vrungel on 03.08.2017.
 */

public class ErrorResponse implements IErrorResponse {
  @SerializedName("error") @Expose private List<String> error = null;

  public ErrorResponse(List<String> error) {
    this.error = error;
  }

  @Override public List<String> getError() {
    return error;
  }
}
