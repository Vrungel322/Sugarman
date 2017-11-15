package com.sugarman.myb.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.rd.PageIndicatorView;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.adapters.IntroPagerAdapter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.utils.IntentExtractorHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.HashMap;
import java.util.Map;

public class IntroActivity extends BaseActivity implements View.OnClickListener {

  private Intent intent;
  ViewPager viewPager;
  public static final String CODE_IS_OPEN_LOGIN_ACTIVITY = "code_is_open_login_activity";
  private PageIndicatorView piv;
  ImageView nextButton;
  TextView backButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_intro);
    super.onCreate(savedInstanceState);

    backButton = (TextView) findViewById(R.id.arrow_prev_page);
    viewPager = (ViewPager) findViewById(R.id.viewpager_intro);
    final IntroPagerAdapter adapter = new IntroPagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(adapter);
    piv = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
    //piv.setSelection(3);
    //piv.setStrokeWidth(50);

    nextButton = (ImageView) findViewById(R.id.arrow_next_page);
    findViewById(R.id.bg_intro).setOnClickListener(this);
    nextButton.setOnClickListener(this);
    intent = getIntent();
    if (this.intent != null && intent.getBooleanExtra(CODE_IS_OPEN_LOGIN_ACTIVITY, false)) {
      backButton.setText(getString(R.string.skip));
    }
    backButton.setOnClickListener(this);
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case (R.id.bg_intro):
        //goToNextScreen();
        break;
      case (R.id.arrow_next_page):
        System.out.println("Pressed next page");
        if (viewPager.getCurrentItem() == 3) {
          goToNextScreen();
          break;
        } else {
          viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
          break;
        }

      case (R.id.arrow_prev_page):
        System.out.println("Pressed prev page");
        skipOrClose();
        break;
      default:
        break;
    }
  }

  public void goToNextScreen() {
    SharedPreferenceHelper.showedIntro();

    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance().trackEvent(App.getInstance().getApplicationContext(), "af_finish_tutorial", eventValue);

    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.putExtra(Constants.INTENT_MY_TRACKINGS, IntentExtractorHelper.getTrackings(this.intent));
    intent.putExtra(Constants.INTENT_MY_INVITES, IntentExtractorHelper.getInvites(this.intent));
    intent.putExtra(Constants.INTENT_MY_REQUESTS, IntentExtractorHelper.getRequests(this.intent));
    intent.putExtra(Constants.INTENT_MY_NOTIFICATIONS,
        IntentExtractorHelper.getNotifications(this.intent));
    startActivity(intent);
  }

  public void skipOrClose() {
    SharedPreferenceHelper.showedIntro();


    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance().trackEvent(App.getInstance().getApplicationContext(), "af_skip_tutorial", eventValue);


    if (this.intent != null && intent.getBooleanExtra(CODE_IS_OPEN_LOGIN_ACTIVITY, false)) {
      Intent intent = new Intent(IntroActivity.this, MainActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      intent.putExtra(Constants.INTENT_MY_TRACKINGS,
          IntentExtractorHelper.getTrackings(this.intent));
      intent.putExtra(Constants.INTENT_MY_INVITES, IntentExtractorHelper.getInvites(this.intent));
      intent.putExtra(Constants.INTENT_MY_REQUESTS, IntentExtractorHelper.getRequests(this.intent));
      intent.putExtra(Constants.INTENT_MY_NOTIFICATIONS,
          IntentExtractorHelper.getNotifications(this.intent));
      startActivity(intent);
    } else {
      finish();
    }
  }

  @Override public void onBackPressed() {
    super.onBackPressed();  // optional depending on your needs
  }
}
