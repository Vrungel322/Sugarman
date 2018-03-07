package com.sugarman.myb.ui.fragments.list_friends_fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.widget.GameRequestDialog;
import com.sugarman.myb.R;
import com.sugarman.myb.adapters.FriendsAdapter;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.base.BasicFragment;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.ui.activities.editProfile.EditProfileActivity;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.vk.sdk.VKSdk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import timber.log.Timber;

/**
 * Created by nikita on 19.12.2017.
 */

public class FriendListFragment extends BasicFragment implements IFriendListFragmentView {
  public static final int IN_SHOP_INVITE = 1;
  public static final int IN_CREATE_GROUP = 2;
  public static final int IN_EDIT_GROUP = 3;
  private static final String GROUP_NAME = "GROUP_NAME";
  private static final String FRAGMENT_SHOWS_IN = "FRAGMEN_SHOWS_IN";
  @InjectPresenter FriendListFragmentPresenter mPresenter;

  @BindView(R.id.rcv_friends) RecyclerView mRecyclerViewListOfFriends;
  @BindView(R.id.et_group_name) EditText mEditTextGroupName;

  @BindView(R.id.tvInAppFbCount) TextView tvInAppFbCount;
  @BindView(R.id.tvTotalFbCount) TextView tvTotalFbCount;
  @BindView(R.id.tvInAppVkCount) TextView tvInAppVkCount;
  @BindView(R.id.tvTotalVkCount) TextView tvTotalVkCount;
  @BindView(R.id.tvInAppPhCount) TextView tvInAppPhCount;
  @BindView(R.id.tvTotalPhCount) TextView tvTotalPhCount;
  @BindView(R.id.fbFilter) ImageView ivFbFilter;
  @BindView(R.id.vkFilter) ImageView ivVkFilter;
  @BindView(R.id.phFilter) ImageView ivPhFilter;
  @BindView(R.id.pb) ProgressBar mProgressBar;
  // number of total count/ number of count people with app BY PH
  private int numberOfMemberTotalAppPh;
  private int numberOfMemberWithAppPh;
  // number of total count/ number of count people with app BY FB
  private int numberOfMemberTotalAppFb;
  private int numberOfMemberWithAppFb;
  // number of total count/ number of count people with app BY VK
  private int numberOfMemberTotalAppVk;
  private int numberOfMemberWithAppVk;
  private FriendsAdapter friendsAdapter;
  private CallbackManager fbCallbackManager;
  private GameRequestDialog fbInviteDialog;
  private IFriendListFragmentListener listener;
  private List<FacebookFriend> membersToSend = new ArrayList<>();
  private List<FacebookFriend> membersToSendByEditing = new ArrayList<>();
  private List<FacebookFriend> membersToSendByInviteToShop = new ArrayList<>();
  private boolean createGroupFlow;
  private boolean editGroupFlow;
  private boolean inviteToShopFlow;
  private List<Member> mPendingsMembers = new ArrayList<>();
  private List<Member> mAddedMembers = new ArrayList<>();
  private int fragmentShowsIn;

  @SuppressLint("ValidFragment") public FriendListFragment(int layoutId) {
    super(layoutId);
  }

  public FriendListFragment() {
    super(R.layout.fragment_friend_list_test);
  }

  public static FriendListFragment newInstance(int layoutId, String groupName,
      int fragmentShowsIn) {
    Bundle args = new Bundle();
    FriendListFragment fragment = new FriendListFragment(layoutId);
    args.putString(GROUP_NAME, groupName);
    args.putInt(FRAGMENT_SHOWS_IN, fragmentShowsIn);
    fragment.setArguments(args);
    return fragment;
  }

  public void setListener(IFriendListFragmentListener listener) {
    this.listener = listener;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    friendsAdapter = new FriendsAdapter(getContext());
    mRecyclerViewListOfFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
    mRecyclerViewListOfFriends.setAdapter(friendsAdapter);
    mEditTextGroupName.setText(getArguments().getString(GROUP_NAME, ""));
    fragmentShowsIn = getArguments().getInt(FRAGMENT_SHOWS_IN, 111);
    highlightConnectedSocialNetworks();
  }

  public void startCreateGroupFlow() {
    createGroupFlow = true;
    Timber.e("startCreateGroupFlow " + createGroupFlow);
    List<String> ids = new ArrayList<>();
    for (FacebookFriend fbf : friendsAdapter.getSelectedFriends()) {
      if (fbf.getIsInvitable() == FacebookFriend.CODE_INVITABLE && !fbf.getSocialNetwork()
          .equals("ph") && !fbf.getSocialNetwork().equals("vk")) {
        ids.add(fbf.getId());
        Timber.e("startCreateGroupFlow id " + fbf.getId());
      } else {
        membersToSend.add(fbf);
      }
    }

    if (ids.isEmpty()) {
      mPresenter.createGroupSendDataToServer(membersToSend);
    } else {
      convertFbInvitebleFrientdsIdsAndCreateGroup(ids);
    }
  }

