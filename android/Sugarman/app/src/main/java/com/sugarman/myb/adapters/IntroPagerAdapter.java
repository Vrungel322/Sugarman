package com.sugarman.myb.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.sugarman.myb.ui.fragments.FragmentIntro;

public class IntroPagerAdapter extends FragmentPagerAdapter {

  public static final int NUM_PAGES = 4;

  public IntroPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override public Fragment getItem(int position) {
    return FragmentIntro.newInstance(position);
  }

  @Override public int getCount() {
    return NUM_PAGES;
  }
}
