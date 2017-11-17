package com.sugarman.myb.ui.activities.groupDetails.adapter;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Subscription;

/**
 * Created by nikita on 06.11.2017.
 */
@InjectViewState public class GroupMembersAdapterPresenter
    extends BasicPresenter<IGroupMembersAdapterView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void deleteUser(String trackingId, String userId, int position) {
    Subscription subscription = mDataManager.deleteUser(trackingId, userId)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(voidResponse -> {
          if (voidResponse.code() == 200) {
            getViewState().removeUser(position);
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
