package com.sugarman.myb.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.clover_studio.spikachatmodule.api.retrofit.CustomResponse;
import com.clover_studio.spikachatmodule.api.retrofit.SpikaOSRetroApiInterface;
import com.clover_studio.spikachatmodule.base.SingletonLikeApp;
import com.clover_studio.spikachatmodule.managers.socket.SocketManager;
import com.clover_studio.spikachatmodule.managers.socket.SocketManagerListener;
import com.clover_studio.spikachatmodule.models.GetMessagesModel;
import com.clover_studio.spikachatmodule.models.Login;
import com.clover_studio.spikachatmodule.models.Message;
import com.clover_studio.spikachatmodule.models.SendTyping;
import com.clover_studio.spikachatmodule.models.User;
import com.clover_studio.spikachatmodule.utils.Const;
import com.clover_studio.spikachatmodule.utils.EmitJsonCreator;
import com.clover_studio.spikachatmodule.utils.LogCS;
import com.clover_studio.spikachatmodule.utils.SeenByUtils;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Group;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.models.ChallengeMember;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import com.sugarman.myb.ui.views.CropSquareTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.IntentExtractorHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

public abstract class ChallengeFragment extends BaseChallengeFragment
    implements View.OnClickListener {

  private static final String TAG = ChallengeFragment.class.getName();
  final ChallengeMember[] challengeMembers = new ChallengeMember[4]; // 4 icons in fragment
  protected List<Message> lastDataFromServer = new ArrayList<>();
  protected Retrofit client;
  String lastMessageId;
  Thread th;
  TextView messageCounter;
  Tracking tracking;
  int allSteps;
  User user;
  View vWillContainer;
  View vChallengeContainer;
  boolean firstTime = true;
  TextView stepsTotal;
  ImageView daysStrip, progressStrip;
  TextView mTextViewTimesSave;
  private Context context;
  private int red;
  private int gray;
  private int darkGray;
  private String groupOwnerId;
  private SocketManagerListener socketListener = new SocketManagerListener() {
    @Override public void onConnect() {
      LogCS.w("LOG", "CONNECTED TO SOCKET");
    }

    @Override public void onSocketFailed() {
    }

    @Override public void onNewUser(Object... args) {
      Log.w("LOG", "new user, args" + args[0].toString());
    }

    @Override public void onLoginWithSocket() {
      JSONObject emitLogin = EmitJsonCreator.createEmitLoginMessage(user);
      SocketManager.getInstance().emitMessage(Const.EmitKeyWord.LOGIN, emitLogin);
    }

    @Override public void onUserLeft(User user) {

    }

    @Override public void onTyping(SendTyping typing) {

    }

    @Override public void onMessageReceived(Message message) {

    }

    @Override public void onMessagesUpdated(List<Message> messages) {

    }

    @Override public void onSocketError(int code) {

    }
  };

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_challenge, container, false);
    client = new Retrofit.Builder().baseUrl(
        SingletonLikeApp.getInstance().getConfig(getActivity()).apiBaseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    stepsTotal = (TextView) root.findViewById(R.id.steps_total);
    allSteps = 0;
    daysStrip = (ImageView) root.findViewById(R.id.days_strip);
    progressStrip = (ImageView) root.findViewById(R.id.progress_strip);
    mTextViewTimesSave = (TextView) root.findViewById(R.id.tvTimesSave);

    Bundle args = getArguments();
    position = IntentExtractorHelper.getTrackingPosition(args);
    totalItems = IntentExtractorHelper.getTrackingsCount(args);

    context = getContext();
    red = ContextCompat.getColor(context, R.color.red);
    gray = ContextCompat.getColor(context, R.color.gray);
    darkGray = ContextCompat.getColor(context, R.color.dark_gray);

    TextView tvGroupName = (TextView) root.findViewById(R.id.tv_group_name);
    TextView tvDay = (TextView) root.findViewById(R.id.tv_day);
    TextView tvDayNum = (TextView) root.findViewById(R.id.tv_day_num);
    messageCounter = (TextView) root.findViewById(R.id.tv_avatar_events);

    vChallengeContainer = root.findViewById(R.id.cv_challenge_container);
    vWillContainer = root.findViewById(R.id.ll_will_container);

    ImageView ivGroupAvatar = (ImageView) root.findViewById(R.id.group_avatar);

    tracking = IntentExtractorHelper.getTracking(args);
    if (tracking != null) {
      Group group = tracking.getGroup();
      tvGroupName.setText(group.getName());

      Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "din_regular.ttf");

      tvDay.setTypeface(tf);
      tvDayNum.setTypeface(tf);

      final float day = (System.currentTimeMillis() - tracking.getStartUTCDate().getTime())
          / (float) Constants.MS_IN_DAY;
      if (day <= 0) {
        tvDayNum.setText(R.string.warming_up);
      } else if (day - (int) day > 0) {
        tvDayNum.setText(Integer.toString((int) (day + 1)));
      } else {
        tvDayNum.setText(Integer.toString((int) (day)));
      }

      final Member[] members = getMembers();
      Member[] pendings = getPendingMembers();

      if (members.length > 0) {
        Arrays.sort(members, Member.BY_STEPS_ASC);

        Member best = members[members.length - 1];

        TextView bestName = (TextView) (root.findViewById(R.id.best_name));
        TextView bestSteps = (TextView) root.findViewById(R.id.best_steps);
        ImageView fastestAvatarBorder =
            (ImageView) root.findViewById(R.id.iv_fastest_avatar_border);
        ImageView bestAvatar = (ImageView) root.findViewById(R.id.iv_best_avatar);

        String str = "";
        str = best.getName() == null ? "" : best.getName();
        Timber.e("Best " + best.getName());
        if (str.contains(" ")) str = str.replaceAll("( +)", " ").trim();

        String name = str;
        if (str.length() > 0 && str.contains(" ")) {
          name = str.substring(0, (best.getName().indexOf(" ")));

          bestName.setText(name);
        } else {
          bestName.setText(str);
        }
        bestSteps.setText(String.format(Locale.US, "%,d", best.getSteps()));
        if (best.getPictureUrl() == null || best.getPictureUrl().equals(" ") || best.getPictureUrl()
            .equals("")) {
          best.setPictureUrl("https://sugarman-myb.s3.amazonaws.com/Group_New.png");
        }

        for (int i = 0; i < tracking.getMembers().length; i++) {
          if (best.getId().equals(SharedPreferenceHelper.getUserId())) {
            bestSteps.setText(String.format(Locale.US, "%,d",
                SharedPreferenceHelper.getReportStatsLocal(
                    SharedPreferenceHelper.getUserId())[0].getStepsCount()));
          }
        }

        CustomPicasso.with(getActivity())
            .load(best.getPictureUrl())
            //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_red_avatar)
            .transform(new CropCircleTransformation(0xff7ECC10, 1))
            .into(bestAvatar);

        int color = 0xff54cc14;
        if (best.getSteps() < 5000) {
          color = 0xffe10f0f;
        } else if (best.getSteps() >= 5000
            && best.getSteps() < 7500) {
          color = 0xffeb6117;
        } else if (best.getSteps() >= 7500
            && best.getSteps() < 10000) {
          color = 0xffF6B147;
        }
        fastestAvatarBorder.setBackgroundColor(color);
        bestName.setBackgroundColor(color);
        bestSteps.setBackgroundColor(color);

        Member laziest = members[0];

        TextView laziestName = (TextView) (root.findViewById(R.id.laziest_name));
        TextView laziestSteps = (TextView) root.findViewById(R.id.laziest_steps);
        ImageView laziestAvatarBorder =
            (ImageView) root.findViewById(R.id.iv_laziest_avatar_border);

        ImageView laziestAvatar = (ImageView) root.findViewById(R.id.iv_laziest_avatar);

        str = laziest.getName();
        str = str.replaceAll("( +)", " ").trim();
        if (str.length() > 0 && str.contains(" ")) {
          name = str.substring(0, (laziest.getName().indexOf(" ")));
        } else {
          name = str;
        }

        laziestName.setText(name);
        laziestSteps.setText(String.format(Locale.US, "%,d", laziest.getSteps()));

        for (int i = 0; i < tracking.getMembers().length; i++) {
          if (laziest.getId().equals(SharedPreferenceHelper.getUserId())) {
            laziestSteps.setText(String.format(Locale.US, "%,d",
                SharedPreferenceHelper.getReportStatsLocal(
                    SharedPreferenceHelper.getUserId())[0].getStepsCount()));
          }
        }

        if (laziest.getPictureUrl() == null
            || laziest.getPictureUrl().equals("")
            || laziest.getPictureUrl().equals(" ")) {
          laziest.setPictureUrl("https://sugarman-myb.s3.amazonaws.com/Group_New.png");
        }
        CustomPicasso.with(getActivity())
            .load(laziest.getPictureUrl())
            //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_red_avatar)
            .transform(new CropCircleTransformation(0xffff0000, 1))
            .into(laziestAvatar);

        if (laziest.getSteps() < 5000) {
          color = 0xffe10f0f;
        } else if (laziest.getSteps() >= 5000
            && laziest.getSteps() < 7500) {
          color = 0xffeb6117;
        } else if (laziest.getSteps() >= 7500
            && laziest.getSteps() < 10000) {
          color = 0xffF6B147;
        }
        laziestAvatarBorder.setBackgroundColor(color);
        laziestName.setTextColor(color);
        laziestSteps.setTextColor(color);

        TextView fastestName = (TextView) (root.findViewById(R.id.fastest_name));
        TextView fastestSteps = (TextView) root.findViewById(R.id.fastest_steps);
        ImageView fastestAvatar = (ImageView) root.findViewById(R.id.iv_fastest_avatar);
        ImageView fastestBorderAvatar = (ImageView) root.findViewById(R.id.iv_fastest_avatar_border);
        if (tracking.hasDailyWinner()) {
          Member fastest = tracking.getDailySugarman().getUser();

          str = fastest.getName();
          str = str.replaceAll("( +)", " ").trim();
          if (str.length() > 0) {
            if (str.contains(" ")) {
              name = str.substring(0, (fastest.getName().indexOf(" ")));
            } else {
              name = str;
            }
          }

          fastestName.setText(name);
          fastestSteps.setText(String.format(Locale.US, "%,d", fastest.getSteps()));
          CustomPicasso.with(getActivity())
              .load(fastest.getPictureUrl())
              //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
              .placeholder(R.drawable.ic_gray_avatar)
              .error(R.drawable.ic_red_avatar)
              .transform(new CropCircleTransformation(0xffff0000, 1))
              .into(fastestAvatar);

          //this is fixing of server bug, but on client
          for (int i = 0; i < tracking.getMembers().length; i++) {
            if (fastest.getId().equals(SharedPreferenceHelper.getUserId())) {
              fastestSteps.setText(String.format(Locale.US, "%,d",
                  SharedPreferenceHelper.getReportStatsLocal(
                      SharedPreferenceHelper.getUserId())[0].getStepsCount()));
            }
          }

          if (tracking.getDailySugarman().getUser().getSteps() < 5000) {
            color = 0xffe10f0f;
          } else if (tracking.getDailySugarman().getUser().getSteps() >= 5000
              && tracking.getDailySugarman().getUser().getSteps() < 7500) {
            color = 0xffeb6117;
          } else if (tracking.getDailySugarman().getUser().getSteps() >= 7500
              && tracking.getDailySugarman().getUser().getSteps() < 10000) {
            color = 0xffF6B147;
          }
          fastestBorderAvatar.setBackgroundColor(color);
          fastestName.setTextColor(color);
          fastestSteps.setTextColor(color);
        } else {
          fastestName.setText(getResources().getString(R.string.sugarman_is));
          fastestSteps.setText(getResources().getString(R.string.todays_fastest));
          CustomPicasso.with(getActivity())
              .load(R.drawable.sugar_next)
              //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
              .placeholder(R.drawable.ic_gray_avatar)
              .error(R.drawable.ic_red_avatar)
              .transform(new CropCircleTransformation(0xffff0000, 1))
              .into(fastestAvatar);
        }
      }

      TextView allName = (TextView) (root.findViewById(R.id.all_name));
      final TextView tv_allSteps = (TextView) root.findViewById(R.id.all_steps);
      ImageView allAvatar = (ImageView) root.findViewById(R.id.iv_all_avatar);
      String str = Integer.toString(tracking.getMembers().length) + " " + getResources().getString(
          R.string.users);

      allName.setText(str);

      CustomPicasso.with(getActivity())
          .load(R.drawable.white_bg)
          //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
          .placeholder(R.drawable.ic_gray_avatar)
          .error(R.drawable.ic_red_avatar)
          .transform(new CropCircleTransformation(0xffff0000, 1))
          .into(allAvatar);

      //            Log.e("Daily sugar", ""+tracking.getDailySugarman().getUser().getName());

      convertMembers(members, pendings);

      int totalMembers = members.length + pendings.length;
      String membersPlural =
          getResources().getQuantityString(R.plurals.members, totalMembers, totalMembers);
      String membersFormatted =
          String.format(getString(R.string.plural_decimal_template), totalMembers, membersPlural);

      ViewTreeObserver vto = daysStrip.getViewTreeObserver();
      vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override public void onGlobalLayout() {
          daysStrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          int width = daysStrip.getMeasuredWidth();
          int height = daysStrip.getMeasuredHeight();
          Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
          if (width > 0 && height > 0) {
            Bitmap bmp = Bitmap.createBitmap(width, height, conf); // this creates a MUTABLE bitmap
            Canvas canvas = new Canvas(bmp);
            Paint p = new Paint();
            p.setStrokeWidth(height / 2);
            p.setStrokeCap(Paint.Cap.ROUND);

            p.setStyle(Paint.Style.STROKE);

            int padding = 10;

            float length = (width - padding * 22) / 21f;

            p.setColor(0x2f414141);
            Log.e("day", "" + Math.floor(day));
            for (int i = 0; i < 21; i++) {
              if (i < Math.floor(day)) {
                p.setColor(0x50FF0000);
              } else if (i == Math.floor(day)) {
                p.setColor(0xffFF0000);
              } else {
                p.setColor(0x2f414141);
              }
              canvas.drawLine(padding * (i + 1) + length * i, height / 2,
                  padding * (i + 1) + length * i + length, height / 2, p);
            }

            daysStrip.setImageBitmap(bmp);
          }
        }
      });

      vto = progressStrip.getViewTreeObserver();
      vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override public void onGlobalLayout() {
          progressStrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          int width = progressStrip.getMeasuredWidth();
          int height = progressStrip.getMeasuredHeight();
          Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
          if (width > 0 && height > 0) {
            Bitmap bmp = Bitmap.createBitmap(width, height, conf); // this creates a MUTABLE bitmap
            Canvas canvas = new Canvas(bmp);
            Paint p = new Paint();
            p.setStrokeWidth(height / 3 * 2);
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStyle(Paint.Style.STROKE);

            p.setColor(0xffE5E5E5);
            canvas.drawLine(0, height / 2, width, height / 2, p);
            p.setColor(0xffFD3E3E);

            for (int i = 0; i < members.length; i++) {
              allSteps += members[i].getSteps();
            }

            tv_allSteps.setText("" + String.format(Locale.US, "%,d", allSteps));
            stepsTotal.setText(
                "" + String.format(Locale.US, "%,d", allSteps) + "/" + String.format(Locale.US,
                    "%,d", (members.length * 10000)));

            float drawto = width * (float) ((float) allSteps / (float) (members.length * 10000));
            Log.e("drawTo", "" + drawto);
            canvas.drawLine(0, height / 2, drawto, height / 2, p);

            progressStrip.setImageBitmap(bmp);
          }
        }
      });

      // tvMembers.setText(membersFormatted);

      //      liDanger.updateIndicator(Config.MAX_STEPS_PER_DAY, members.length > 0 ? members[0].getSteps() : 0);
      if (members.length == 0) {
        //vDividerAvatarFirst.setVisibility(View.VISIBLE);
      }

      prepareMembers(members.length);
    }

    user = new User();
    user.roomID = tracking.getId(); // ->  id of room
    user.userID = SharedPreferenceHelper.getUserId(); // ->  id of user
    user.name = SharedPreferenceHelper.getUserName(); // ->  name of user
    user.avatarURL = SharedPreferenceHelper.getAvatar();//  ->  user avatar, this is optional

    login(user);

    Resources resources = getResources();
    LinearLayout.LayoutParams params =
        (LinearLayout.LayoutParams) vChallengeContainer.getLayoutParams();
    if (position == 0 && totalItems == 1) {
      params.rightMargin = resources.getDimensionPixelSize(R.dimen.challenge_padding);
      params.leftMargin = resources.getDimensionPixelSize(R.dimen.challenge_padding);
    } else if (position == 0) {
      params.leftMargin = resources.getDimensionPixelSize(R.dimen.challenge_small_margin);
      params.rightMargin = resources.getDimensionPixelSize(R.dimen.challenge_small_margin);
    } else if (position == totalItems - 1) {
      params.leftMargin = resources.getDimensionPixelSize(R.dimen.challenge_padding);
      params.rightMargin = resources.getDimensionPixelSize(R.dimen.challenge_padding);
    } else {
      params.rightMargin = resources.getDimensionPixelSize(R.dimen.challenge_small_margin);
    }
    vChallengeContainer.setLayoutParams(params);

    vWillContainer.setOnClickListener(this);

    Group group = tracking.getGroup();

    CustomPicasso.with(getActivity())
        .load(group.getPictureUrl())
        .placeholder(R.drawable.ic_gray_avatar)
        .error(R.drawable.ic_red_avatar)
        .transform(new CropSquareTransformation())
        .transform(new MaskTransformation(getActivity(), R.drawable.group_avatar, false, 0xfff))
        .into(ivGroupAvatar);

    groupOwnerId = group.getOwner().getId();

    if (tracking.getTimesSave() != null && tracking.getTimesSave() > 0) {
      mTextViewTimesSave.setVisibility(View.VISIBLE);
      mTextViewTimesSave.setText(tracking.getTimesSave().toString());
    } else {
      mTextViewTimesSave.setVisibility(View.GONE);
    }

    getMessages(lastMessageId);
    return root;
  }

  @Override public void onResume() {
    super.onResume();
    Log.e("KEK", "onResume() called");

    Timer t = new Timer();
    t.scheduleAtFixedRate(new TimerTask() {
      @Override public void run() {
        Log.e("Tas", "Called");
        getMessages(lastMessageId);
      }
    }, 0, 300000);
  }

  private void login(User user) {

    SpikaOSRetroApiInterface retroApiInterface =
        getRetrofit().create(SpikaOSRetroApiInterface.class);
    Call<Login> call = retroApiInterface.login(user);
    call.enqueue(new CustomResponse<Login>(getActivity(), true, true) {

      @Override public void onCustomSuccess(Call<Login> call, Response<Login> response) {
        super.onCustomSuccess(call, response);
        SingletonLikeApp.getInstance()
            .getSharedPreferences(getActivity())
            .setToken(response.body().data.token);
        SingletonLikeApp.getInstance()
            .getSharedPreferences(getActivity())
            .setUserId(response.body().data.token);

        connectToSocket();

        //progress is visible, and it is showed in login method
        if (firstTime) {
          boolean isInit = true;
          lastMessageId = null;
          getMessages(lastMessageId);

          firstTime = false;
        }
      }
    });
  }

  private void connectToSocket() {

    SocketManager.getInstance().setListener(socketListener);
    SocketManager.getInstance().connectToSocket(getActivity());
  }

  private void getMessages(String lastMessageId) {

    if (TextUtils.isEmpty(lastMessageId)) {
      lastMessageId = "0";
    }
    SpikaOSRetroApiInterface retroApiInterface =
        getRetrofit().create(SpikaOSRetroApiInterface.class);
    Call<GetMessagesModel> call = retroApiInterface.getMessages(user.roomID, lastMessageId,
        SingletonLikeApp.getInstance().getSharedPreferences(getActivity()).getToken());
    call.enqueue(new CustomResponse<GetMessagesModel>(getActivity(), true, true) {

      @Override public void onCustomSuccess(Call<GetMessagesModel> call,
          Response<GetMessagesModel> response) {
        super.onCustomSuccess(call, response);
        lastDataFromServer.clear();
        lastDataFromServer.addAll(response.body().data.messages);

        List<String> unReadMessages =
            SeenByUtils.getUnSeenMessages(response.body().data.messages, user);
        for (String s : unReadMessages) {
          Log.e("MESSAGE", s);
        }

        if (unReadMessages.size() > 0) {
          messageCounter.setVisibility(View.VISIBLE);
        } else {
          messageCounter.setVisibility(View.GONE);
        }

        messageCounter.setText(Integer.toString(unReadMessages.size()));
        Log.e("MESSAGE COUNT", "" + unReadMessages.size());
      }
    });
  }

  public Retrofit getRetrofit() {
    return client;
  }

  @Override public void onClick(View v) {
    Timber.e("Challenge click");
    int id = v.getId();
    switch (id) {
      case R.id.cv_mentor_challenge_container:
        openGroupDetailsActivity(true);
        Timber.e("Open MENTOR");
        break;
      case R.id.ll_will_container:
      case R.id.cv_challenge_container:
        openGroupDetailsActivity(false); // TODO: 11/2/17 set to false
        Timber.e("Open NOT MENTOR");
        break;
      default:
        Log.d(TAG,
            "Click on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }

  private void prepareMembers(int countMembers) {
    for (int i = 0; i < challengeMembers.length; i++) {
      ChallengeMember challengeMember = challengeMembers[i];
      if (challengeMember == null) {
        //nums[i].setVisibility(View.GONE);

      } else {

        int color = challengeMember.getColor();
        int num = countMembers - i;

        int holder = challengeMember.getHolder();
        String pictureUrl = challengeMember.getPictureUrl();
        if (TextUtils.isEmpty(pictureUrl)) {
        }
      }
    }
  }

  private void convertMembers(Member[] members, Member[] pending) {
    int countChallengeMembers = challengeMembers.length;
    int countRedMembers = Config.getCountRedMembers(members.length);

    for (int i = 0; i < countChallengeMembers; i++) {
      ChallengeMember member = null;
      if (i < members.length) {
        member = new ChallengeMember(members[i]);

        if (i < countRedMembers && member.getSteps() < Config.MAX_STEPS_PER_DAY) {
          member.setColor(red);
          member.setHolder(R.drawable.ic_red_avatar);
        } else {
          member.setColor(darkGray);
          member.setHolder(R.drawable.ic_gray_avatar);
        }
      } else if (i - members.length < pending.length) {
        member = new ChallengeMember(pending[i - members.length]);
        member.setColor(gray);
        member.setHolder(R.drawable.ic_gray_avatar);
        member.setPending(true);
      }

      challengeMembers[i] = member;
    }
  }

  private void openGroupDetailsActivity(boolean isMentorGroup) {
    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance()
        .trackEvent(App.getInstance().getApplicationContext(),
            "af_open_group_details_from_main_screen", eventValue);

    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).openGroupDetailsActivity(tracking.getId(), isMentorGroup,
          groupOwnerId);
    }
  }

  abstract Member[] getMembers();

  abstract Member[] getPendingMembers();
}