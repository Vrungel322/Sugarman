package com.sugarman.myb.ui.activities.groupDetails.adapter;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import rx.Observable;

/**
 * Created by nikita on 06.11.2017.
 */
@InjectViewState
public class GroupMembersAdapterPresenter extends BasicPresenter<IGroupMembersAdapterView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void test() {
    addToUnsubscription(Observable.just("stroka").subscribe(s -> getViewState().showTest(s)));
  }
}
