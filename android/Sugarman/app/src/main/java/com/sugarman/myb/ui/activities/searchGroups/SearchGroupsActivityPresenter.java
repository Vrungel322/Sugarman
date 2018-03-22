package com.sugarman.myb.ui.activities.searchGroups;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by nikita on 22.03.2018.
 */
@InjectViewState public class SearchGroupsActivityPresenter
    extends BasicPresenter<ISearchGroupsActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
