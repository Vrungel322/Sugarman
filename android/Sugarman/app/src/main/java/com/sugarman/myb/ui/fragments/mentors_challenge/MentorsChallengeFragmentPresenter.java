package com.sugarman.myb.ui.fragments.mentors_challenge;

import android.text.TextUtils;
import com.arellomobile.mvp.InjectViewState;
import com.clover_studio.spikachatmodule.models.User;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.models.chat_refactor.Message;
import com.sugarman.myb.models.chat_refactor.SeenBy;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 26.10.2017.
 */
@InjectViewState public class MentorsChallengeFragmentPresenter
    extends BasicPresenter<IMentorsChallengeFragmentView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
  }

  public void fetchMessages(String roomId, String lastMessageId, String token) {
    Timber.e(" fetchMessages token: "
        + token
        + "   lastMessageId: "
        + lastMessageId
        + "  roomId: "
        + roomId);
    if (TextUtils.isEmpty(lastMessageId)) {
      lastMessageId = "0";
    }

    User user = new User();
    user.roomID = roomId; // ->  id of room
    user.userID = SharedPreferenceHelper.getUserId(); // ->  id of user
    user.name = SharedPreferenceHelper.getUserName(); // ->  name of user
    user.avatarURL = SharedPreferenceHelper.getAvatar();//  ->  user avatar, this is optional

    Timber.e("user :" + user.toString());

    String finalLastMessageId = lastMessageId;
    Subscription subscription = mDataManager.fetchMessagesSpika(roomId, finalLastMessageId, token)
        .filter(getMessagesModelRefactored -> (getMessagesModelRefactored != null
            && getMessagesModelRefactored.getData() != null))
        .concatMap(getMessagesModelRefactored -> {
          List<String> unReadMessagesIds =
              getUnSeenMessages(getMessagesModelRefactored.getData().getMessages(),
                  SharedPreferenceHelper.getUserId());
          return Observable.just(unReadMessagesIds);
        })
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(unReadMessagesIds -> {
          Timber.e(" fetchMessages" + "   unReadMessages: " + unReadMessagesIds.size());
          getViewState().setUnreadMessages(unReadMessagesIds.size());
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  private List<String> getUnSeenMessages(List<Message> allMessages, String userID) {
    List<String> unSeenMessagesIds = new ArrayList<>();
    for (Message item : allMessages) {
      boolean seen = false;
      if (userID.equals(item.getUser().getUserID())) {
        seen = true;
      } else {
        for (SeenBy itemUser : item.getSeenBy()) {
          if (itemUser.getUser().getUserID().equals(userID)) {
            seen = true;
            continue;
          }
        }
      }
      if (!seen) {
        unSeenMessagesIds.add(item.getId());
      }
    }
    return unSeenMessagesIds;
  }
}
