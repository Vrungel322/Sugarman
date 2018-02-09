package com.sugarman.myb.ui.activities.createGroup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.clover_studio.spikachatmodule.utils.Const;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.widget.GameRequestDialog;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.adapters.FriendsAdapter;
import com.sugarman.myb.api.clients.CheckPhonesClient;
import com.sugarman.myb.api.clients.CheckVkClient;
import com.sugarman.myb.api.clients.CreateGroupClient;
import com.sugarman.myb.api.clients.FBApiClient;
import com.sugarman.myb.api.clients.JoinGroupClient;
import com.sugarman.myb.api.models.responses.Phones;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.api.models.responses.me.groups.CreatedGroup;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.eventbus.events.ReportStepsEvent;
import com.sugarman.myb.listeners.ApiCheckPhoneListener;
import com.sugarman.myb.listeners.ApiCheckVkListener;
import com.sugarman.myb.listeners.ApiCreateGroupListener;
import com.sugarman.myb.listeners.ApiJoinGroupListener;
import com.sugarman.myb.listeners.AsyncSaveBitmapToFileListener;
import com.sugarman.myb.listeners.OnFBGetFriendsListener;
import com.sugarman.myb.tasks.SaveBitmapToFileAsyncTask;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.activities.editProfile.EditProfileActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.dialogs.sendVkInvitation.SendVkInvitationDialog;
import com.sugarman.myb.ui.views.MaskImage;
import com.sugarman.myb.utils.AnalyticsHelper;
import com.sugarman.myb.utils.BitmapUtils;
import com.sugarman.myb.utils.ContactsHelper;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

import static com.sugarman.myb.utils.ImageHelper.scaleCenterCrop;

