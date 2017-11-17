package com.sugarman.myb.utils;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sugarman.myb.BuildConfig;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import java.io.IOException;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by nikita on 19.09.17.
 */

public class RetrofitUtils {

  public static Retrofit provideRetrofit() {
    return new Retrofit.Builder().baseUrl(Config.SERVER_URL)
        .client(provideOkClient())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
        .addConverterFactory(provideConverterFactory())
        .build();
  }

  public static Converter.Factory provideConverterFactory() {
    return GsonConverterFactory.create(provideGson());
  }

  public static Gson provideGson() {
    return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        .serializeNulls()
        .create();
  }

  public static OkHttpClient provideOkClient() {
    return new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(provideHeaderInterceptor())
        .addInterceptor(provideHttpLoggingInterceptor())
        .addNetworkInterceptor(provideHttpLoggingInterceptor())
        .build();
  }

  public static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
    HttpLoggingInterceptor interceptor =
        new HttpLoggingInterceptor(message -> Timber.tag("response").d(message));
    interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.HEADERS
        : HttpLoggingInterceptor.Level.NONE);
    return interceptor;
  }

  public static okhttp3.Interceptor provideHeaderInterceptor() {
    Timber.e("Got into interceptor");
    okhttp3.Interceptor requestInterceptor = new okhttp3.Interceptor() {
      @Override public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request;

        String token = SharedPreferenceHelper.getAccessToken();
        Log.e("APP", "Token = " + token);
        Request.Builder requestBuilder = original.newBuilder();
        // .header("content-type", "application/json");

        if (!TextUtils.isEmpty(token)) {
          requestBuilder.header(Constants.AUTHORIZATION, Constants.BEARER + token);
          requestBuilder.header(Constants.TIMEZONE, TimeZone.getDefault().getID());
          requestBuilder.header(Constants.TIMESTAMP, System.currentTimeMillis() + "");
          requestBuilder.header(Constants.VERSION, DeviceHelper.getAppVersionName());
        }

        request = requestBuilder.build();

        return chain.proceed(request);
      }
    };
    return requestInterceptor;
  }
}