package com.sugarman.myb.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.clients.GetHighScoreClient;
import com.sugarman.myb.api.models.responses.Group;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.me.score.HighScore;
import com.sugarman.myb.api.models.responses.me.score.HighScores;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.listeners.ApiGetHighScoresListener;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.views.StrokeImage;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;

public class HighScoreActivity extends BaseActivity
    implements View.OnClickListener, ApiGetHighScoresListener {

  private static final String TAG = HighScoreActivity.class.getName();

  private View vDividerCreated;
  private View vNothingShowCreated;
  private StrokeImage ivGroupAvatarCreated;
  private TextView tvGroupNameCreated;
  private TextView tvGroupMembersCreated;
  private TextView tvGroupKickedCreated;
  private TextView tvGroupPositionCreated;

  private View vDividerParticipated;
  private View vNothingShowParticipated;
  private StrokeImage ivGroupAvatarParticipated;
  private TextView tvGroupNameParticipated;
  private TextView tvGroupMembersParticipated;
  private TextView tvGroupKickedParticipated;
  private TextView tvGroupPositionParticipated;

  private String membersTemplate;
  private String positionTemplate;

  private GetHighScoreClient mGetHighScoreClient;

  private final Callback createdCallback = new Callback() {
    @Override public void onSuccess() {
      ivGroupAvatarCreated.setBackgroundResource(R.drawable.dark_gray_double_stroke_background);
    }

    @Override public void onError() {
      // nothing
    }
  };

  private final Callback participatedCallback = new Callback() {
    @Override public void onSuccess() {
      ivGroupAvatarParticipated.setBackgroundResource(
          R.drawable.dark_gray_double_stroke_background);
    }

    @Override public void onError() {
      // nothing
    }
  };

  @Override protected void onCreate(Bundle saveStateInstance) {
    setContentView(R.layout.activity_high_score);
    super.onCreate(saveStateInstance);

    View vBack = findViewById(R.id.iv_back);

    vNothingShowCreated = findViewById(R.id.tv_nothing_to_show_created);
    vDividerCreated = findViewById(R.id.v_divider_group_created);
    ivGroupAvatarCreated = (StrokeImage) findViewById(R.id.iv_group_avatar_created);
    tvGroupNameCreated = (TextView) findViewById(R.id.tv_group_name_created);
    tvGroupMembersCreated = (TextView) findViewById(R.id.tv_group_members_created);
    tvGroupKickedCreated = (TextView) findViewById(R.id.tv_group_kicked_created);
    tvGroupPositionCreated = (TextView) findViewById(R.id.tv_group_position_created);

    vNothingShowParticipated = findViewById(R.id.tv_nothing_to_show_participated);
    vDividerParticipated = findViewById(R.id.v_divider_group_participated);
    ivGroupAvatarParticipated = (StrokeImage) findViewById(R.id.iv_group_avatar_participated);
    tvGroupNameParticipated = (TextView) findViewById(R.id.tv_group_name_participated);
    tvGroupMembersParticipated = (TextView) findViewById(R.id.tv_group_members_participated);
    tvGroupKickedParticipated = (TextView) findViewById(R.id.tv_group_kicked_participated);
    tvGroupPositionParticipated = (TextView) findViewById(R.id.tv_group_position_participated);

    membersTemplate = getString(R.string.plural_decimal_template);
    positionTemplate = getString(R.string.group_position_template);

    vBack.setOnClickListener(this);

    initCreatedGroup(null);
    initParticipatedGroup(null);

    mGetHighScoreClient = new GetHighScoreClient();
  }

  @Override protected void onResume() {
    super.onResume();

    showProgressFragment();
    mGetHighScoreClient.getHighScores();
  }

  @Override protected void onStart() {
    super.onStart();

    mGetHighScoreClient.registerListener(this);
  }

  @Override protected void onStop() {
    super.onStop();

    mGetHighScoreClient.unregisterListener();
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.iv_back:
        finish();
        break;
      default:
        Log.d(TAG,
            "Click on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }

  @Override public void onClickDialog(SugarmanDialog dialog, DialogButton button) {
    String id = dialog.getId();

    switch (id) {
      case DialogConstants.API_GET_HIGH_SCORES_FAILURE_ID:
        dialog.dismiss();
        finish();
        break;
      default:
        super.onClickDialog(dialog, button);
        break;
    }
  }

  @Override public void onApiGetHighScoresSuccess(HighScores scores) {
    initCreatedGroup(scores.getCreated().length > 0 ? scores.getCreated()[0] : null);
    initParticipatedGroup(scores.getParticipated().length > 0 ? scores.getParticipated()[0] : null);

    closeProgressFragment();
  }

  @Override public void onApiGetHighScoresFailure(String message) {
    closeProgressFragment();
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.API_GET_HIGH_SCORES_FAILURE_ID).content(
          message).btnCallback(this).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  private void initCreatedGroup(HighScore score) {
    boolean isCreated = score != null;
    if (isCreated) {
      Group group = score.getGroup();
      Member[] members = score.getMembers();

      vNothingShowCreated.setVisibility(View.GONE);
      vDividerCreated.setVisibility(View.VISIBLE);
      ivGroupAvatarCreated.setVisibility(View.VISIBLE);
      tvGroupNameCreated.setVisibility(View.VISIBLE);
      tvGroupMembersCreated.setVisibility(View.VISIBLE);
      tvGroupKickedCreated.setVisibility(View.VISIBLE);
      tvGroupPositionCreated.setVisibility(View.VISIBLE);

      String urlAvatar = group.getPictureUrl();
      if (TextUtils.isEmpty(urlAvatar)) {
        ivGroupAvatarCreated.setImageResource(R.drawable.ic_group);
      } else {
        Picasso.with(this)
            .load(urlAvatar)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_group)
            .error(R.drawable.ic_group)
            .into(ivGroupAvatarCreated.getImageView(), createdCallback);
      }

      String groupName = group.getName();
      if (!TextUtils.isEmpty(groupName)) {
        tvGroupNameCreated.setText(groupName);
      }

      int membersCount = members.length + score.getPending().length;
      String membersPlural =
          getResources().getQuantityString(R.plurals.members, membersCount, membersCount);
      String membersFormatted = String.format(membersTemplate, membersCount, membersPlural);
      tvGroupMembersCreated.setText(membersFormatted);

      int kicked = score.getMyAssKickCount();
      String kickedPlural =
          getResources().getQuantityString(R.plurals.group_kicked_template, kicked, kicked);
      String kickedFormatted = String.format(kickedPlural, kicked);
      tvGroupKickedCreated.setText(String.format(kickedFormatted, kicked));

      int position = getMyPosition(members);
      tvGroupPositionCreated.setText(String.format(positionTemplate, position));
    } else {
      vNothingShowCreated.setVisibility(View.VISIBLE);
      vDividerCreated.setVisibility(View.GONE);
      ivGroupAvatarCreated.setVisibility(View.GONE);
      tvGroupNameCreated.setVisibility(View.GONE);
      tvGroupMembersCreated.setVisibility(View.GONE);
      tvGroupKickedCreated.setVisibility(View.GONE);
      tvGroupPositionCreated.setVisibility(View.GONE);
    }
  }

  private void initParticipatedGroup(HighScore score) {
    boolean isParticipated = score != null;
    if (isParticipated) {
      Group group = score.getGroup();
      Member[] members = score.getMembers();

      vNothingShowParticipated.setVisibility(View.GONE);
      vDividerParticipated.setVisibility(View.VISIBLE);
      ivGroupAvatarParticipated.setVisibility(View.VISIBLE);
      tvGroupNameParticipated.setVisibility(View.VISIBLE);
      tvGroupMembersParticipated.setVisibility(View.VISIBLE);
      tvGroupKickedParticipated.setVisibility(View.VISIBLE);
      tvGroupPositionParticipated.setVisibility(View.VISIBLE);

      String urlAvatar = group.getPictureUrl();
      if (TextUtils.isEmpty(urlAvatar)) {
        ivGroupAvatarParticipated.setImageResource(R.drawable.ic_group);
      } else {
        Picasso.with(this)
            .load(urlAvatar)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_group)
            .error(R.drawable.ic_group)
            .into(ivGroupAvatarParticipated.getImageView(), participatedCallback);
      }

      String groupName = group.getName();
      if (!TextUtils.isEmpty(groupName)) {
        tvGroupNameParticipated.setText(groupName);
      }

      int membersCount = members.length + score.getPending().length;
      String membersPlural =
          getResources().getQuantityString(R.plurals.members_cap, membersCount, membersCount);
      String membersFormatted = String.format(membersTemplate, membersCount, membersPlural);
      tvGroupMembersParticipated.setText(membersFormatted);

      int kicked = score.getMyAssKickCount();
      String kickedPlural =
          getResources().getQuantityString(R.plurals.group_kicked_template, kicked, kicked);
      String kickedFormatted = String.format(kickedPlural, kicked);
      tvGroupKickedParticipated.setText(String.format(kickedFormatted, kicked));

      int position = getMyPosition(members);
      tvGroupPositionParticipated.setText(String.format(positionTemplate, position));
    } else {
      vNothingShowParticipated.setVisibility(View.VISIBLE);
      vDividerParticipated.setVisibility(View.GONE);
      ivGroupAvatarParticipated.setVisibility(View.GONE);
      tvGroupNameParticipated.setVisibility(View.GONE);
      tvGroupMembersParticipated.setVisibility(View.GONE);
      tvGroupKickedParticipated.setVisibility(View.GONE);
      tvGroupPositionParticipated.setVisibility(View.GONE);
    }
  }

  private int getMyPosition(Member[] members) {
    String id = SharedPreferenceHelper.getUserId();
    int mySteps = 0;
    for (Member member : members) {
      if (TextUtils.equals(member.getId(), id)) {
        mySteps = member.getSteps();
        break;
      }
    }

    int position = 0;

    for (Member member : members) {
      if (member.getSteps() <= mySteps) {
        position++;
      }
    }

    return members.length - position;
  }
}
