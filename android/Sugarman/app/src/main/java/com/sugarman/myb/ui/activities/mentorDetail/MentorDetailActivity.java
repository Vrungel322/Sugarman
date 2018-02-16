package com.sugarman.myb.ui.activities.mentorDetail;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.models.mentor.MentorEntity;
import com.sugarman.myb.models.mentor.MentorsSkills;
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
import com.sugarman.myb.ui.activities.groupDetails.GroupDetailsActivity;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.DialogHelper;
import com.sugarman.myb.utils.ItemClickSupport;
import com.sugarman.myb.utils.inapp.IabHelper;
import com.sugarman.myb.utils.inapp.IabResult;
import com.sugarman.myb.utils.inapp.Inventory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

public class MentorDetailActivity extends BasicActivity implements IMentorDetailActivityView {
  //______________________________________________________________________
  @InjectPresenter MentorDetailActivityPresenter mPresenter;
  @BindView(R.id.iv_back) ImageView ivBack;
  @BindView(R.id.iv_avatar) ImageView ivAvatar;
  @BindView(R.id.wave1) ImageView wave1;
  @BindView(R.id.wave2) ImageView wave2;
  @BindView(R.id.wave3) ImageView wave3;
  @BindView(R.id.ivSubscribeMentor) ImageView ivSubscribeMentor;
  @BindView(R.id.tv_mentor_name) TextView mentorName;
  @BindView(R.id.appCompatRatingBar) RatingBar ratingBar;
  @BindView(R.id.ll_container_layout) LinearLayout linearLayoutContainer;
  @BindView(R.id.rvFriends) RecyclerView mRecyclerViewFriends;
  @BindView(R.id.rcv_comments) RecyclerView mRecyclerViewComments;
  @BindView(R.id.llCommentsContainer) LinearLayout mCommentsContainer;
  @BindView(R.id.tvMentorPrice) TextView mentorPrice;
  @BindView(R.id.piechartSuccessRate) PieChart successRate;
  @BindView(R.id.pcSuccessRateToday) PieChart successRateToday;
  @BindView(R.id.pcSuccessRateWeekly) PieChart successRateWeekly;
  @BindView(R.id.pcSuccessRateMonthly) PieChart successRateMonthly;
  @BindView(R.id.tvSuccessRateToday) TextView tvSuccessRateToday;
  @BindView(R.id.tvSuccessRateWeekly) TextView tvSuccessRateWeek;
  @BindView(R.id.tvSuccessRateMonthly) TextView tvSuccessRateMonth;
  @BindView(R.id.llSuccessRateContainer) LinearLayout llSuccessRateContainer;
  IabHelper mHelper;
  IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = (purchase, result) -> {
    Timber.e("mConsumeFinishedListener" + purchase.toString());
    Timber.e("mConsumeFinishedListener" + result.toString());
    if (result.isSuccess()) {
    } else {
      // handle error
    }
  };
  //______________________________________________________________________
  @BindView(R.id.llVideos) LinearLayout llVideos;
  @BindView(R.id.rvVideos) RecyclerView mRecyclerViewVideos;
  private String mFreeSku;
  IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = (result, purchase) -> {
    Timber.e("mFreeSku mPurchaseFinishedListener " + mFreeSku);

    if (result.isFailure()) {
      // Handle error
      return;
    } else if (purchase.getSku().equals(mFreeSku)) {
      consumeItem();
      Timber.e(mHelper.getMDataSignature());
    } else {
      Timber.e(result.getMessage());
    }
  };
  private MentorEntity mMentorEntity;
  //IabHelper.OnConsumeMultiFinishedListener mOnConsumeMultiFinishedListener = (purchases, results) -> {
  //
  //};
  IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener =
      new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
          Timber.e("mFreeSku mReceivedInventoryListener " + mFreeSku);

