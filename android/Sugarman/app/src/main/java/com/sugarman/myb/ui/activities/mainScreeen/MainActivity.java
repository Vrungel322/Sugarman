package com.sugarman.myb.ui.activities.mainScreeen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.facebook.FacebookException;
import com.mzelzoghbi.zgallery.CustomViewPager;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.App;
import com.sugarman.myb.BuildConfig;
import com.sugarman.myb.R;
import com.sugarman.myb.adapters.TrackingsPagerAdapter;
import com.sugarman.myb.api.clients.GetMyInvitesClient;
import com.sugarman.myb.api.clients.GetMyRequestsClient;
import com.sugarman.myb.api.clients.GetMyTrackingsClient;
import com.sugarman.myb.api.clients.GetNotificationsClient;
import com.sugarman.myb.api.clients.MarkNotificationClient;
import com.sugarman.myb.api.clients.SendFirebaseTokenClient;
import com.sugarman.myb.api.models.requests.ReportStats;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.invites.Invite;
import com.sugarman.myb.api.models.responses.me.notifications.Notification;
import com.sugarman.myb.api.models.responses.me.requests.Request;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.constants.NotificationMessageType;
import com.sugarman.myb.eventbus.events.ChallengeSelectedEvent;
import com.sugarman.myb.eventbus.events.DebugCacheStepsEvent;
import com.sugarman.myb.eventbus.events.DebugRealStepAddedEvent;
import com.sugarman.myb.eventbus.events.DebugRefreshStepsEvent;
import com.sugarman.myb.eventbus.events.DebugRequestStepsEvent;
import com.sugarman.myb.eventbus.events.GetInAppNotificationsEvent;
import com.sugarman.myb.eventbus.events.InviteRemovedEvent;
import com.sugarman.myb.eventbus.events.InvitesUpdatedEvent;
import com.sugarman.myb.eventbus.events.NeedOpenSpecificActivityEvent;
import com.sugarman.myb.eventbus.events.NotificationRemovedEvent;
import com.sugarman.myb.eventbus.events.RefreshTrackingsEvent;
import com.sugarman.myb.eventbus.events.ReportStepsEvent;
import com.sugarman.myb.eventbus.events.RequestsRemovedEvent;
import com.sugarman.myb.eventbus.events.RequestsUpdatedEvent;
import com.sugarman.myb.eventbus.events.StatsOpenedEvent;
import com.sugarman.myb.eventbus.events.StepServiceStartedEvent;
import com.sugarman.myb.eventbus.events.SwitchUIToNextDayEvent;
import com.sugarman.myb.eventbus.events.UpdateInvitesEvent;
import com.sugarman.myb.eventbus.events.UpdateRequestsEvent;
import com.sugarman.myb.listeners.ApiGetMyInvitesListener;
import com.sugarman.myb.listeners.ApiGetMyRequestListener;
import com.sugarman.myb.listeners.ApiGetMyTrackingsListener;
import com.sugarman.myb.listeners.ApiGetNotificationsListener;
import com.sugarman.myb.listeners.ApiMarkNotificationListener;
import com.sugarman.myb.listeners.ApiSendFirebaseTokenListener;
import com.sugarman.myb.listeners.SugarmanDialogListener;
import com.sugarman.myb.models.BaseChallengeItem;
import com.sugarman.myb.models.ChallengeItem;
import com.sugarman.myb.models.ChallengeMentorItem;
import com.sugarman.myb.models.ChallengeRescueItem;
import com.sugarman.myb.models.ChallengeWillStartItem;
import com.sugarman.myb.models.ContactForServer;
import com.sugarman.myb.models.ContactListForServer;
import com.sugarman.myb.models.NoChallengeItem;
import com.sugarman.myb.models.NoMentorsChallengeItem;
import com.sugarman.myb.models.ab_testing.ABTesting;
import com.sugarman.myb.models.custom_events.CustomUserEvent;
import com.sugarman.myb.services.MasterStepDetectorService;
import com.sugarman.myb.services.fetching_animation.FetchingAnimationService;
import com.sugarman.myb.ui.activities.CongratulationsActivity;
import com.sugarman.myb.ui.activities.DailyActivity;
import com.sugarman.myb.ui.activities.FailedActivity;
import com.sugarman.myb.ui.activities.GetUserInfoActivity;
import com.sugarman.myb.ui.activities.SearchGroupsActivity;
import com.sugarman.myb.ui.activities.StatsTrackingActivity;
import com.sugarman.myb.ui.activities.createGroup.CreateGroupActivity;
import com.sugarman.myb.ui.activities.groupDetails.GroupDetailsActivity;
import com.sugarman.myb.ui.activities.mentorList.MentorListActivity;
import com.sugarman.myb.ui.activities.profile.ProfileActivity;
import com.sugarman.myb.ui.activities.shop.ShopActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.dialogs.dialogRescueBoldMan.DialogRescueBoldMan;
import com.sugarman.myb.ui.dialogs.dialogRescueBoldManKick.DialogRescueBoldManKick;
import com.sugarman.myb.ui.fragments.BaseFragment;
import com.sugarman.myb.ui.fragments.NotificationsFragment;
import com.sugarman.myb.ui.views.CircleIndicatorView;
import com.sugarman.myb.ui.views.CustomFontTextView;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.ui.views.SquarePagerIndicatorArrows;
import com.sugarman.myb.utils.ContactsHelper;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.ImageToDraw;
import com.sugarman.myb.utils.IntentExtractorHelper;
import com.sugarman.myb.utils.RxBusHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.SoundHelper;
import com.sugarman.myb.utils.StringHelper;
import com.sugarman.myb.utils.animation.AnimationHelper;
import com.sugarman.myb.utils.inapp.IabHelper;
import com.sugarman.myb.utils.inapp.IabResult;
import com.sugarman.myb.utils.inapp.Inventory;
import com.sugarman.myb.utils.licence.LicenceChecker;
import com.sugarman.myb.utils.md5.MD5Util;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

