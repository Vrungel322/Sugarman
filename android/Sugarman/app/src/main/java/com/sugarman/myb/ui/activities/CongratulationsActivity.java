package com.sugarman.myb.ui.activities;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;
import com.facebook.share.widget.ShareButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Group;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.listeners.ApiGetTrackingInfoListener;
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
import com.sugarman.myb.ui.views.CustomFontSpan;
import com.sugarman.myb.ui.views.StrokeImage;
import java.util.List;

public class CongratulationsActivity extends NotificationFullScreenActivity
    implements ApiGetTrackingInfoListener {

  private StrokeImage ivGroupAvatar;
  private TextView tvDescription;

  private final Callback avatarLoadCallback = new Callback() {
    @Override public void onSuccess() {
      makeScreenshot();
    }

    @Override public void onError() {
      makeScreenshot();
    }
  };

  @Override protected void onCreate(Bundle saveStateInstance) {
    setContentView(R.layout.activity_congratulations);
    super.onCreate(saveStateInstance);

    vCross = findViewById(R.id.iv_cross);
    vRoot = findViewById(R.id.ll_container_root);
    tvDescription = (TextView) findViewById(R.id.tv_congratulation_description);
    ivGroupAvatar = (StrokeImage) findViewById(R.id.iv_group_avatar);
    shareButton = (ShareButton) findViewById(R.id.btn_fb_share);

    shareButton.setOnClickListener(this);
    vCross.setOnClickListener(this);

    vRoot.setDrawingCacheEnabled(true);

    if (TextUtils.isEmpty(trackingId)) {
      makeScreenshot();
    }
  }

  @Override public void onApiGetTrackingInfoSuccess(Tracking tracking, List<MentorsCommentsEntity> commentsEntities) {
    Group group = tracking.getGroup();

    String description =
        String.format(getString(R.string.congratulations_description_template), group.getName());
    String flag1 = getString(R.string.congratulations_description_flag1);
    String flag2 = getString(R.string.congratulations_description_flag2);
    SpannableStringBuilder span = new SpannableStringBuilder(description);
    int regularStartSecond = description.indexOf(flag2);
    int boldStart = flag1.length();

    span.setSpan(new CustomFontSpan("", regular), 0, boldStart, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    span.setSpan(new CustomFontSpan("", bold), boldStart, regularStartSecond,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    span.setSpan(new CustomFontSpan("", regular), regularStartSecond, description.length(),
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    tvDescription.setText(span);

    closeProgressFragment();

    String pictureUrl = group.getPictureUrl();
    boolean isPictureExists = !TextUtils.isEmpty(pictureUrl);
    if (isPictureExists) {
      Picasso.with(this)
          .load(pictureUrl)
          .fit()
          .centerCrop()
          .error(R.drawable.ic_group)
          .placeholder(R.drawable.ic_group)
          .into(ivGroupAvatar.getImageView(), avatarLoadCallback);
    } else {
      ivGroupAvatar.setImageResource(R.drawable.ic_group);
    }

    if (!isPictureExists) {
      makeScreenshot();
    }
  }
}
