package com.sugarman.myb.ui.activities.mentorList;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.models.mentor.MentorEntity;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.Subscription;

/**
 * Created by yegoryeriomin on 10/27/17.
 */
@InjectViewState public class MentorListActivityPresenter
    extends BasicPresenter<IMentorListActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    getViewState().setUpUI();
    List<MentorEntity> cachedMentors = mDataManager.getCachedMentors();
    if (cachedMentors!=null && !cachedMentors.isEmpty()){
      getViewState().fillMentorsList(cachedMentors);
    }
    fetchMentors();
  }

  private void fetchMentors() {
    Subscription subscription = mDataManager.fetchMentors()
        .filter(mentorStupidAbstraction -> mentorStupidAbstraction.getMentorEntities() != null)
        .concatMap(mentorStupidAbstraction -> rx.Observable.just(
            mentorStupidAbstraction.getMentorEntities()))
        .concatMap(mentorEntities -> {
          Collections.sort(mentorEntities, (mentorEntity, t1) -> Float.valueOf(t1.getMentorRating())
              .compareTo(Float.valueOf(mentorEntity.getMentorRating())));
          return Observable.just(mentorEntities);
        })
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(mentorEntities -> {
          mDataManager.cacheMentors(mentorEntities);
          getViewState().fillMentorsList(mentorEntities);
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
