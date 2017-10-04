package com.sugarman.myb.utils;

import com.sugarman.myb.di.modules.RetrofitModule;
import okhttp3.Request;

/**
 * Created by Vrungel on 12.07.2017.
 */

public class IgnoreRequestUtils {
  /**
   * This method forbid to cache some {@param requests}
   *
   * @param requestMethod GET POST ...
   * @param requests full links that we do not need to cache
   * @return just bool to control cache. Connected with {@link RetrofitModule#}!!!!
   */
  public static boolean ignoreRequests(Request request, String requestMethod, String... requests) {
    boolean isNeedToSaveCurrentRequest = false;
    if (request.method().equals(requestMethod)) {
      for (String s : requests) {
        if (request.url().toString().equals(s)) {
          isNeedToSaveCurrentRequest = false;
          return isNeedToSaveCurrentRequest;
        } else {
          isNeedToSaveCurrentRequest = true;
        }
      }
      return isNeedToSaveCurrentRequest;
    } else {
      return false;
    }
  }
}
