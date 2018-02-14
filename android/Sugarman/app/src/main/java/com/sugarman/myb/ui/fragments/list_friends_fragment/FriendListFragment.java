package com.sugarman.myb.ui.fragments.list_friends_fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
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
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.DeviceHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import timber.log.Timber;

/**
 * Created by nikita on 19.12.2017.
 */

public class FriendListFragment extends BasicFragment implements IFriendListFragmentView {
  private static final String GROUP_NAME = "GROUP_NAME";
  @InjectPresenter FriendListFragmentPresenter mPresenter;

  @BindView(R.id.rcv_friends) RecyclerView mRecyclerViewListOfFriends;
  @BindView(R.id.et_group_name) EditText mEditTextGroupName;

  @BindView(R.id.tvInAppFbCount) TextView tvInAppFbCount;
  @BindView(R.id.tvTotalFbCount) TextView tvTotalFbCount;
  @BindView(R.id.tvInAppVkCount) TextView tvInAppVkCount;
  @BindView(R.id.tvTotalVkCount) TextView tvTotalVkCount;
  @BindView(R.id.tvInAppPhCount) TextView tvInAppPhCount;
  @BindView(R.id.tvTotalPhCount) TextView tvTotalPhCount;
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

  @SuppressLint("ValidFragment") public FriendListFragment(int layoutId) {
    super(layoutId);
  }

  public FriendListFragment() {
    super(R.layout.fragment_friend_list_test);
  }

  public static FriendListFragment newInstance(int layoutId, String groupName) {
    Bundle args = new Bundle();
    FriendListFragment fragment = new FriendListFragment(layoutId);
    args.putString(GROUP_NAME, groupName);
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
    mPresenter.filterBySocial(friendsAdapter.getAllList(), "fb");
  }

  @OnClick(R.id.phFilter) public void filterPh() {
    mPresenter.filterBySocial(friendsAdapter.getAllList(), "ph");
  }

  @OnClick(R.id.vkFilter) public void filterVk() {
    mPresenter.filterBySocial(friendsAdapter.getAllList(), "vk");
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

  private void showNoInternetConnectionDialog() {
    new SugarmanDialog.Builder(getActivity(), DialogConstants.NO_INTERNET_CONNECTION_ID).content(
        R.string.no_internet_connection).btnCallback((dialog, button) -> {
      dialog.dismiss();
    }).build().show();
  }

  @Override public void setFriendsFb(List<FacebookFriend> friends) {
    showCounters(friends, tvTotalFbCount, tvInAppFbCount);
    if (!mPendingsMembers.isEmpty() && !mAddedMembers.isEmpty()) {
      friendsAdapter.setValue(mPresenter.checkForUniqueMembers(mPendingsMembers, mAddedMembers, friends));
    }
    else {
      friendsAdapter.setValue(friends);
    }
  }

  @Override public void setFriendsVk(List<FacebookFriend> friends) {
    showCounters(friends, tvTotalVkCount, tvInAppVkCount);
    if (!mPendingsMembers.isEmpty() && !mAddedMembers.isEmpty()) {
      friendsAdapter.setValue(mPresenter.checkForUniqueMembers(mPendingsMembers, mAddedMembers, friends));
    }
    else {
      friendsAdapter.setValue(friends);
    }
  }

  @Override public void setFriendsPh(List<FacebookFriend> friends) {
    showCounters(friends, tvTotalPhCount, tvInAppPhCount);
    if (!mPendingsMembers.isEmpty() && !mAddedMembers.isEmpty()) {
      friendsAdapter.setValue(mPresenter.checkForUniqueMembers(mPendingsMembers, mAddedMembers, friends));
    }
    else {
      friendsAdapter.setValue(friends);
    }
  }

  @Override public void setFriendsFilter(List<FacebookFriend> friends) {
    if (!mPendingsMembers.isEmpty() && !mAddedMembers.isEmpty()) {
      friendsAdapter.setValue(mPresenter.checkForUniqueMembers(mPendingsMembers, mAddedMembers, friends));
    }
    else {
      friendsAdapter.setValue(friends);
    }
  }

  @Override public void setFriends(List<FacebookFriend> friends) {
    if (!mPendingsMembers.isEmpty() && !mAddedMembers.isEmpty()) {
      friendsAdapter.setValue(mPresenter.checkForUniqueMembers(mPendingsMembers, mAddedMembers, friends));
    }
    else {
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

  private void showCounters(List<FacebookFriend> friends, TextView tvTotalCount,
      TextView tvInAppCount) {
    int inAppMemberCountPh = 0;
    int totalCountPh = 0;
    for (FacebookFriend fb : friends) {
      if (fb.getIsInvitable() == FacebookFriend.CODE_NOT_INVITABLE) {
        inAppMemberCountPh++;
      }
      totalCountPh++;
    }
    tvInAppCount.setText(String.valueOf(inAppMemberCountPh));
    tvTotalCount.setText(String.valueOf(totalCountPh));
  }

  @Override public void setUpUI() {

  }

  public void checkForUniqueMembers(Member[] pendingsMembers, Member[] addedMembers) {
    Timber.e("checkForUniqueMembers pendingsMembers: "
        + pendingsMembers.length
        + " addedMembers:"
        + addedMembers.length);
    Collections.addAll(mPendingsMembers, pendingsMembers);
    Collections.addAll(mAddedMembers, addedMembers);
  }
}
