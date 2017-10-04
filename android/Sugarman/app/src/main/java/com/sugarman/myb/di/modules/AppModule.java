package com.sugarman.myb.di.modules;

import android.app.Application;
import android.content.Context;
import com.sugarman.myb.di.scopes.AppScope;
import com.sugarman.myb.utils.RxBus;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Vrungel on 25.01.2017.
 */

@Module(includes = { DataModule.class }) public class AppModule {

  private final Application mApplication;

  public AppModule(Application application) {
    mApplication = application;
  }

  @Provides @AppScope Context provideAppContext() {
    return mApplication;
  }

  @Provides @AppScope RxBus provideRxBus() {
    return new RxBus();
  }
}
