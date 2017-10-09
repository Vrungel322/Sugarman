package com.sugarman.myb.base;

import android.content.Context;
import android.support.annotation.NonNull;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;
import com.google.gson.Gson;
import com.sugarman.myb.api.error.IErrorResponse;
import com.sugarman.myb.data.DataManager;
import com.sugarman.myb.utils.RxBus;
import java.io.IOException;
import javax.inject.Inject;
import okhttp3.ResponseBody;
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

  public <T extends IErrorResponse> String handleError(ResponseBody errorBody, Class<T> clazz) {
    T errorResponse = null;
    StringBuilder sb = new StringBuilder();
    try {
      errorResponse = new Gson().fromJson(errorBody.string(), clazz);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (errorResponse != null) {
      for (int i = 0; i < errorResponse.getError().size(); i++) {
        sb.append(errorResponse.getError().get(i));
      }
    }
    return sb.toString();
  }
}
