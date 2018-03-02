package com.sugarman.myb.ui.activities.invitesScreen;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 02.03.2018.
 */
@InjectViewState public class InvitesActivityPresenter
    extends BasicPresenter<IInvitesActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void declineInvitation(String inviteId) {
    Subscription subscription = mDataManager.declineInvite(inviteId)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(objectResponse -> {
          Timber.e("declineInvitation code: " + objectResponse.code());
          if (objectResponse.code() >= 200 && objectResponse.code() < 300) {
            getViewState().declineInviteAction();
          }
          else {
            getViewState().errorMsg(objectResponse.errorBody().toString());
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