  public void startEditGroupFlow() {
    editGroupFlow = true;
    Timber.e("startCreateGroupFlow " + editGroupFlow);
    List<String> ids = new ArrayList<>();
    for (FacebookFriend fbf : friendsAdapter.getSelectedFriends()) {
      if (fbf.getIsInvitable() == FacebookFriend.CODE_INVITABLE && !fbf.getSocialNetwork()
          .equals("ph") && !fbf.getSocialNetwork().equals("vk")) {
        ids.add(fbf.getId());
        Timber.e("startCreateGroupFlow id " + fbf.getId());
      } else {
        membersToSendByEditing.add(fbf);
      }
    }

    if (ids.isEmpty()) {
      mPresenter.editGroupSendDataToServer(membersToSendByEditing);
    } else {
      convertFbInvitebleFrientdsIdsAndCreateGroup(ids);
    }
  }

  public void startInvitetoShopFlow() {
    inviteToShopFlow = true;
    Timber.e("startInvitetoShopFlow " + inviteToShopFlow);
    List<String> ids = new ArrayList<>();
    for (FacebookFriend fbf : friendsAdapter.getSelectedFriends()) {
      if (fbf.getIsInvitable() == FacebookFriend.CODE_INVITABLE && !fbf.getSocialNetwork()
          .equals("ph") && !fbf.getSocialNetwork().equals("vk")) {
        ids.add(fbf.getId());
        Timber.e("startCreateGroupFlow id " + fbf.getId());
      } else {
        membersToSendByInviteToShop.add(fbf);
      }
    }

    if (ids.isEmpty()) {
      mPresenter.inviteToShopSendDataToServer(membersToSendByInviteToShop);
    } else {
      convertFbInvitebleFrientdsIdsAndCreateGroup(ids);
    }
  }

  @OnClick(R.id.fbFilter) public void filterFb() {
    if (SharedPreferenceHelper.getFacebookId().equals("none")) {
      askToConnect("fb");
    } else {
      mPresenter.filterBySocial(friendsAdapter.getAllList(), "fb");
      setSelectedFilter("fb");
    }
  }

  @OnClick(R.id.phFilter) public void filterPh() {
    mPresenter.filterBySocial(friendsAdapter.getAllList(), "ph");
    setSelectedFilter("ph");
  }

  @OnClick(R.id.vkFilter) public void filterVk() {
    if (!VKSdk.isLoggedIn()) {
      askToConnect("vk");
    } else {
      mPresenter.filterBySocial(friendsAdapter.getAllList(), "vk");
      setSelectedFilter("vk");
    }
  }

  private void setSelectedFilter(String socialTag) {
    switch (socialTag) {
      case "fb": {
        ivFbFilter.setAlpha(1f);
        ivVkFilter.setAlpha(0.5f);
        ivPhFilter.setAlpha(0.5f);
        break;
      }
      case "ph": {
        ivFbFilter.setAlpha(0.5f);
        ivVkFilter.setAlpha(0.5f);
        ivPhFilter.setAlpha(1f);
        break;
      }
      case "vk": {
        ivFbFilter.setAlpha(0.5f);
        ivVkFilter.setAlpha(1f);
        ivPhFilter.setAlpha(0.5f);
        break;
      }
    }
  }

