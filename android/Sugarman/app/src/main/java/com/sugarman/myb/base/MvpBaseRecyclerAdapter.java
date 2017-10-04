package com.sugarman.myb.base;

import android.support.v7.widget.RecyclerView;
import com.arellomobile.mvp.MvpDelegate;

/**
 * Created by John on 28.02.2017.
 */

public abstract class MvpBaseRecyclerAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  private MvpDelegate<? extends MvpBaseRecyclerAdapter> mMvpDelegate;
  private MvpDelegate<?> mParentDelegate;
  private String mChildId;

  public MvpBaseRecyclerAdapter(MvpDelegate<?> parentDelegate, String childId) {
    mParentDelegate = parentDelegate;
    mChildId = childId;

    getMvpDelegate().onCreate();
  }

  public MvpDelegate getMvpDelegate() {
    if (mMvpDelegate == null) {
      mMvpDelegate = new MvpDelegate<>(this);
      mMvpDelegate.setParentDelegate(mParentDelegate, mChildId);
    }
    return mMvpDelegate;
  }
}