public class CreateGroupActivity extends BaseActivity
    implements View.OnClickListener, OnFBGetFriendsListener, ApiCreateGroupListener,
    ApiJoinGroupListener, AsyncSaveBitmapToFileListener, View.OnFocusChangeListener,
    ICreateGroupActivityView, ApiCheckPhoneListener, ApiCheckVkListener {

  private static final String TAG = CreateGroupActivity.class.getName();
  private final List<FacebookFriend> filtered = new ArrayList<>();
  private final List<FacebookFriend> allFriends = new ArrayList<>();
  private final List<FacebookFriend> invitable = new ArrayList<>();
  private final List<FacebookFriend> members = new ArrayList<>();
  public CheckPhonesClient mCheckPhoneClient;
  public CheckVkClient mCheckVkClient;
  @BindView(R.id.fb_filter) ImageView fbFilter;
  @BindView(R.id.vk_filter) ImageView vkFilter;
  @BindView(R.id.ph_filter) ImageView phFilter;
  @BindView(R.id.pb_spinner) RelativeLayout pb;
  @BindView(R.id.tvInAppFbCount) TextView tvInAppFbCount;
  @BindView(R.id.tvTotalFbCount) TextView tvTotalFbCount;
  @BindView(R.id.tvInAppVkCount) TextView tvInAppVkCount;
  @BindView(R.id.tvTotalVkCount) TextView tvTotalVkCount;
  @BindView(R.id.tvInAppPhCount) TextView tvInAppPhCount;
  @BindView(R.id.tvTotalPhCount) TextView tvTotalPhCount;
  String currentFilter = "";
  boolean isVkLoggedIn = false, isFbLoggedIn = false;
  MaskImage mi;
  @InjectPresenter CreateGroupActivityPresenter mPresenter;
  View vApply;
  private FriendsAdapter friendsAdapter;
  private RecyclerView rcvFriends;
  private ImageView ivGroupAvatar;
  private EditText etGroupName;
  private EditText etMemberFilter;
  private View vAddPhotoContainer;
  private View vNoFriends;
  private View vClearGroupName;
  private final TextWatcher groupNameWatcher = new TextWatcher() {
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // nothing
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      // nothing
    }

    @Override public void afterTextChanged(Editable s) {
      boolean isEmpty = TextUtils.isEmpty(s);
      vClearGroupName.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
  };
  private View vClearMembersFilter;
  private final TextWatcher filterWatcher = new TextWatcher() {
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // nothing
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      // nothing
    }

    @Override public void afterTextChanged(Editable s) {
      boolean isEmpty = TextUtils.isEmpty(s);
      vClearMembersFilter.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
      if (!isEmpty) {
        filterFacebookFriends(s.toString());
      } else {
        filtered.clear();
        filtered.addAll(allFriends);
        setFriends(filtered);
      }
    }
  };
  private FBApiClient fbApiClient;
  private final FacebookCallback<GameRequestDialog.Result> fbInviteCallback =
      new FacebookCallback<GameRequestDialog.Result>() {

        @Override public void onSuccess(GameRequestDialog.Result result) {
          showProgressFragmentTemp();

          List<String> recipients = result.getRequestRecipients();
          fbApiClient.getFriendsInfo(recipients);
        }

        @Override public void onCancel() {
          // nothing
        }

        @Override public void onError(FacebookException error) {
          if (DeviceHelper.isNetworkConnected()) {
            new SugarmanDialog.Builder(CreateGroupActivity.this,
                DialogConstants.FAILURE_INVITE_FB_FRIENDS_ID).content(error.getMessage()).show();
          } else {
            showNoInternetConnectionDialog();
          }
        }
      };
  private CreateGroupClient mCreateGroupClient;
  private JoinGroupClient mJoinGroupClient;
  private CallbackManager fbCallbackManager;
  private GameRequestDialog fbInviteDialog;
  private boolean isFriendsFound = false;
  private File selectedFile;
  private int networksToLoad = 0, networksLoaded = 0;
  private boolean vkPeopleAdd;
  private List<Phones> mDistinktorList = new ArrayList<>();

  // number of total count/ number of count people with app BY PH
  private int numberOfMemberTotalAppPh;
  private int numberOfMemberWithAppPh;
  // number of total count/ number of count people with app BY FB
  private int numberOfMemberTotalAppFb;
  private int numberOfMemberWithAppFb;
  // number of total count/ number of count people with app BY VK
  private int numberOfMemberTotalAppVk;
  private int numberOfMemberWithAppVk;

  @Override protected void onDestroy() {
    super.onDestroy();
    //if(mCheckPhoneClient.isRequestRunning())
    mCheckPhoneClient.cancelRequest();
    //if(mCheckVkClient.isRequestRunning())
    mCheckVkClient.cancelRequest();
  }

  @Override protected void onCreate(Bundle savedStateInstance) {
    setContentView(R.layout.activity_create_group);
    super.onCreate(savedStateInstance);
    pb.setVisibility(View.GONE);
    pb.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

      }
    });
    Timber.e("VK TOKEN " + SharedPreferenceHelper.getVkToken());

    if (!SharedPreferenceHelper.getFacebookId().equals("none")) {
      networksToLoad++;
      isFbLoggedIn = true;
      fbFilter.setAlpha(1.0f);
    }
    if (VKSdk.isLoggedIn()) {
      networksToLoad++;
      isVkLoggedIn = true;
      vkFilter.setAlpha(1.0f);
    }

    //getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background));

    //        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
    //        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
    //        Log.e("Contact", ""+phones.getCount());
    //        while (phones.moveToNext())
    //        {
    //            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
    //            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    ////
    //            Log.e("Contact", name + "-" + phoneNumber);
    ////
    //        }
    //        phones.close();

    fbApiClient = new FBApiClient();
    mCreateGroupClient = new CreateGroupClient();
    mJoinGroupClient = new JoinGroupClient();
    mCheckPhoneClient = new CheckPhonesClient();
    mCheckVkClient = new CheckVkClient();

    friendsAdapter = new FriendsAdapter(this);

    fbCallbackManager = CallbackManager.Factory.create();
    fbInviteDialog = new GameRequestDialog(this);
    fbInviteDialog.registerCallback(fbCallbackManager, fbInviteCallback);

    vAddPhotoContainer = findViewById(R.id.ll_add_photo_container);
    vNoFriends = findViewById(R.id.tv_no_friends);
    etGroupName = (EditText) findViewById(R.id.et_group_name);
    etMemberFilter = (EditText) findViewById(R.id.et_search);
    ivGroupAvatar = (ImageView) findViewById(R.id.iv_group_avatar);
    rcvFriends = (RecyclerView) findViewById(R.id.rcv_friends);
    vClearGroupName = findViewById(R.id.iv_clear_group_name_input);
    vClearMembersFilter = findViewById(R.id.iv_clear_member_filter_input);
    View vCross = findViewById(R.id.iv_cross);
    vApply = findViewById(R.id.iv_apply);

    rcvFriends.setLayoutManager(new LinearLayoutManager(this));
    rcvFriends.setAdapter(friendsAdapter);

    etMemberFilter.addTextChangedListener(filterWatcher);
    etGroupName.addTextChangedListener(groupNameWatcher);

    String userName = SharedPreferenceHelper.getUserName();
    String groupName = String.format(getString(R.string.group_name_template), userName);
    etGroupName.setText(groupName);

    vApply.setOnClickListener(this);
    vCross.setOnClickListener(this);
    vClearGroupName.setOnClickListener(this);
    vClearMembersFilter.setOnClickListener(this);
    vAddPhotoContainer.setOnClickListener(this);

    etMemberFilter.setOnFocusChangeListener(this);
    etGroupName.setOnFocusChangeListener(this);

    Bitmap bitmap = ((BitmapDrawable) ivGroupAvatar.getDrawable()).getBitmap();
    mi = new MaskImage(this, R.drawable.group_avatar, false, 0xff000000);
    bitmap = mi.transform(bitmap);
    ivGroupAvatar.setImageBitmap(bitmap);

    if (checkCallingOrSelfPermission(Constants.READ_PHONE_CONTACTS_PERMISSION)
        == PackageManager.PERMISSION_GRANTED) {
      networksToLoad++;

      //load friends from DB
      mPresenter.fillListByCachedData();

      AsyncTask.execute(() -> {
        HashMap<String, String> contactList =
            ContactsHelper.getContactList(CreateGroupActivity.this);
        List<String> phonesToCheck = new ArrayList<String>();
        for (String key : contactList.keySet()) {
          FacebookFriend friend = new FacebookFriend(contactList.get(key).replace(" ", ""), key,
              "https://sugarman-myb.s3.amazonaws.com/Group_New.png", FacebookFriend.CODE_INVITABLE,
              "ph");
          allFriends.add(friend);
          phonesToCheck.add(contactList.get(key).replace(" ", ""));
          //  Timber.e(contactList.get(key));
        }
        numberOfMemberTotalAppPh = contactList.size();
        runOnUiThread(() -> tvTotalPhCount.setText(String.valueOf(numberOfMemberTotalAppPh)));
        Timber.e("numberOfMemberTotalAppPh size " + numberOfMemberTotalAppPh);

        mCheckPhoneClient.checkPhones(phonesToCheck);

        Timber.e("Contacts loaded");
        //runOnUiThread(() -> setFriends(allFriends));
      });
    } else {
      phFilter.setAlpha(0.5f);
    }

    final View contentView = findViewById(android.R.id.content);
    contentView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          private int mPreviousHeight;

          @Override public void onGlobalLayout() {
            int newHeight = contentView.getHeight();
            if (mPreviousHeight != 0) {
              if (mPreviousHeight > newHeight) {
                // Height decreased: keyboard was shown
                if (etMemberFilter.isFocused()) {
                  //vAddPhotoContainer.setVisibility(View.GONE);
                } else {
                  vAddPhotoContainer.setVisibility(View.VISIBLE);
                }
              } else if (mPreviousHeight < newHeight) {
                // Height increased: keyboard was hidden
                vAddPhotoContainer.setVisibility(View.VISIBLE);
              }
            }
            mPreviousHeight = newHeight;
          }
        });

    VKRequest request = new VKRequest("friends.get",
        VKParameters.from(VKApiConst.FIELDS, "photo_100", "order", "name"));
    request.executeWithListener(new VKRequest.VKRequestListener() {
      @Override public void onComplete(VKResponse response) {
        super.onComplete(response);
        JSONObject resp = response.json;
        Log.e("VK response", response.responseString);
        List<String> vkToCheck = new ArrayList<String>();
        try {
          JSONArray items = resp.getJSONObject("response").getJSONArray("items");
          for (int i = 0; i < items.length(); i++) {

            JSONObject item = items.getJSONObject(i);
            int id = item.getInt("id");
            String firstName = item.getString("first_name");
            String lastName = item.getString("last_name");
            String photoUrl = item.getString("photo_100");
            int online = item.getInt("online");

            FacebookFriend friend =
                new FacebookFriend(Integer.toString(id), firstName + " " + lastName, photoUrl,
                    FacebookFriend.CODE_INVITABLE, "vk");

            allFriends.add(friend);
            vkToCheck.add(friend.getId());
          }

          mCheckVkClient.checkVks(vkToCheck);

          Timber.e("VK LOADED");
          //setFriends(allFriends);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override public void onError(VKError error) {
        super.onError(error);
        //Log.e("VK", error.errorMessage);
      }
    });

    AnalyticsHelper.reportInvite();
  }

  @OnClick(R.id.fb_filter) public void showFbFriends() {
    if (isFbLoggedIn) {
      if (!currentFilter.equals("fb")) {
        filtered.clear();

        for (FacebookFriend friend : allFriends) {
          if (friend.getSocialNetwork().equals("fb")) {
            filtered.add(friend);
          }
        }

        setFriends(filtered);
        currentFilter = "fb";
        phFilter.setAlpha(0.5f);
        vkFilter.setAlpha(0.5f);
        fbFilter.setAlpha(1.0f);
      } else {
        setFriends(allFriends);
        currentFilter = "";
        phFilter.setAlpha(1.0f);
        if (isVkLoggedIn) vkFilter.setAlpha(1.0f);
      }
    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
      builder.setMessage(getResources().getString(R.string.log_in_to_fb))
          .setTitle(getResources().getString(R.string.not_logged_in));
      builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          Intent intent = new Intent(CreateGroupActivity.this, EditProfileActivity.class);

          startActivity(intent);
        }
      });
      builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          // User cancelled the dialog
        }
      });

      // Create the AlertDialog
      AlertDialog dialog = builder.create();
      dialog.show();
    }
  }

  @OnClick(R.id.vk_filter) public void showVkFriends() {
    if (isVkLoggedIn) {
      if (!currentFilter.equals("vk")) {
        filtered.clear();

        for (FacebookFriend friend : allFriends) {
          if (friend.getSocialNetwork().equals("vk")) {
            filtered.add(friend);
          }
        }

        setFriends(filtered);
        currentFilter = "vk";
        fbFilter.setAlpha(0.5f);
        phFilter.setAlpha(0.5f);
        vkFilter.setAlpha(1.0f);
      } else {
        setFriends(allFriends);
        currentFilter = "";
        if (isFbLoggedIn) fbFilter.setAlpha(1.0f);
        phFilter.setAlpha(1.0f);
      }
    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
      builder.setMessage(getResources().getString(R.string.log_in_to_vk))
          .setTitle(getResources().getString(R.string.not_logged_in));
      builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          Intent intent = new Intent(CreateGroupActivity.this, EditProfileActivity.class);
          startActivity(intent);
        }
      });
      builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          // User cancelled the dialog
        }
      });

      // Create the AlertDialog
      AlertDialog dialog = builder.create();
      dialog.show();
    }
  }

  @OnClick(R.id.ph_filter) public void showPhFriends() {
    if (!currentFilter.equals("ph")) {
      filtered.clear();

      for (FacebookFriend friend : allFriends) {
        if (friend.getSocialNetwork().equals("ph")) {
          filtered.add(friend);
        }
      }

      setFriends(filtered);
      if (checkCallingOrSelfPermission(Constants.READ_PHONE_CONTACTS_PERMISSION)
          != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_CONTACTS },
            Const.PermissionCode.READ_CONTACTS);
      }
      currentFilter = "ph";
      fbFilter.setAlpha(0.5f);
      vkFilter.setAlpha(0.5f);
      phFilter.setAlpha(1.0f);
    } else {
      setFriends(allFriends);
      currentFilter = "";
      if (isFbLoggedIn) fbFilter.setAlpha(1.0f);
      if (isVkLoggedIn) vkFilter.setAlpha(1.0f);
    }
  }

  private void checkNetworksLoaded() {
    Timber.e(
        networksLoaded + " out of " + networksToLoad + "allFriends side is " + allFriends.size());
    if (networksLoaded == networksToLoad) {
      Timber.e(
          networksLoaded + " out of " + networksToLoad + "allFriends side is " + allFriends.size());
      //closeProgressFragment();
      hideProgress();

    }
  }

  @Override protected void onStart() {
    super.onStart();

    fbApiClient.registerListener(this);
    mCreateGroupClient.registerListener(this);
    mJoinGroupClient.registerListener(this);
    mCheckPhoneClient.registerListener(this);
    mCheckVkClient.registerListener(this);
  }

  @Override protected void onStop() {
    super.onStop();

    fbApiClient.unregisterListener();
    mCreateGroupClient.unregisterListener();
    mJoinGroupClient.unregisterListener();
    mCheckPhoneClient.unregisterListener();
    mCheckVkClient.unregisterListener();
    mi = null;
    mCheckVkClient.cancelRequest();
    mCheckPhoneClient.cancelRequest();
  }

  @Override protected void onResume() {
    super.onResume();
    vApply.setEnabled(true);
    if (!isFriendsFound) {
      getFacebookFriends();
    }
  }

  @SuppressLint("NewApi") // checking version inside
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    fbCallbackManager.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case Constants.INTENT_CHOOSER_IMAGE_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          if (data == null || (data.getData() == null && data.getClipData() == null)) {
            // return data from camera
            processCaptureCamera();
          } else {
            // return data from gallery
            processCameraGallery(data);
          }
        } else {
          Log.d(TAG, "chooser image result: " + requestCode);
        }

        SharedPreferenceHelper.saveCaptureCameraUri("");
        SharedPreferenceHelper.saveCaptureCameraPath("");
        break;
      default:
        break;
    }
  }

  @Override
  public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
      @NonNull final int[] grantResults) {
    boolean userAllowed = true;
    switch (requestCode) {
      case Constants.REQUEST_EXTERNAL_STORAGE_PERMISSION_CODE:
        for (final int result : grantResults) {
          userAllowed &= (result == PackageManager.PERMISSION_GRANTED);
        }

        if (userAllowed) {
          chooseGroupAvatar();
        } else {
          showNeedExternalStoragePermission();
        }
        break;
      default:
        Log.d(TAG, "not supported request code: " + requestCode);
        break;
    }
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.iv_cross:
        Timber.e("iv_cross");
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(AFInAppEventParameterName.LEVEL, 9);
        eventValue.put(AFInAppEventParameterName.SCORE, 100);
        AppsFlyerLib.getInstance()
            .trackEvent(getApplicationContext(), "af_cancel_group_creation", eventValue);
        setResult(RESULT_CANCELED);
        hideProgress();
        //closeProgressFragment();
        finish();
        break;
      case R.id.iv_apply:
        DeviceHelper.hideKeyboard(this);
        Map<String, Object> eventValues = new HashMap<>();
        eventValues.put(AFInAppEventParameterName.LEVEL, 9);
        eventValues.put(AFInAppEventParameterName.SCORE, 100);
        AppsFlyerLib.getInstance()
            .trackEvent(getApplicationContext(), "af_create_group_inside", eventValues);
        checkFilledData();
        break;
      case R.id.ll_add_photo_container:
        tryChooseGroupAvatar();
        break;
      case R.id.iv_clear_group_name_input:
        etGroupName.setText("");
        break;
      case R.id.iv_clear_member_filter_input:
        etMemberFilter.setText("");
        break;
      default:
        Log.d(TAG,
            "Click on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }

  @Override public void onFocusChange(View v, boolean hasFocus) {
    int id = v.getId();
    switch (id) {
      case R.id.et_group_name:
        if (hasFocus) {
          vAddPhotoContainer.setVisibility(View.VISIBLE);
        }
        break;
      case R.id.et_search:
        //vAddPhotoContainer.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
        break;
      default:
        Log.d(TAG,
            "focus on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }

  @Override public void onClickDialog(SugarmanDialog dialog, DialogButton button) {
    String id = dialog.getId();

    switch (id) {
      case DialogConstants.API_CREATE_GROUP_FAILURE_ID:
      case DialogConstants.FAILURE_CONVERT_FB_FRIENDS_ID:
      case DialogConstants.FAILURE_INVITE_FB_FRIENDS_ID:
        dialog.dismiss();
        setResult(RESULT_CANCELED);
        finish();
        break;
      default:
        super.onClickDialog(dialog, button);
        break;
    }
  }

  @Override public void onGetFacebookFriendsSuccess(List<FacebookFriend> friends,
      List<FacebookFriend> invitable) {
    numberOfMemberWithAppFb = friends.size();
    numberOfMemberTotalAppFb = invitable.size();
    tvInAppFbCount.setText(String.valueOf(numberOfMemberWithAppFb));
    tvTotalFbCount.setText(String.valueOf(numberOfMemberTotalAppFb));
    List<FacebookFriend> temp = new ArrayList<>();
    temp.addAll(friends);
    temp.addAll(invitable);
    Timber.e("numberOfMemberWithAppFb size " + numberOfMemberWithAppFb);
    Timber.e("numberOfMemberTotalAppFb size " + numberOfMemberTotalAppFb);

    isFriendsFound = true;

    int size = friends.size();
    for (int i = 0; i < size; i++) {
      friends.get(i).setIsInvitable(FacebookFriend.CODE_NOT_INVITABLE);
    }

    size = invitable.size();

    for (int i = 0; i < size; i++) {
      invitable.get(i).setIsInvitable(FacebookFriend.CODE_INVITABLE);
    }

    this.invitable.clear();
    this.invitable.addAll(invitable);

    for (FacebookFriend friend : friends)
      if (friend.getSocialNetwork() == null) friend.setSocialNetwork("fb");
    for (FacebookFriend friend : invitable)
      if (friend.getSocialNetwork() == null) friend.setSocialNetwork("fb");

    List<FacebookFriend> facebookFriendList = new ArrayList<>();
    facebookFriendList.addAll(friends);
    facebookFriendList.addAll(invitable);
    mPresenter.cacheFriends(facebookFriendList);
    facebookFriendList.clear();
    //allFriends.clear();
    allFriends.addAll(friends);
    allFriends.addAll(invitable);

    etMemberFilter.setText("");

    setFriends(allFriends);
    networksLoaded++;
    Timber.e("FB LOADED");
    checkNetworksLoaded();

  }

  @Override public void onGetFacebookFriendsFailure(String message) {
    this.allFriends.clear();
    this.invitable.clear();

    setFriends(allFriends);
    //closeProgressFragment();
    hideProgress();
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.FAILURE_GET_FACEBOOK_FRIENDS_ID).content(
          message).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onGetFriendInfoSuccess(List<FacebookFriend> convertedFriends) {
    members.addAll(convertedFriends);
    createGroup(members);
  }

  @Override public void onGetFriendInfoFailure(String message) {
    //closeProgressFragment();
    hideProgress();

    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.FAILURE_CONVERT_FB_FRIENDS_ID).content(
          message).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onApiCreateGroupSuccess(CreatedGroup createdGroup) {
    mJoinGroupClient.joinGroup(createdGroup.getId());
    hideProgress();
  }

  @Override public void onApiCreateGroupFailure(String message) {
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.API_CREATE_GROUP_FAILURE_ID).content(message)
          .show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onApiJoinGroupSuccess(Tracking result) {
    //closeProgressFragment();
    hideProgress();
    int activeTrackings = SharedPreferenceHelper.getActiveTrackingsCreated();
    SharedPreferenceHelper.saveActiveTrackingsCreated(++activeTrackings);

    App.getEventBus().post(new ReportStepsEvent());
    Intent data = new Intent();
    data.putExtra(Constants.INTENT_CREATED_TRACKING, result);
    setResult(RESULT_OK, data);

    List<String> idsFb = new ArrayList<>();
    List<FacebookFriend> intiteByPh = new ArrayList<>();
    ArrayList<FacebookFriend> intiteByVk = new ArrayList<>();
    for (FacebookFriend friend : friendsAdapter.getSelectedFriends()) {
      if (friend.getSocialNetwork().equals("fb")) {
        idsFb.add(friend.getId());
      }
      if (friend.getSocialNetwork().equals("ph")) {
        intiteByPh.add(friend);
      }
      if (friend.getSocialNetwork().equals("vk")) {
        intiteByVk.add(friend);
      }
    }
    if (!intiteByVk.isEmpty()) {
      //finish();
      SendVkInvitationDialog sendVkInvitationDialog =
          SendVkInvitationDialog.newInstance(intiteByVk, (Dialog dialog) -> {
            dialog.dismiss();
            finish();
          });
      sendVkInvitationDialog.show(getFragmentManager(), "SendVkInvitationDialog");
    } else {
      Timber.e("1");
      finish();
    }
    if (intiteByVk.isEmpty() && idsFb.isEmpty()) {
      Timber.e("2");
      finish();
    }
  }

  @Override public void onApiJoinGroupFailure(String message) {
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.API_JOIN_GROUP_FAILURE_ID).content(message)
          .show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onAsyncBitmapSaveSuccess(String path) {
    selectedFile = new File(path);
    //closeProgressFragment();
    hideProgress();
  }

  @Override public void onAsyncBitmapSaveFailed() {
    //closeProgressFragment();
    hideProgress();
  }

  private void tryChooseGroupAvatar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
          != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
            android.Manifest.permission.RECORD_AUDIO)) {
          showNeedExternalStoragePermission();
        } else {
          requestPermissions(Constants.PERMISSION_EXTERNAL_STORAGE,
              Constants.REQUEST_EXTERNAL_STORAGE_PERMISSION_CODE);
        }
      } else {
        chooseGroupAvatar();
      }
    } else {
      chooseGroupAvatar();
    }
  }

  private void chooseGroupAvatar() {
    Intent galleryIntent = DeviceHelper.getGalleryIntent();
    Pair<String[], Parcelable[]> cameraData = DeviceHelper.getCameraData();
    Parcelable[] cameraIntents = cameraData.second;

    String path = cameraData.first[0];
    String uri = cameraData.first[1];

    SharedPreferenceHelper.saveCaptureCameraUri(uri);
    SharedPreferenceHelper.saveCaptureCameraPath(path);

    if (!TextUtils.isEmpty(uri)
        && !TextUtils.isEmpty(path)
        && galleryIntent != null
        && cameraIntents != null) {
      Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.add_photo));
      chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents);

      startActivityForResult(chooserIntent, Constants.INTENT_CHOOSER_IMAGE_REQUEST_CODE);
    } else {
      Log.e(TAG, "failure prepare data for get group avatar");
    }
  }

  private void showNeedExternalStoragePermission() {
    new SugarmanDialog.Builder(this, DialogConstants.NEED_EXTERNAL_STORAGE_PERMISSION_ID).content(
        R.string.need_your_permission).positiveText(R.string.okay).show();
  }

  private void processCaptureCamera() {
    String filePath = SharedPreferenceHelper.getCaptureCameraPath();
    String cameraUri = SharedPreferenceHelper.getCaptureCameraUri();

    selectedFile = new File(filePath);
    setGroupAvatar(selectedFile);

    if (!TextUtils.isEmpty(cameraUri)) {
      Uri uri = Uri.parse(cameraUri);
      DeviceHelper.revokeWritePermission(uri);
    } else {
      Log.e(TAG, "captured camera uri is null");
    }
  }

  private void processCameraGallery(Intent data) {
    ClipData clipData = data.getClipData();
    if (clipData != null
        && clipData.getItemAt(0) != null
        && clipData.getItemAt(0).getUri() != null) {
      Uri uri = clipData.getItemAt(0).getUri();
      selectedFile = DeviceHelper.pickedExistingPicture(uri);
      setGroupAvatar(selectedFile);
    } else if (data.getData() != null) {
      String path = DeviceHelper.getRealPathFromUri(data.getData());
      selectedFile = new File(path);
      setGroupAvatar(selectedFile);
    } else {
      Log.e(TAG, "failure get image from gallery");
    }
  }

  private void filterFacebookFriends(String filter) {
    filtered.clear();
    if (TextUtils.isEmpty(filter)) {
      filtered.addAll(allFriends);
    } else {
      for (FacebookFriend friend : allFriends) {
        if (friend.getName().toLowerCase().contains(filter.toLowerCase())) {
          filtered.add(friend);
        }
      }
    }

    setFriends(filtered);
  }

  private void getFacebookFriends() {
    if (DeviceHelper.isNetworkConnected()) {
      //showProgressFragmentTemp();
      fbApiClient.searchFriends();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  private void checkFilledData() {

    filtered.clear();
    filtered.addAll(allFriends);
    setFriends(filtered);

    String groupName = etGroupName.getText().toString();
    List<FacebookFriend> selectedFriends = friendsAdapter.getSelectedFriends();
    members.clear();

    if (TextUtils.isEmpty(groupName.replace(" ", ""))) {
      new SugarmanDialog.Builder(this, DialogConstants.GROUP_NAME_IS_EMPTY_ID).content(
          R.string.group_name_is_empty).show();
    } else if (selectedFriends.isEmpty()) {
      new SugarmanDialog.Builder(this, DialogConstants.FRIENDS_LIST_IS_IMPTY_ID).content(
          R.string.members_list_is_empty).show();
    } else {
      showProgress();

      List<String> ids = new ArrayList<>();
      String id;
      for (FacebookFriend friend : selectedFriends) {
        if (invitable.contains(friend)) {
          id = friend.getId();
          //Timber.e("Invitable: " + friend.getName());
          ids.add(id);
        } else {
          members.add(friend);
        }
      }

      if (!ids.isEmpty()) {
        GameRequestContent content =
            new GameRequestContent.Builder().setMessage(getString(R.string.play_with_me))
                .setRecipients(ids)
                .build();
        fbInviteDialog.show(content);
      } else {
        createGroup(members);
      }
    }
  }

  private void createGroup(List<FacebookFriend> members) {
    showProgress();
    vApply.setEnabled(false);
    List<String> facebookElements = new ArrayList<>();
    ArrayList<FacebookFriend> vkElements = new ArrayList<>();

    //chech if some of members are present in mDistinktorList, if yes -> send him msg in social nenwork , else by sms
    //______________________________________________________________________________________________
    for (int i = 0; i < members.size(); i++) {
      for (int j = 0; j < mDistinktorList.size(); j++) {
        if (!members.isEmpty() && mDistinktorList.get(j).getFbid() != null && mDistinktorList.get(j)
            .getFbid()
            .equals(members.get(i).getId())) {
          facebookElements.add(members.get(i).getId());
          members.remove(i);
        }
      }
    }
    for (int i = 0; i < members.size(); i++) {
      for (int j = 0; j < mDistinktorList.size(); j++) {
        if (!members.isEmpty() && mDistinktorList.get(j).getVkid() != null && mDistinktorList.get(j)
            .getVkid()
            .equals(members.get(i).getId())) {
          vkElements.add(members.get(i));
          members.remove(i);
        }
      }
    }
    if (!vkElements.isEmpty()) {
      mPresenter.sendInvitationInVk(vkElements, getString(R.string.invite_message));
    }
    if (!facebookElements.isEmpty()) {
      GameRequestContent content =
          new GameRequestContent.Builder().setMessage(getString(R.string.play_with_me))
              .setRecipients(facebookElements)
              .build();
      fbInviteDialog.show(content);
    }
    //______________________________________________________________________________________________
    String groupName = etGroupName.getText().toString();
    mPresenter.checkRuleXNewUsersInvite(members);
    Timber.e("members " + members.size());
    mCreateGroupClient.createGroup(members, groupName, selectedFile, CreateGroupActivity.this);
  }

  private void setFriends(List<FacebookFriend> friends) {
    friendsAdapter.setValue(friends);

    if (friends.isEmpty()) {
      rcvFriends.setVisibility(View.GONE);
      //vNoFriends.setVisibility(View.VISIBLE);
    } else {
      rcvFriends.setVisibility(View.VISIBLE);
      // vNoFriends.setVisibility(View.GONE);
    }
  }

  private void sendInvitation() {

    List<String> ids = new ArrayList<>();
    List<FacebookFriend> intiteByVk = new ArrayList<>();
    String id;
    for (FacebookFriend friend : friendsAdapter.getSelectedFriends()) {

      id = friend.getId();
      ids.add(id);

      if (friend.getSocialNetwork().equals("vk")) {
        intiteByVk.add(friend);
      }
    }
    if (!intiteByVk.isEmpty()) {
      mPresenter.sendInvitationInVk(intiteByVk, getResources().getString(R.string.invite_message));
    }

    if (!ids.isEmpty()) {
      GameRequestContent content =
          new GameRequestContent.Builder().setMessage(getString(R.string.play_with_me))
              .setRecipients(ids)
              .build();
      fbInviteDialog.show(content);
    }
  }

  private void setGroupAvatar(File file) {
    if (file != null && file.exists() && file.isFile()) {
      String path = file.getAbsolutePath();
      Bitmap bm = BitmapFactory.decodeFile(path);
      bm = BitmapUtils.getRotatedBitmap(path, bm);
      bm = BitmapUtils.scaleBitmap(bm, Config.MAX_PICTURE_SIZE_SEND_TO_SERVER,
          Config.MAX_PICTURE_SIZE_SEND_TO_SERVER);

      showProgress();
      new SaveBitmapToFileAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bm);

      bm = scaleCenterCrop(bm, bm.getHeight() < bm.getWidth() ? bm.getHeight() : bm.getWidth(),
          bm.getHeight() < bm.getWidth() ? bm.getHeight() : bm.getWidth());
      bm = Bitmap.createScaledBitmap(bm, bm.getWidth() / 3, bm.getHeight() / 3, true);
      MaskImage mi = new MaskImage(this, R.drawable.group_avatar, false, 0xff000000);
      bm = mi.transform(bm);
      Log.e("Image TRAAAAH", "" + BitmapCompat.getAllocationByteCount(bm));
      ivGroupAvatar.setImageBitmap(bm);
      ivGroupAvatar.setVisibility(View.VISIBLE);
      //vAddPhotoContainer.setBackgroundResource(R.drawable.dark_gray_double_stroke_background);
    } else {
      Log.e(TAG, "file isn't exists: " + (file == null ? null : file.getAbsolutePath()));
    }
  }

  @Override public void onApiCheckPhoneSuccess(List<Phones> phones) {
    Timber.e("onApiCheckPhoneSuccess phones size " + phones.size());
    mDistinktorList = phones;
    numberOfMemberWithAppPh = phones.size();
    tvInAppPhCount.setText(String.valueOf(numberOfMemberWithAppPh));

    Timber.e("numberOfMemberWithAppPh size " + numberOfMemberWithAppPh);

    //Timber.e("Check phones " + mDistinktorList.size());

    //Timber.e("SET INVITABLE 1 " + phones.size());

    for (Phones p : phones) {
      // Timber.e("SET INVITABLE IF 1.5 ");
      for (FacebookFriend friend : allFriends) {
        //  Timber.e("SET INVITABLE for 2 " + friend.getName());
        if (friend.getSocialNetwork().equals("ph")) {
          //  Timber.e("SET INVITABLE IF 3 " + friend.getName());
          if (friend.getId().equals(p.getPhone())) {
            //    Timber.e("SET INVITABLE IF 4 " + friend.getName());
            friend.setIsInvitable(FacebookFriend.CODE_NOT_INVITABLE);
          }
        }
      }
    }

    runOnUiThread(() -> {
      //setFriends(allFriends);
      friendsAdapter.notifyItemRangeChanged(0, allFriends.size());
    });
    networksLoaded++;
    Timber.e("Check phones " + networksLoaded);
    checkNetworksLoaded();
  }

  @Override public void onApiCheckPhoneFailure(String message) {
    Timber.e("Govno check phone");
  }

  @Override public void onApiCheckVkSuccess(List<String> vks) {

    //  Timber.e("OnApiCheckVkSuccess");

    // Timber.e("SET INVITABLE IF 1 " + vks.size());
    for (String s : vks) {
      //Timber.e("SET INVITABLE IF 1.5 ");
      for (FacebookFriend friend : allFriends) {
        //   Timber.e("SET INVITABLE for 2 " + friend.getName());
        if (friend.getSocialNetwork().equals("vk")) {
          numberOfMemberTotalAppVk++;
          //     Timber.e("SET INVITABLE IF 3 " + friend.getName());
          if (friend.getId().equals(s)) {
            numberOfMemberWithAppVk++;
            //      Timber.e("SET INVITABLE IF 4 " + friend.getName());
            friend.setIsInvitable(FacebookFriend.CODE_NOT_INVITABLE);
          }
        }
      }
    }
    tvTotalVkCount.setText(String.valueOf(numberOfMemberTotalAppVk));
    tvInAppVkCount.setText(String.valueOf(numberOfMemberWithAppVk));
    Timber.e("numberOfMemberWithAppVk size " + numberOfMemberWithAppVk);
    Timber.e("numberOfMemberTotalAppVk size " + numberOfMemberTotalAppVk);

    runOnUiThread(new Runnable() {
      @Override public void run() {
        setFriends(allFriends);
      }
    });
    networksLoaded++;
    Timber.e("networkds loaded checkvk " + networksLoaded);
    checkNetworksLoaded();
  }

  @Override public void onApiCheckVkFailure(String message) {

  }

  @Override public void fillListByCachedData(List<FacebookFriend> facebookFriends) {
    if (facebookFriends != null) {
      facebookFriends.addAll(allFriends);
      Timber.e("fillListByCachedData " + facebookFriends.size());
      for (FacebookFriend fb : facebookFriends) {
        Timber.e("fillListByCachedData " + fb.getPicture());
        if (fb.getIsInvitable() == FacebookFriend.CODE_NOT_INVITABLE){
          numberOfMemberWithAppFb++;
        }
      }
      for (FacebookFriend fTemp : facebookFriends) {
        if (!allFriends.contains(fTemp)) {
          allFriends.add(fTemp);
        }
        friendsAdapter.setValue(facebookFriends);
        friendsAdapter.notifyDataSetChanged();
      }
      tvInAppFbCount.setText(String.valueOf(numberOfMemberWithAppFb));
      tvTotalFbCount.setText(String.valueOf(facebookFriends.size()));
    }
  }

  @Override public void showProgress() {
    pb.setVisibility(View.VISIBLE);
  }

  @Override public void hideProgress() {
    pb.setVisibility(View.GONE);
  }

  //@Override public void doEventActionResponse(CustomUserEvent build) {
  //  doEventAction(build,null);
  //}
}
