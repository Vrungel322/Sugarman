package com.sugarman.myb.ui.activities.mentorDetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.squareup.picasso.Picasso;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.models.mentor.MentorEntity;
import com.sugarman.myb.models.mentor.MentorsSkills;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.List;
import timber.log.Timber;

public class MentorDetailActivity extends BasicActivity {
  private MentorEntity mMentorEntity;

  @BindView(R.id.iv_back) ImageView ivBack;
  @BindView(R.id.iv_avatar) ImageView ivAvatar;
  @BindView(R.id.wave1) ImageView wave1;
  @BindView(R.id.wave2) ImageView wave2;
  @BindView(R.id.wave3) ImageView wave3;
  @BindView(R.id.tv_mentor_name) TextView mentorName;
  @BindView(R.id.appCompatRatingBar) RatingBar ratingBar;
  @BindView(R.id.ll_container_layout) LinearLayout linearLayoutContainer;

  @OnClick(R.id.iv_back) public void onBackPressed()
  {
    finish();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_mentor_detail);
    super.onCreate(savedInstanceState);
    mMentorEntity = getIntent().getExtras().getParcelable(MentorEntity.MENTOR_ENTITY);
    Timber.e(mMentorEntity.getMentorName() + " " + mMentorEntity.getMentorSkills().size());
    String mentorNameText = mMentorEntity.getMentorName();
    ratingBar.setRating(Float.valueOf(mMentorEntity.getMentorRating()));
    mentorName.setText(mentorNameText);
    List<MentorsSkills> skillsList =  mMentorEntity.getMentorSkills();
    LayoutInflater vi =
        (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    for(MentorsSkills skills: skillsList) {
      View v = vi.inflate(R.layout.item_mentor_skill_header, null);
      ((TextView)(v.findViewById(R.id.tv_skill_name))).setText(skills.getSkillTitle());
      linearLayoutContainer.addView(v);
      for(String s : skills.getSkills())
      {
        Timber.e("added");
        v = vi.inflate(R.layout.item_mentor_skill, null);
        ((TextView)(v.findViewById(R.id.tv_skill_name))).setText(s);
        linearLayoutContainer.addView(v);
      }
    }
    View v = vi.inflate(R.layout.item_mentor_skill_header, null);
    ((TextView)(v.findViewById(R.id.tv_skill_name))).setText("Friends in the group");
    linearLayoutContainer.addView(v);
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

    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);
    Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);
    Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);

    new Thread(new Runnable() {
      @Override public void run() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            wave1.startAnimation(animation);
          }
        });

        try {
          Thread.currentThread().sleep(700);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
          @Override public void run() {
            wave2.startAnimation(animation2);
          }
        });
        try {
          Thread.currentThread().sleep(700);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
          @Override public void run() {
            wave3.startAnimation(animation3);
          }
        });
      }
    }).start();
  }
}
