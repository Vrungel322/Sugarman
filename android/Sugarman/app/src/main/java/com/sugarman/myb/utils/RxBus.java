package com.sugarman.myb.utils;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by John on 26.01.2017.
 */

public class RxBus {

  private final PublishSubject<Object> mBusSubject;

  public RxBus() {
    mBusSubject = PublishSubject.create();
  }

  public void post(Object event) {
    mBusSubject.onNext(event);
  }

  public Observable<Object> observable() {
    return mBusSubject;
  }

  public <T> Observable<T> filteredObservable(final Class<T> eventClass) {
    return mBusSubject.ofType(eventClass);
  }

  public boolean hasObservers() {
    return mBusSubject.hasObservers();
  }
}
