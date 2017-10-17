package com.sugarman.myb.ui.activities.addMember;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.clover_studio.spikachatmodule.utils.Const;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.widget.GameRequestDialog;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.R;
import com.sugarman.myb.adapters.VkFriendsAdapter;
import com.sugarman.myb.adapters.membersAdapter.MembersAdapter;
import com.sugarman.myb.api.clients.AddMembersClient;
import com.sugarman.myb.api.clients.CheckPhonesClient;
import com.sugarman.myb.api.clients.CheckVkClient;
import com.sugarman.myb.api.clients.EditGroupClient;
import com.sugarman.myb.api.clients.FBApiClient;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Phones;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.listeners.ApiAddMembersListener;
import com.sugarman.myb.listeners.ApiCheckPhoneListener;
import com.sugarman.myb.listeners.ApiCheckVkListener;
import com.sugarman.myb.listeners.ApiEditGroupListener;
import com.sugarman.myb.listeners.AsyncSaveBitmapToFileListener;
import com.sugarman.myb.listeners.OnFBGetFriendsListener;
import com.sugarman.myb.models.VkFriend;
import com.sugarman.myb.tasks.SaveBitmapToFileAsyncTask;
import com.sugarman.myb.ui.activities.EditProfileActivity;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.dialogs.sendVkInvitation.SendVkInvitationDialog;
import com.sugarman.myb.ui.views.MaskImage;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.AnalyticsHelper;
import com.sugarman.myb.utils.BitmapUtils;
import com.sugarman.myb.utils.ContactsHelper;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.IntentExtractorHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

import static com.sugarman.myb.utils.ImageHelper.scaleCenterCrop;

