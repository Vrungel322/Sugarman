package com.sugarman.myb.ui.activities.mentorDetail;

import android.content.Context;
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
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.models.mentor.MentorEntity;
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
import com.sugarman.myb.models.mentor.MentorsSkills;
import com.sugarman.myb.ui.views.MaskTransformation;
import java.util.ArrayList;
import java.util.List;

public class MentorDetailActivity extends BasicActivity implements IMentorDetailActivityView {

  @InjectPresenter MentorDetailActivityPresenter mPresenter;
  @BindView(R.id.iv_back) ImageView ivBack;
  @BindView(R.id.iv_avatar) ImageView ivAvatar;
  @BindView(R.id.wave1) ImageView wave1;
  @BindView(R.id.wave2) ImageView wave2;
  @BindView(R.id.wave3) ImageView wave3;
  @BindView(R.id.tv_mentor_name) TextView mentorName;
  @BindView(R.id.appCompatRatingBar) RatingBar ratingBar;
  @BindView(R.id.ll_container_layout) LinearLayout linearLayoutContainer;
  @BindView(R.id.rvFriends) RecyclerView mRecyclerViewFriends;
  @BindView(R.id.rcv_comments) RecyclerView mRecyclerViewComments;
  @BindView(R.id.llCommentsContainer) LinearLayout mCommentsContainer;
  @BindView(R.id.tvMentorPrice) TextView mentorPrice;
  @BindView(R.id.piechartSuccessRate) PieChart successRate;
  private MentorEntity mMentorEntity;
  private MentorsFriendAdapter mMentorsFriendAdapter;
  private MentorsCommentsAdapter mMentorsCommentsAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_mentor_detail);
    super.onCreate(savedInstanceState);
    mMentorEntity = getIntent().getExtras().getParcelable(MentorEntity.MENTOR_ENTITY);
    ratingBar.setRating(Float.valueOf(mMentorEntity.getMentorRating()));
    mentorName.setText(mMentorEntity.getMentorName());
    mentorPrice.setText("Apply now for " + "2$");
    List<PieEntry> entries = new ArrayList<>();

    entries.add(new PieEntry(100 - 18.5f, ""));
    entries.add(new PieEntry(18.5f, ""));


    PieDataSet set = new PieDataSet(entries, "");
    set.setColors(new int[]{0xffdc0c0c, 0xffffffff});
    set.setValueTextColor(0x00000000);
    PieData data = new PieData(set);
    successRate.getLegend().setEnabled(false);
    successRate.setDrawEntryLabels(false);
    successRate.setDrawSliceText(false);
    successRate.setDrawHoleEnabled(true);
    successRate.getDescription().setText("");
    successRate.setCenterTextSize(9);
    successRate.setDrawCenterText(true);
    successRate.setCenterText("" + (100-18.5f) + "%");
    //successRate
    successRate.setData(data);
    successRate.invalidate(); // refresh


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
  }

  @Override protected void onResume() {
    super.onResume();
    String urlAvatar = mMentorEntity.getMentorImgUrl();

    if (TextUtils.isEmpty(urlAvatar)) {
      ivAvatar.setImageResource(R.drawable.ic_red_avatar);
    } else {
      Picasso.with(this)
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
  }

  @Override public void fillCommentsList(List<MentorsCommentsEntity> mentorsCommentsEntities) {
    if(mentorsCommentsEntities.size()>0)
    mCommentsContainer.setVisibility(View.VISIBLE);
    mMentorsCommentsAdapter.setMentorsCommentsEntities(mentorsCommentsEntities);
  }

  @Override public void fillMentorsFriendsList() {
    mMentorsFriendAdapter.setMemberOfMentorsGroupEntity(mMentorEntity.getMembersOfMentorsGroup());
  }

  @OnClick(R.id.iv_back) public void onBackPressed() {
    finish();
  }
}
