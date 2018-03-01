package com.sugarman.myb.di.modules;

import android.content.ContentResolver;
import android.content.Context;
import com.google.gson.Gson;
import com.sugarman.myb.api.ApiRx;
import com.sugarman.myb.api.RestApi;
import com.sugarman.myb.api.RestApiSpika;
import com.sugarman.myb.api.SpikaOSRetroApiInterfaceRx;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.data.DataManager;
import com.sugarman.myb.data.db.DbHelper;
import com.sugarman.myb.data.db.DbRepositoryStats;
import com.sugarman.myb.data.local.PreferencesHelper;
import com.sugarman.myb.di.scopes.AppScope;
import com.sugarman.myb.utils.apps_Fly.AppsFlyRemoteLogger;
import com.sugarman.myb.utils.inapp.IabHelper;
import com.sugarman.myb.utils.purchase.ProviderManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Created by Vrungel on 26.01.2017.
 */

@Module(includes = { RetrofitModule.class }) public class DataModule {

  @Provides @AppScope SpikaOSRetroApiInterfaceRx provideSpikaApi(
      @Named("Spika") Retrofit retrofit) {
    return retrofit.create(SpikaOSRetroApiInterfaceRx.class);
  }

  @Provides @AppScope ApiRx provideApi(@Named("Sugarman") Retrofit retrofit) {
    return retrofit.create(ApiRx.class);
  }

  @Provides @AppScope RestApi provideRestApi(ApiRx api) {
    return new RestApi(api);
  }

  @Provides @AppScope RestApiSpika provideRestApiSpika(SpikaOSRetroApiInterfaceRx api) {
    return new RestApiSpika(api);
  }

  @Provides @AppScope public ContentResolver provideContentResolver(Context context) {
    return context.getContentResolver();
  }

  @Provides @AppScope DbHelper provideDbHelper() {
    return new DbHelper();
  }

  @Provides @AppScope IabHelper provideIabHelper(Context context) {
    IabHelper mHelper = new IabHelper(context, Config.BASE_64_ENCODED_PUBLIC_KEY);

    mHelper.startSetup(result -> {
      if (!result.isSuccess()) {
        Timber.e("In-app Billing setup failed: " + result);
      } else {
        Timber.e("In-app Billing is set up OK");
      }
    });
    mHelper.enableDebugLogging(true);
    return mHelper;
  }

  @Provides @AppScope ProviderManager provideProviderManager(Context context, RestApi restApi,
      IabHelper iabHelper) {
    return new ProviderManager(context, restApi, iabHelper);
  }

  @Provides @AppScope DataManager provideDataManager(RestApiSpika restApiSpika, RestApi restApi,
      PreferencesHelper preferencesHelper, ContentResolver contentResolver, DbHelper dbHelper,
      AppsFlyRemoteLogger appsFlyRemoteLogger, ProviderManager providerManager) {
    return new DataManager(restApiSpika, restApi, preferencesHelper, contentResolver, dbHelper,
        appsFlyRemoteLogger, providerManager);
  }

  @Provides @AppScope PreferencesHelper providePreferencesHelper(Context context, Gson gson) {
    return new PreferencesHelper(context, gson);
  }

  @Provides @AppScope DbRepositoryStats provideDbRepositoryStats(){
    return new DbRepositoryStats();
  }
}
