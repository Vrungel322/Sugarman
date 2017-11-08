package com.sugarman.myb.ui.fragments.mentors_challenge;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.base.BasicFragment;
import com.sugarman.myb.models.ChallengeMentorItem;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.ui.fragments.no_mentors_challenge.NoMentorsChallengeFragmentPresenter;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import com.sugarman.myb.ui.views.CropSquareTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import java.util.Arrays;
import java.util.Locale;
import timber.log.Timber;

/**
 * Created by nikita on 26.10.2017.
 */

public class MentorsChallengeFragment extends BasicFragment
    implements IMentorsChallengeFragmentView {
  private static final String MENTOR_CHALLENGE = "MENTOR_CHALLENGE";
  @InjectPresenter NoMentorsChallengeFragmentPresenter mPresenter;

  @BindView(R.id.group_avatar) ImageView mImageViewGroupAvatar;
  @BindView(R.id.tv_avatar_events) TextView mTextViewNumAvatarEvents;
  @BindView(R.id.tv_group_name) TextView mTextViewGroupName;
  @BindView(R.id.best_name) TextView mTextViewBestName;
  @BindView(R.id.best_steps) TextView mTextViewBestSteps;
  @BindView(R.id.iv_best_avatar) ImageView mImageViewBestAvatar;
  @BindView(R.id.indicator_best) ImageView mImageViewIndicatorBest;
  @BindView(R.id.fastest_name) TextView mTextViewFastestName;
  @BindView(R.id.fastest_steps) TextView mTextViewFastestSteps;
  @BindView(R.id.iv_fastest_avatar) ImageView mImageViewFastestAvatar;
  @BindView(R.id.indicator_fastest) ImageView mImageViewIndicatorFastest;
  @BindView(R.id.laziest_name) TextView mTextViewLaziestName;
  @BindView(R.id.laziest_steps) TextView mTextViewLaziestSteps;
  @BindView(R.id.iv_laziest_avatar) ImageView mImageViewLaziestAvatar;
  @BindView(R.id.indicator_laziest) ImageView mImageViewLaziestIndicator;
  @BindView(R.id.all_name) TextView mTextViewAllName;
  @BindView(R.id.all_steps) TextView mTextViewAllSteps;
  @BindView(R.id.iv_all_avatar) ImageView mImageViewAllAvatar;
  @BindView(R.id.indicator_all) ImageView mImageViewIndicatorAll;
  @BindView(R.id.iv_broken_avatar_first) ImageView mImageViewBrocenAvatarFirst;
  @BindView(R.id.iv_broken_avatar_second) ImageView mImageViewBrocenAvatarSecond;
  @BindView(R.id.iv_broken_avatar_third) ImageView mImageViewBrocenAvatarThird;
  @BindView(R.id.iv_broken_avatar_fourth) ImageView mImageViewBrocenAvatarFourth;
  @BindView(R.id.progress_strip) ImageView mImageViewProgressStripe;
  @BindView(R.id.steps_total) TextView mTextViewTotalSteps;
  private ChallengeMentorItem mChallengeItem;
  private Tracking mTracking;
  private Member[] mMembers;
  private int mAllSteps;
  CardView vChallengeContainer;

  public MentorsChallengeFragment() {
    super(R.layout.fragment_mentor_challenge);
  }

  public static MentorsChallengeFragment newInstance(ChallengeMentorItem item) {
    Bundle args = new Bundle();
    args.putParcelable(MENTOR_CHALLENGE, item);
    MentorsChallengeFragment fragment = new MentorsChallengeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mChallengeItem = getArguments().getParcelable(MENTOR_CHALLENGE);
    mTracking = mChallengeItem.getTracking();
    Timber.e(mChallengeItem.getTracking().getChallengeName());
    vChallengeContainer = (CardView) view.findViewById(R.id.cv_mentor_challenge_container);


    Picasso.with(getActivity())
        .load(mTracking.getGroup().getPictureUrl())
        .placeholder(R.drawable.ic_gray_avatar)
        .error(R.drawable.ic_red_avatar)
        .transform(new CropSquareTransformation())
        .transform(new MaskTransformation(getActivity(), R.drawable.group_avatar, false, 0xfff))
        .into(mImageViewGroupAvatar);

    mTextViewGroupName.setText(mTracking.getGroup().getName());

    //set up best
    Arrays.sort(mTracking.getMembers(), Member.BY_STEPS_ASC);
    mMembers = mTracking.getMembers();
    Member best;
    if(mMembers.length>0) {
      best = mMembers[mMembers.length - 1];

      String str = "";
      str = best.getName() == null ? "" : best.getName();
      Timber.e("Best " + best.getName());
      if (str.contains(" ")) str = str.replaceAll("( +)", " ").trim();

      String name = str;
      if (str.length() > 0 && str.contains(" ")) {
        name = str.substring(0, (best.getName().indexOf(" ")));
      } else {
        name = str;
      }

      mTextViewBestName.setText(name);
      mTextViewBestSteps.setText(String.format(Locale.US, "%,d", best.getSteps()));

      Picasso.with(getActivity())
          .load(best.getPictureUrl())
          .placeholder(R.drawable.ic_gray_avatar)
          .error(R.drawable.ic_red_avatar)
          .transform(new CropSquareTransformation())
          .transform(new CropCircleTransformation(0xffff0000, 1))
          .into(mImageViewBestAvatar);

      //set up fastest
      if (mTracking.hasDailyWinner()) {
        str = mTracking.getDailySugarman().getUser().getName();
        name = "";
        str = str.replaceAll("( +)", " ").trim();
        if (str.length() > 0)
          name = str.substring(0, (mTracking.getDailySugarman().getUser().getName().indexOf(" ")));
        else name = str;

        mTextViewFastestName.setText(name);
        mTextViewFastestSteps.setText(
            String.format(Locale.US, "%,d", mTracking.getDailySugarman().getUser().getSteps()));
        Picasso.with(getActivity())
            .load(mTracking.getDailySugarman().getUser().getPictureUrl())
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_red_avatar)
            .transform(new CropSquareTransformation())
            .transform(new CropCircleTransformation(0xffff0000, 1))
            .into(mImageViewFastestAvatar);
      } else {
        mTextViewFastestName.setText(getResources().getString(R.string.sugarman_is));
        mTextViewFastestSteps.setText(getResources().getString(R.string.todays_fastest));
        Picasso.with(getActivity())
            .load(R.drawable.sugar_next)
            //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_red_avatar)
            .transform(new CropCircleTransformation(0xffff0000, 1))
            .into(mImageViewFastestAvatar);
      }

      //set up laziest

      Member laziest = mMembers[0];
      str = laziest.getName();
      str = str.replaceAll("( +)", " ").trim();
      if (str.length() > 0 && str.contains(" ")) {
        name = str.substring(0, (laziest.getName().indexOf(" ")));
      } else name = str;
      mTextViewLaziestName.setText(name);
      mTextViewLaziestSteps.setText(String.format(Locale.US, "%,d", mMembers[0].getSteps()));
      Picasso.with(getActivity())
          .load(mMembers[0].getPictureUrl())
          .placeholder(R.drawable.ic_gray_avatar)
          .error(R.drawable.ic_red_avatar)
          .transform(new CropSquareTransformation())
          .transform(new CropCircleTransformation(0xffff0000, 1))
          .into(mImageViewLaziestAvatar);
    }
    //set up all
    setToUiAllSteps();
    Picasso.with(getActivity())
        .load(R.drawable.white_bg)
        //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
        .placeholder(R.drawable.ic_gray_avatar)
        .error(R.drawable.ic_red_avatar)
        .transform(new CropCircleTransformation(0xffff0000, 1))
        .into(mImageViewAllAvatar);

    //progress stripe
    //mImageViewProgressStripe.getViewTreeObserver()
    //    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
    //      @Override public void onGlobalLayout() {
    //        int width = mImageViewProgressStripe.getMeasuredWidth();
    //        int height = mImageViewProgressStripe.getMeasuredHeight();
    //        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
    //        if (width > 0 && height > 0) {
    //          Bitmap bmp =
    //              Bitmap.createBitmap(width, height, conf); // this creates a MUTABLE bitmap
    //          Canvas canvas = new Canvas(bmp);
    //          Paint p = new Paint();
    //          p.setStrokeWidth(height / 3 * 2);
    //          p.setStrokeCap(Paint.Cap.ROUND);
    //          p.setStyle(Paint.Style.STROKE);
    //
    //          p.setColor(0xffE5E5E5);
    //          canvas.drawLine(0, height / 2, width, height / 2, p);
    //          p.setColor(0xffFD3E3E);
    //
    //          setToUiAllSteps();
    //          mTextViewTotalSteps.setText(
    //              "" + String.format(Locale.US, "%,d", mAllSteps) + "/" + String.format(Locale.US,
    //                  "%,d", (mMembers.length * 10000)));
    //
    //          float drawto =
    //              width * (float) ((float) mAllSteps / (float) (mMembers.length * 10000));
    //          Log.e("drawTo", "" + drawto);
    //          canvas.drawLine(0, height / 2, drawto, height / 2, p);
    //
    //          mImageViewProgressStripe.setImageBitmap(bmp);
    //          mImageViewProgressStripe.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    //
    //        }
    //      }
    //    });
  }

  @OnClick(R.id.cv_mentor_challenge_container) public void cvMentorChallengeClicked() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).openGroupDetailsActivity(mTracking.getId(),true,mTracking.getGroupOwnerId());
    }
  }

  private void setToUiAllSteps() {
    mTextViewAllName.setText(
        String.valueOf(mMembers.length) + " " + getResources().getString(R.string.users));
    mAllSteps = 0;
    for (int i = 0; i < mMembers.length; i++) {
      mAllSteps += mMembers[i].getSteps();
    }
    mTextViewAllSteps.setText(String.format(Locale.US, "%,d",mAllSteps));
  }

}