public class MainActivity extends GetUserInfoActivity
    implements View.OnClickListener, IMainActivityView {
  private static final int REQUEST_READ_PHONE_STATE = 322;
  private static final String TAG = MainActivity.class.getName();
  private final List<Notification> myNotifications = new ArrayList<>(0);
  private final List<Invite> myInvites = new ArrayList<>(0);
  private final List<Request> myRequests = new ArrayList<>(0);
  private final HashMap<String, String> failedTrackingsId = new HashMap<>(0);
  private final HashMap<String, String> dailyTrackingsId = new HashMap<>(0);
  private final HashMap<String, String> congratulationTrackingsId = new HashMap<>(0);
  private final ApiSendFirebaseTokenListener apiSendFirebaseTokenListener =
      new ApiSendFirebaseTokenListener() {
        @Override public void onApiSendFirebaseTokenSuccess() {
          SharedPreferenceHelper.saveGCMToken("");
        }

        @Override public void onApiSendFirebaseTokenFailure(String message) {
          Log.d(TAG, "failure update firebase token. " + message);
        }

        @Override public void onApiUnauthorized() {
          Log.e("Token", "unauthorized");
          //logout();
        }

        @Override public void onUpdateOldVersion() {
          showUpdateOldVersionDialog();
        }
      };
  private final ApiMarkNotificationListener apiMarkNotificationListener =
      new ApiMarkNotificationListener() {
        @Override public void onApiMarkNotificationSuccess(String notificationId) {
          // nothing
        }

        @Override public void onApiMarkNotificationFailure(String notificationId, String message) {
          if (!DeviceHelper.isNetworkConnected()) {
            showNoInternetConnectionDialog();
          }
        }

        @Override public void onApiUnauthorized() {
          // logout();
          Log.e("Token", "unauthorized");
        }

        @Override public void onUpdateOldVersion() {
          showUpdateOldVersionDialog();
        }
      };
  IabHelper mHelper;
  IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = (purchase, result) -> {
    Timber.e("mConsumeFinishedListener" + purchase.toString());
    Timber.e("mConsumeFinishedListener" + result.toString());
    if (result.isSuccess()) {
    } else {
      // handle error
    }
  };
  @InjectPresenter MainActivityPresenter mPresenter;
  float angle;
  ImageToDraw img;
  Bitmap bmp;
  ImageView ivAnimatedMan;
  ImageView ivAvatar;
  boolean isUpdating = false;
  @BindView(R.id.vp_challenges) CustomViewPager vpTrackings;
  @BindView(R.id.vpWalkData) CustomViewPager vpWalkData;
  @BindView(R.id.ivLifebuoy) ImageView ivLifebuoy;
  private String mFreeSku = "v1.group_rescue";
  IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener =
      new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
          Timber.e("mFreeSku mReceivedInventoryListener " + mFreeSku);

          if (result.isFailure()) {
            // Handle failure
          } else {
            mHelper.consumeAsync(inventory.getPurchase(mFreeSku), mConsumeFinishedListener);
            //mHelper.consumeAsync(inventory.getAllPurchases(), mOnConsumeMultiFinishedListener);
            Timber.e(result.getMessage());
            Timber.e(inventory.getSkuDetails(mFreeSku).getTitle());
            Timber.e(inventory.getSkuDetails(mFreeSku).getSku());

            mPresenter.checkInAppBilling(inventory.getPurchase(mFreeSku),
                inventory.getSkuDetails(mFreeSku).getTitle(), mFreeSku);
          }
        }
      };
  private CircleIndicatorView civMain;
  private ImageView ivColoredStrip;
  private ImageView ivLeftPagerScroll;
  private ImageView ivRightPagerScroll;
  private ImageView ivBackgroundMenu;
  private TextView tvCounter;
  private TextView tvEvents;
  private TextView tvStartValue;
  private TextView tvCalculated;
  private TextView tvCache;
  private SquarePagerIndicatorArrows spiChallenges;
  private SquarePagerIndicatorArrows spiWalkData;
  private View vCircleContainer;
  private View vOpenStats;
  private View vSearchGroup;
  private View vCreateGroup;
  private CustomFontTextView tvBlinkTitle;
  private ImageView ivSearchGroups;
  private View vShopButton;
  private NotificationsFragment notificationsFragment;
  private String[] notificationsFlags;
  private Tracking[] myTrackings = new Tracking[0];
  private TrackingsPagerAdapter trackingsAdapter;
  private SendFirebaseTokenClient mSendFirebaseTokenClient;
  private GetMyTrackingsClient mGetMyTrackingsClient;
  private final ApiGetMyInvitesListener apiGetMyInvitesListener = new ApiGetMyInvitesListener() {
    @Override public void onApiGetMyInvitesSuccess(Invite[] invites, boolean isRefreshTrackings) {
      Timber.e("invites lendth " + invites.length);
      List<Invite> actualInvites = new ArrayList<>(invites.length);
      for (Invite invite : invites) {
        String status = invite.getTracking().getStatus();
        Timber.e("IS MENTOR GROUP " + invite.getTracking().isMentors());
        if (!TextUtils.equals(status, Constants.STATUS_FAILED) && !TextUtils.equals(status,
            Constants.STATUS_COMPLETED)) {
          actualInvites.add(invite);
        }
      }

      App.getEventBus().post(new InvitesUpdatedEvent(actualInvites));

      myInvites.clear();
      myInvites.addAll(actualInvites);
      updateEventsCount();

      if (isRefreshTrackings) {
        refreshTrackings();
      } else {
        closeProgressFragment();
      }
    }

    @Override public void onApiGetMyInvitesFailure(String message) {
      closeProgressFragment();
    }

    @Override public void onApiUnauthorized() {
      // logout();
      Log.e("Token", "unauthorized");
    }

    @Override public void onUpdateOldVersion() {
      showUpdateOldVersionDialog();
    }
  };
  private final ApiGetMyRequestListener apiGetMyRequestListener = new ApiGetMyRequestListener() {
    @Override
    public void onApiGetMyRequestsSuccess(Request[] requests, boolean isRefreshTrackings) {
      List<Request> actualRequests = new ArrayList<>(requests.length);
      for (Request request : requests) {
        String status = request.getTracking().getStatus();
        if (!TextUtils.equals(status, Constants.STATUS_FAILED) && !TextUtils.equals(status,
            Constants.STATUS_COMPLETED)) {
          actualRequests.add(request);
        }
      }

      App.getEventBus().post(new RequestsUpdatedEvent(actualRequests));
      myRequests.clear();
      myRequests.addAll(actualRequests);
      updateEventsCount();

      if (isRefreshTrackings) {
        refreshTrackings();
      } else {
        closeProgressFragment();
      }
    }

    @Override public void onApiGetMyRequestsFailure(String message) {
      closeProgressFragment();
    }

    @Override public void onApiUnauthorized() {

    }

    @Override public void onUpdateOldVersion() {

    }
  };
  private GetMyInvitesClient mGetMyInvitesClient;
  private GetMyRequestsClient mGetMyRequestsClient;
  private GetNotificationsClient mGetNotificationsClient;
  private MarkNotificationClient mMarkNotificationsClient;
  private AnimationDrawable animationMan;
  private int nextMilestone = 0;
  private SensorManager mSensorManager;
  private Sensor stepCounter;
  private int vpTrackingPadding;
  private int todaySteps = 0;
  private int animationTodaySteps = 0;
  private int animationIndex = 0;
  private boolean isAnimationRunned;
  private int createdTrackingPosition = -1;
  private Tracking swipedTracking;
  private final Runnable showCreatedTracking = new Runnable() {
    @Override public void run() {
      if (vpTrackings != null
          && createdTrackingPosition >= 0
          && createdTrackingPosition < trackingsAdapter.getCount()) {
        vpTrackings.setCurrentItem(createdTrackingPosition, false);
      }

      swipedTracking = null;
      closeProgressFragment();
    }
  };
  private List<Tracking> mMentorsGroups = new ArrayList<>();
  private File cachedImagesFolder;
  private String userId;
  private int[] brokenGlassSoundIds;
  private final ApiGetNotificationsListener apiGetNotificationsListener =
      new ApiGetNotificationsListener() {
        @Override public void onApiGetNotificationsSuccess(Notification[] notifications) {
          for (Notification notification : notifications) {
            if (!myNotifications.contains(notification)) {
              int type = StringHelper.getNotificationMessageType(notificationsFlags,
                  notification.getText());
              if (type == NotificationMessageType.USER_NAME_HAS_POKED_YOU
                  || type == NotificationMessageType.PINGED_YOU_TO_MYB) {
                App.getHandlerInstance().postDelayed(new Runnable() {
                  @Override public void run() {
                    int id = new Random().nextInt(7);
                    SoundHelper.play(brokenGlassSoundIds[id]);
                  }
                }, Config.MAIN_POKE_SOUND_DELAY);
              }

              String trackingId = notification.getTrackingId();
              String notificationId = notification.getId();
              switch (type) {
                case NotificationMessageType.GROUP_NAME_HAS_FAILED:
                  failedTrackingsId.put(notificationId, trackingId);
                  break;
                case NotificationMessageType.CONGRATS:
                  congratulationTrackingsId.put(notificationId, trackingId);
                  break;
                case NotificationMessageType.DAILY_SUGARMAN:
                  dailyTrackingsId.put(notificationId, trackingId);
                  break;
                default:
                  myNotifications.add(notification);
                  break;
              }
            }
          }

          int countNotifications = myNotifications.size();
          if (countNotifications > 0) {
            if (notificationsFragment != null) {
              closeFragment(notificationsFragment);
            }

            notificationsFragment = NotificationsFragment.newInstance(
                myNotifications.toArray(new Notification[countNotifications]), MainActivity.this);
            showFragment(notificationsFragment);
          }

          closeProgressFragment();

          showFullscreenNotifications();
        }

        @Override public void onApiGetNotificationsFailure(String message) {
          closeProgressFragment();
          Timber.e("isNetworkConnected onApiGetNotificationsFailure"
              + DeviceHelper.isNetworkConnected());

          if (DeviceHelper.isNetworkConnected()) {
            new SugarmanDialog.Builder(MainActivity.this,
                DialogConstants.API_GET_NOTIFICATIONS_FAILURE_ID).content(message).show();
          } else {
            showNoInternetConnectionDialog();
          }
        }

        @Override public void onApiUnauthorized() {
          // logout();
          Log.e("Token", "unauthorized");
        }

        @Override public void onUpdateOldVersion() {
          showUpdateOldVersionDialog();
        }
      };
  private WalkDataViewPagerAdapter mWalkDataViewPagerAdapter;
  private int firstFailedGroupPosition = -1;
  private final ApiGetMyTrackingsListener apiGetMyTrackingsListener =
      new ApiGetMyTrackingsListener() {
        @Override
        public void onApiGetMyTrackingSuccess(Tracking[] trackings, List<Tracking> mentorsGroup,
            boolean isRefreshNotifications) {
          myTrackings = trackings;
          mMentorsGroups = mentorsGroup;
          List<BaseChallengeItem> converted = prepareTrackingItems();

          updatePagerTrackings();
          updateAnimations(todaySteps);
          updateTodaySteps(todaySteps);

          if (swipedTracking != null) {
            ChallengeWillStartItem item = new ChallengeWillStartItem();
            item.setTracking(swipedTracking);
            createdTrackingPosition = converted.indexOf(item);

            App.getHandlerInstance()
                .postDelayed(showCreatedTracking, Constants.OPEN_CREATED_CHALLENGE_TIMEOUT);
          }
          //setTrackingsArrowsVisibility(vpTrackings.getCurrentItem());

          if (isRefreshNotifications) {
            mGetNotificationsClient.getNotifications();
          } else {
            closeProgressFragment();
          }
        }

        @Override public void onApiGetMyTrackingsFailure(String message) {
          closeProgressFragment();
          if (DeviceHelper.isNetworkConnected()) {
            // new SugarmanDialog.Builder(MainActivity.this, DialogConstants.API_GET_MY_TRACKINGS_FAILURE_ID)
            //         .content(message)
            //         .btnCallback(MainActivity.this)
            //         .show();
          } else {
            showNoInternetConnectionDialog();
          }
        }

        @Override public void onApiUnauthorized() {
          // logout();
          Log.e("Token", "unauthorized");
        }

        @Override public void onUpdateOldVersion() {
          showUpdateOldVersionDialog();
        }
      };

  public void download() throws Exception {
    List<File> results = new ArrayList<>();
    final int[] done = { 0 };
    List<String> urls = new ArrayList<>();

    for (int i = 0; i < 2000; i++) {
      urls.add(String.valueOf(2000 - i - 1));
    }

    new AnimationHelper(null, urls).download(new AnimationHelper.Callback() {
      @Override public void onEach(File image) {
        results.add(image);
        try {
          FileOutputStream out = new FileOutputStream(image);
          out.flush();
          out.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      @Override public void onDone(File imagesDir) {
        done[0]++;
      }
    });

    while (results.size() < 2000) {
      Thread.sleep(10);
    }
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_main);
    super.onCreate(savedInstanceState);
    Resources resources = getResources();
    img = new ImageToDraw(MainActivity.this);

    //setupInAppPurchase();

    Timber.e(MD5Util.md5("md5 test"));

    ivLifebuoy.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        vpTrackings.setCurrentItem(firstFailedGroupPosition);
      }
    });

    cachedImagesFolder = new File(getFilesDir() + "/animations/");

    //mPresenter.getAnimations(cachedImagesFolder);
    startService(new Intent(this, FetchingAnimationService.class));
    //Timber.e("!!!! " +new File(cachedImagesFolder.list()[0]));
    //Timber.e(
    //    "!!!! " + MD5Util.calculateMD5(new File(getFilesDir() + "/animations/"+cachedImagesFolder.list()[0])));

    //Timber.e("!!!! " +new File(cachedImagesFolder.list()[0]));
    //Timber.e(
    //    "!!!! " + MD5Util.calculateMD5(new File("android.resource://com.sugarman.myb/drawable/image_name")));

    //new SugarmanDialog.Builder(this,"doesn't work").content("INSTALLER PACKAGE NAME : " + getPackageManager()
    //    .getInstallerPackageName(getPackageName()) + " LICENCE CHECKED " + LicenceChecker.isStoreVersion(this)).build().show();

    Timber.e("INSTALLER PACKAGE NAME : "
        + getPackageManager().getInstallerPackageName(getPackageName())
        + " LICENCE CHECKED "
        + LicenceChecker.isStoreVersion(this));

    if (!LicenceChecker.isStoreVersion(this) && !BuildConfig.DEBUG) {
      Timber.e("Ochko ebuchee chmo");
      new SugarmanDialog.Builder(this, "").btnCallback(new SugarmanDialogListener() {
        @Override public void onClickDialog(SugarmanDialog dialog, DialogButton button) {
        }
      }).content("Not licenced").build().show();

      finishAffinity();
      System.exit(0);
    }
    ;

    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), "af_launch_main", eventValue);

    ////Get IMEI
    //int permissionCheck =
    //    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
    //if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
    //  ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_PHONE_STATE },
    //      REQUEST_READ_PHONE_STATE);
    //} else {
    //  saveIMEI();
    //}

    ActivityCompat.requestPermissions(this, new String[] {
        Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA,
        Manifest.permission.READ_PHONE_STATE
    }, 1);
    //ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 2);
    //ViewTreeObserver vto = loadingStrip.getViewTreeObserver();
    //vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
    //    @Override
    //    public void onGlobalLayout() {
    //        loadingStrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    //        int width  = loadingStrip.getMeasuredWidth();
    //        int height = loadingStrip.getMeasuredHeight();
    //        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
    //        Bitmap bmp = Bitmap.createBitmap(width, height, conf); // this creates a MUTABLE bitmap
    //        Canvas canvas = new Canvas(bmp);
    //        Paint p = new Paint();
    //        p.setStrokeWidth(height/2);
    //        p.setStrokeCap(Paint.Cap.ROUND);
    //        p.setColor(0xfff8C1C1);
    //        canvas.drawLine(50,height/2,width-50,height/2, p);
    //        p.setColor(0xffFD3E3E);
    //        int drawto=((width-100)/21) * days;
    //        canvas.drawLine(50,height/2,drawto,height/2, p);
    //-

    //        loadingStrip.setImageBitmap(bmp);
    //
    //    }
    //});

    vShopButton = findViewById(R.id.iv_shop);
    vShopButton.setOnClickListener(view -> {

      Map<String, Object> eventValues = new HashMap<>();
      eventValues.put(AFInAppEventParameterName.LEVEL, 9);
      eventValues.put(AFInAppEventParameterName.SCORE, 100);
      AppsFlyerLib.getInstance()
          .trackEvent(App.getInstance().getApplicationContext(), "af_open_shop", eventValues);

      Intent intent = new Intent(MainActivity.this, ShopActivity.class);
      startActivity(intent);
    });

    DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
    String date = dfDate.format(Calendar.getInstance().getTime());
    Timber.e("Date " + date);
    //SharedPreferenceHelper.setTodayDate(date);
    SharedPreferenceHelper.setTodayDateForShowedSteps(date);
    SharedPreferenceHelper.getTodayDateForShowedSteps();
    Timber.e("Save Showed Steps get today date " + SharedPreferenceHelper.getStepsForTheDate(
        SharedPreferenceHelper.getTodayDate()));
    SharedPreferenceHelper.saveShowedSteps(SharedPreferenceHelper.getStepsForTheDate(date));

    SharedPreferenceHelper.setOnLaunch(true);

    //refreshUserData(AccessToken.getCurrentAccessToken());
    System.out.println(
        "HOW MANY MILISECONDS TO MIDNIGHT: " + DeviceHelper.howManyMillisecondsToMidnight());
    new Handler().postDelayed(() -> {
      Timber.e("Save Showed Steps 0");
      SharedPreferenceHelper.saveShowedSteps(0);
      //SharedPreferenceHelper.shiftStatsLocalOnOneDay(SharedPreferenceHelper.getUserId());
      Log.d("Helper local pref", ""
          + SharedPreferenceHelper.getReportStatsLocal(userId)[0].getDate()
          + " - "
          + SharedPreferenceHelper.getReportStatsLocal(userId)[0].getStepsCount());
      //SharedPreferenceHelper.shiftStatsOnOneDay(SharedPreferenceHelper.getUserId());

      //      refreshUserData(AccessToken.getCurrentAccessToken());
      todaySteps = SharedPreferenceHelper.getReportStatsLocal(SharedPreferenceHelper.getUserId())[0]
          .getStepsCount();
      Calendar c = Calendar.getInstance();
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
      todaySteps = SharedPreferenceHelper.getStepsForTheDate(df.format(c.getTime()));
      System.out.println(todaySteps);
      updateTodaySteps(todaySteps);
      System.out.println("Refreshed data on midnight");
      //doesn't work
    }, DeviceHelper.howManyMillisecondsToMidnight());

    notificationsFlags = resources.getStringArray(R.array.notifications_types);
    vpTrackingPadding = resources.getDimensionPixelSize(R.dimen.main_padding);

    mSendFirebaseTokenClient = new SendFirebaseTokenClient();
    mGetMyTrackingsClient = new GetMyTrackingsClient();
    mGetMyInvitesClient = new GetMyInvitesClient();
    mGetMyRequestsClient = new GetMyRequestsClient();
    mGetNotificationsClient = new GetNotificationsClient();
    mMarkNotificationsClient = new MarkNotificationClient();
    mSendFirebaseTokenClient.registerListener(apiSendFirebaseTokenListener);

    Intent intent = getIntent();
    getIntentData(intent);

    //brokenGlassSoundId = SharedPreferenceHelper.getBrokenGlassId();
    brokenGlassSoundIds = SharedPreferenceHelper.getBrokenGlassIds();

    mSendFirebaseTokenClient.unregisterListener();

    vSearchGroup = findViewById(R.id.ll_search_container);
    vCreateGroup = findViewById(R.id.iv_create_group);
    ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
    vOpenStats = findViewById(R.id.rl_main_steps_container);
    vCircleContainer = findViewById(R.id.rl_container_circle);
    tvCounter = (TextView) findViewById(R.id.tv_counter);
    tvCalculated = (TextView) findViewById(R.id.tv_calculated);
    tvCache = (TextView) findViewById(R.id.tv_cache);
    tvStartValue = (TextView) findViewById(R.id.tv_start_value);
    tvEvents = (TextView) findViewById(R.id.tv_avatar_events);
    civMain = (CircleIndicatorView) findViewById(R.id.civ_main);
    //vpTrackings = (ViewPager) findViewById(R.id.vp_challenges);
    //ivLeftPagerScroll = (ImageView) findViewById(R.id.iv_left_pager_scroll);
    //ivRightPagerScroll = (ImageView) findViewById(R.id.iv_right_pager_scroll);
    spiChallenges = (SquarePagerIndicatorArrows) findViewById(R.id.spi_challenges);
    spiWalkData = (SquarePagerIndicatorArrows) findViewById(R.id.spiWalkData);
    civMain.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    tvBlinkTitle = (CustomFontTextView) findViewById(R.id.tv_blink_title);
    ivColoredStrip = (ImageView) findViewById(R.id.colored_strip);
    ivBackgroundMenu = (ImageView) findViewById(R.id.background_menu);
    Log.e("Indicator in create", "" + civMain.getWidth());

    Typeface tfDin = Typeface.createFromAsset(getAssets(), "din_light.ttf");

    tvCounter.setTypeface(tfDin);

    ivSearchGroups = (ImageView) findViewById(R.id.iv_search_group);
    ivSearchGroups.setX(ivSearchGroups.getX() + 10);
    ivBackgroundMenu.setX(ivBackgroundMenu.getX() - 10);
    vCreateGroup.setX(vCreateGroup.getX() + 10);
    ivSearchGroups.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        openSearchGroupActivity();
      }
    });

    if (BuildConfig.DEBUG) {
      //tvCalculated.setVisibility(View.VISIBLE);
      //tvCache.setVisibility(View.VISIBLE);
      //tvStartValue.setVisibility(View.VISIBLE);
    }

    updateEventsCount();

    trackingsAdapter = new TrackingsPagerAdapter(getSupportFragmentManager());

    vpTrackings.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override public void onPageSelected(int position) {
        //setTrackingsArrowsVisibility(position);
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
    vpTrackings.setAdapter(trackingsAdapter);

    updatePagerTrackings();

    vCreateGroup.setOnClickListener(this);
    vSearchGroup.setOnClickListener(this);
    ivAvatar.setOnClickListener(this);
    vOpenStats.setOnClickListener(this);

    startStepDetectorService();

    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    stepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    mGetMyTrackingsClient.registerListener(apiGetMyTrackingsListener);
    mGetMyInvitesClient.registerListener(apiGetMyInvitesListener);
    mGetMyRequestsClient.registerListener(apiGetMyRequestListener);
    mGetNotificationsClient.registerListener(apiGetNotificationsListener);
    mMarkNotificationsClient.registerListener(apiMarkNotificationListener);

    int openActivityCode = IntentExtractorHelper.getOpenActivityCode(intent);
    String trackingId = IntentExtractorHelper.getTrackingIdFromFcm(intent);
    String url = IntentExtractorHelper.getUrlFromFcm(intent);
    Timber.e("openActivityCode " + openActivityCode);
    switch (openActivityCode) {
      case Constants.OPEN_INVITES_ACTIVITY:
        openInvitesActivity();
        break;
      case Constants.OPEN_REQUESTS_ACTIVITY:
        openRequestsActivity();
        break;
      case Constants.OPEN_CONGRATULATION_ACTIVITY:
        openCongratulationActivity(trackingId);
        break;
      case Constants.OPEN_DAILY_ACTIVITY:
        openDailyActivity(trackingId);
        break;
      case Constants.OPEN_FAILED_ACTIVITY:
        openFailedActivity(trackingId);
        break;
      case Constants.OPEN_EXTERNAL_URL:
        Timber.e("OPEN EXTERNAL URL");
        openLink(url);
        break;
      case Constants.OPEN_MAIN_ACTIVITY:
        if (!TextUtils.isEmpty(trackingId)) {
          swipedTracking = new Tracking();
          swipedTracking.setId(trackingId);

          refreshTrackings();
        }
        break;
    }

    userId = SharedPreferenceHelper.getUserId();

    ivAnimatedMan = (ImageView) findViewById(R.id.iv_animated_man);
    Log.e("Showed steps before ai", "" + SharedPreferenceHelper.getShowedSteps());
    updateAnimations(todaySteps);

    mGetMyInvitesClient.getInvites(true);
    mGetMyRequestsClient.getRequests(true);

    // pager adapter
    mWalkDataViewPagerAdapter = new WalkDataViewPagerAdapter(this);
    vpWalkData.setAdapter(mWalkDataViewPagerAdapter);

    int positionToSet = Integer.MAX_VALUE / 2;

    vpWalkData.setCurrentItem(positionToSet);
    spiWalkData.showArrows(false);
    spiWalkData.setMaxIndicatorCircles(3);
    spiWalkData.setViewPager(vpWalkData);
    spiWalkData.requestLayout();
    spiWalkData.setVisibility(View.GONE);
  }

  @Override public void onRequestPermissionsResult(int requestCode, String permissions[],
      int[] grantResults) {
    switch (requestCode) {
      case 1:
        if ((grantResults.length > 0) && (grantResults[2] == PackageManager.PERMISSION_GRANTED)) {

          if (!SharedPreferenceHelper.getContactsSent()) {
            AsyncTask.execute(() -> {
              List<ContactForServer> contacts = new ArrayList<>();
              ContactListForServer list =
                  ContactsHelper.getContactListMultipleNumbers(MainActivity.this);

              mPresenter.sendContacts(list);
            });
          }

          saveIMEI();
        }
        break;

      default:
        break;
    }
  }

  private void saveIMEI() {
    TelephonyManager telephonyManager =
        (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    SharedPreferenceHelper.setIMEI(telephonyManager.getDeviceId());
  }

  private void updateAnimations(int todaySteps) {
    // TODO: 12.07.2017 next milestone
    int animSteps = todaySteps;
    if (todaySteps > 0 && myTrackings.length == 0) {
      Timber.e(" updateAnimations 1");
      ivAnimatedMan.setBackgroundResource(R.drawable.animation_sugarman_walkpoint);
      animationMan = (AnimationDrawable) ivAnimatedMan.getBackground();
      animationMan.stop();
      animationMan.start();
      return;
    }
    if (todaySteps == 0 && myTrackings.length == 0) {
      Timber.e(" updateAnimations 2");

      ivAnimatedMan.setBackgroundResource(R.drawable.animation_sugarman_point);
      animationMan = (AnimationDrawable) ivAnimatedMan.getBackground();
      animationMan.stop();
      animationMan.start();
      return;
    } else {
      Log.e("Anim", "Milestone " + nextMilestone + "; Steps " + animSteps);

      if (nextMilestone == 0) {
        ivAnimatedMan.setBackgroundResource(R.drawable.animation_sugarman_stand);
        animationMan = (AnimationDrawable) ivAnimatedMan.getBackground();
        if (animSteps > 0) {
          nextMilestone = 5000;
          Log.e("Anim", "STARTTTTTT");
          animationMan.stop();
          animationMan.start();
        }
      } else if (nextMilestone == 5000) {

        //ivAnimatedMan.setBackgroundResource(R.drawable.animation_sugarman_slow);
        //animationMan = (AnimationDrawable) ivAnimatedMan.getBackground();
        //if (animSteps > 5000) {
        //  nextMilestone = 7500;
        //  animationMan.stop();
        //  animationMan.start();
        //}
      } else if (nextMilestone == 7500) {

        //ivAnimatedMan.setBackgroundResource(R.drawable.animation_sugarman);
        //animationMan = (AnimationDrawable) ivAnimatedMan.getBackground();
        //if (animSteps > 7500) {
        //  nextMilestone = 10000;
        //  animationMan.stop();
        //  animationMan.start();
        //}
      } else if (nextMilestone == 10000) {
        //ivAnimatedMan.setBackgroundResource(R.drawable.animation_sugarman_fast);
        //animationMan = (AnimationDrawable) ivAnimatedMan.getBackground();
        //if (animSteps > 10000) {
        //  nextMilestone = 11000;
        //  animationMan.stop();
        //  animationMan.start();
        //}
      } else if (nextMilestone == 11000) {
        //ivAnimatedMan.setBackgroundResource(R.drawable.animation_sugarman_eleven);
        //animationMan = (AnimationDrawable) ivAnimatedMan.getBackground();
        //if (animSteps > 11000) {
        //  nextMilestone = 12000;
        //  animationMan.stop();
        //  animationMan.start();
        //}
      } else {
        //ivAnimatedMan.setBackgroundResource(R.drawable.animation_sugarman_twelve);
        //animationMan = (AnimationDrawable) ivAnimatedMan.getBackground();
      }
      if (!animationMan.isRunning()) animationMan.start();
    }
  }

  @Override protected void onResume() {
    super.onResume();
    String urlAvatar = SharedPreferenceHelper.getAvatar();

    if (TextUtils.isEmpty(urlAvatar)) {
    } else {
      CustomPicasso.with(this)
          .load(urlAvatar)
          .fit()
          .centerCrop()
          .error(R.drawable.ic_red_avatar)
          .transform(new MaskTransformation(this, R.drawable.main_screen_avatar, false, 0xfff))
          .into(ivAvatar);
    }
    updateTodaySteps(todaySteps);
    //int currentItem = vpTrackings.getCurrentItem();
    //
    //

    vCircleContainer.setVisibility(View.VISIBLE);
    vSearchGroup.setVisibility(View.VISIBLE);
    vpTrackings.setVisibility(View.VISIBLE);

    //setTrackingsArrowsVisibility(vpTrackings.getCurrentItem());

    vOpenStats.setOnClickListener(this);

    String fcmToken = SharedPreferenceHelper.getGCMToken();
    if (!TextUtils.isEmpty(fcmToken)) {
      mSendFirebaseTokenClient.sendFirebaseToken(fcmToken);
    }

    int countNotifications = myNotifications.size();
    if (countNotifications > 0) {
      notificationsFragment = NotificationsFragment.newInstance(
          myNotifications.toArray(new Notification[countNotifications]), MainActivity.this);
      showFragment(notificationsFragment);
    } else if (countNotifications == 0 && notificationsFragment != null) {
      closeFragment(notificationsFragment);
    }

    //if(!isUpdating) {

    mGetMyTrackingsClient.getMyTrackings(true);
    //Timer t = new Timer();
    //t.schedule(new TimerTask() {
    //                          @Override
    //                          public void run() {
    //                              Log.e("TRRRRAH", "KEKE");
    //                              mGetMyTrackingsClient.getMyTrackings(true);
    //                          }
    //
    //                      },
    //        0,
    //        5000);
    // TODO: 23.08.2017 METOCHKA
    //vpTrackings.setCurrentItem(0);
    //spiChallenges.setCurrentItem(0);

    //}
  }

  public void setAnimationMan(int animationId) {
    ivAnimatedMan.setBackgroundResource(animationId);
    animationMan = (AnimationDrawable) ivAnimatedMan.getBackground();
    animationMan.start();
  }

  @Override protected void onStart() {
    super.onStart();
    Timber.e("onStart");

    //if(animationMan!=null)
    //animationMan.start();
    ReportStats lastStats[] =
        SharedPreferenceHelper.getReportStatsLocal(SharedPreferenceHelper.getUserId());
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "MainActivity start() uid "
        + SharedPreferenceHelper.getUserId()
        + "length report stats "
        + lastStats.length);
    if (lastStats != null && lastStats.length != 0) {
      todaySteps = SharedPreferenceHelper.getReportStatsLocal(SharedPreferenceHelper.getUserId())[0]
          .getStepsCount();
    } else {
      todaySteps = 0;
    }

    if (todaySteps == 0) {
      nextMilestone = 0;
    } else if (todaySteps > 0 && todaySteps < 5000) {
      nextMilestone = 5000;
    } else if (todaySteps >= 5000 && todaySteps < 7500) {
      nextMilestone = 7500;
    } else if (todaySteps >= 7500 && todaySteps < 10000) {
      nextMilestone = 11000;
    } else if (todaySteps >= 10000 && todaySteps < 11000) {
      nextMilestone = 12000;
    } else {
      nextMilestone = 10228;
    }

    App.getEventBus().post(new DebugRequestStepsEvent(todaySteps));
    showFullscreenNotifications();
    updateAnimations(todaySteps);
    mGetMyTrackingsClient.getMyTrackings(true);
  }

  @Override protected void onStop() {
    super.onStop();

    if (animationMan != null) animationMan.stop();
    Timber.e("Save Showed Steps on stop " + todaySteps);
    SharedPreferenceHelper.saveShowedSteps(todaySteps);
    Log.d("!!!", "save showed: " + todaySteps);
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    if (mHelper != null) mHelper.dispose();

    mSendFirebaseTokenClient.unregisterListener();
    mGetMyTrackingsClient.unregisterListener();
    mGetMyInvitesClient.unregisterListener();
    mGetMyRequestsClient.unregisterListener();
    mGetNotificationsClient.unregisterListener();
    mMarkNotificationsClient.unregisterListener();
    //vpTrackings.setAdapter(null);
  }

  @SuppressLint("NewApi") // checking version inside
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mPresenter.postEventAboutInAppPurchase(requestCode, resultCode, data);
    //if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
    //}

    switch (requestCode) {
      case Constants.CREATE_GROUP_ACTIVITY_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          if (data != null && data.hasExtra(Constants.INTENT_CREATED_TRACKING)) {
            swipedTracking = data.getParcelableExtra(Constants.INTENT_CREATED_TRACKING);
          } else {
            Log.e(TAG, "created tracking is absent");
          }

          refreshTrackings();
        }
        break;
      case Constants.OPEN_PROFILE_ACTIVITY_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          if (data != null && data.hasExtra(Constants.INTENT_IS_NEED_REFRESH_TRACKINGS)) {
            boolean isNeedRefresh =
                data.getBooleanExtra(Constants.INTENT_IS_NEED_REFRESH_TRACKINGS, false);
            String trackingId =
                data.getStringExtra(Constants.INTENT_LAST_ACCEPT_INVITE_TRACKING_ID);
            if (!TextUtils.isEmpty(trackingId) && isNeedRefresh) {
              swipedTracking = new Tracking();
              swipedTracking.setId(trackingId);

              refreshTrackings();
            }
          } else {
            Log.e(TAG, "flag is need refresh actualTrackings is absent");
          }
        }
        break;
      case Constants.GROUP_DETAILS_ACTIVITY_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          if (data != null && data.hasExtra(Constants.INTENT_IS_NEED_REFRESH_TRACKINGS)) {
            String trackingId =
                data.getStringExtra(Constants.INTENT_LAST_ACCEPT_INVITE_TRACKING_ID);
            if (!TextUtils.isEmpty(trackingId)) {
              swipedTracking = new Tracking();
              swipedTracking.setId(trackingId);
            }
            refreshTrackings();
          }
        }
        break;
      case Constants.OPEN_FAILED_ACTIVITY_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          refreshTrackings();
        }
        break;
      default:
        break;
    }
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.iv_avatar:

        //openProfileActivity();
        DialogRescueBoldMan.newInstance(myTrackings[0],DialogRescueBoldMan.MONEY).show(getFragmentManager(),"DialogRescueBoldMan");
        //DialogRescueGirl.newInstance(myTrackings[0]).show(getFragmentManager(), "DialogRescueGirl");
        //DialogRescueGirCongratulations.newInstance(myTrackings[0]).show(getFragmentManager(), "DialogRescueGirCongratulations");
        //DialogRescueBoldManKick.newInstance(myTrackings[0]).show(getFragmentManager(), "DialogRescueBoldManKick");

        break;
      case R.id.iv_create_group:
        //if (SharedPreferenceHelper.getActiveTrackingsCreated()
        //    < Config.MAX_ACTIVE_CREATED_TRACKINGS) {
        if (SharedPreferenceHelper.getActiveTrackingsCreated() < Integer.parseInt(
            SharedPreferenceHelper.getGroupsLimit())) {
          openCreateGroupActivity();
        } else {
          new SugarmanDialog.Builder(this, DialogConstants.MAX_ACTIVE_CREATED_TRACKINS_ID).content(
              String.format(getString(R.string.max_active_groups),
                  SharedPreferenceHelper.getGroupsLimit())).show();
        }
        break;
      case R.id.ll_search_container:
        openSearchGroupActivity();
        break;
      case R.id.rl_main_steps_container:
        openStatsActivity();
        break;
      default:
        Log.d(TAG,
            "Click on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }

  @Override public void onClickDialog(SugarmanDialog dialog, DialogButton button) {
    String id = dialog.getId();

    switch (id) {
      case DialogConstants.API_GET_MY_TRACKINGS_FAILURE_ID:
        dialog.dismiss();
        finish();
        break;
      default:
        super.onClickDialog(dialog, button);
        break;
    }
  }

  @Override public void closeFragment(BaseFragment fragment) {
    super.closeFragment(fragment);
    if (fragment != null && fragment instanceof NotificationsFragment) {
      notificationsFragment = null;
    }
  }

  @Override public void OnTokenRefreshFailed(FacebookException exception) {
    showNoInternetConnectionDialog();
  }

  @Override public void onApiRefreshUserDataFailure(String message) {
    showNoInternetConnectionDialog();
  }

  @Override public void onApiSendFirebaseTokenFailure(String message) {
    showNoInternetConnectionDialog();
  }

  @Override public void onApiGetMyAllUserInfoFailure(String message) {
    showNoInternetConnectionDialog();
  }

  @Override public void noAccessToken() {
    closeSugarmanProgressFragment();
    //logout();
    Log.e("Token", "unauthorized facebook");
  }

  @Override public void haveTokensAndUserData() {
    closeSugarmanProgressFragment();
    setActualDataToViews();
  }

  @Override public void connectionEnabled() {
    showSugarmanProgressFragment();
    getUserDataWithDelay(0);
  }

  @Subscribe public void onEvent(GetInAppNotificationsEvent event) {
    showProgressFragment();
    mGetMyTrackingsClient.getMyTrackings(true);
  }

  @Subscribe public void onEvent(RxBusHelper.ShowDialogRescue event) {
    Timber.e("ShowDialogRescue " + event.getTrackingId());
    for (Tracking t : myTrackings) {
      if (t.getId().equals(event.getTrackingId())) {
        for (Member member : t.getMembers()) {
          if (member.getId().equals(SharedPreferenceHelper.getUserId())) {
            if (member.getFailureStatus() != Member.FAIL_STATUS_NORMAL) {
              if (SharedPreferenceHelper.getAorB() == ABTesting.B) {
                DialogRescueBoldMan.newInstance(t, DialogRescueBoldMan.MONEY)
                    .show(getFragmentManager(), "DialogRescueBoldMan");
              } else {
                DialogRescueBoldMan.newInstance(t, DialogRescueBoldMan.INVITES)
                    .show(getFragmentManager(), "DialogRescueBoldMan");
              }
            } else {
              DialogRescueBoldManKick.newInstance(t)
                  .show(getFragmentManager(), "DialogRescueBoldManKick");
            }
          }
        }
      }
    }
  }

  @Subscribe public void onEvent(SwitchUIToNextDayEvent event) {
    todaySteps = 0;
    updateTodaySteps(todaySteps);
  }

  @Subscribe public void onEvent(StatsOpenedEvent event) {
    vCircleContainer.setVisibility(View.GONE);
    vSearchGroup.setVisibility(View.GONE);
    vpTrackings.setVisibility(View.GONE);
    //  ivLeftPagerScroll.setVisibility(View.GONE);
    //  ivRightPagerScroll.setVisibility(View.GONE);
  }

  @Subscribe public void onEvent(UpdateInvitesEvent event) {
    Timber.e("UpdateInvitesEvent");
    showProgressFragment();
    mGetMyInvitesClient.getInvites(true);
  }

  @Subscribe public void onEvent(UpdateRequestsEvent event) {
    showProgressFragment();
    mGetMyRequestsClient.getRequests(true);
  }

  @Subscribe public void onEvent(RequestsRemovedEvent event) {
    String id = event.getId();
    for (Request request : myRequests) {
      if (TextUtils.equals(request.getId(), id)) {
        myRequests.remove(request);
        break;
      }
    }

    updateEventsCount();
  }

  @Subscribe public void onEvent(InviteRemovedEvent event) {
    String id = event.getId();
    for (Invite invite : myInvites) {
      if (TextUtils.equals(invite.getId(), id)) {
        myInvites.remove(invite);
        break;
      }
    }

    updateEventsCount();
  }

  @Subscribe public void onEvent(NotificationRemovedEvent event) {
    String id = event.getNotificationId();
    for (Notification notification : myNotifications) {
      if (TextUtils.equals(notification.getId(), id)) {
        myNotifications.remove(notification);
        break;
      }
    }
  }

  @Subscribe public void onEvent(ChallengeSelectedEvent event) {
    String trackingId = event.getId();
    int position = trackingsAdapter.getTrackingPosition(trackingId);

    if (vpTrackings != null && position >= 0 && position < trackingsAdapter.getCount()) {
      vpTrackings.setCurrentItem(position, true);
    }
  }

  @Subscribe public void onEvent(RefreshTrackingsEvent event) {
    String trackingId = event.getTrackingId();
    if (!TextUtils.isEmpty(trackingId)) {
      swipedTracking = new Tracking();
      swipedTracking.setId(trackingId);
      refreshTrackings();
    }
  }

  @Subscribe public void onEvent(NeedOpenSpecificActivityEvent event) {
    int activityCode = event.getActivityCode();
    switch (activityCode) {
      case Constants.OPEN_INVITES_ACTIVITY:
        openInvitesActivity();
        break;
      case Constants.OPEN_REQUESTS_ACTIVITY:
        openRequestsActivity();
        break;
      case Constants.OPEN_MAIN_ACTIVITY:
        String trackingId = event.getTrackingId();
        if (!TextUtils.isEmpty(trackingId)) {
          swipedTracking = new Tracking();
          swipedTracking.setId(trackingId);

          refreshTrackings();
        }
        break;
      case Constants.OPEN_CONGRATULATION_ACTIVITY:
        openCongratulationActivity(event.getTrackingId());
        break;
      case Constants.OPEN_DAILY_ACTIVITY:
        openDailyActivity(event.getTrackingId());
        break;
      case Constants.OPEN_FAILED_ACTIVITY:
        openFailedActivity(event.getTrackingId());
        break;
      default:
        Log.e(TAG, "not supported activity code: " + activityCode);
        break;
    }
  }

  @Subscribe public void onEvent(DebugRealStepAddedEvent event) {
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
        "MA DebugRealStepAddedEvent " + "  stepsCalculated " + event.getStepsCalculated());

    updateTodaySteps(event.getStepsCalculated());

    tvCalculated.setText("real steps: " + event.getStepsCalculated());
    Log.d("!!!", "AddStepsEvent. added: " + " current: " + todaySteps);
    todaySteps = event.getStepsCalculated();
  }

  @Subscribe public void onEvent(DebugCacheStepsEvent event) {
    tvCache.setText("cache: " + event.getCache());
  }

  @Subscribe public void onEvent(DebugRefreshStepsEvent event) {
    tvStartValue.setText("start value: " + event.getStartValue());
    tvCalculated.setText("real steps: " + event.getRealSteps());
    tvCache.setText("cache: " + event.getCacheSteps());

    final int showedSteps = SharedPreferenceHelper.getShowedSteps();

    todaySteps = event.getStartValue() + event.getRealSteps();
    Log.d("!!!", "get from preferences total: " + todaySteps);
    Log.d("!!!", "get from preferences showed: " + showedSteps);

    if (showedSteps != todaySteps) {
      if (civMain != null) {
        civMain.post(new Runnable() {
          @Override public void run() {
            Log.e("Indicator in runnable", "" + civMain.getWidth());
            animateUpdateTodaySteps(showedSteps, todaySteps);
          }
        });
      }
    } else {
      updateTodaySteps(todaySteps);
    }
  }

  @Subscribe public void onEvent(StepServiceStartedEvent event) {
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
        "MainActivity StepServiceStartedEvent todaySteps " + todaySteps);
    App.getEventBus().post(new DebugRequestStepsEvent(todaySteps));
    App.getEventBus().post(new ReportStepsEvent());
  }

  private void setActualDataToViews() {
    myTrackings = new Tracking[actualTrackings.length];

    System.arraycopy(actualTrackings, 0, myTrackings, 0, actualTrackings.length);
    for (Tracking t : myTrackings) {
      Log.e("TRAAAAAAAAAAAAACKING", "" + t.getGroup().getName() + " " + t.getId());
    }
    Log.e("MainActivity", "huy huy huy " + myTrackings.length);

    myNotifications.clear();
    myNotifications.addAll(prepareNotifications(actualNotifications));
    myInvites.clear();
    myInvites.addAll(Arrays.asList(actualInvites));
    myRequests.clear();
    myRequests.addAll(Arrays.asList(actualRequests));

    updateEventsCount();

    updatePagerTrackings();

    int countNotifications = myNotifications.size();
    if (countNotifications > 0) {
      notificationsFragment = NotificationsFragment.newInstance(
          myNotifications.toArray(new Notification[countNotifications]), MainActivity.this);
      showFragment(notificationsFragment);
    } else if (countNotifications == 0 && notificationsFragment != null) {
      closeFragment(notificationsFragment);
    }

    showFullscreenNotifications();
  }

  public void openCreateGroupActivity() {

    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), "af_create_group", eventValue);

    Intent intent = new Intent(this, CreateGroupActivity.class);
    //Intent intent = new Intent(this, InviteForRescueActivity.class);
    startActivityForResult(intent, Constants.CREATE_GROUP_ACTIVITY_REQUEST_CODE);
  }

  public void openMentorScreenActivity() {
    Intent intent = new Intent(this, MentorListActivity.class);
    startActivity(intent);
  }

  public void openGroupDetailsActivity(String trackingId, boolean isMentorGroup,
      String groupOwnerId) {
    Intent intent = new Intent(this, GroupDetailsActivity.class);
    intent.putExtra(Constants.INTENT_TRACKING_ID, trackingId);
    intent.putExtra("isMentorGroup", isMentorGroup);
    intent.putExtra("mentorId", groupOwnerId);
    startActivityForResult(intent, Constants.GROUP_DETAILS_ACTIVITY_REQUEST_CODE);
  }

  public void openGroupDetailsActivity(String trackingId, boolean isRescueGroup) {
    Intent intent = new Intent(this, GroupDetailsActivity.class);
    intent.putExtra(Constants.INTENT_TRACKING_ID, trackingId);
    intent.putExtra("isRescueGroup", isRescueGroup);
    startActivityForResult(intent, Constants.GROUP_DETAILS_ACTIVITY_REQUEST_CODE);
  }

  public void openCongratulationActivity(String trackingId) {
    Intent intent = new Intent(this, CongratulationsActivity.class);
    intent.putExtra(Constants.INTENT_TRACKING_ID, trackingId);
    startActivity(intent);
  }

  public void openDailyActivity(String trackingId) {
    Intent intent = new Intent(this, DailyActivity.class);
    intent.putExtra(Constants.INTENT_TRACKING_ID, trackingId);
    startActivity(intent);
  }

  public void openFailedActivity(String trackingId) {
    Intent intent = new Intent(this, FailedActivity.class);
    intent.putExtra(Constants.INTENT_TRACKING_ID, trackingId);
    startActivityForResult(intent, Constants.OPEN_FAILED_ACTIVITY_REQUEST_CODE);
  }

  public void openInvitesActivity() {
    Intent intent = new Intent(this, ProfileActivity.class);
    intent.putExtra(Constants.INTENT_MY_INVITES, myInvites.toArray(new Invite[myInvites.size()]));
    intent.putExtra(Constants.INTENT_MY_REQUESTS,
        myRequests.toArray(new Request[myRequests.size()]));
    intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, Constants.OPEN_INVITES_ACTIVITY);
    startActivityForResult(intent, Constants.OPEN_PROFILE_ACTIVITY_REQUEST_CODE);
  }

  public void openRequestsActivity() {
    Intent intent = new Intent(this, ProfileActivity.class);
    intent.putExtra(Constants.INTENT_MY_INVITES, myInvites.toArray(new Invite[myInvites.size()]));
    intent.putExtra(Constants.INTENT_MY_REQUESTS,
        myRequests.toArray(new Request[myRequests.size()]));
    intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, Constants.OPEN_REQUESTS_ACTIVITY);
    startActivityForResult(intent, Constants.OPEN_PROFILE_ACTIVITY_REQUEST_CODE);
  }

  public void openLink(String url) {
    if (url != null && !url.equals("") && !url.equals(" ")) {
      Timber.e("FCM URL " + url);
      if (!url.contains("://")) {
        url = new String("http://" + url);
      }
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(url));
      startActivity(i);
    }
  }

  public void refreshTrackings() {
    showProgressFragment();
    mGetMyTrackingsClient.getMyTrackings();
  }

  private void openProfileActivity() {

    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance()
        .trackEvent(App.getInstance().getApplicationContext(), "af_go_to_menu", eventValue);

    Intent intent = new Intent(this, ProfileActivity.class);
    intent.putExtra(Constants.INTENT_MY_INVITES, myInvites.toArray(new Invite[myInvites.size()]));
    intent.putExtra(Constants.INTENT_MY_REQUESTS,
        myRequests.toArray(new Request[myRequests.size()]));
    startActivityForResult(intent, Constants.OPEN_PROFILE_ACTIVITY_REQUEST_CODE);
  }

  private void openSearchGroupActivity() {
    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), "af_group_search", eventValue);
    Intent intent = new Intent(this, SearchGroupsActivity.class);
    startActivity(intent);
  }

  private void openStatsActivity() {
    Tracking tracking = trackingsAdapter.getTracking(vpTrackings.getCurrentItem());
    if (tracking != null) {
      vOpenStats.setOnClickListener(null);

      Intent intent = new Intent(this, StatsTrackingActivity.class);
      intent.putExtra(Constants.INTENT_TRACKING, tracking);
      startActivity(intent);
      overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
    } else {
      Timber.e("tracking == null" + vpTrackings.getCurrentItem());
    }
  }

  private void getIntentData(Intent intent) {
    myTrackings = IntentExtractorHelper.getTrackings(intent);

    myNotifications.clear();
    myNotifications.addAll(prepareNotifications(IntentExtractorHelper.getNotifications(intent)));
    myInvites.clear();
    myInvites.addAll(Arrays.asList(IntentExtractorHelper.getInvites(intent)));
    myRequests.clear();
    myRequests.addAll(Arrays.asList(IntentExtractorHelper.getRequests(intent)));
  }

  private void startStepDetectorService() {
    if (!DeviceHelper.isMyServiceRunning(MasterStepDetectorService.class)) {
      Intent startIntent = new Intent(this, MasterStepDetectorService.class);
      startIntent.setAction(Constants.COMMAND_START_STEP_DETECTOR_SERVICE);
      startService(startIntent);
    }
  }

  private void updateTodaySteps(int todaySteps) {
    //civMain.updateIndicator(Config.MAX_STEPS_PER_DAY, todaySteps);
    int color = 0xffffffff;
    if (todaySteps < 5000) {
      color = 0xffE10F0F;
    } else if (todaySteps > 5000 && todaySteps < 7500) {
      color = 0xffEB6117;
    } else if (todaySteps > 7500 && todaySteps < 10000) {
      color = 0xffF6B147;
    } else {
      color = 0xff54CC14;
    }
    angle = (float) todaySteps / 10000f * 360f;

    //bmp =new SoftReference<Bitmap>( BitmapFactory.decodeResource(getResources(), R.drawable.red_circle));

    ViewTreeObserver vto = ivColoredStrip.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        ivColoredStrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        int width = ivColoredStrip.getMeasuredWidth();
        int height = ivColoredStrip.getMeasuredHeight();

        if (width > 0 && height > 0) {

          if (bmp != null) {
            bmp.recycle();
            bmp = null;
          }
          bmp = BitmapFactory.decodeResource(getResources(), R.drawable.red_circle);
          ivColoredStrip.setImageBitmap(
              Bitmap.createScaledBitmap(img.transform(bmp, angle), width, height, false));
        }
      }
    });

    Log.e("Indicator", "" + civMain.getWidth());
    //indicator.update(Config.MAX_STEPS_PER_DAY, todaySteps);
    String counter = todaySteps < 1000 ? String.valueOf(todaySteps)
        : String.format(Locale.US, "%,d", todaySteps); // add divider ',' between thousands
    tvCounter.setText(counter);
    tvCounter.setTextColor(color);
    color = color - 0xaa000000;
    tvCounter.setShadowLayer(12, 0, 10, color);
    updateAnimations(todaySteps);
    mWalkDataViewPagerAdapter.setWalkData(String.valueOf(todaySteps));
    mWalkDataViewPagerAdapter.notifyDataSetChanged();
    Timber.e("updateTodaySteps "
        + !SharedPreferenceHelper.isRulesBlocked()
        + " l "
        + myTrackings.length);

    if (!SharedPreferenceHelper.isRulesBlocked()) {
      mPresenter.checkIfRuleStepsDone(todaySteps, myTrackings.length);
      mPresenter.checkIfRule15KStepsDone(todaySteps);
    }
  }

  private List<BaseChallengeItem> convertTrackingsToItems() {
    List<BaseChallengeItem> items = new ArrayList<>(myTrackings.length);

    if (mMentorsGroups != null) {
      for (Tracking mentorsGroup : mMentorsGroups) {
        ChallengeMentorItem item = new ChallengeMentorItem();
        item.setTracking(mentorsGroup);
        items.add(item);
      }
    }

    for (Tracking tracking : myTrackings) {
      if (!SharedPreferenceHelper.getTrackingSeenDailySugarman(tracking.getId())
          && tracking.hasDailyWinner()) {
        Intent intent = new Intent(this, DailyActivity.class);
        intent.putExtra(Constants.INTENT_TRACKING_ID, tracking.getId());
        startActivity(intent);
        SharedPreferenceHelper.saveTrackingSeenDailySugarman(tracking.getId(), true);
      }
      String status = tracking.getStatus();
      if (!TextUtils.equals(status, Constants.STATUS_FAILED) && !TextUtils.equals(status,
          Constants.STATUS_COMPLETED)) {
        Date startedDate = tracking.getStartUTCDate();

        if (tracking.getFailGroupStatus() != Tracking.STATUS_FAIL) {
          if (System.currentTimeMillis() < startedDate.getTime()) {
            ChallengeWillStartItem item = new ChallengeWillStartItem();
            item.setTracking(tracking);
            items.add(item);
          } else {
            // TODO: 12/8/17 check for failing group
            ChallengeItem item = new ChallengeItem();
            item.setTracking(tracking);
            item.setUnreadMessages(5); // TODO: 09.08.2017 ТУТ
            items.add(item);
          }
        } else {

          boolean isFailedStatusAdapter = false;

          for(int i = 0; i<tracking.getMembers().length; i++)
          {
            if(tracking.getMembers()[i].getFailureStatus() == Member.FAIL_STATUS_FAILUER) {
              isFailedStatusAdapter = true;
              break;
            }
          }

          // TODO: 06.12.2017 make check if tracking soon fail (need to be added new bool field on server)
          //if (tracking.getFailGroupStatus() == Tracking.STATUS_FAIL) {
          if (isFailedStatusAdapter) {
            ChallengeRescueItem item = new ChallengeRescueItem();
            item.setTracking(tracking);
            items.add(item);
            if (firstFailedGroupPosition == -1) {
              if (mMentorsGroups != null && mMentorsGroups.size() > 0) {
                Timber.e("Mentors Groups != 0 " + mMentorsGroups.size());
                firstFailedGroupPosition = items.size()
                    - 1;    // setting the first position of the failed groups so that we are able to scroll to them
              } else {
                Timber.e("Mentors Groups == 0");
                firstFailedGroupPosition =
                    items.size(); // didn't use (size-1) because we have a mentor group commercial first
              }
              ivLifebuoy.setVisibility(View.VISIBLE);
            }
          }
        }
      }
    }

    return items;
  }

  private void updateEventsCount() {
    int countEvents = myInvites.size() + myRequests.size();
    if (countEvents == 0) {
      tvEvents.setVisibility(View.GONE);
    } else {
      tvEvents.setText(String.valueOf(countEvents));
      tvEvents.setVisibility(View.VISIBLE);
    }
  }

  private List<BaseChallengeItem> prepareTrackingItems() {
    List<BaseChallengeItem> convertedItems = convertTrackingsToItems();
    if (convertedItems.isEmpty()) {
      blinkAndRotate();
      convertedItems.add(new NoChallengeItem());
    }

    return convertedItems;
  }

  private void blinkAndRotate() {
    RotateAnimation rotate =
        new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f);
    rotate.setDuration(800);
    rotate.setInterpolator(new LinearInterpolator());
    rotate.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {

      }

      @Override public void onAnimationEnd(Animation animation) {
        //tvBlinkTitle.setBackgroundResource(android.R.color.transparent);
        RotateAnimation rotate = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(800);
        rotate.setInterpolator(new LinearInterpolator());
        // vCreateGroup.startAnimation(rotate);
        rotate.setAnimationListener(new Animation.AnimationListener() {
          @Override public void onAnimationStart(Animation animation) {

          }

          @Override public void onAnimationEnd(Animation animation) {
            Handler handler = App.getHandlerInstance();
            tvBlinkTitle.setBackgroundResource(R.color.red);
            handler.postDelayed(new Runnable() {
              @Override public void run() {
                tvBlinkTitle.setBackgroundResource(android.R.color.transparent);
                if (convertTrackingsToItems().isEmpty()) {
                  blinkAndRotate();
                }
              }
            }, 800);
          }

          @Override public void onAnimationRepeat(Animation animation) {

          }
        });
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });

    // vCreateGroup.startAnimation(rotate);
  }

  private void setupInAppPurchase() {
    mHelper = new IabHelper(this, Config.BASE_64_ENCODED_PUBLIC_KEY);

    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
      public void onIabSetupFinished(IabResult result) {
        if (!result.isSuccess()) {
          Timber.e("In-app Billing setup failed: " + result);
        } else {

          //consumeItem();
          startPurchaseFlow("v1.group_rescue");
          Timber.e("In-app Billing is set up OK");
        }
      }
    });
    mHelper.enableDebugLogging(true);
  }

  public void startPurchaseFlow(String freeSku) {
    mFreeSku = freeSku;
    Timber.e("mFreeSku startPurchaseFlow " + mFreeSku);

    mHelper.launchPurchaseFlow(this, freeSku, 10001, (result, purchase) -> {
      Timber.e("Ochko huy pizda");
      Timber.e("mFreeSku mPurchaseFinishedListener " + mFreeSku);

      if (result.isFailure()) {
        Timber.e("Result is failure");
        // Handle error
        return;
      } else if (purchase.getSku().equals(mFreeSku)) {
        Timber.e("Id is correct");
        consumeItem();
        Timber.e(mHelper.getMDataSignature());
      } else {
        Timber.e(result.getMessage());
      }
    }, "mypurchasetoken");
  }

  public void consumeItem() {
    Timber.e("Item consumed");
    mHelper.queryInventoryAsync(true, Arrays.asList(mFreeSku), mReceivedInventoryListener);
  }

  private List<Notification> prepareNotifications(Notification[] notifications) {
    List<Notification> preparedNotifications = new ArrayList<>(notifications.length);
    for (Notification notification : notifications) {
      String notificationId = notification.getId();
      int type =
          StringHelper.getNotificationMessageType(notificationsFlags, notification.getText());

      String trackingId = notification.getTrackingId();
      switch (type) {
        case NotificationMessageType.GROUP_NAME_HAS_FAILED:
          failedTrackingsId.put(notificationId, trackingId);
          mMarkNotificationsClient.mark(notificationId);
          break;
        case NotificationMessageType.CONGRATS:
          congratulationTrackingsId.put(notificationId, trackingId);
          mMarkNotificationsClient.mark(notificationId);
          break;
        case NotificationMessageType.DAILY_SUGARMAN:
          dailyTrackingsId.put(notificationId, trackingId);
          mMarkNotificationsClient.mark(notificationId);
          break;
        default:
          if (type != NotificationMessageType.PINGED_YOU_TO_MYB
              && type != NotificationMessageType.USER_NAME_HAS_POKED_YOU
              && type != NotificationMessageType.USER_NAME_REQUESTED
              && type != NotificationMessageType.USER_NAME_INVITED) {
            preparedNotifications.add(notification);
          } else {
            mMarkNotificationsClient.mark(notificationId);
          }
          break;
      }
    }
    return preparedNotifications;
  }

  private void animateUpdateTodaySteps(int showedSteps, final int totalSteps) {
    int dif = totalSteps - showedSteps;
    final int iterations =
        (dif < Config.ANIMATION_COUNT_ITERATIONS) ? dif : Config.ANIMATION_COUNT_ITERATIONS;

    if (iterations > 0) {
      animationTodaySteps = showedSteps;
      updateTodaySteps(showedSteps);

      final int period = dif / iterations;
      final Handler handler = App.getHandlerInstance();

      animationIndex = 0;

      isAnimationRunned = true;
      handler.postDelayed(new Runnable() {
        @Override public void run() {
          if (animationIndex < iterations) {
            animationTodaySteps += period;
            animationTodaySteps =
                animationTodaySteps > totalSteps ? totalSteps : animationTodaySteps;
            updateTodaySteps(animationTodaySteps);
            handler.postDelayed(this, Config.STATS_ANIMATION_STEPS_ITERATION_DELAY);
          } else {
            animationTodaySteps = todaySteps;
            updateTodaySteps(todaySteps);
            Timber.e("Save Showed Steps " + todaySteps);
            SharedPreferenceHelper.saveShowedSteps(todaySteps);
            isAnimationRunned = false;
          }
          animationIndex++;
        }
      }, Config.STATS_ANIMATION_STEPS_ITERATION_DELAY);
    } else {
      updateTodaySteps(totalSteps);
    }
  }

  private void showFullscreenNotifications() {
    Set<Map.Entry<String, String>> set;
    Map.Entry<String, String> entry;
    String notificationId;

    if (!failedTrackingsId.isEmpty()) {
      set = failedTrackingsId.entrySet();
      entry = set.iterator().next();
      notificationId = entry.getKey();

      mMarkNotificationsClient.mark(notificationId);
      openFailedActivity(entry.getValue());

      failedTrackingsId.remove(notificationId);
    } else if (!dailyTrackingsId.isEmpty()) {
      set = dailyTrackingsId.entrySet();
      entry = set.iterator().next();
      notificationId = entry.getKey();

      mMarkNotificationsClient.mark(notificationId);
      openDailyActivity(entry.getValue());

      dailyTrackingsId.remove(notificationId);
    } else if (!congratulationTrackingsId.isEmpty()) {
      set = congratulationTrackingsId.entrySet();
      entry = set.iterator().next();
      notificationId = entry.getKey();

      mMarkNotificationsClient.mark(notificationId);
      openCongratulationActivity(entry.getValue());

      congratulationTrackingsId.remove(notificationId);
    }
  }

  private void updatePagerTrackings() {
    List<BaseChallengeItem> converted = prepareTrackingItems();
    if (converted.size() == 1) {
      vpTrackings.setPadding(0, 0, 0, 0);
    } else {
      //vpTrackings.setPadding(vpTrackingPadding, 0, 0, 0);
    }
    // TODO: 10/27/17 Random position for noMentorsChallenge
    if (mMentorsGroups == null || mMentorsGroups.size() <= 0) {
      converted.add(0, new NoMentorsChallengeItem());
    }
    //converted.add(new ChallengeRescueItem());
    trackingsAdapter.setItems(converted);
    spiChallenges.setMaxIndicatorCircles(5);
    spiChallenges.setViewPager(vpTrackings);
    spiChallenges.requestLayout();
    spiChallenges.invalidate();
  }

  @Override public void setAnimation(List<Drawable> drawable, int duration) {
    //Timber.e("Set animation" + drawable.size());
    AnimationDrawable animation = new AnimationDrawable();
    for (Drawable d : drawable) {
      if (d != null) {
        animation.addFrame(d, duration);
      }
    }

    runOnUiThread(() -> {
      ivAnimatedMan.setBackgroundDrawable(null);
      if (ivAnimatedMan.getAnimation() != null) {
        ivAnimatedMan.getAnimation().cancel();
      }
      //Timber.e("" + animation.getNumberOfFrames());
      if (App.isAppForeground()) {
        ivAnimatedMan.setImageDrawable(animation);
        animation.start();
      }
      // Picasso.with(this).load("1").placeholder(animation).error(animation).into(ivAnimatedMan);
    });
  }
  //@Override public void setAnimation(AnimationDrawable animation) {
  //
  //  runOnUiThread(() -> {
  //    ivAnimatedMan.setBackgroundDrawable(null);
  //    Timber.e("" + animation.getNumberOfFrames());
  //    if (animation.getNumberOfFrames()!=0) {
  //      ivAnimatedMan.setImageDrawable(animation);
  //      animation.start();
  //    }
  //    // Picasso.with(this).load("1").placeholder(animation).error(animation).into(ivAnimatedMan);
  //  });
  //}

  @Override public void doEventActionResponse(CustomUserEvent customUserEvent) {
    Timber.e("EVENT_NAME_ANIMATION "
        + customUserEvent.getNameOfAnim()
        + ", EVENT_NAME_TYPE "
        + customUserEvent.getStrType());
    if (customUserEvent.getEventName().equals(Constants.EVENT_X_STEPS_DONE)
        && customUserEvent.getStrType().equals(POPUP_ACTION)) {
      doEventAction(customUserEvent, null);
    }
    if (customUserEvent.getEventName().equals(Constants.EVENT_X_STEPS_DONE)
        && customUserEvent.getStrType().equals(ANIMATION_ACTION)) {
      doEventAction(customUserEvent, () -> {
        mPresenter.getAnimationByName(customUserEvent.getNameOfAnim(),
            getFilesDir().getAbsolutePath());
      });
    }
    if (customUserEvent.getEventName().equals(Constants.EVENT_PLAY_ANIMATION)) {
      doEventAction(customUserEvent, () -> {
        mPresenter.getAnimationByName(customUserEvent.getNameOfAnim(),
            getFilesDir().getAbsolutePath());
      });
    }
  }
}
