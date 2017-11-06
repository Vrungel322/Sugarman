package com.sugarman.myb;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.clover_studio.spikachatmodule.base.SingletonLikeApp;
import com.clover_studio.spikachatmodule.utils.CustomImageDownloader;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.paypal.android.MEP.PayPal;
import com.sugarman.myb.api.Api;
import com.sugarman.myb.api.ApiRx;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.di.components.AppComponent;
import com.sugarman.myb.di.components.DaggerAppComponent;
import com.sugarman.myb.di.modules.AppModule;
import com.sugarman.myb.eventbus.UIThreadBus;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.RetrofitUtils;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.StringHelper;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class App extends MultiDexApplication {

  private static AppComponent sAppComponent;

  public static AppComponent getAppComponent() {
    return sAppComponent;
  }

  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
  private static App sInstance;
  private static Context context;
  private static final Interceptor requestInterceptor = new Interceptor() {
    @Override public Response intercept(Chain chain) throws IOException {
      Request original = chain.request();
      Request request;

      String token = SharedPreferenceHelper.getAccessToken();
      Log.e("APP", "Token = " + token);
      Request.Builder requestBuilder = original.newBuilder();
      // .header("content-type", "application/json");
      TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      //Timber.e("IMEI" + telephonyManager.getDeviceId());
      //SharedPreferenceHelper.setIMEI(telephonyManager.getDeviceId());

      if (!TextUtils.isEmpty(token)) {
        requestBuilder.header(Constants.AUTHORIZATION, Constants.BEARER + token);
        requestBuilder.header(Constants.TIMEZONE, TimeZone.getDefault().getID());
        requestBuilder.header(Constants.TIMESTAMP, System.currentTimeMillis() + "");
        requestBuilder.header(Constants.VERSION, DeviceHelper.getAppVersionName());
        //requestBuilder.header(Constants.IMEI, SharedPreferenceHelper.getIMEI());
      }

      request = requestBuilder.build();

      return chain.proceed(request);
    }
  };
  private static volatile Handler sHandler;
  private static volatile Api sApiInstance;
  private static volatile ApiRx sRxApiInstance;
  private static volatile UIThreadBus sEventBus;
  private static volatile Gson sGson;
  private static volatile SoundPool soundPool;
  private final ActivityCallback activityCallback = new ActivityCallback();
  VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
    @Override public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
      Log.e("VK", "Token changed");
      if (newToken == null) {
        Log.e("VK", "New token is invalid");
      }
    }
  };
  private Tracker mTracker;

  public static synchronized App getInstance() {
    return sInstance;
  }

  public static synchronized Handler getHandlerInstance() {

    Handler localInstance = sHandler;
    if (localInstance == null) {
      synchronized (Handler.class) {
        localInstance = sHandler;
        if (localInstance == null) {
          sHandler = new Handler();
          localInstance = sHandler;
        }
      }
    }
    return localInstance;
  }

  public static synchronized SoundPool getSoundPoolInstance() {
    SoundPool localInstance = soundPool;
    if (localInstance == null) {
      synchronized (SoundPool.class) {
        localInstance = soundPool;
        if (localInstance == null) {
          soundPool = createSoundPool();
          localInstance = soundPool;
        }
      }
    }
    return localInstance;
  }

  public static synchronized EventBus getEventBus() {
    UIThreadBus localInstance = sEventBus;
    if (localInstance == null) {
      synchronized (UIThreadBus.class) {
        localInstance = sEventBus;
        if (localInstance == null) {
          sEventBus = new UIThreadBus();
          localInstance = sEventBus;
        }
      }
    }
    return localInstance;
  }

  public static synchronized Api getApiInstance() {
    Api localInstance = sApiInstance;
    if (localInstance == null) {
      synchronized (Api.class) {
        localInstance = sApiInstance;
        if (localInstance == null) {
          sApiInstance = createApi();
          localInstance = sApiInstance;
        }
      }
    }
    return localInstance;
  }

  public static synchronized ApiRx getRxApiInstance() {
    ApiRx localInstance = sRxApiInstance;
    if (localInstance == null) {
      synchronized (ApiRx.class) {
        localInstance = sRxApiInstance;
        if (localInstance == null) {
          sRxApiInstance = createRXApi();
          localInstance = sRxApiInstance;
        }
      }
    }
    return localInstance;
  }

  public static synchronized Gson getGsonInstance() {
    Gson localInstance = sGson;
    if (localInstance == null) {
      synchronized (Gson.class) {
        localInstance = sGson;
        if (localInstance == null) {
          sGson = createGson();
          localInstance = sGson;
        }
      }
    }
    return localInstance;
  }

  private static Gson createGson() {
    return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        .setDateFormat(StringHelper.SERVER_DATE_FORMAT)
        .create();
  }

  private static Api createApi() {
    OkHttpClient.Builder okHttpBuilder =
        new OkHttpClient.Builder().readTimeout(Config.RETROFIT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Config.RETROFIT_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(Config.RETROFIT_TIMEOUT, TimeUnit.SECONDS);

    okHttpBuilder.addInterceptor(requestInterceptor);

    if (BuildConfig.DEBUG) {
      HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      okHttpBuilder.addInterceptor(loggingInterceptor); // Only for debug.
    }

    OkHttpClient okHttpClient = okHttpBuilder.build();

    Retrofit retrofit = new Retrofit.Builder().baseUrl(Config.SERVER_URL)
    //Retrofit retrofit = new Retrofit.Builder().baseUrl(SharedPreferenceHelper.getBaseUrl())
        .addConverterFactory(GsonConverterFactory.create(getGsonInstance()))
        .client(okHttpClient)
        .build();
    return retrofit.create(Api.class);
  }

  private static ApiRx createRXApi() {
    return RetrofitUtils.provideRetrofit().create(ApiRx.class);
  }

  @SuppressWarnings("deprecation") private static SoundPool createSoundPool() {
    SoundPool soundPool;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      AudioAttributes attributes =
          new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build();
      soundPool = new SoundPool.Builder().setAudioAttributes(attributes).build();
    } else {
      soundPool = new SoundPool(5, AudioManager.STREAM_NOTIFICATION, 0);
    }

    return soundPool;
  }

  public static void appendLog(String tag, String text) {
    Log.d(tag, text);
    //if (BuildConfig.SAVE_LOGS) {
    //    File logFile = new File("sdcard/suga_log.txt");
    //    if (!logFile.exists()) {
    //        try {
    //            logFile.createNewFile();
    //        } catch (IOException e) {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }
    //    }
    //    try {
    //        //BufferedWriter for performance, true to set append to file flag
    //        BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
    //        buf.append("time  " + sdf.format(new Date(System.currentTimeMillis())) + "/" + tag + "/ " + text);
    //        buf.newLine();
    //        buf.close();
    //    } catch (IOException e) {
    //        // TODO Auto-generated catch block
    //        e.printStackTrace();
    //    }
    //}
  }

  @Override public void onCreate() {
    super.onCreate();

    context = getApplicationContext();

    sAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    PayPal ppObj = PayPal.initWithAppID(this, "APP-80W284485P519543T", PayPal.ENV_LIVE);

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    vkAccessTokenTracker.startTracking();
    VKSdk.initialize(this);

    Realm.init(this);


    Map<String, String> headers = new HashMap<>();
    headers.put("Referer", SingletonLikeApp.getInstance().getConfig(App.this).apiBaseUrl);
    DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
        .cacheOnDisk(true)
        .extraForDownloader(headers)
        .build();

    ImageLoaderConfiguration configImageLoader =
        new ImageLoaderConfiguration.Builder(getApplicationContext()).threadPoolSize(3)
            .defaultDisplayImageOptions(defaultOptions)
            .imageDownloader(new CustomImageDownloader(App.this))
            .build();
    ImageLoader.getInstance().init(configImageLoader);

    Crashlytics crashAnalyticsKit = new Crashlytics.Builder().core(
        new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build();

    Fabric.with(this, crashAnalyticsKit);

    registerActivityLifecycleCallbacks(activityCallback);

    sInstance = this;

    getDefaultTracker();

    FacebookSdk.sdkInitialize(this);
    AppEventsLogger.activateApp(this);
    Realm.init(this);
  }

  private synchronized Tracker getDefaultTracker() {
    if (mTracker == null) {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
      analytics.setAppOptOut(BuildConfig.DEBUG);
      mTracker = analytics.newTracker(R.xml.global_tracker);
    }
    return mTracker;
  }

  public boolean isAppForeground() {
    return activityCallback.isAppForeground();
  }

  public Activity getCurrentActivity() {
    return activityCallback.getCurrentActivity();
  }
}