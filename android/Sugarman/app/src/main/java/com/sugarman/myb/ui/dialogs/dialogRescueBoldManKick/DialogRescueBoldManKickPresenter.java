package com.sugarman.myb.ui.dialogs.dialogRescueBoldManKick;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.util.List;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 14.12.2017.
 */
@InjectViewState public class DialogRescueBoldManKickPresenter
    extends BasicPresenter<IDialogRescueBoldManKickView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void superPoke(List<Member> failures, String trackingId) {
    Subscription subscription = Observable.from(failures)
        .filter(member -> member.getFailureStatus() == Member.FAIL_STATUS_FAILUER)
        .concatMap(member -> Observable.just(member.getId()))
        .concatMap(id -> mDataManager.poke(id, trackingId))
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(objectResponse -> {
          Timber.e("objectResponse code " + objectResponse.code());
          if (objectResponse.isSuccessful()) {
            getViewState().superKickResponse();
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
