package com.sugarman.myb.ui.activities.groupDetails.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.base.MvpBaseRecyclerAdapter;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.listeners.ItemGroupMemberListener;
import com.sugarman.myb.listeners.OnStepMembersActionListener;
import com.sugarman.myb.models.GroupMember;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.DialogHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.SoundHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import lombok.Getter;
import timber.log.Timber;

public class GroupMembersAdapter extends MvpBaseRecyclerAdapter<RecyclerView.ViewHolder>
    implements ItemGroupMemberListener, IGroupMembersAdapterView {

  public static final int MEMBER_TYPE = 0;
  public static final int PENDING_LABEL_TYPE = 2;
  private static final String TAG = GroupMembersAdapter.class.getName();
  private static final int PENDING_MEMBER_TYPE = 1;
  private final WeakReference<OnStepMembersActionListener> actionListener;
  private final int red;
  private final int darkGray;
  private final int walkBackgroundColor;
  private final int[] brokenGlassIds;
  private final List<GroupMember> mData = new ArrayList<>();
  private final String userId;
  private final String lazy;
  private final String walk;
  private final String run;
  private final Context context;
  private final Handler handler;
  @InjectPresenter GroupMembersAdapterPresenter mAdapterPresenter;
  @Getter boolean editMode;
  PendingMemberHolder pendingMemberHolder;
  Animation connectingAnimation;
  private String mTrackingId;
  private int countRedMembers;
  private boolean animationStarted = false;
  private int userSteps;
  private boolean isShownActionBackground;
  private int countMembers = 0;
  private int myPosition = -1;
  private boolean amIMentor = false;

  public GroupMembersAdapter(MvpDelegate<?> parentDelegate, Context context,
      OnStepMembersActionListener listener, String trackingId, boolean isMentor) {
    super(parentDelegate, "GroupMembersAdapter");

    mTrackingId = trackingId;
    this.context = context;
    amIMentor = isMentor;
    connectingAnimation = AnimationUtils.loadAnimation(context, R.anim.scale);
    userId = SharedPreferenceHelper.getUserId();
    brokenGlassIds = SharedPreferenceHelper.getBrokenGlassIds();

    actionListener = new WeakReference<>(listener);

    userSteps = SharedPreferenceHelper.getStatsTodaySteps(userId);

    handler = App.getHandlerInstance();

    red = ContextCompat.getColor(context, R.color.red);
    darkGray = ContextCompat.getColor(context, R.color.dark_gray);
    walkBackgroundColor = ContextCompat.getColor(context, R.color.red);

    lazy = context.getString(R.string.lazy);
    walk = context.getString(R.string.walk);
    run = context.getString(R.string.run);
  }

  public void setEditMode(boolean editMode) {
    this.editMode = editMode;
    notifyDataSetChanged();
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view;
    RecyclerView.ViewHolder holder = null;

    switch (viewType) {
      case MEMBER_TYPE:
        view =
            LayoutInflater.from(context).inflate(R.layout.layout_item_group_member, parent, false);
        holder = new MemberHolder(view, this);
        break;
      case PENDING_MEMBER_TYPE:
        view = LayoutInflater.from(context)
            .inflate(R.layout.layout_item_group_pending_member, parent, false);
        holder = new PendingMemberHolder(view, this);
        break;
      case PENDING_LABEL_TYPE:
        view = LayoutInflater.from(context)
            .inflate(R.layout.layout_item_group_pending_label, parent, false);
        holder = new PendingLabelHolder(view);
        break;
      default:
        Log.e(TAG, "not supported element type: " + viewType);
        break;
    }

    return holder;
  }

  @Override public void onViewRecycled(RecyclerView.ViewHolder holder) {
    super.onViewRecycled(holder);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    final GroupMember member = getValue(position);
    if (member != null) {
      int type = getItemViewType(position);

      int stepsCount = member.getSteps();
      String stepsPlural =
          context.getResources().getQuantityString(R.plurals.steps, stepsCount, stepsCount);
      String stepsCountFormatted = stepsCount < 1000 ? String.valueOf(stepsCount)
          : String.format(Locale.US, "%,d", stepsCount);
      String stepsFormatted = String.format(stepsCountFormatted);

      String url = member.getPictureUrl();

      switch (type) {
        case MEMBER_TYPE:
          final MemberHolder memberHolder = (MemberHolder) holder;
          if (isEditMode()) {
            ((MemberHolder) holder).ivKick.setVisibility(View.VISIBLE);
          } else {
            ((MemberHolder) holder).ivKick.setVisibility(View.GONE);
          }

          if(myPosition!=-1)
          memberHolder.tvSteps.setText(stepsFormatted + " " + context.getString(R.string.steps));
          //memberHolder.tvNum.setText(String.valueOf(countMembers - position));
          String str = member.getName();
          str = str.replaceAll("( +)", " ").trim();
          String name = str;

          if (str.length() > 0 && str.contains(" ")) {
            name = str.substring(0, (member.getName().indexOf(" ")));
          }
          memberHolder.tvMemberName.setText(name);
          Log.e("Action", "" + member.getAction());
          if (member.getAction() != Member.ACTION_LAZY) {
            Log.e("Action", "not lazy");
            if (!animationStarted) {
              Log.e("animation", "" + connectingAnimation.hasStarted());
              memberHolder.ivAvatar.startAnimation(connectingAnimation);
              animationStarted = true;
            }
          } else {
            memberHolder.ivAvatar.clearAnimation();
            connectingAnimation.cancel();
            connectingAnimation.reset();
            animationStarted = false;
          }

          if (TextUtils.isEmpty(url)) {
            memberHolder.ivAvatar.setImageResource(R.drawable.ic_gray_avatar);
          } else {
            Picasso.with(context).cancelRequest(memberHolder.ivAvatar);
            if (member.getId().equals(SharedPreferenceHelper.getUserId())) {
              int color = 0xff54cc14;
              int steps = member.getSteps();
              if (steps < 5000) {
                color = 0xffe10f0f;
              } else if (steps >= 5000 && steps < 7500) {
                color = 0xffeb6117;
              } else if (steps >= 7500 && steps < 10000) color = 0xffF6B147;

              memberHolder.tvSteps.setTextColor(color);
              Typeface face = Typeface.createFromAsset(context.getAssets(), "sans-serif-medium");
              //memberHolder.tvMemberName.setTypeface(face);

              Picasso.with(context)
                  .load(url)
                  .fit()
                  .centerCrop()
                  .placeholder(R.drawable.ic_gray_avatar)
                  .error(R.drawable.ic_gray_avatar)
                  .networkPolicy(NetworkPolicy.NO_CACHE)
                  .transform(new MaskTransformation(context, R.drawable.mask, true, color))
                  .into(memberHolder.ivAvatar);

              //Drawable drawable = context.getResources().getDrawable(R.drawable.hexagon_border);
              //System.out.println("Drawable + " + drawable);
              //Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
              //System.out.println("Bitmap + " + bitmap);
              //memberHolder.ivAvatar.setImageBitmap(bitmap);
            } else {
              int color = 0xff54cc14;
              int steps = member.getSteps();
              if (steps < 5000) {
                color = 0xffe10f0f;
              } else if (steps >= 5000 && steps < 7500) {
                color = 0xffeb6117;
              } else if (steps >= 7500 && steps < 10000) color = 0xffF6B147;
              memberHolder.tvSteps.setTextColor(color);

              Picasso.with(context)
                  .load(url)
                  .fit()
                  .centerCrop()
                  .placeholder(R.drawable.ic_gray_avatar)
                  .error(R.drawable.ic_gray_avatar)
                  .networkPolicy(NetworkPolicy.NO_CACHE)
                  .transform(new CropCircleTransformation(color, 4))
                  .into(memberHolder.ivAvatar);
            }
          }

          if (position < countRedMembers && stepsCount < Config.MAX_STEPS_PER_DAY) {
            // memberHolder.tvNum.setTextColor(red);
            //memberHolder.tvSteps.setTextColor(red);
            //  memberHolder.tvMemberName.setTextColor(red);
          } else {
            // memberHolder.tvNum.setTextColor(darkGray);
            // memberHolder.tvSteps.setTextColor(darkGray);
            //memberHolder.tvMemberName.setTextColor(darkGray);
          }

          if (TextUtils.equals(member.getId(), userId)
              || stepsCount >= Config.MAX_STEPS_PER_DAY
              || stepsCount >= userSteps
              || myPosition == -1
              || myPosition == position) {
            //                        memberHolder.ivBad.setVisibility(View.GONE);
          } else {
            //     memberHolder.ivBad.setVisibility(View.VISIBLE);
          }
          if (member.isBroken()) {
            memberHolder.ivBroken.setVisibility(View.VISIBLE);
            int id = new Random().nextInt(7);
            SoundHelper.play(brokenGlassIds[id]);

            member.setBroken(false);
            handler.postDelayed(new Runnable() {
              @Override public void run() {
                memberHolder.ivBroken.setVisibility(View.GONE);
              }
            }, Config.BROKEN_AVATAR_TIME);
          } else {
            memberHolder.ivBroken.setVisibility(View.GONE);
          }

          //if (!isShownActionBackground) {
          //    memberHolder.tvNum.setBackgroundColor(Color.TRANSPARENT);
          //    memberHolder.tvNum.setTextColor(darkGray);
          //} else if (!TextUtils.equals(member.getId(), userId) && member.isBlinked()) {
          //    memberHolder.tvNum.setBackgroundColor(walkBackgroundColor);
          //    memberHolder.tvNum.setTextColor(Color.WHITE);
          //}

          if (myPosition == -1) {
            //memberHolder.tvSteps.setVisibility(View.GONE);
            //                        memberHolder.tvAction.setVisibility(View.GONE);
          } else if (!TextUtils.equals(member.getId(), userId)) {
            switch (member.getAction()) {
              case (Member.ACTION_ZERO):
                //memberHolder.tvAction.setVisibility(View.INVISIBLE);
                break;
              case (Member.ACTION_LAZY):
                //memberHolder.tvAction.setVisibility(View.VISIBLE);
                //                                memberHolder.tvAction.setText(lazy);
                break;
              case (Member.ACTION_WALK):
                //memberHolder.tvAction.setVisibility(View.VISIBLE);
                //                                memberHolder.tvAction.setText(walk);
                break;
              case (Member.ACTION_RUN):
                //memberHolder.tvAction.setVisibility(View.VISIBLE);
                //                               memberHolder.tvAction.setText(run);
                break;
            }
          } else {
            //memberHolder.tvAction.setVisibility(View.INVISIBLE);
          }
          break;
        case PENDING_MEMBER_TYPE:
          pendingMemberHolder = (PendingMemberHolder) holder;
          pendingMemberHolder.tvSteps.setText(context.getString(R.string.pending) + "…");
          String str1 = member.getName();
          str1 = str1.replaceAll("( +)", " ").trim();

          String name1 = str1;
          if (member.getName().contains(" ")) {
            name1 = str1.substring(0, (member.getName().indexOf(" ")));
          }
          pendingMemberHolder.tvMemberName.setText(name1);

          if (TextUtils.isEmpty(url)) {
            pendingMemberHolder.ivAvatar.setImageResource(R.drawable.ic_gray_avatar);
          } else {
            Picasso.with(context).cancelRequest(pendingMemberHolder.ivAvatar);
            if (member.getId().equals(SharedPreferenceHelper.getUserId())) {
              Picasso.with(context)
                  .load(url)
                  .fit()
                  .centerCrop()
                  .placeholder(R.drawable.ic_gray_avatar)
                  .error(R.drawable.ic_gray_avatar)
                  .networkPolicy(NetworkPolicy.NO_CACHE)
                  .transform(new MaskTransformation(context, R.drawable.mask, true, 0xcccccccc))
                  .into(pendingMemberHolder.ivAvatar);
            } else {
              Picasso.with(context)
                  .load(url)
                  .fit()
                  .centerCrop()
                  .placeholder(R.drawable.ic_gray_avatar)
                  .error(R.drawable.ic_gray_avatar)
                  .networkPolicy(NetworkPolicy.NO_CACHE)
                  .transform(new CropCircleTransformation(0xff94989B, 4))
                  .into(pendingMemberHolder.ivAvatar);
            }
          }
          break;
        case PENDING_LABEL_TYPE:
          // nothing
          break;
        default:
          Log.e(TAG, "not supported element type: " + type);
          break;
      }
    }
  }

  @Override public int getItemCount() {
    return mData.size();
  }

  @Override public int getItemViewType(int position) {
    GroupMember member = getValue(position);
    return member == null ? MEMBER_TYPE : member.getGroupType();
  }

  @Override public void onClickMemberAvatar(int position) {
    if (amIMentor && isEditMode()) {
      GroupMember toDelete = mData.get(position);
      if (!toDelete.getId().equals(SharedPreferenceHelper.getUserId())) {
        DialogHelper.createSimpleDialog(context.getString(R.string.okay),
            context.getString(R.string.discard), context.getString(R.string.warning),
            context.getString(R.string.delete_user_from_group), context, (dialogInterface, i) -> {
              mAdapterPresenter.deleteUser(mTrackingId, toDelete.getId(), position);
              Map<String, Object> eventValue = new HashMap<>();
              eventValue.put(AFInAppEventParameterName.LEVEL, 9);
              eventValue.put(AFInAppEventParameterName.SCORE, 100);
              AppsFlyerLib.getInstance()
                  .trackEvent(App.getInstance().getApplicationContext(),
                      "af_delete_person_from_group", eventValue);
            }, (dialogInterface, i) -> {
              dialogInterface.dismiss();
            }).create().show();
      }
    }
    Timber.e("My ID "
        + SharedPreferenceHelper.getUserId()
        + ", myPosition = "
        + myPosition
        + " user clicked "
        + mData.get(position).getId());
    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance()
        .trackEvent(App.getInstance().getApplicationContext(), "af_kick_single_person", eventValue);

    if (amIMentor) {
      GroupMember member = mData.get(position);
      member.setBroken(true);
      notifyItemChanged(position);
      if (TextUtils.equals(member.getId(), userId)) {
        actionListener.get().onPokeSelf();
      } else {
        actionListener.get().onPokeMember(member);
      }
    } else {
      if (position >= 0 && position < mData.size()) {
        if (actionListener.get() != null) {
          GroupMember member = mData.get(position);
          int memberSteps = member.getSteps();
          if (myPosition == -1) {
            actionListener.get().onPokeInForeignGroup();
          } else if (TextUtils.equals(member.getId(), userId)) {
            actionListener.get().onPokeSelf();
          } else if (memberSteps >= Config.MAX_STEPS_PER_DAY) {
            actionListener.get().onPokeCompletedDaily();
          } else if (memberSteps >= userSteps) {
            actionListener.get().onPokeMoreThatSelf();
          } else {
            member.setBroken(true);
            notifyItemChanged(position);
            actionListener.get().onPokeMember(member);
          }
        }

        notifyItemChanged(position);
      } else {
        notifyDataSetChanged();
      }
    }
  }

  @Override public void onClickPendingAvatar(int position) {
    if (position >= 0 && position < mData.size()) {
      if (actionListener.get() != null) {
        if (myPosition == -1) {
          actionListener.get().onPokeInForeignGroup();
        } else {
          actionListener.get().onPokePendingMember();
        }
      }

      notifyItemChanged(position);
    } else {
      notifyDataSetChanged();
    }
  }

  public void addMember(int pos, GroupMember member) {
    mData.add(pos, member);
    notifyDataSetChanged();
  }

  public void setValues(List<Member> members, List<Member> pendings) {

    countMembers = members.size();
    countRedMembers = Config.getCountRedMembers(countMembers);

    setMyStepsAndResortMembers(members);

    List<GroupMember> listGroupmembers = new ArrayList<>();
    for (Member member : members) {
      Log.d("member inside ", member.getName() + " " + member.getSteps());
      GroupMember groupMember = new GroupMember(member);
      groupMember.setGroupType(MEMBER_TYPE);
      if (mData.contains(groupMember)) {
        Member oldMember = mData.get(mData.indexOf(groupMember));
        if (groupMember.getSteps() > oldMember.getSteps()) {
          groupMember.setBlinked(true);
        }
      }
      listGroupmembers.add(groupMember);
    }

    mData.clear();
    mData.addAll(listGroupmembers);
    listGroupmembers.clear();

    if (!pendings.isEmpty()) {
      setMyStepsAndResortMembers(pendings);
      //mData.add(new GroupMember());

      for (Member member : pendings) {
        GroupMember groupMember = new GroupMember(member);
        groupMember.setGroupType(PENDING_MEMBER_TYPE);
        mData.add(groupMember);
      }
    }

    GroupMember memberWithMyId = new GroupMember();
    memberWithMyId.setId(userId);
    for (int i = 0; i < mData.size(); i++) {
      Timber.e("Position " + i + " mdata " + mData.get(i).getId());
      if (mData.get(i).getId().equals(userId)) {
        Timber.e("EUREKA");
        myPosition = i;
      }
    }
    Timber.e("My position " + myPosition);
    setMySteps(userSteps);
    notifyDataSetChanged();
    isShownActionBackground = true;
    handler.postDelayed(new Runnable() {
      @Override public void run() {
        isShownActionBackground = false;
        notifyDataSetChanged();
      }
    }, Config.SHOW_ACTION_BACKGROUND_TIME);
  }

  private void setMyStepsAndResortMembers(List<Member> members) {
    Member memberWithMyId = new Member();
    memberWithMyId.setId(userId);
    int position = members.indexOf(memberWithMyId);
    if (position != -1) {
      members.get(position).setSteps(userSteps);
    }

    Collections.sort(members, Member.BY_STEPS_DESC);
  }

  public List<GroupMember> getData() {
    return mData;
  }

  private GroupMember getValue(int position) {
    if (position >= 0 && position < mData.size()) {
      return mData.get(position);
    } else {
      return null;
    }
  }

  public void setMySteps(int mySteps) {
    userSteps = mySteps;
    if (myPosition != -1 && mData != null && mData.size() != 0) {
      mData.get(myPosition).setSteps(mySteps);
      notifyItemChanged(myPosition);
    }
  }

  @Override public void removeUser(int position) {

    mData.remove(position);
    notifyItemRemoved(position);
    for (int i = 0; i < mData.size(); i++) {
      Timber.e("Position " + i + " mdata " + mData.get(i).getId());
      if (mData.get(i).getId().equals(userId)) {
        Timber.e("EUREKA");
        myPosition = i;
      }
    }
    Timber.e("SUCCESS");
  }

  private static class MemberHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private final WeakReference<ItemGroupMemberListener> mActionItemListener;

    private final TextView tvMemberName;
    private final TextView tvSteps;
    private final View ivBroken;
    private final TextView tvAction;
    //private final ImageView ivBad;
    public ImageView ivKick;
    //private final TextView tvNum;
    private ImageView ivAvatar;

    MemberHolder(View itemView, ItemGroupMemberListener listener) {
      super(itemView);

      mActionItemListener = new WeakReference<>(listener);

      View vContainer = itemView.findViewById(R.id.ll_group_member_container);

      // tvNum = (TextView) itemView.findViewById(R.id.tv_num);
      tvMemberName = (TextView) itemView.findViewById(R.id.tv_member_name);
      tvSteps = (TextView) itemView.findViewById(R.id.tv_steps);
      tvAction = (TextView) itemView.findViewById(R.id.tv_action);
      ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
      ivBroken = itemView.findViewById(R.id.iv_broken_avatar);
      ivKick = (ImageView) itemView.findViewById(R.id.ivKickOverlay);
      //ivBad = (ImageView) itemView.findViewById(R.id.iv_bad);

      vContainer.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      int id = v.getId();
      int position = getAdapterPosition();

      switch (id) {
        case R.id.ll_group_member_container:
          Timber.e("My ID ");

          if (mActionItemListener.get() != null) {
            mActionItemListener.get().onClickMemberAvatar(position);
          }
          break;
        default:
          Log.d(TAG,
              "Click on not processed view with id " + v.getResources().getResourceEntryName(id));
          break;
      }
    }
  }

  private static class PendingMemberHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private final WeakReference<ItemGroupMemberListener> mActionItemListener;

    private final TextView tvMemberName;
    private final TextView tvSteps;
    private final ImageView ivAvatar;

    PendingMemberHolder(View itemView, ItemGroupMemberListener listener) {
      super(itemView);

      mActionItemListener = new WeakReference<>(listener);

      View vContainer = itemView.findViewById(R.id.ll_group_member_container);
      tvMemberName = (TextView) itemView.findViewById(R.id.tv_member_name);
      tvSteps = (TextView) itemView.findViewById(R.id.tv_steps);
      ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);

      vContainer.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      int id = v.getId();
      int position = getAdapterPosition();

      switch (id) {
        case R.id.ll_group_member_container:
          if (mActionItemListener.get() != null) {
            mActionItemListener.get().onClickPendingAvatar(position);
          }
          break;
        default:
          Log.d(TAG,
              "Click on not processed view with id " + v.getResources().getResourceEntryName(id));
          break;
      }
    }
  }

  private static class PendingLabelHolder extends RecyclerView.ViewHolder {

    PendingLabelHolder(View itemView) {
      super(itemView);
    }
  }
}
