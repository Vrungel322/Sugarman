package com.sugarman.myb.adapters.membersAdapter;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.RxBus;
import com.sugarman.myb.utils.RxBusHelper;
import javax.inject.Inject;

/**
 * Created by nikita on 26.09.17.
 */
@InjectViewState
public class MembersAdapterPresenter extends BasicPresenter<IMembersAdapterView> {
  @Inject RxBus mRxBus;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void postShowAddFriendBtn() {
    mRxBus.post(new RxBusHelper.ShowAddFriendBtnEvent());
  }

  public void postHideAddFriendBtn() {
    mRxBus.post(new RxBusHelper.HideAddFriendBtnEvent());

  }
}
