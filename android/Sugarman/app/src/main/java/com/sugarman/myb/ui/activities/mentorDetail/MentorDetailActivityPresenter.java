package com.sugarman.myb.ui.activities.mentorDetail;

import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by yegoryeriomin on 10/30/17.
 */

public class MentorDetailActivityPresenter extends BasicPresenter<IMentorDetailActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
  }
}
