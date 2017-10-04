package com.sugarman.myb.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.sugarman.myb.App;
import javax.inject.Inject;

/**
 * Created by John on 27.01.2017.
 */

public abstract class BasicActivity extends MvpAppCompatActivity {

  @Inject protected Context mContext;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    App.getAppComponent().inject(this);
  }

  protected void showToastMessage(String message) {
    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
  }

  protected void showToastMessage(@StringRes int id) {
    Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
  }
}