          if (result.isFailure()) {
            // Handle failure
          } else {
            mHelper.consumeAsync(inventory.getPurchase(mFreeSku), mConsumeFinishedListener);
            //mHelper.consumeAsync(inventory.getAllPurchases(), mOnConsumeMultiFinishedListener);
            Timber.e(result.getMessage());
            Timber.e(inventory.getSkuDetails(mFreeSku).getTitle());
            Timber.e(inventory.getSkuDetails(mFreeSku).getSku());

            mPresenter.checkInAppBilling(inventory.getPurchase(mFreeSku),
                inventory.getSkuDetails(mFreeSku).getTitle(), mMentorEntity.getUserId(), mFreeSku);
          }
        }
      };
  private MentorsFriendAdapter mMentorsFriendAdapter;
  private MentorsCommentsAdapter mMentorsCommentsAdapter;
  private MentorsVideosAdapter mMentorsVideosAdapter;
  private List<String> youtubeVideos;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_mentor_detail);
    super.onCreate(savedInstanceState);

    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance()
        .trackEvent(App.getInstance().getApplicationContext(), "af_open_mentor_detail", eventValue);

    mMentorEntity = getIntent().getExtras().getParcelable(MentorEntity.MENTOR_ENTITY);
    Timber.e("daily rate " + mMentorEntity.getDailySuccessRate());

    youtubeVideos = mMentorEntity.getYoutubeVideos();
    if (youtubeVideos.size() > 0) {
      llVideos.setVisibility(View.VISIBLE);
    }

    for (String s : youtubeVideos) {
      Timber.e("youtube link " + s);
    }

    ratingBar.setRating(Float.valueOf(mMentorEntity.getMentorRating()));
    mentorName.setText(mMentorEntity.getMentorName());
    if (mMentorEntity.getPrice() != null) {
      mentorPrice.setText(
          getResources().getString(R.string.apply_now) + " " + mMentorEntity.getPrice() + "$");
    } else {
      mentorPrice.setText(getResources().getString(R.string.apply_now) + " 2$");
    }

    List<PieEntry> entries = new ArrayList<>();

    entries.add(new PieEntry(mMentorEntity.getDailySuccessRate() * 100f, ""));
    entries.add(new PieEntry(100 - mMentorEntity.getDailySuccessRate() * 100f, ""));

    PieDataSet set = new PieDataSet(entries, "");

    set.setColors(new int[] { 0xffdc0c0c, 0x00000000 });
    set.setValueTextColor(0x00000000);
    PieData data = new PieData(set);
    successRate.setOnTouchListener(null);
    successRate.getLegend().setEnabled(false);
    successRate.setDrawEntryLabels(false);
    successRate.setDrawSliceText(false);
    successRate.setDrawHoleEnabled(true);
    successRate.getDescription().setText("");
    successRate.setCenterTextSize(9);
    successRate.setDrawCenterText(true);
    successRate.setCenterText("" + mMentorEntity.getDailySuccessRate() * 100f + "%");
    successRate.setData(data);
    successRate.invalidate(); // refresh

    Timber.e("  DailySuccessRate: "
        + mMentorEntity.getDailySuccessRate()
        + "  WeeklySuccessRate: "
        + mMentorEntity.getWeeklySuccessRate()
        + "  MonthlySuccessRate: "
        + mMentorEntity.getMonthlySuccessRate()
        + "  bool: "
        + (mMentorEntity.getDailySuccessRate() == 0
        && mMentorEntity.getWeeklySuccessRate() == 0
        && mMentorEntity.getMonthlySuccessRate() == 0));

    setSuccessRateData(successRateToday, mMentorEntity.getDailySuccessRate(), tvSuccessRateToday);
    setSuccessRateData(successRateWeekly, mMentorEntity.getWeeklySuccessRate(), tvSuccessRateWeek);
    setSuccessRateData(successRateMonthly, mMentorEntity.getMonthlySuccessRate(),
        tvSuccessRateMonth);

    LayoutInflater vi =
        (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    for (MentorsSkills skills : mMentorEntity.getMentorSkills()) {
      View v = vi.inflate(R.layout.item_mentor_skill_header, null);
      ((TextView) (v.findViewById(R.id.tv_skill_name))).setText(skills.getSkillTitle());
      linearLayoutContainer.addView(v);
      for (String s : skills.getSkills()) {
        v = vi.inflate(R.layout.item_mentor_skill, null);
        ((TextView) (v.findViewById(R.id.tv_skill_name))).setText(s);
        linearLayoutContainer.addView(v);
      }
    }
    mPresenter.fetchComments(mMentorEntity.getUserId());

    setupInAppPurchase();

    if (mMentorEntity.isOwned()) {
      ivSubscribeMentor.setEnabled(false);
      mentorPrice.setText(getResources().getString(R.string.mentor_already_owned));
    } else {
      ivSubscribeMentor.setEnabled(true);
    }
  }

  private void setSuccessRateData(PieChart pieChart, float successRate, TextView tvIndicator) {

    List<PieEntry> entries = new ArrayList<>();

    entries.add(new PieEntry(successRate * 100f, ""));
    entries.add(new PieEntry(100 - successRate * 100f, ""));

    PieDataSet set = new PieDataSet(entries, "");

    set.setColors(new int[] { 0xffdc0c0c, 0xffffffff });
    set.setValueTextColor(0x00000000);
    PieData data = new PieData(set);

    pieChart.setOnTouchListener(null);
    pieChart.getLegend().setEnabled(false);
    pieChart.setDrawEntryLabels(false);
    pieChart.setDrawSliceText(false);
    pieChart.setDrawHoleEnabled(false);
    pieChart.getDescription().setText("");
    pieChart.setCenterTextSize(9);
    pieChart.setDrawCenterText(false);
    pieChart.setCenterText("" + successRate * 100f + "%");
    pieChart.setData(data);
    pieChart.invalidate();

    tvIndicator.setText(tvIndicator.getText() + " - " + +successRate * 100f + "%");
  }

  private void setupInAppPurchase() {
    mHelper = new IabHelper(this, Config.BASE_64_ENCODED_PUBLIC_KEY);

    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
      public void onIabSetupFinished(IabResult result) {
        if (!result.isSuccess()) {
          Timber.e("In-app Billing setup failed: " + result);
        } else {
          Timber.e("In-app Billing is set up OK");
        }
      }
    });
    mHelper.enableDebugLogging(true);
  }

  @Override protected void onResume() {
    super.onResume();
    String urlAvatar = mMentorEntity.getMentorImgUrl();

    if (TextUtils.isEmpty(urlAvatar)) {
      ivAvatar.setImageResource(R.drawable.ic_red_avatar);
    } else {
      CustomPicasso.with(this)
          .load(urlAvatar)
          .fit()
          .centerCrop()
          .placeholder(R.drawable.ic_red_avatar)
          .transform(new MaskTransformation(this, R.drawable.profile_mask, false, 0xffff0000))
          .error(R.drawable.ic_red_avatar)
          .into(ivAvatar);
    }

    //Animate avatar
    Animation animation =
        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);
    Animation animation2 =
        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);
    Animation animation3 =
        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);

    new Thread(() -> {
      runOnUiThread(() -> wave1.startAnimation(animation));

      try {
        Thread.currentThread().sleep(700);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      runOnUiThread(() -> wave2.startAnimation(animation2));
      try {
        Thread.currentThread().sleep(700);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      runOnUiThread(() -> wave3.startAnimation(animation3));
    }).start();
  }

  @Override public void setUpUI() {
    //mentors comments
    mRecyclerViewComments.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    mMentorsCommentsAdapter = new MentorsCommentsAdapter();
    mRecyclerViewComments.setAdapter(mMentorsCommentsAdapter);

    //mentors members
    mRecyclerViewFriends.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    mMentorsFriendAdapter = new MentorsFriendAdapter();
    mRecyclerViewFriends.setAdapter(mMentorsFriendAdapter);

    //mentors videos
    mRecyclerViewVideos.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    mMentorsVideosAdapter = new MentorsVideosAdapter();
    mRecyclerViewVideos.setAdapter(mMentorsVideosAdapter);
  }

  @Override public void fillCommentsList(List<MentorsCommentsEntity> mentorsCommentsEntities) {
    if (mentorsCommentsEntities.size() > 0) mCommentsContainer.setVisibility(View.VISIBLE);
    mMentorsCommentsAdapter.setMentorsCommentsEntities(mentorsCommentsEntities);
  }

  @Override public void moveToMainActivity() {
    Intent intent = new Intent(MentorDetailActivity.this, MainActivity.class);
    //intent.putExtra(IntroActivity.CODE_IS_OPEN_LOGIN_ACTIVITY, true);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  @Override public void fillMentorsFriendsList() {
    mMentorsFriendAdapter.setMemberOfMentorsGroupEntity(mMentorEntity.getMembersOfMentorsGroup());
  }

  @Override public void fillMentorsVideosList() {
    mMentorsVideosAdapter.setMentorsVideosEntities(youtubeVideos);
    ItemClickSupport.addTo(mRecyclerViewVideos)
        .setOnItemClickListener((recyclerView, position, v) -> {

          String videoID = Uri.parse(youtubeVideos.get(position)).getQueryParameter("v");
          Intent applicationIntent =
              new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID));
          Intent browserIntent = new Intent(Intent.ACTION_VIEW,
              Uri.parse("http://www.youtube.com/watch?v=" + videoID));
          try {
            startActivity(applicationIntent);
          } catch (ActivityNotFoundException ex) {
            startActivity(browserIntent);
          }
        });
  }

  @OnClick(R.id.ivSubscribeMentor) public void ivSubscribeMentorClicked() {
    Timber.e("ivSubscribeMentorClicked mentorsId: " + mMentorEntity.getMentorId());
      mPresenter.getMentorsVendor(mMentorEntity.getMentorId(),this);
  }

  @Override public void startPurchaseFlow(String freeSku) {
    mFreeSku = freeSku;
    Timber.e("mFreeSku startPurchaseFlow " + mFreeSku);
    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance()
        .trackEvent(App.getInstance().getApplicationContext(), "af_tap_apply_for_mentor",
            eventValue);

    mHelper.launchSubscriptionPurchaseFlow(this, freeSku, 10001, mPurchaseFinishedListener,
        "mypurchasetoken");
  }

  @Override public void showAllSlotsNotEmptyDialog() {
    DialogHelper.createSimpleInfoDialog(getString(R.string.okay), getString(R.string.error),
        getString(R.string.slots_error), this, (dialogInterface, i) -> dialogInterface.dismiss())
        .create()
        .show();
  }

  @Override public void moveToMentorsDetailActivity(Tracking tracking) {
    Intent intent = new Intent(this, GroupDetailsActivity.class);
    intent.putExtra(Constants.INTENT_TRACKING_ID, tracking.getId());
    intent.putExtra("isMentorGroup", tracking.isMentors());
    intent.putExtra("mentorId", tracking.getGroupOwnerId());
    startActivityForResult(intent, Constants.GROUP_DETAILS_ACTIVITY_REQUEST_CODE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  @OnClick(R.id.iv_back) public void onBackPressed() {
    finish();
  }

  public void consumeItem() {
    mHelper.queryInventoryAsync(true, Arrays.asList(mFreeSku), mReceivedInventoryListener);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (mHelper != null) mHelper.dispose();
    mHelper = null;
  }
}
