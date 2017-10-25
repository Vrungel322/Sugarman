package com.sugarman.myb.di.modules;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sugarman.myb.BuildConfig;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.di.scopes.AppScope;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.IgnoreRequestUtils;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import dagger.Module;
import dagger.Provides;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
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

  @Provides @AppScope Retrofit provideRetrofit(Converter.Factory converterFactory,
      OkHttpClient okClient) {
    return new Retrofit.Builder().baseUrl(Config.SERVER_URL)
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

  @Provides @AppScope OkHttpClient provideOkClient(
      @Named("HeaderInterceptor") Interceptor headerInterceptor) {
    return new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS)
        .addNetworkInterceptor(headerInterceptor)
        .build();
  }

  @Provides @AppScope HttpLoggingInterceptor provideHttpLoggingInterceptor() {
    HttpLoggingInterceptor interceptor =
        new HttpLoggingInterceptor(message -> Timber.tag("response").d(message));
    interceptor.setLevel(
        BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BODY);
    return interceptor;
  }

  /**
   * For Cache
   */
  //@Provides @AppScope Cache provideCache(Context context) {
  //  Cache cache = null;
  //  try {
  //    cache = new Cache(new File(context.getCacheDir(), "http-cache"), 10 * 1024 * 1024); // 10 MB
  //  } catch (Exception e) {
  //    Timber.e(e, "Could not create Cache!");
  //  }
  //  return cache;
  //}
  //
  @Provides @AppScope @Named("HeaderInterceptor") Interceptor provideHeaderInterceptor() {
    return chain -> {
      Request request = chain.request();
      Response response = chain.proceed(chain.request());

      String token = SharedPreferenceHelper.getAccessToken();
      response = response.newBuilder().
          header(Constants.AUTHORIZATION, Constants.BEARER + token).
          header(Constants.TIMEZONE, TimeZone.getDefault().getID()).
          header(Constants.TIMESTAMP, System.currentTimeMillis() + "").
          header(Constants.VERSION, DeviceHelper.getAppVersionName()).build();

      return response;
    };
  }
  //
  //@Provides @AppScope @Named("OfflineCacheInterceptor") Interceptor provideOfflineCacheInterceptor(
  //    Context context) {
  //  return chain -> {
  //    Request request = chain.request();
  //    if (!NetworkUtil.isNetworkConnected(context)) {
  //      CacheControl cacheControl = new CacheControl.Builder().maxStale(7, TimeUnit.DAYS).build();
  //      request = request.newBuilder().cacheControl(cacheControl).build();
  //    }
  //    return chain.proceed(request);
  //  };
  //}
}
