package com.sugarman.myb.di.modules;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sugarman.myb.BuildConfig;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.di.scopes.AppScope;
import dagger.Module;
import dagger.Provides;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
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

  @Provides @AppScope OkHttpClient provideOkClient(HttpLoggingInterceptor httpLoggingInterceptor
      /*,
      Cache cache, @Named("CacheInterceptor") Interceptor cacheInterceptor,
      @Named("OfflineCacheInterceptor") Interceptor offlineCacheInterceptor*/) {
    return new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor)
        //.addInterceptor(offlineCacheInterceptor)
        //.addNetworkInterceptor(cacheInterceptor)
        //.cache(cache)
        .build();
  }

  @Provides @AppScope HttpLoggingInterceptor provideHttpLoggingInterceptor() {
    HttpLoggingInterceptor interceptor =
        new HttpLoggingInterceptor(message -> Timber.tag("response").d(message));
    interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
        : HttpLoggingInterceptor.Level.BODY);
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
  //@Provides @AppScope @Named("CacheInterceptor") Interceptor provideCacheInterceptor() {
  //  return chain -> {
  //    Request request = chain.request();
  //    Response response = chain.proceed(chain.request());
  //
  //    if (IgnoreRequestUtils.ignoreRequests(request, "GET",
  //        Constants.Remote.BASE_URL + "api/v1/masters",
  //        Constants.Remote.BASE_URL + "api/v1/services",
  //        Constants.Remote.BASE_URL + "api/v1/categories",
  //        //all below cache to realm
  //        Constants.Remote.BASE_URL + "api/v1/pages", Constants.Remote.BASE_URL + "api/v1/products",
  //        Constants.Remote.BASE_URL + "api/v1/galleries",
  //        Constants.Remote.BASE_URL + "api/v1/users/me/bonuses_history",
  //        Constants.Remote.BASE_URL + "api/v1/users/me/bonuses",
  //        Constants.Remote.BASE_URL + "api/v1/texts",
  //        Constants.Remote.BASE_URL + "api/v1/partners")) {
  //      CacheControl cacheControl = new CacheControl.Builder().build();
  //      response = response.newBuilder()
  //          .removeHeader("Pragma")
  //          .header("Cache-Control", cacheControl.toString())
  //          .build();
  //    }
  //    return response;
  //  };
  //}
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
