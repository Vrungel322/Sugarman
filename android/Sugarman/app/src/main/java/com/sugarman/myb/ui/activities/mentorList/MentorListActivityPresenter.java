package com.sugarman.myb.ui.activities.mentorList;

import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by yegoryeriomin on 10/27/17.
 */

public class MentorListActivityPresenter extends BasicPresenter<IMentorListActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
