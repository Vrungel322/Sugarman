package com.sugarman.myb.di.modules;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sugarman.myb.BuildConfig;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.di.scopes.AppScope;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.IgnoreRequestUtils;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.apps_Fly.AppsFlyRemoteLogger;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
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
 * Created by John on 26.01.2017.
 */
@Module public class RetrofitModule {

  @Provides @AppScope @Named("Spika") Retrofit provideSpikaRetrofit(
      Converter.Factory converterFactory, OkHttpClient okClient) {
    return new Retrofit.Builder().baseUrl("https://sugarman-spika.herokuapp.com/spika/v1/")
        //return new Retrofit.Builder().baseUrl(Config.SERVER_URL)
        .client(okClient)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
        .addConverterFactory(converterFactory)
        .build();
  }

  @Provides @AppScope @Named("Sugarman") Retrofit provideRetrofit(
      Converter.Factory converterFactory, OkHttpClient okClient) {
    return new Retrofit.Builder().baseUrl(SharedPreferenceHelper.getBaseUrl())
        //return new Retrofit.Builder().baseUrl(Config.SERVER_URL)
        .client(okClient)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
        .addConverterFactory(converterFactory)
        .build();
  }

  @Provides @AppScope Converter.Factory provideConverterFactory(Gson gson) {
    return GsonConverterFactory.create(gson);
  }

  @Provides @AppScope Gson provideGson() {
    return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        .serializeNulls()
        .create();
  }

  @Provides @AppScope OkHttpClient provideOkClient(HttpLoggingInterceptor httpLoggingInterceptor,
      Cache cache, @Named("CacheInterceptor") Interceptor cacheInterceptor,
      @Named("OfflineCacheInterceptor") Interceptor offlineCacheInterceptor,
      @Named("HeaderInterceptor") Interceptor headerInterceptor) {
    return new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)
        .addInterceptor(httpLoggingInterceptor)

        //To turn on caching response - uncomment next three lines
        //.addInterceptor(offlineCacheInterceptor)
        //.addNetworkInterceptor(cacheInterceptor)
        //.cache(cache)
        .build();
  }

  @Provides @AppScope HttpLoggingInterceptor provideHttpLoggingInterceptor() {
    HttpLoggingInterceptor interceptor =
        new HttpLoggingInterceptor(message -> Timber.tag("response").d(message));
    interceptor.setLevel(
        BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BODY);
    return interceptor;
  }

  @Provides @AppScope @Named("HeaderInterceptor") Interceptor provideHeaderInterceptor(
      AppsFlyRemoteLogger appsFlyRemoteLogger) {
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
          requestBuilder.header(Constants.IMEI, SharedPreferenceHelper.getIMEI());
        }

        request = requestBuilder.build();
        Response response = chain.proceed(request);
        //Remote Logger
        Map<String, String> map = new HashMap<>();
        map.put("url", original.url().toString());
        map.put("response_code", "" + response.code());
        //if(response.body()!=null)
        //map.put("response_body" ,""+response.body().string());
        appsFlyRemoteLogger.report("server_request", map);
        return response;
      }
    };
    return requestInterceptor;
  }

  /**
   * For Cache
   */
  @Provides @AppScope Cache provideCache(Context context) {
    Cache cache = null;
    try {
      cache = new Cache(new File(context.getCacheDir(), "http-cache"), 10 * 1024 * 1024); // 10 MB
    } catch (Exception e) {
      Timber.e(e, "Could not create Cache!");
    }
    return cache;
  }

  @Provides @AppScope @Named("CacheInterceptor") Interceptor provideCacheInterceptor() {
    return chain -> {
      Request request = chain.request();
      Response response = chain.proceed(chain.request());

      if (IgnoreRequestUtils.ignoreRequests(request, "POST",
          SharedPreferenceHelper.getBaseUrl() + "v2/users")) {
        CacheControl cacheControl = new CacheControl.Builder().build();
        response = response.newBuilder()
            .removeHeader("Pragma")
            .header("Cache-Control", cacheControl.toString())
            .build();
      }
      return response;
    };
  }

  @Provides @AppScope @Named("OfflineCacheInterceptor") Interceptor provideOfflineCacheInterceptor(
      Context context) {
    return chain -> {
      Request request = chain.request();
      if (!DeviceHelper.isNetworkConnected()) {
        CacheControl cacheControl = new CacheControl.Builder().maxStale(7, TimeUnit.DAYS).build();
        request = request.newBuilder().cacheControl(cacheControl).build();
      }
      return chain.proceed(request);
    };
  }
}
