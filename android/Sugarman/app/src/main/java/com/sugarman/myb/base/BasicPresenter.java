package com.sugarman.myb.base;

import android.content.Context;
import android.support.annotation.NonNull;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;
import com.sugarman.myb.data.DataManager;
import com.sugarman.myb.utils.RxBus;
import javax.inject.Inject;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Vrungel on 25.01.2017.
 */
public abstract class BasicPresenter<V extends MvpView> extends MvpPresenter<V> {

  @Inject protected RxBus mRxBus;
  @Inject protected DataManager mDataManager;
  @Inject protected Context mContext;

  private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

  public BasicPresenter() {
    inject();
  }

  protected void addToUnsubscription(@NonNull Subscription subscription) {
    mCompositeSubscription.add(subscription);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mCompositeSubscription.clear();
  }

  protected abstract void inject();
}