  private void askToConnect(String socialTag) {
    String message = "";
    if (socialTag.equals("vk")) {
      message = getResources().getString(R.string.log_in_to_vk);
    }
    if (socialTag.equals("fb")) {
      message = getResources().getString(R.string.log_in_to_fb);
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(message).setTitle(getResources().getString(R.string.not_logged_in));
    builder.setPositiveButton(R.string.OK, (dialog, id) -> {
      Intent intent = new Intent(getActivity().getApplicationContext(), EditProfileActivity.class);
      startActivity(intent);
    })
        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
        .create()
        .show();
  }

  @OnTextChanged(value = R.id.et_search, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
  public void filterText(Editable s) {
    mPresenter.filterByName(friendsAdapter.getAllList(), s.toString());
  }

  /**
   * Из всех выбраных людей распределяем, если выбраный человек имеет статус invitable то кладем его
   * в колеллекцию List<String> ids, если нет то кладем его в membersToSend.
   * После прохода про данному циклу , проверяем если коллекция List<String> ids пустая то выполняем
   * mPresenter.createGroupSendDataToServer(membersToSend); сразу,
   * иначе выполняем convertFbInvitebleFrientdsIdsAndCreateGroup(List<String> ids), что в свою
   * очередь вызовет mPresenter.getFriendsInfo(recipients); дальше ->
   * onGetFriendInfoSuccess(List<FacebookFriend> convertedFriends)
   * где в membersToSend добавятся люди которые прошли через преобразования айди и произойдет
   * отправка на сервер.
   *
   * когда выбираешь друзей - всех кто имеет статус invitable нужно пропустить через этот метод, для
   * того что б преобразовать их айди в нормальный вид.
   * Передавать сюда нужно список айдишей этих друзей.
   * В методе onGetFriendInfoSuccess можно уже создавать ... добавлять в группу людей итд
   */
  private void convertFbInvitebleFrientdsIdsAndCreateGroup(List<String> ids) {
    Timber.e("loadFbFriends " + ids.get(0));
    fbCallbackManager = CallbackManager.Factory.create();
    fbInviteDialog = new GameRequestDialog(this);
    fbInviteDialog.registerCallback(fbCallbackManager,
        new FacebookCallback<GameRequestDialog.Result>() {
          @Override public void onSuccess(GameRequestDialog.Result result) {
            Timber.e("loadFbFriends onSuccess");
            List<String> recipients = result.getRequestRecipients();
            mPresenter.getFriendsInfo(recipients);
          }

          @Override public void onCancel() {

          }

          @Override public void onError(FacebookException error) {
            if (DeviceHelper.isNetworkConnected()) {
              new SugarmanDialog.Builder(getActivity(),
                  DialogConstants.FAILURE_INVITE_FB_FRIENDS_ID).content(error.getMessage()).show();
            } else {
              showNoInternetConnectionDialog();
            }
          }
        });

    if (!ids.isEmpty()) {
      GameRequestContent content =
          new GameRequestContent.Builder().setMessage(getString(R.string.play_with_me))
              .setRecipients(ids)
              .build();
      fbInviteDialog.show(content);
    }
  }

  private void highlightConnectedSocialNetworks() {
    if (!SharedPreferenceHelper.getFacebookId().equals("none")) {
      ivFbFilter.setAlpha(1.0f);
    } else {
      ivFbFilter.setAlpha(0.5f);
    }
    if (VKSdk.isLoggedIn()) {
      ivVkFilter.setAlpha(1.0f);
    } else {
      ivVkFilter.setAlpha(0.5f);
    }
  }

  private void showNoInternetConnectionDialog() {
    new SugarmanDialog.Builder(getActivity(), DialogConstants.NO_INTERNET_CONNECTION_ID).content(
        R.string.no_internet_connection).btnCallback((dialog, button) -> {
      dialog.dismiss();
    }).build().show();
  }

  @Override public void setFriendsFb(List<FacebookFriend> friends) {
    removeNotInvIfShopInvite(friends);
    showCounters(friends, tvTotalFbCount, tvInAppFbCount, "fb");
    mPresenter.saveFBCounters(friends);
    //mPresenter.saveFBMembers(friends);
    if (!mPendingsMembers.isEmpty() && !mAddedMembers.isEmpty()) {
      friendsAdapter.setValue(
          mPresenter.checkForUniqueMembers(mPendingsMembers, mAddedMembers, friends));
    } else {
      friendsAdapter.setValue(friends);
    }
  }

  @Override public void setFriendsVk(List<FacebookFriend> friends) {
    removeNotInvIfShopInvite(friends);
    showCounters(friends, tvTotalVkCount, tvInAppVkCount, "vk");
    if (!mPendingsMembers.isEmpty() && !mAddedMembers.isEmpty()) {
      friendsAdapter.setValue(
          mPresenter.checkForUniqueMembers(mPendingsMembers, mAddedMembers, friends));
    } else {
      friendsAdapter.setValue(friends);
    }
  }

  @Override public void setFriendsPh(List<FacebookFriend> friends) {
    removeNotInvIfShopInvite(friends);
    showCounters(friends, tvTotalPhCount, tvInAppPhCount, "ph");
    mPresenter.savePhCounters(friends);
    mPresenter.savePhMembers(friends);
    if (!mPendingsMembers.isEmpty() && !mAddedMembers.isEmpty()) {
      friendsAdapter.setValue(
          mPresenter.checkForUniqueMembers(mPendingsMembers, mAddedMembers, friends));
    } else {
      friendsAdapter.setValue(friends);
    }
  }

  private void removeNotInvIfShopInvite(List<FacebookFriend> friends) {
    List<FacebookFriend> temp = new ArrayList<>();
    if (fragmentShowsIn == IN_SHOP_INVITE) {
      for (int i = 0; i < friends.size(); i++) {
        if (friends.get(i).getIsInvitable() != FacebookFriend.CODE_NOT_INVITABLE) {
          temp.add(friends.get(i));
        }
      }
      friends.clear();
      friends.addAll(temp);
    }
  }

  @Override public void setFriendsFilter(List<FacebookFriend> friends) {
    if (!mPendingsMembers.isEmpty() && !mAddedMembers.isEmpty()) {
      friendsAdapter.setValue(
          mPresenter.checkForUniqueMembers(mPendingsMembers, mAddedMembers, friends));
    } else {
      friendsAdapter.setValue(friends);
    }
  }

  @Override public void setFriends(List<FacebookFriend> friends) {
    if (!mPendingsMembers.isEmpty() && !mAddedMembers.isEmpty()) {
      friendsAdapter.setValue(
          mPresenter.checkForUniqueMembers(mPendingsMembers, mAddedMembers, friends));
    } else {
      friendsAdapter.setValue(friends);
    }
  }

  @Override public void onGetFriendInfoFailure(String message) {

    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(getActivity(),
          DialogConstants.FAILURE_CONVERT_FB_FRIENDS_ID).content(message).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onGetFriendInfoSuccess(List<FacebookFriend> convertedFriends) {
    if (editGroupFlow) {
      membersToSendByEditing.addAll(convertedFriends);
      mPresenter.editGroupSendDataToServer(membersToSendByEditing);
    }
    if (createGroupFlow) {
      membersToSend.addAll(convertedFriends);
      mPresenter.createGroupSendDataToServer(membersToSend);
    }
  }

  @Override public void createGroupViaListener(List<FacebookFriend> toSendList) {
    Timber.e("createGroupViaListener " + toSendList.size());
    listener.createGroup(toSendList, mEditTextGroupName.getText().toString());
  }

  @Override public void editGroupViaListener(List<FacebookFriend> membersToSendByEditing) {
    Timber.e("editGroupViaListener " + membersToSendByEditing.size());
    listener.editGroup(membersToSendByEditing, mEditTextGroupName.getText().toString());
  }

  @Override public void inviteToShopViaListener(List<FacebookFriend> membersToSendByInviteToShop) {
    Timber.e("inviteToShopViaListener " + membersToSendByInviteToShop.size());
    listener.inviteFriendToShop(membersToSendByInviteToShop);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    listener = null;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    fbCallbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void showFBCounters(List<FacebookFriend> friends) {
    showCounters(friends, tvTotalFbCount, tvInAppFbCount, "fb");
  }

  @Override public void showPHCounters(List<FacebookFriend> friends) {
    showCounters(friends, tvTotalPhCount, tvInAppPhCount, "ph");
  }

  private void showCounters(List<FacebookFriend> friends, TextView tvTotalCount,
      TextView tvInAppCount, String socialTag) {
    if (friends != null) {
      int inAppMemberCountPh = 0;
      int totalCountPh = 0;
      for (FacebookFriend fb : friends) {
        if (fb.getIsInvitable() == FacebookFriend.CODE_NOT_INVITABLE && fb.getSocialNetwork()
            .equals(socialTag)) {
          inAppMemberCountPh++;
        }
        if (fb.getSocialNetwork().equals(socialTag)) {
          totalCountPh++;
        }
      }
      tvInAppCount.setText(String.valueOf(inAppMemberCountPh));
      tvTotalCount.setText(String.valueOf(totalCountPh));
    }
  }

  @Override public void setUpUI() {
    if (getArguments().getString(GROUP_NAME, "").isEmpty()) {
      mEditTextGroupName.setText(String.format(getString(R.string.group_name_template),
          SharedPreferenceHelper.getUserName()));
    }
  }

  public void checkForUniqueMembers(Member[] pendingsMembers, Member[] addedMembers) {
    Timber.e("checkForUniqueMembers pendingsMembers: "
        + pendingsMembers.length
        + " addedMembers:"
        + addedMembers.length);
    Collections.addAll(mPendingsMembers, pendingsMembers);
    Collections.addAll(mAddedMembers, addedMembers);
  }

  public void hideProgress() {
    mProgressBar.setVisibility(View.GONE);
  }

  public void showProgress() {
    mProgressBar.setVisibility(View.VISIBLE);
  }
}
