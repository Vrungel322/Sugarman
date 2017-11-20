package com.sugarman.myb.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.share.widget.ShareButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.clients.RecreateGroupClient;
import com.sugarman.myb.api.models.responses.Group;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.listeners.ApiRecreateGroupListener;
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
import com.sugarman.myb.ui.views.CustomFontSpan;
import com.sugarman.myb.ui.views.CustomFontTextView;
import com.sugarman.myb.ui.views.StrokeImage;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FailedActivity extends NotificationFullScreenActivity
    implements ApiRecreateGroupListener {

  private TextView tvDescription;
  private LinearLayout llRecreate;

  private StrokeImage[] avatars;
  private View[] vDividers;
  private CustomFontTextView[] tvNames;

  private int countAvatars;
  private int loadedImagesCount;

  private RecreateGroupClient mRecreateGroupClient;

  private final Callback avatarLoadCallback = new Callback() {
    @Override public void onSuccess() {
      loadedImagesCount++;
      if (loadedImagesCount == countAvatars) {
        makeScreenshot();
      }
    }

    @Override public void onError() {
      loadedImagesCount++;
      if (loadedImagesCount == countAvatars) {
        makeScreenshot();
      }
    }
  };

  @Override protected void onCreate(Bundle saveStateInstance) {
    setContentView(R.layout.activity_failed);
    super.onCreate(saveStateInstance);

    View vDividerFirst = findViewById(R.id.v_divider_first);
    View vDividerSecond = findViewById(R.id.v_divider_second);
    StrokeImage ivFirstAvatar = (StrokeImage) findViewById(R.id.iv_avatar_first);
    StrokeImage ivSecondAvatar = (StrokeImage) findViewById(R.id.iv_avatar_second);
    StrokeImage ivThirdAvatar = (StrokeImage) findViewById(R.id.iv_avatar_third);
    CustomFontTextView tvFirstName = (CustomFontTextView) findViewById(R.id.tv_name_first);
    CustomFontTextView tvSecondName = (CustomFontTextView) findViewById(R.id.tv_name_second);
    CustomFontTextView tvThirdName = (CustomFontTextView) findViewById(R.id.tv_name_third);
    vCross = findViewById(R.id.iv_cross);
    vRoot = findViewById(R.id.ll_container_root);
    tvDescription = (TextView) findViewById(R.id.tv_failed_description);
    shareButton = (ShareButton) findViewById(R.id.btn_fb_share);
    llRecreate = (LinearLayout) findViewById(R.id.ll_recreate);
    llRecreate.setOnClickListener(this);

    avatars = new StrokeImage[] { ivFirstAvatar, ivSecondAvatar, ivThirdAvatar };
    vDividers = new View[] { vDividerFirst, vDividerSecond };
    tvNames = new CustomFontTextView[] { tvFirstName, tvSecondName, tvThirdName };

    shareButton.setOnClickListener(this);
    vCross.setOnClickListener(this);

    mRecreateGroupClient = new RecreateGroupClient();

    vRoot.setDrawingCacheEnabled(true);

    if (TextUtils.isEmpty(trackingId)) {
      for (View avatar : avatars) {
        avatar.setVisibility(View.VISIBLE);
      }

      for (View divider : vDividers) {
        divider.setVisibility(View.VISIBLE);
      }

      makeScreenshot();
    }
  }

  @Override protected void onStart() {
    super.onStart();

    if (mRecreateGroupClient != null) {
      mRecreateGroupClient.registerListener(this);
    }
  }

  @Override protected void onStop() {
    super.onStop();

    if (mRecreateGroupClient != null) {
      mRecreateGroupClient.unregisterListener();
    }
  }

  @Override public void onClick(View v) {
    super.onClick(v);
    int id = v.getId();
    switch (id) {
      case R.id.ll_recreate:
        showProgressFragmentTemp();
        mRecreateGroupClient.recreate(trackingId);
        break;
      default:
        break;
    }
  }

  @Override public void onApiGetTrackingInfoSuccess(Tracking tracking, List<MentorsCommentsEntity> commentsEntities) {
    Group group = tracking.getGroup();
    List<Member> members = new ArrayList<>(Arrays.asList(tracking.getFailingMembers()));
    List<Member> notFailingMembers =
        new ArrayList<>(Arrays.asList(tracking.getNotFailingMembers()));
    Collections.sort(members, Member.BY_STEPS_ASC);
    for (Iterator<Member> iterator = members.iterator(); iterator.hasNext(); ) {
      Member member = iterator.next();
      if (member.getSteps() >= Config.MAX_STEPS_PER_DAY) {
        iterator.remove();
      }
    }

    for (int i = 0; i < avatars.length; i++) {
      Member member = i < members.size() ? members.get(i) : null;
      if (member == null) {
        avatars[i].setVisibility(View.GONE);
        if (i - 1 < vDividers.length && i - 1 >= 0) {
          vDividers[i - 1].setVisibility(View.GONE);
        }
      } else {
        countAvatars++;
        if (i < vDividers.length) {
          vDividers[i].setVisibility(View.VISIBLE);
        }

        StrokeImage avatar = avatars[i];
        avatar.setVisibility(View.VISIBLE);
        CustomFontTextView tvName = tvNames[i];
        tvName.setVisibility(View.VISIBLE);

        String pictureUrl = member.getPictureUrl();
        String name = member.getName();
        if (TextUtils.isEmpty(name)) {
          tvName.setText("");
        } else {
          tvName.setText(name);
        }
        if (TextUtils.isEmpty(pictureUrl)) {
          avatar.setImageResource(R.drawable.ic_red_avatar);
          loadedImagesCount++;
        } else {
          CustomPicasso.with(this)
              .load(pictureUrl)
              .fit()
              .centerCrop()
              .placeholder(R.drawable.ic_red_avatar)
              .error(R.drawable.ic_red_avatar)
              .into(avatar.getImageView(), avatarLoadCallback);
        }
      }
    }

    int darkGray = ContextCompat.getColor(this, R.color.dark_gray);

    String description =
        String.format(getString(R.string.failed_thanks_to_bears_template), group.getName());
    String flag1 = getString(R.string.failed_thanks_to_bears_left);
    String flag2 = getString(R.string.failed_thanks_to_bears_right);
    SpannableStringBuilder span = new SpannableStringBuilder(description);
    int regularStartSecond = description.indexOf(flag2);
    int boldStart = flag1.length();

    span.setSpan(new CustomFontSpan("", regular), 0, boldStart, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    span.setSpan(new CustomFontSpan("", bold), boldStart, regularStartSecond,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    span.setSpan(new CustomFontSpan("", regular), regularStartSecond, description.length(),
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    span.setSpan(new ForegroundColorSpan(darkGray), boldStart, regularStartSecond,
        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

    tvDescription.setText(span);

    if (TextUtils.equals(tracking.getGroupOwnerId(), SharedPreferenceHelper.getUserId())
        && notFailingMembers.size() > 1
        && iIsNotFailing(notFailingMembers)) {
      llRecreate.setVisibility(View.VISIBLE);
    }

    closeProgressFragment();

    if (loadedImagesCount == countAvatars) {
      makeScreenshot();
    }
  }

  @Override public void onApiRecreateSuccess() {
    closeProgressFragment();
    closeScreenAndRefreshTrackings();
    finish();
  }

  @Override public void onApiRecreateFailure(String message) {
    closeProgressFragment();
    closeScreenAndRefreshTrackings();
    finish();
  }

  private void closeScreenAndRefreshTrackings() {
    Intent data = new Intent();
    setResult(RESULT_OK, data);
    finish();
  }

  private boolean iIsNotFailing(List<Member> notFailingMembers) {
    String userId = SharedPreferenceHelper.getUserId();
    if (!TextUtils.isEmpty(userId)) {
      for (int i = 0; i < notFailingMembers.size(); i++) {
        if (TextUtils.equals(notFailingMembers.get(i).getId(), userId)) {
          return true;
        }
      }
      return false;
    } else {
      return false;
    }
  }
}