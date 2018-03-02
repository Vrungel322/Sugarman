package com.sugarman.myb.ui.fragments.rescue_challenge;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 06.12.2017.
 */
@InjectViewState public class ChallengeRescueFragmentPresenter
    extends BasicPresenter<IChallengeRescueFragmentView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void pokeAll(Tracking tracking) {
    Subscription subscription = Observable.from(tracking.getMembers())
        .filter(member -> member.getFailureStatus() == Member.FAIL_STATUS_FAILUER)
        .filter(member -> !member.getId().equals(SharedPreferenceHelper.getUserId()))
        .concatMap(member -> Observable.just(member.getId()))
        .concatMap(id -> mDataManager.poke(id, tracking.getId()))
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(objectResponse -> {
          Timber.e("pokeAll objectResponse code " + objectResponse.code());
          if (objectResponse.isSuccessful()) {
            getViewState().superKickResponse();
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void pokeMember(Member member, Tracking tracking) {
    if (!member.getId().equals(SharedPreferenceHelper.getUserId())) {
      Subscription subscription = mDataManager.poke(member.getId(), tracking.getId())
          .compose(ThreadSchedulers.applySchedulers())
          .subscribe(objectResponse -> {
            Timber.e(" pokeMember objectResponse code " + objectResponse.code());
            if (objectResponse.isSuccessful()) {
              getViewState().superKickResponse();
            }
          }, Throwable::printStackTrace);
      addToUnsubscription(subscription);
    } else {
      getViewState().youCanNotPokeYourselfView();
    }
  }
}
