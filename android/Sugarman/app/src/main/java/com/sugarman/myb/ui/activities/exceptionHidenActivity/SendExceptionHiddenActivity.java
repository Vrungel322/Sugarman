package com.sugarman.myb.ui.activities.exceptionHidenActivity;

import android.os.Bundle;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.firebase.database.FirebaseDatabase;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.handlingExceptions.ExceptionHandler;
import timber.log.Timber;

public class SendExceptionHiddenActivity extends BasicActivity implements ISendExceptionHiddenView {
  @InjectPresenter SendExceptionHiddenPresenter mPresenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_send_exception_hiden);
    super.onCreate(savedInstanceState);
    Timber.e("uncaughtException onCreate");

    String errorBody = getIntent().getExtras().getString(ExceptionHandler.ERROR_KEY);
    if (errorBody != null) {
      Timber.e("uncaughtException errorBody: " + errorBody);
      FirebaseDatabase.getInstance()
          .getReference()
          .child("remoteLoggingAndroid")
          .child("error")
          .child(SharedPreferenceHelper.getUserName() + " : " + SharedPreferenceHelper.getUserId())
          .setValue(errorBody)
          .addOnCompleteListener(task -> {
            Timber.e("uncaughtException onCompleateListener " + task.isSuccessful() + " result: " + task.getResult());
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
          });

    }
  }
}
