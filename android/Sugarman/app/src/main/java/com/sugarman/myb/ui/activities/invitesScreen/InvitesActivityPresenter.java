package com.sugarman.myb.ui.activities.invitesScreen;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.db.DbRepositoryInvites;
import com.sugarman.myb.utils.ThreadSchedulers;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 02.03.2018.
 */
@InjectViewState public class InvitesActivityPresenter
    extends BasicPresenter<IInvitesActivityView> {
  @Inject DbRepositoryInvites mDbRepositoryInvites;
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    fetchInvites();
  }

  private void fetchInvites() {
    getViewState().showInvites(mDbRepositoryInvites.getAllEntities());
    Subscription subscription = mDataManager.getMyInvites()
        .concatMap(
            invitesResponseResponse -> Observable.from(invitesResponseResponse.body().getResult()))
        .filter(invite -> invite.getTracking().getStatus() != Constants.STATUS_FAILED)
        .filter(invite -> invite.getTracking().getStatus() != Constants.STATUS_COMPLETED)
        .toList()
        .filter(invites -> invites!=null)
        .flatMap(invites -> {
          mDbRepositoryInvites.saveEntity(invites);
          return Observable.just(invites);
        })
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(invites -> {
          Timber.e("fetchInvites size: " + invites.size());
          getViewState().showInvites(invites);
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void declineInvitation(String inviteId) {
    Subscription subscription = mDataManager.declineInvite(inviteId)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(objectResponse -> {
          Timber.e("declineInvitation code: " + objectResponse.code());
          if (objectResponse.code() >= 200 && objectResponse.code() < 300) {
            getViewState().declineInviteAction();
          } else {
            getViewState().errorMsg(objectResponse.errorBody().toString());
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void acceptInvitation(String inviteId) {
    Subscription subscription = mDataManager.acceptInvite(inviteId)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(objectResponse -> {
          Timber.e("acceptInvitation code: " + objectResponse.code());
          if (objectResponse.code() >= 200 && objectResponse.code() < 300) {
            getViewState().acceptInviteAction();
          } else {
            getViewState().errorMsg(objectResponse.errorBody().toString());
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
