package com.sugarman.myb.api.clients;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.JsonSyntaxException;
import com.sugarman.myb.App;
import com.sugarman.myb.BuildConfig;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.ErrorResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.utils.StringHelper;
import java.io.IOException;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import timber.log.Timber;

public abstract class BaseApiClient {

  private static final String TAG = BaseApiClient.class.getName();

  public static final String RESPONSE_IS_NULL = "Response is null";

  public static final int OLD_VERSION_CODE = 666;

  public static final String FAILURE_PARSE_ERROR_RESPONSE = "Failure parse error response";

  public static final String DEF_MESSAGE=App.getInstance().getString(R.string.no_internet_connection);

  WeakReference<ApiBaseListener> clientListener = new WeakReference<>(null);

  BaseApiClient() {
    //DEF_MESSAGE = App.getInstance().getString(R.string.no_internet_connection);
  }

  public void unregisterListener() {
    clientListener.clear();
  }

  void listenerNotRegistered(String prefix) {
    Log.d(TAG, prefix + " listener isn't registered");
  }

  void responseIsNull(String prefix) {
    Log.e(TAG, prefix + " response is null");
  }

  void responseFailure(String prefix, String message) {
    Log.e(TAG, prefix + " response is failure: " + message);
  }

  public static String requestFailure(String prefix, Throwable t) {
    String message = BuildConfig.DEBUG ? (t == null ? "" : t.getMessage()) : DEF_MESSAGE;
    Timber.e("request failure: " + message);
    return TextUtils.isEmpty(message) ? DEF_MESSAGE : message;
  }

  String parseErrorBody(ResponseBody errorBody) {
    return getErrorMessage(errorBody);
  }

  private String getErrorMessage(ResponseBody errorBody) {
    String error = "";
    String message;
    try {
      error = errorBody.string();
    } catch (IOException e) {
      Log.e(TAG, "failure parse error body", e);
    }
    if (TextUtils.isEmpty(error)) {
      message = FAILURE_PARSE_ERROR_RESPONSE;
    } else {
      try {
        message = App.getGsonInstance().fromJson(error, ErrorResponse.class).getMessage();
      } catch (JsonSyntaxException e) {
        String title = StringHelper.getApiErrorTitle(error);
        String errorMessage = StringHelper.getApiErrorMessage(error);
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(errorMessage)) {
          Log.e(TAG, "failure parse error", e);
          message = FAILURE_PARSE_ERROR_RESPONSE;
        } else {
          message = title + ". " + errorMessage;
        }
      }
    }
    return message;
  }

  abstract void registerListener(ApiBaseListener listener);
}
