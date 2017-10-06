package com.sugarman.myb.ui.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Group;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.models.ChallengeWillStartItem;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WillStartChallengeFragment extends ChallengeFragment {

  private TextView tvTime;

  private long startTimestamp;
  private String timerTemplate;
  private RelativeLayout timeContainer;

  private ImageView[] images;
  private ImageView myAvatar;

  private final NumberFormat timeFormatter = new DecimalFormat("00");

  private final Handler mHandler = App.getHandlerInstance();

  private final Runnable timer = new Runnable() {
    @Override public void run() {

      long timeMs = startTimestamp - System.currentTimeMillis();
      if (timeMs <= 0) {
        refreshTrackings();
      } else {
        int days = (int) (timeMs / Constants.MS_IN_DAY);
        int hours = (int) ((timeMs % Constants.MS_IN_DAY) / Constants.MS_IN_HOUR);
        int minutes = (int) ((timeMs % Constants.MS_IN_HOUR) / Constants.MS_IN_MIN);
        int sec = (int) ((timeMs % Constants.MS_IN_MIN) / Constants.MS_IN_SEC);

        String timeFormatted =
            String.format(timerTemplate, timeFormatter.format(days), timeFormatter.format(hours),
                timeFormatter.format(minutes), timeFormatter.format(sec));

        tvTime.setText(timeFormatted);

        mHandler.postDelayed(this, Constants.MS_IN_SEC);
      }
    }
  };

  public static WillStartChallengeFragment newInstance(ChallengeWillStartItem item, int position,
      int total) {
    WillStartChallengeFragment fragment = new WillStartChallengeFragment();

    Bundle args = new Bundle();
    args.putParcelable(Constants.BUNDLE_TRACKING_ITEM, item.getTracking());
    args.putInt(Constants.BUNDLE_TRACKING_POSITION, position);
    args.putInt(Constants.BUNDLE_TRACKINS_COUNT, total);
    fragment.setArguments(args);

    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View root = super.onCreateView(inflater, container, savedInstanceState);

    images = new ImageView[6];
    myAvatar = (ImageView) root.findViewById(R.id.my_picture);

    images[0] = (ImageView) root.findViewById(R.id.image3);
    images[1] = (ImageView) root.findViewById(R.id.image4);
    images[2] = (ImageView) root.findViewById(R.id.image2);
    images[3] = (ImageView) root.findViewById(R.id.image5);
    images[4] = (ImageView) root.findViewById(R.id.image1);
    images[5] = (ImageView) root.findViewById(R.id.image6);

    if (root != null) {
      tvTime = (TextView) root.findViewById(R.id.tv_time);
      TextView tvGroupName = (TextView) root.findViewById(R.id.tv_will_group_name);
      TextView tvMembers = (TextView) root.findViewById(R.id.tv_will_members);
      timeContainer = (RelativeLayout) root.findViewById(R.id.time_container);

      if (tracking != null) {
        Group group = tracking.getGroup();

        startTimestamp = tracking.getStartUTCDate().getTime();
        timerTemplate = getString(R.string.timer_template);

        int members = tracking.getMembers().length + tracking.getPending().length;
        String membersPlural =
            getResources().getQuantityString(R.plurals.members, members, members);
        String membersFormatted =
            String.format(getString(R.string.plural_decimal_template), members, membersPlural);

        tvGroupName.setText(group.getName());
        tvMembers.setText(membersFormatted);

        ViewTreeObserver vto = timeContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            timeContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            int width = timeContainer.getMeasuredWidth();
            int height = timeContainer.getMeasuredHeight();

            if (width > 0 && height > 0) {
              Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types

              Bitmap bmp =
                  Bitmap.createBitmap(width, height, conf); // this creates a MUTABLE bitmap
              Canvas canvas = new Canvas(bmp);
              int padding = height / 35 * 2;
              Paint p = new Paint();
              p.setStrokeWidth(height / 80);
              p.setStrokeCap(Paint.Cap.ROUND);
              p.setColor(0xffff0000);

              canvas.drawLine(padding, padding, padding + height / 10, padding, p);
              canvas.drawLine(padding, padding, padding, padding + height / 10, p);

              canvas.drawLine(width - padding, padding, width - padding - height / 10, padding, p);
              canvas.drawLine(width - padding, padding, width - padding, padding + height / 10, p);

              canvas.drawLine(padding, height - padding, padding + height / 10, height - padding,
                  p);
              canvas.drawLine(padding, height - padding, padding, height - padding - height / 10,
                  p);

              canvas.drawLine(width - padding, height - padding, width - padding - height / 10,
                  height - padding, p);
              canvas.drawLine(width - padding, height - padding, width - padding,
                  height - padding - height / 10, p);

              timeContainer.setBackground(new BitmapDrawable(getActivity().getResources(), bmp));
            }
          }
        });

        List<Member> lst = new ArrayList<>(Arrays.asList(tracking.getMembers()));
        for (int i = 0; i < tracking.getPending().length; i++)
          lst.add(tracking.getPending()[i]);
        int index = 0;
        for (Member m : lst) {
          if (index < 6) {
            Picasso.with(getActivity())
                .load(m.getPictureUrl())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_gray_avatar)
                .error(R.drawable.ic_red_avatar)
                .transform(new CropCircleTransformation(0xfff, 4))
                .into(images[index]);
            index++;
          }
        }

        Picasso.with(getActivity())
            .load(group.getPictureUrl())
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_red_avatar)
            .transform(new MaskTransformation(getActivity(), R.drawable.group_avatar, false, 0xfff))
            .into(myAvatar);
      }

      vWillContainer.setVisibility(View.VISIBLE);
    }

    return root;
  }

  @Override Member[] getMembers() {
    return tracking.getMembers();
  }

  @Override Member[] getPendingMembers() {
    return tracking.getPending();
  }

  @Override public void onStart() {
    super.onStart();

    mHandler.postDelayed(timer, 0);
  }

  @Override public void onStop() {
    super.onStop();

    mHandler.removeCallbacks(timer);
  }

  private void refreshTrackings() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).refreshTrackings();
    }
  }
}
