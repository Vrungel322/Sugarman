package com.sugarman.myb.ui.activities.shopInviteFriend;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.BindViews;
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
import com.sugarman.myb.adapters.membersAdapter.MembersAdapter;
import com.sugarman.myb.api.clients.FBApiClient;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.listeners.OnFBGetFriendsListener;
import com.sugarman.myb.ui.activities.editProfile.EditProfileActivity;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import com.sugarman.myb.ui.views.CustomFontEditText;
import com.sugarman.myb.utils.AnalyticsHelper;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.IntentExtractorHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import timber.log.Timber;

/*
LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
View v = vi.inflate(R.layout.your_layout, null);

// fill in any details dynamically here
TextView textView = (TextView) v.findViewById(R.id.a_text_view);
textView.setText("your text");

// insert into main view
ViewGroup insertPoint = (ViewGroup) findViewById(R.id.insert_point);
insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
*/

//Это очень хороший кусок кода) Тут что-то типа адаптера, но просто через инфлейтер создаются вьюшки

public class ShopInviteFriendsActivity extends BasicActivity
    implements OnFBGetFriendsListener, IShopInviteFriendsActivityView {

  private static final String TAG = ShopInviteFriendsActivity.class.getName();
  //private AddMembersClient mAddMembersClient;
  //private EditGroupClient mEditGroupClient;
  private final List<FacebookFriend> filtered = new ArrayList<>();
  private final List<FacebookFriend> allFriends = new ArrayList<>();
  private final List<FacebookFriend> invitable = new ArrayList<>();
  private final List<FacebookFriend> members = new ArrayList<>();
  private final TextWatcher groupNameWatcher = new TextWatcher() {
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // nothing
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      // nothing

    }

    @Override public void afterTextChanged(Editable s) {
      boolean isEmpty = TextUtils.isEmpty(s);
      //  vClearGroupName.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
  };
  @InjectPresenter ShopInviteFriendsActivityPresenter mPresenter;
  @BindView(R.id.bAddFriends) ImageView bAddFriends;
  @BindView(R.id.cfetSearchFriends) CustomFontEditText mEditTextSearch;
  @BindView(R.id.fb_filter) ImageView fbFilter;
  @BindView(R.id.vk_filter) ImageView vkFilter;
  @BindView(R.id.ph_filter) ImageView phFilter;
  @BindViews({
      R.id.avatar_invites_1, R.id.avatar_invites_2, R.id.avatar_invites_3, R.id.avatar_invites_4,
      R.id.avatar_invites_5,
  }) List<ImageView> ivListInviter;
  Typeface tfDin;
  List<TextView> allTexts;
  private MembersAdapter membersAdapter;
  private RecyclerView rcvMembers;
  private final TextWatcher membersWatcher = new TextWatcher() {
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // nothing
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      //nothing
    }

    @Override public void afterTextChanged(Editable s) {
      String filter = s.toString();
      //  vClearMembersFilter.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
      filterMembers(filter);
    }
  };
  private FBApiClient fbApiClient;
  private final FacebookCallback<GameRequestDialog.Result> fbInviteCallback =
      new FacebookCallback<GameRequestDialog.Result>() {

        @Override public void onSuccess(GameRequestDialog.Result result) {

          List<String> recipients = result.getRequestRecipients();
          fbApiClient.getFriendsInfo(recipients);
        }

        @Override public void onCancel() {
          // nothing
        }

        @Override public void onError(FacebookException error) {

        }
      };
  private Member[] addedMembers;
  private Member[] pendingMembers;
  private boolean isFriendsFound = false;
  private CallbackManager fbCallbackManager;
  private GameRequestDialog fbInviteDialog;
  private String trackingId;
  private List<String> mInviteByFbIds;
  private ArrayList<FacebookFriend> mIntiteByVk;
  private ArrayList<FacebookFriend> mIntiteByPh;
  private List<FacebookFriend> toFilterList = new ArrayList<>();
  private boolean isVkLoggedIn;
  private boolean isFbLoggedIn;
  private String currentFilter = "";

  @Override protected void onCreate(Bundle savedStateInstance) {
    setContentView(R.layout.activity_shop_invite_friends);
    super.onCreate(savedStateInstance);
    bAddFriends.setEnabled(true);
    ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);

    //getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background));

    Intent intent = getIntent();
    addedMembers = IntentExtractorHelper.getMembers(intent);
    trackingId = IntentExtractorHelper.getTrackingId(intent);
    pendingMembers = IntentExtractorHelper.getPendings(intent);

    fbApiClient = new FBApiClient();
    //  mAddMembersClient = new AddMembersClient();

    membersAdapter = new MembersAdapter(getMvpDelegate(), this);

    fbCallbackManager = CallbackManager.Factory.create();
    fbInviteDialog = new GameRequestDialog(this);
    fbInviteDialog.registerCallback(fbCallbackManager, fbInviteCallback);

    rcvMembers = (RecyclerView) findViewById(R.id.rcv_friends);

    rcvMembers.setLayoutManager(new LinearLayoutManager(this));
    rcvMembers.setAdapter(membersAdapter);

    rcvMembers.setNestedScrollingEnabled(false);
    AnalyticsHelper.reportInvite();

    allTexts = getTextViews(mainLayout);

    for (TextView v : allTexts)
      v.setTypeface(tfDin);

    //filtering
    mEditTextSearch.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() != 0) {
          mPresenter.filterFriends(mEditTextSearch.getText().toString(), toFilterList);
        }
      }

      @Override public void afterTextChanged(Editable editable) {

      }
    });

    if (!SharedPreferenceHelper.getFbId().equals("none")) {
      isFbLoggedIn = true;
      fbFilter.setAlpha(1.0f);
    }
    if (!SharedPreferenceHelper.getVkId().equals("none")) {
      isVkLoggedIn = true;
      vkFilter.setAlpha(1.0f);
    }
  }

  @Override protected void onStart() {
    super.onStart();
    // mEditGroupClient.registerListener(this);
    fbApiClient.registerListener(this);
    //mAddMembersClient.registerListener(this);
  }

  @Override protected void onStop() {
    super.onStop();
    //        mEditGroupClient.unregisterListener();
    fbApiClient.unregisterListener();
    //   mAddMembersClient.unregisterListener();
  }

  @Override protected void onResume() {
    super.onResume();

    if (!isFriendsFound) {
      getFacebookFriends();
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
      AlertDialog.Builder builder = new AlertDialog.Builder(ShopInviteFriendsActivity.this);
      builder.setMessage(getResources().getString(R.string.log_in_to_fb))
          .setTitle(getResources().getString(R.string.not_logged_in));
      builder.setPositiveButton(R.string.OK, (dialog, id) -> {
        Intent intent = new Intent(ShopInviteFriendsActivity.this, EditProfileActivity.class);
        startActivity(intent);
      });
      builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
        // User cancelled the dialog
      });

      // Create the AlertDialog
      AlertDialog dialog = builder.create();
      dialog.show();
    }
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
      AlertDialog.Builder builder = new AlertDialog.Builder(ShopInviteFriendsActivity.this);
      builder.setMessage(getResources().getString(R.string.log_in_to_vk))
          .setTitle(getResources().getString(R.string.not_logged_in));
      builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          Intent intent = new Intent(ShopInviteFriendsActivity.this, EditProfileActivity.class);
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
    Timber.e("allFriends " + allFriends);

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
    Timber.e("currentFilter " + currentFilter);
  }

  private void setFilteredFriends(List<FacebookFriend> filtered) {
    membersAdapter.setFilteredValue(filtered);
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

  @Override public void onGetFacebookFriendsSuccess(List<FacebookFriend> friends,
      List<FacebookFriend> invitable) {

    hideLoader();
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

    allFriends.clear();
    //allFriends.addAll(friends);
    allFriends.addAll(invitable);
    toFilterList.addAll(invitable);

    for (FacebookFriend friend : allFriends) {
      friend.setSocialNetwork("fb");
      for (Member member : addedMembers) {
        if (TextUtils.equals(member.getName(), friend.getName())) {
          friend.setAdded(true);
        }
      }

      for (Member member : pendingMembers) {
        if (TextUtils.equals(member.getName(), friend.getName())) {
          friend.setPending(true);
        }
      }
    }

    Collections.sort(allFriends, FacebookFriend.BY_NAME_ASC);

    setFriends(allFriends);
  }

  @Override public void onGetFacebookFriendsFailure(String message) {
    this.allFriends.clear();
    this.invitable.clear();

    setFriends(allFriends);

    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.FAILURE_GET_FACEBOOK_FRIENDS_ID).content(
          message).show();
    } else {
    }
  }

  @Override public void onGetFriendInfoSuccess(List<FacebookFriend> convertedFriends) {
    members.addAll(convertedFriends);
    addMembers(members);
    mInviteByFbIds.clear();
    mPresenter.addFriendsToShopGroup(new ArrayList<>(convertedFriends));
  }

  @Override public void onGetFriendInfoFailure(String message) {
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.FAILURE_CONVERT_FB_FRIENDS_ID).content(
          message).show();
    } else {
    }
  }

  @Override public void hideLoader() {
    findViewById(R.id.nsv_friends).setVisibility(View.VISIBLE);
    findViewById(R.id.progressBarLayout).setVisibility(View.GONE);
  }

  @Override public void updateRvFriends(List<FacebookFriend> friends) {
    membersAdapter.setValuesClearList(friends);
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

  private void sendInvitation() {

    mInviteByFbIds = new ArrayList<>();
    mIntiteByVk = new ArrayList<>();
    mIntiteByPh = new ArrayList<>();
    String id;
    for (FacebookFriend friend : membersAdapter.getSelectedMembers()) {

      id = friend.getId();
      if (friend.getSocialNetwork().equals("fb")) {
        mInviteByFbIds.add(id);
      }
      if (friend.getSocialNetwork().equals("vk")) {
        mIntiteByVk.add(friend);
      }
      if (friend.getSocialNetwork().equals("ph")) {
        mIntiteByPh.add(friend);
      }
    }

    if (!mIntiteByVk.isEmpty()) {
      Timber.e("Vk here " + mIntiteByPh.size());
      mPresenter.addFriendsToShopGroup(new ArrayList<>(mIntiteByPh));
      mIntiteByPh.clear();
    }

    if (!mInviteByFbIds.isEmpty()) {
      GameRequestContent content =
          new GameRequestContent.Builder().setMessage(getString(R.string.play_with_me))
              .setRecipients(mInviteByFbIds)
              .build();
      fbInviteDialog.show(content);
    }
    if (!mIntiteByPh.isEmpty()) {
      Timber.e("Ph here " + mIntiteByPh.size());
      mPresenter.addFriendsToShopGroup(new ArrayList<>(mIntiteByPh));
      mIntiteByPh.clear();
    }
  }

  private ArrayList<TextView> getTextViews(ViewGroup root) {
    ArrayList<TextView> views = new ArrayList<>();
    for (int i = 0; i < root.getChildCount(); i++) {
      View v = root.getChildAt(i);
      if (v instanceof TextView) {
        views.add((TextView) v);
      } else if (v instanceof ViewGroup) {
        views.addAll(getTextViews((ViewGroup) v));
      }
    }
    return views;
  }

  private void filterMembers(String filter) {
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

  private void addMembers(List<FacebookFriend> members) {

    //mAddMembersClient.addMembers(trackingId, members);
  }

  private void getFacebookFriends() {
    if (DeviceHelper.isNetworkConnected()) {
      fbApiClient.searchFriends();
    } else {
    }
  }

  private void setFriends(List<FacebookFriend> friends) {
    membersAdapter.setValue(friends);
    Collections.sort(allFriends, FacebookFriend.BY_NAME_ASC);
    if (friends.isEmpty()) {
      rcvMembers.setVisibility(View.GONE);
      //          vNoFriends.setVisibility(View.VISIBLE);
    } else {
      rcvMembers.setVisibility(View.VISIBLE);
      //            vNoFriends.setVisibility(View.GONE);
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    fbCallbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @OnClick(R.id.bAddFriends) public void bAddFriendsClicked() {
    bAddFriends.setEnabled(false);
    sendInvitation();
  }

  @OnClick(R.id.iv_back) public void bBackClicked() {
    finish();
  }

  @Override public void addPhoneContact(List<FacebookFriend> facebookFriends) {
    allFriends.addAll(facebookFriends);
    toFilterList.addAll(facebookFriends);
    membersAdapter.addPhoneContacts(facebookFriends);
  }

  @Override public void finishShopInviteActivity() {
    bAddFriends.setVisibility(View.GONE);
    if (mIntiteByPh.isEmpty() && mInviteByFbIds.isEmpty() && mIntiteByVk.isEmpty()) {
      finish();
    }
  }

  @Override public void hideAddFriendButton() {
    bAddFriends.setVisibility(View.GONE);
  }

  @Override public void showAddFriendBtn() {
    bAddFriends.setVisibility(View.VISIBLE);
  }

  @Override public void addVkFriends(List<FacebookFriend> friends) {
    allFriends.addAll(friends);
    toFilterList.addAll(friends);
    membersAdapter.addVkFriends(friends);
  }

  @Override public void showToast() {
    showToastMessage("Sent");
  }

  @Override public void loadInviterImgUrls(List<String> imgUrls) {
    for (int i = 0; i < imgUrls.size(); i++) {
      Picasso.with(this)
          .load(Uri.parse(imgUrls.get(i)))
          .fit()
          .centerCrop()
          .placeholder(R.drawable.ic_gray_avatar)
          .error(R.drawable.ic_red_avatar)
          .transform(new CropCircleTransformation(0x00ffffff, 4))
          .into(ivListInviter.get(i));
    }
  }
}