public class AddMemberActivity extends BaseActivity
    implements View.OnClickListener, OnFBGetFriendsListener, AsyncSaveBitmapToFileListener,
    ApiAddMembersListener, ApiEditGroupListener, IAddMemberActivityView, ApiCheckVkListener,
    ApiCheckPhoneListener {
  private static final String TAG = AddMemberActivity.class.getName();
  private final List<FacebookFriend> filtered = new ArrayList<>();
  private final List<FacebookFriend> allFriends = new ArrayList<>();
  private final List<FacebookFriend> invitable = new ArrayList<>();
  private final List<FacebookFriend> members = new ArrayList<>();
  List<FacebookFriend> phoneFriends;
  @InjectPresenter AddMemberActivityPresenter mPresenter;
  @BindView(R.id.fb_filter) ImageView fbFilter;
  @BindView(R.id.vk_filter) ImageView vkFilter;
  @BindView(R.id.ph_filter) ImageView phFilter;
  String currentFilter = "";
  View vApply;
  boolean isFbLoggedIn = false, isVkLoggedIn = false;
  private AddMembersClient mAddMembersClient;
  private EditGroupClient mEditGroupClient;
  private CheckVkClient mCheckVkClient;
  private CheckPhonesClient mCheckPhoneClient;
  private MembersAdapter membersAdapter;
  private VkFriendsAdapter vkFriendsAdapter;
  private List<VkFriend> vkFriends;
  private EditText etMembers;
  private EditText etGroupName;
  private RecyclerView rcvMembers;
  private RecyclerView rcvVkFriends;
  private View vClearMembersFilter;
  private final TextWatcher membersWatcher = new TextWatcher() {
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // nothing
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      //nothing
    }

    @Override public void afterTextChanged(Editable s) {
      String filter = s.toString();
      vClearMembersFilter.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
      Timber.e("afterTextChanged");
      filterMembers(filter);
      //membersAdapter.clearLists();
    }
  };
  private View vNoFriends;
  private ImageView sivGroupAvatar;
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
  private FBApiClient fbApiClient;
  private String groupName;
  private String groupPictureUrl;
  private Member[] addedMembers;
  private Member[] pendingMembers;
  private List<FacebookFriend> mTotalListOfSelectedMembers = new ArrayList<>();
  private boolean isFriendsFound = false;
  private CallbackManager fbCallbackManager;
  private GameRequestDialog fbInviteDialog;
  private String trackingId;
  private File selectedFile;
  private int networksToLoad = 0, networksLoaded = 0;
  private boolean vkIntitationSend;
  private List<String> mIdsFb = new ArrayList<>();
  private List<FacebookFriend> mInviteByPh = new ArrayList<>();
  private ArrayList<FacebookFriend> mInviteByVk = new ArrayList<>();
  private final FacebookCallback<GameRequestDialog.Result> fbInviteCallback =
      new FacebookCallback<GameRequestDialog.Result>() {

        @Override public void onSuccess(GameRequestDialog.Result result) {
          showProgressFragment();

          List<String> recipients = result.getRequestRecipients();
          fbApiClient.getFriendsInfo(recipients);
          checkFilledData();
          mIdsFb.clear();
          if (mInviteByPh.isEmpty() && mInviteByVk.isEmpty()) {
            Timber.e("Facebook finish");
            finish();
          }
        }

        @Override public void onCancel() {
          // nothing
        }

        @Override public void onError(FacebookException error) {
          if (DeviceHelper.isNetworkConnected()) {
            new SugarmanDialog.Builder(AddMemberActivity.this,
                DialogConstants.FAILURE_INVITE_FB_FRIENDS_ID).content(error.getMessage()).show();
          } else {
            showNoInternetConnectionDialog();
          }
        }
      };

  @Override protected void onCreate(Bundle savedStateInstance) {
    setContentView(R.layout.activity_add_member);
    super.onCreate(savedStateInstance);
    if (!SharedPreferenceHelper.getFbId().equals("none")) {
      networksToLoad++;
      isFbLoggedIn = true;
      fbFilter.setAlpha(1.0f);
    }
    if (!SharedPreferenceHelper.getVkId().equals("none")) {
      networksToLoad++;
      isVkLoggedIn = true;
      vkFilter.setAlpha(1.0f);
    }

    vkFriends = new ArrayList<>();

    //getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background));

    Intent intent = getIntent();
    phoneFriends = new ArrayList<FacebookFriend>();
    addedMembers = IntentExtractorHelper.getMembers(intent);
    Timber.e("Added Members " + addedMembers.length);
    trackingId = IntentExtractorHelper.getTrackingId(intent);
    pendingMembers = IntentExtractorHelper.getPendings(intent);
    groupName = IntentExtractorHelper.getGroupName(intent);
    groupPictureUrl = IntentExtractorHelper.getGroupPicture(intent);

    mEditGroupClient = new EditGroupClient();

    fbApiClient = new FBApiClient();
    mAddMembersClient = new AddMembersClient();
    mCheckPhoneClient = new CheckPhonesClient();
    mCheckVkClient = new CheckVkClient();

    membersAdapter = new MembersAdapter(getMvpDelegate(), this);
    vkFriendsAdapter = new VkFriendsAdapter(this);

    fbCallbackManager = CallbackManager.Factory.create();
    fbInviteDialog = new GameRequestDialog(this);
    fbInviteDialog.registerCallback(fbCallbackManager, fbInviteCallback);

    View vCross = findViewById(R.id.iv_cross);
    vApply = findViewById(R.id.iv_apply);
    vClearMembersFilter = findViewById(R.id.iv_clear_member_filter_input);
    rcvMembers = (RecyclerView) findViewById(R.id.rcv_friends);
    rcvVkFriends = (RecyclerView) findViewById(R.id.rcv_vk_friends);
    vNoFriends = findViewById(R.id.tv_no_friends);
    etMembers = (EditText) findViewById(R.id.et_search);
    etGroupName = (EditText) findViewById(R.id.et_group_name);
    sivGroupAvatar = (ImageView) findViewById(R.id.iv_group_avatar);
    vClearGroupName = findViewById(R.id.iv_clear_group_name_input);

    etGroupName.addTextChangedListener(groupNameWatcher);

    etGroupName.setText(groupName);

    if (TextUtils.isEmpty(groupPictureUrl)) {
      Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_group);
      bm = scaleCenterCrop(bm, bm.getHeight() < bm.getWidth() ? bm.getHeight() : bm.getWidth(),
          bm.getHeight() < bm.getWidth() ? bm.getHeight() : bm.getWidth());
      MaskImage mi = new MaskImage(this, R.drawable.group_avatar, false, 0xff000000);
      bm = mi.transform(bm);

      sivGroupAvatar.setImageBitmap(bm);
      //sivGroupAvatar.setImageResource(R.drawable.ic_group);
    } else {
      Picasso.with(this)
          .load(groupPictureUrl)
          .fit()
          .centerCrop()
          //.placeholder(R.drawable.ic_group)
          .error(R.drawable.ic_group)
          .transform(new MaskTransformation(AddMemberActivity.this, R.drawable.group_avatar, false,
              0xff000000))
          .into(sivGroupAvatar);
    }

    etMembers.addTextChangedListener(membersWatcher);

    rcvMembers.setLayoutManager(new LinearLayoutManager(this));
    rcvMembers.setAdapter(membersAdapter);

    rcvVkFriends.setLayoutManager(new LinearLayoutManager(this));
    rcvVkFriends.setAdapter(vkFriendsAdapter);

    vClearMembersFilter.setOnClickListener(this);
    vCross.setOnClickListener(this);
    vApply.setOnClickListener(this);
    sivGroupAvatar.setOnClickListener(this);
    vClearGroupName.setOnClickListener(this);

    if (checkCallingOrSelfPermission(Constants.READ_PHONE_CONTACTS_PERMISSION)
        == PackageManager.PERMISSION_GRANTED) {

      AsyncTask.execute(() -> {
        List<String> phToCheck = new ArrayList<String>();
        HashMap<String, String> contactList = ContactsHelper.getContactList(AddMemberActivity.this);

        for (String key : contactList.keySet()) {
          String phone = contactList.get(key);
          FacebookFriend friend =
              new FacebookFriend(phone, key, "", FacebookFriend.CODE_INVITABLE, "ph");
          Timber.e(phone);
          allFriends.add(friend);
          //phoneFriends.add(friend);
          phToCheck.add(contactList.get(key).replace(" ", ""));
        }
        checkForUnique();
        networksToLoad++;
        mCheckPhoneClient.checkPhones(phToCheck);
      });
    } else {
      phFilter.setAlpha(0.5f);
    }

    AnalyticsHelper.reportInvite();

    VKRequest request = new VKRequest("friends.get",
        VKParameters.from(VKApiConst.FIELDS, "photo_100", "order", "name"));
    request.executeWithListener(new VKRequest.VKRequestListener() {
      @Override public void onComplete(VKResponse response) {
        List<String> vkToCheck = new ArrayList<String>();
        super.onComplete(response);
        JSONObject resp = response.json;
        try {
          JSONArray items = resp.getJSONObject("response").getJSONArray("items");
          for (int i = 0; i < items.length(); i++)
          //{"id":4082081,"first_name":"Irishka","last_name":"Danilchenko","online":0, "photo_100":""}
          {

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
        } catch (JSONException e) {
          e.printStackTrace();
        }
        networksLoaded++;
        mCheckVkClient.checkVks(vkToCheck);

        setFriends(allFriends);
        checkForUnique();
      }

      @Override public void onError(VKError error) {
        super.onError(error);
        setFriends(allFriends);
        //        Log.e("VK", error.errorMessage);
      }
    });
  }

  void checkForUnique() {
    Timber.e("checkForUnique");
    for (int i = 0; i < allFriends.size(); i++) {
      for (Member member : addedMembers) {
        if (TextUtils.equals(member.getName(), allFriends.get(i).getName())
            || member.getFbid()
            .equals(allFriends.get(i).getId())
            || member.getVkId().equals(allFriends.get(i).getId())
            || member.getPhoneNumber().equals(allFriends.get(i).getId())) {
          Timber.e("1");

          allFriends.get(i).setAdded(true);
        }
      }

      for (Member member : pendingMembers) {
        //if (TextUtils.equals(member.getName(), allFriends.get(i).getName())) {
        if (TextUtils.equals(member.getName(), allFriends.get(i).getName())
            || member.getFbid()
            .equals(allFriends.get(i).getId())
            || member.getVkId().equals(allFriends.get(i).getId())
            || member.getPhoneNumber().equals(allFriends.get(i).getId())) {
          Timber.e("2");

          allFriends.get(i).setPending(true);
        }
      }
      Timber.e(String.valueOf(allFriends.get(i).isAdded()));
    }
  }

  @OnClick(R.id.fb_filter) public void showFbFriends() {
    //addMembersFromPreviousAdapter();
    if (isFbLoggedIn) {
      if (!currentFilter.equals("fb")) {
        filtered.clear();

        for (FacebookFriend friend : allFriends) {
          if (friend.getSocialNetwork().equals("fb")) {
            filtered.add(friend);
          }
        }

        Timber.e("showFbFriends");
        setFilteredFriends(filtered);
        currentFilter = "fb";
        phFilter.setAlpha(0.5f);
        vkFilter.setAlpha(0.5f);
        fbFilter.setAlpha(1.0f);
      } else {
        setFilteredFriends(allFriends);
        currentFilter = "";
        phFilter.setAlpha(1.0f);
        if (isVkLoggedIn) vkFilter.setAlpha(1.0f);
      }
    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder(AddMemberActivity.this);
      builder.setMessage(getResources().getString(R.string.log_in_to_fb))
          .setTitle(getResources().getString(R.string.not_logged_in));
      builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          Intent intent = new Intent(AddMemberActivity.this, EditProfileActivity.class);
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

  private void setFilteredFriends(List<FacebookFriend> filtered) {
    membersAdapter.setFilteredValue(filtered);
  }

  @OnClick(R.id.vk_filter) public void showVkFriends() {
    //addMembersFromPreviousAdapter();
    if (isVkLoggedIn) {
      if (!currentFilter.equals("vk")) {
        filtered.clear();

        for (FacebookFriend friend : allFriends) {
          if (friend.getSocialNetwork().equals("vk")) {
            filtered.add(friend);
          }
        }
        Timber.e(String.valueOf(filtered.size()));

        setFilteredFriends(filtered);
        currentFilter = "vk";
        fbFilter.setAlpha(0.5f);
        phFilter.setAlpha(0.5f);
        vkFilter.setAlpha(1.0f);
      } else {
        setFilteredFriends(allFriends);
        currentFilter = "";
        if (isFbLoggedIn) fbFilter.setAlpha(1.0f);
        phFilter.setAlpha(1.0f);
      }
    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder(AddMemberActivity.this);
      builder.setMessage(getResources().getString(R.string.log_in_to_vk))
          .setTitle(getResources().getString(R.string.not_logged_in));
      builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          Intent intent = new Intent(AddMemberActivity.this, EditProfileActivity.class);
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
    //addMembersFromPreviousAdapter();
    if (!currentFilter.equals("ph")) {
      filtered.clear();

      for (FacebookFriend friend : allFriends) {
        if (friend.getSocialNetwork().equals("ph")) {
          filtered.add(friend);
        }
      }

      setFilteredFriends(filtered);
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
      setFilteredFriends(allFriends);
      currentFilter = "";
      if (isFbLoggedIn) fbFilter.setAlpha(1.0f);
      if (isVkLoggedIn) vkFilter.setAlpha(1.0f);
    }
  }

  @Override protected void onStart() {
    super.onStart();
    mEditGroupClient.registerListener(this);
    fbApiClient.registerListener(this);
    mAddMembersClient.registerListener(this);
    mCheckPhoneClient.registerListener(this);
    mCheckVkClient.registerListener(this);
  }

  @Override protected void onStop() {
    super.onStop();
    mEditGroupClient.unregisterListener();
    fbApiClient.unregisterListener();
    mAddMembersClient.unregisterListener();
    mCheckPhoneClient.unregisterListener();
    mCheckVkClient.unregisterListener();
  }

  @Override protected void onResume() {
    super.onResume();

    if (!isFriendsFound) {
      getFacebookFriends();
    }
  }

  private void checkNetworksLoaded() {
    Timber.e(networksLoaded + " out of " + networksToLoad);
    if (networksLoaded == networksToLoad) closeProgressFragment();
  }

  @SuppressLint("NewApi") // checking version inside
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Timber.e("ActivityResult");
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

  @Override public void onAsyncBitmapSaveSuccess(String path) {
    selectedFile = new File(path);
    closeProgressFragment();
  }

  @Override public void onAsyncBitmapSaveFailed() {

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

  private void setGroupAvatar(File file) {
    if (file != null && file.exists() && file.isFile()) {
      String path = file.getAbsolutePath();
      Bitmap bm = BitmapFactory.decodeFile(path);
      bm = BitmapUtils.getRotatedBitmap(path, bm);
      bm = BitmapUtils.scaleBitmap(bm, Config.MAX_PICTURE_SIZE_SEND_TO_SERVER,
          Config.MAX_PICTURE_SIZE_SEND_TO_SERVER);

      showProgressFragment();
      new SaveBitmapToFileAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bm);

      bm = scaleCenterCrop(bm, bm.getHeight() < bm.getWidth() ? bm.getHeight() : bm.getWidth(),
          bm.getHeight() < bm.getWidth() ? bm.getHeight() : bm.getWidth());
      MaskImage mi = new MaskImage(this, R.drawable.group_avatar, false, 0xff000000);
      bm = mi.transform(bm);

      sivGroupAvatar.setImageBitmap(bm);
    } else {
      Log.e(TAG, "file isn't exists: " + (file == null ? null : file.getAbsolutePath()));
    }
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.iv_group_avatar:
        tryChooseGroupAvatar();
        break;
      case R.id.iv_apply:
        DeviceHelper.hideKeyboard(this);
        vApply.setEnabled(false);
        showProgressFragment();

        for (FacebookFriend friend : allFriends) {
          if (friend.isSelected()) {
            if (friend.getSocialNetwork().equals("fb")) {
              if (friend.getIsInvitable() == FacebookFriend.CODE_INVITABLE) {
                mIdsFb.add(friend.getId());
              }
            }
            if (friend.getSocialNetwork().equals("ph")) {
              mInviteByPh.add(friend);
            }
            if (friend.getSocialNetwork().equals("vk")) {
              mInviteByVk.add(friend);
            }
          }
        }
        if (mIdsFb.isEmpty() && mInviteByPh.isEmpty() && mInviteByVk.isEmpty()) {
          mEditGroupClient.editGroup(trackingId, members, etGroupName.getText().toString(),
              selectedFile);
          finish();
        }
        if (!mInviteByPh.isEmpty()) {
          Timber.e("ph here");
          addMembersPh(mInviteByPh);
        }
        if (!mIdsFb.isEmpty()) {
          Timber.e("fb here");

          Timber.e(String.valueOf(membersAdapter.getSelectedMembers().size()));
          GameRequestContent content =
              new GameRequestContent.Builder().setMessage(getString(R.string.play_with_me))
                  .setRecipients(mIdsFb)
                  .build();
          fbInviteDialog.show(content);
        } else {
          Timber.e("!mIdsFb.isEmpty()");
          // TODO: 12.10.2017 check Fb invite and create group
          //checkFilledData();
        }
        if (!mInviteByVk.isEmpty()) {
          //mPresenter.sendInvitationInVk(mInviteByVk,
          //    getResources().getString(R.string.invite_message));
          SendVkInvitationDialog.newInstance(mInviteByVk)
              .show(getFragmentManager(), "SendVkInvitationDialog");
          Timber.e("vk here");
          vkIntitationSend = true;
        }
        break;
      case R.id.iv_cross:
        finish();
        setResult(RESULT_CANCELED);
        break;
      case R.id.iv_clear_member_filter_input:
        etMembers.setText("");
        filterMembers("");
      case R.id.iv_clear_group_name_input:
        etGroupName.setText("");
        break;
      default:
        Log.d(TAG,
            "Click on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }

  @Override public void onGetFacebookFriendsSuccess(List<FacebookFriend> friends,
      List<FacebookFriend> invitable) {
    isFriendsFound = true;

    for (int i = 0; i < friends.size(); i++) {
      friends.get(i).setIsInvitable(FacebookFriend.CODE_NOT_INVITABLE);
    }

    for (int i = 0; i < invitable.size(); i++) {
      invitable.get(i).setIsInvitable(FacebookFriend.CODE_INVITABLE);
    }

    this.invitable.clear();
    this.invitable.addAll(invitable);

    for (FacebookFriend friend : friends) {
      friend.setSocialNetwork("fb");
    }
    for (FacebookFriend friend : invitable) {

      friend.setSocialNetwork("fb");
    }
    //allFriends.clear();
    allFriends.addAll(friends);
    allFriends.addAll(invitable);

    for (int i = 0; i < allFriends.size(); i++) {
      for (Member member : addedMembers) {
        if (TextUtils.equals(member.getName(), allFriends.get(i).getName()) || member.getFbid()
            .equals(allFriends.get(i).getId())) {
          allFriends.get(i).setAdded(true);
        }
      }

      for (Member member : pendingMembers) {
        if (TextUtils.equals(member.getName(), allFriends.get(i).getName())) {
          allFriends.get(i).setPending(true);
        }
      }
    }
    checkForUnique();

    Collections.sort(allFriends, FacebookFriend.BY_NAME_ASC);
    //etMembers.setText("");

    networksLoaded++;

    Timber.e("onGetFacebookFriendsSuccess");
    setFriends(friends);
    setFriends(invitable);
    checkNetworksLoaded();
  }

  @Override public void onGetFacebookFriendsFailure(String message) {
    //this.allFriends.clear();
    this.invitable.clear();
    Timber.e("onGetFacebookFriendsFailure");

    setFriends(allFriends);
    closeProgressFragment();

    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.FAILURE_GET_FACEBOOK_FRIENDS_ID).content(
          message).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onGetFriendInfoSuccess(List<FacebookFriend> convertedFriends) {
    Timber.e("getFriendInfoSuccess");
    members.addAll(convertedFriends);
    showProgressFragment();
    mEditGroupClient.editGroup(trackingId, members, etGroupName.getText().toString(), selectedFile);
    mAddMembersClient.addMembers(trackingId, members);
  }

  @Override public void onGetFriendInfoFailure(String message) {
    closeProgressFragment();
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.FAILURE_CONVERT_FB_FRIENDS_ID).content(
          message).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onApiAddMembersSuccess() {
    Timber.e("onApiAddMembersSuccess");
    membersAdapter.markSelectedAsPending();
    closeProgressFragment();
    setResult(RESULT_OK);
    if (vkIntitationSend) {
      vkIntitationSend = false;
    } else {
      if (mIdsFb.isEmpty() && mInviteByVk.isEmpty() && mInviteByPh.isEmpty()) {
        Timber.e("addMember finish");
        finish();
      }
    }
  }

  @Override public void onApiAddMembersFailure(String message) {
    closeProgressFragment();
    if (DeviceHelper.isNetworkConnected()) {
      //new SugarmanDialog.Builder(this, DialogConstants.API_FAILURE_ADD_MEMBERS_ID)
      //        .content(message)
      //        .show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override protected void onDestroy() {
    membersAdapter.clearLists();
    super.onDestroy();
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

  private void checkFilledData() {

    List<FacebookFriend> selectedFriends = membersAdapter.getSelectedMembers();
    members.clear();

    if (selectedFriends.isEmpty()) {
           /* new SugarmanDialog.Builder(this, DialogConstants.FRIENDS_LIST_IS_IMPTY_ID)
                    .content(R.string.members_list_is_empty)
                    .show();*/
      showProgressFragment();
      mEditGroupClient.editGroup(trackingId, members, etGroupName.getText().toString(),
          selectedFile);
      mAddMembersClient.addMembers(trackingId, members);
    } else {
      List<String> ids = new ArrayList<>();
      String id;
      for (FacebookFriend friend : selectedFriends) {
        if (invitable.contains(friend)) {
          id = friend.getId();
          ids.add(id);
        } else {
          members.add(friend);
        }
      }

      if (!ids.isEmpty()) {
        //onC
      } else {
      }
      Timber.e("checkFilledData empty " + members.size());
      showProgressFragment();
      mEditGroupClient.editGroup(trackingId, members, etGroupName.getText().toString(),
          selectedFile);
      mAddMembersClient.addMembers(trackingId, members);
    }
  }

  private void filterMembers(String filter) {
    Timber.e("filterMembers");
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

    setFilteredFriends(filtered);
  }

  private void addMembersPh(List<FacebookFriend> members) {
    Timber.e("addMembersPh " + members.size());
    showProgressFragment();
    //mInviteByPh.clear();

    mEditGroupClient.editGroup(trackingId, members, etGroupName.getText().toString(), selectedFile);
    mAddMembersClient.addMembers(trackingId, members);
  }

  private void getFacebookFriends() {
    if (DeviceHelper.isNetworkConnected()) {
      showProgressFragment();
      fbApiClient.searchFriends();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  private void setVkFriends(List<VkFriend> friends) {
    vkFriendsAdapter.setValue(friends);
    if (friends.isEmpty()) {
      rcvVkFriends.setVisibility(View.GONE);
      //vNoFriends.setVisibility(View.VISIBLE);
    } else {
      rcvVkFriends.setVisibility(View.VISIBLE);
      //vNoFriends.setVisibility(View.GONE);
    }
  }

  private void setFriends(List<FacebookFriend> friends) {
    membersAdapter.setValue(friends);

    if (friends.isEmpty()) {
      rcvMembers.setVisibility(View.GONE);
      //vNoFriends.setVisibility(View.VISIBLE);
    } else {
      rcvMembers.setVisibility(View.VISIBLE);
      //vNoFriends.setVisibility(View.GONE);
    }
  }

  @Override public void onApiEditGroupSuccess(Tracking group) {
    Timber.e("onApiEditGroupSuccess");

    closeProgressFragment();
    finish();
  }

  @Override public void onApiEditGroupFailure(String message) {
    if (DeviceHelper.isNetworkConnected()) {
      //new SugarmanDialog.Builder(this, DialogConstants.API_JOIN_GROUP_FAILURE_ID)
      //        .content(message)
      //        .show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void addMemberToServer(List<FacebookFriend> mFacebookFriends) {
    Timber.e("addMemberToServer RxBus" + mFacebookFriends.get(0).getSocialNetwork());
    addMembersVk(mFacebookFriends);
  }

  private void addMembersVk(List<FacebookFriend> members) {
    showProgressFragment();
    Timber.e("addMembersVk" + members.get(0).getSocialNetwork());

    // TODO: 03.10.2017 uznat pochemu ne dobavlyaetsya na ui chelovek iz VK
    //mInviteByVk.clear();

    mEditGroupClient.editGroup(trackingId, members, etGroupName.getText().toString(), selectedFile);
    mAddMembersClient.addMembers(trackingId, members);
  }

  @Override public void onApiCheckVkSuccess(List<String> vks) {

    Timber.e("SET INVITABLE IF 1 " + vks.size());
    for (String s : vks) {
      Timber.e("SET INVITABLE IF 1.5 ");
      for (FacebookFriend friend : allFriends) {
        Timber.e("SET INVITABLE for 2 " + friend.getName());
        if (friend.getSocialNetwork().equals("vk")) {
          Timber.e("SET INVITABLE IF 3 " + friend.getName());
          if (friend.getId().equals(s)) {
            Timber.e("SET INVITABLE IF 4 " + friend.getName());
            friend.setIsInvitable(FacebookFriend.CODE_NOT_INVITABLE);
          }
        }
      }
    }

    runOnUiThread(new Runnable() {
      @Override public void run() {
        //setFriends(allFriends);
        membersAdapter.notifyDataSetChanged();
      }
    });
    checkNetworksLoaded();
  }

  @Override public void onApiCheckVkFailure(String message) {

  }

  @Override public void onApiCheckPhoneSuccess(List<Phones> phones) {

    for (Phones p : phones) {
      for (FacebookFriend friend : allFriends) {
        if (friend.getSocialNetwork().equals("ph")) {
          if (friend.getId().equals(p.getPhone())) {
            friend.setIsInvitable(FacebookFriend.CODE_NOT_INVITABLE);
          }
        }
      }
    }

    runOnUiThread(new Runnable() {
      @Override public void run() {
        membersAdapter.notifyDataSetChanged();
        //setFriends(allFriends);
      }
    });
    networksLoaded++;
    checkNetworksLoaded();
  }

  @Override public void onApiCheckPhoneFailure(String message) {

  }
}
