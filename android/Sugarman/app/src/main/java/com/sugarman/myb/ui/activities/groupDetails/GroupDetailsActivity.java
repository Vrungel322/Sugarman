package com.sugarman.myb.ui.activities.groupDetails;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.clover_studio.spikachatmodule.CameraPhotoPreviewActivity;
import com.clover_studio.spikachatmodule.ChatActivity;
import com.clover_studio.spikachatmodule.LocationActivity;
import com.clover_studio.spikachatmodule.RecordAudioActivity;
import com.clover_studio.spikachatmodule.RecordVideoActivity;
import com.clover_studio.spikachatmodule.adapters.MessageRecyclerViewAdapter;
import com.clover_studio.spikachatmodule.api.DownloadFileManager;
import com.clover_studio.spikachatmodule.api.UploadFileManagement;
import com.clover_studio.spikachatmodule.api.retrofit.CustomResponse;
import com.clover_studio.spikachatmodule.api.retrofit.SpikaOSRetroApiInterface;
import com.clover_studio.spikachatmodule.base.SingletonLikeApp;
import com.clover_studio.spikachatmodule.dialogs.DownloadFileDialog;
import com.clover_studio.spikachatmodule.dialogs.InfoMessageDialog;
import com.clover_studio.spikachatmodule.dialogs.NotifyDialog;
import com.clover_studio.spikachatmodule.dialogs.PreviewAudioDialog;
import com.clover_studio.spikachatmodule.dialogs.PreviewMessageDialog;
import com.clover_studio.spikachatmodule.dialogs.PreviewPhotoDialog;
import com.clover_studio.spikachatmodule.dialogs.PreviewVideoDialog;
import com.clover_studio.spikachatmodule.dialogs.SimpleProgressDialog;
import com.clover_studio.spikachatmodule.dialogs.UploadFileDialog;
import com.clover_studio.spikachatmodule.managers.socket.SocketManager;
import com.clover_studio.spikachatmodule.managers.socket.SocketManagerListener;
import com.clover_studio.spikachatmodule.models.Attributes;
import com.clover_studio.spikachatmodule.models.Config;
import com.clover_studio.spikachatmodule.models.GetMessagesModel;
import com.clover_studio.spikachatmodule.models.LocationModel;
import com.clover_studio.spikachatmodule.models.Login;
import com.clover_studio.spikachatmodule.models.Message;
import com.clover_studio.spikachatmodule.models.ParsedUrlData;
import com.clover_studio.spikachatmodule.models.SendTyping;
import com.clover_studio.spikachatmodule.models.UploadFileResult;
import com.clover_studio.spikachatmodule.models.User;
import com.clover_studio.spikachatmodule.utils.AnimUtils;
import com.clover_studio.spikachatmodule.utils.ApplicationStateManager;
import com.clover_studio.spikachatmodule.utils.BuildTempFileAsync;
import com.clover_studio.spikachatmodule.utils.Const;
import com.clover_studio.spikachatmodule.utils.EmitJsonCreator;
import com.clover_studio.spikachatmodule.utils.ErrorHandle;
import com.clover_studio.spikachatmodule.utils.LogCS;
import com.clover_studio.spikachatmodule.utils.OpenDownloadedFile;
import com.clover_studio.spikachatmodule.utils.ParseUrlLinkMetadata;
import com.clover_studio.spikachatmodule.utils.SeenByUtils;
import com.clover_studio.spikachatmodule.utils.Tools;
import com.clover_studio.spikachatmodule.view.menu.MenuManager;
import com.clover_studio.spikachatmodule.view.menu.OnMenuButtonsListener;
import com.clover_studio.spikachatmodule.view.menu.OnMenuManageListener;
import com.clover_studio.spikachatmodule.view.stickers.StickersManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Clear;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.clients.GetTrackingInfoClient;
import com.sugarman.myb.api.clients.PokeClient;
import com.sugarman.myb.api.models.responses.Group;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.eventbus.events.DebugRealStepAddedEvent;
import com.sugarman.myb.eventbus.events.DebugRefreshStepsEvent;
import com.sugarman.myb.eventbus.events.DebugRequestStepsEvent;
import com.sugarman.myb.eventbus.events.RefreshTrackingsEvent;
import com.sugarman.myb.listeners.ApiGetTrackingInfoListener;
import com.sugarman.myb.listeners.ApiPokeListener;
import com.sugarman.myb.listeners.OnStepMembersActionListener;
import com.sugarman.myb.models.GroupMember;
import com.sugarman.myb.models.iab.SubscriptionEntity;
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
import com.sugarman.myb.models.splash_activity.DataForMainActivity;
import com.sugarman.myb.ui.activities.addMember.AddMemberActivity;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.activities.groupDetails.adapter.GroupMembersAdapter;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.views.CropSquareTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.Converters;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.DialogHelper;
import com.sugarman.myb.utils.IntentExtractorHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.SoundHelper;
import com.sugarman.myb.utils.apps_Fly.AppsFlyerEventSender;
import com.sugarman.myb.utils.inapp.IabHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

public class GroupDetailsActivity extends BaseActivity
    implements View.OnClickListener, ApiPokeListener, OnStepMembersActionListener,
    ApiGetTrackingInfoListener, IGroupDetailsActivityView {
  private static final String TAG = GroupDetailsActivity.class.getName();
  private static final int TAKE_PICTURE = 12;
  private final int DELAY_REFRESH_GROUP_INFO = 5000;
  private final Handler handler = App.getHandlerInstance();
  protected boolean doNotHideProgressNow = false;
  protected boolean doNotShowProgressNow = false;
  protected Retrofit client;
  protected RecyclerView rvMessages;
  protected MenuManager menuManager;
  protected StickersManager stickersManager;
  protected List<String> sentMessages = new ArrayList<>();
  protected List<User> typingUsers = new ArrayList<>();
  //data from last paging
  protected List<Message> lastDataFromServer = new ArrayList<>();
  //for scroll when keyboard opens
  protected int lastVisibleItem = 0;
  @BindView(R.id.ivCancelSubscription) ImageView ivCancelSubscription;
  @InjectPresenter GroupDetailsActivityPresenter mPresenter;
  @BindView(R.id.tvOk) TextView tvOk;
  @BindView(R.id.pcSuccessRateToday) PieChart pieChart;
  @BindView(R.id.successRateStroke) ImageView successRateStroke;
  @BindView(R.id.rbMentor) AppCompatRatingBar mAppCompatRatingBarMentor;
  @BindView(R.id.etCommentBody) EditText mEditTextCommentBody;
  @BindView(R.id.ivEditMentor) ImageView ivEditMentor;
  @BindView(R.id.tv_group_steps) TextView groupSteps;
  @BindView(R.id.ivGroupRescueCircle) ImageView rescueCircle;
  @BindView(R.id.tvTotalGroupSteps) TextView tvTotalGroupSteps;
  @BindView(R.id.tvRescueTimer) TextView mTextViewRescueTimer;
  Thread thread = new Thread();
  FrameLayout parentLayout;
  View borderline;
  int[] location;
  //LinearLayout hiddenContentContainer;
  int[] brokenGlassIds;
  File pathToSerialize;
  boolean amIInGroup = false;
  //TextView sneekPeakTextView;
  TextView tvMySteps;
  boolean showChat = true;
  boolean blockChat = false;
  User user;
  SimpleProgressDialog dialog;
  Button chatTestBtn;
  RelativeLayout rlChat, rlInfo;
  ImageView attachButton;
  String timeFormatted;
  private boolean amIMentor = false;
  private RelativeLayout rlComments;
  private CardView cvCommentContainer;
  private TextView tvCancel;
  private ImageView ivMentorAvatar;
  private List<Member> lessThanYou = new ArrayList<>();
  private String trackingId;
  private String groupPictureUrl;
  private String groupName;
  private Member[] members = new Member[0];
  private Member[] pendings = new Member[0];
  private List<Member> failers = new ArrayList<>();
  private List<Member> savers = new ArrayList<>();
  private long timestampCreate;
  private boolean isEditable = false;
  private int assesCount = 0;
  //LinearLayout tabsContainer;
  private int todaySteps;
  private int groupStepsWithoutMe;
  private PokeClient mPokeClient;
  private View vEdit;
  private TextView tvAsses;
  private TextView tvGroupName;
  private TextView tvSteps;
  //protected TextView tvTyping;
  private ImageView ivGroupAvatar;
  private LinearLayout linearInfo, linearTimer;
  private TextView tvEditGroup, tvTimer;
  private TextView tvChatTab, tvInfoTab;
  private GroupMembersAdapter membersAdapter;
  private GetTrackingInfoClient mTrackingInfoClient;
  private final Runnable runnable = new Runnable() {
    @Override public void run() {
      mTrackingInfoClient.getTrackingInfo(trackingId);
    }
  };
  private User activeUser;
  private ListView settingsListView;
  private EditText etMessage;
  private RelativeLayout chatToHide, chatNotAvailable;
  private ImageButton btnSend;
  private ImageButton btnStickers;
  private ProgressBar pbAboveSend;
  private ChatActivity.ButtonType buttonType = ChatActivity.ButtonType.MENU;
  protected OnMenuManageListener onMenuManagerListener = new OnMenuManageListener() {
    @Override public void onMenuOpened() {
      buttonType = ChatActivity.ButtonType.MENU_OPENED;
    }

    @Override public void onMenuClosed() {
      buttonType = ChatActivity.ButtonType.MENU;
      etMessage.setEnabled(true);
      findViewById(R.id.viewForMenuBehind).setVisibility(View.GONE);
    }
  };
  private ChatActivity.StickersType stickersType = ChatActivity.StickersType.CLOSED;
  private ChatActivity.TypingType typingType = ChatActivity.TypingType.BLANK;
  protected TextWatcher etMessageTextWatcher = new TextWatcher() {
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override public void afterTextChanged(Editable s) {
      if (s.length() == 0) {
        animateSendButton(false);
      } else {
        animateSendButton(true);
      }
      sendTypingType(s.length());
    }
  };
  private TextView newMessagesButton;
  // is socket closed on pause
  private boolean pausedForSocket = false;
  // don't close socket when open camera or location or audio activity
  private boolean forceStaySocket = false;
  protected OnMenuButtonsListener onMenuButtonsListener = new OnMenuButtonsListener() {
    @Override public void onCameraClicked() {
      forceStaySocket = true;
      CameraPhotoPreviewActivity.starCameraPhotoPreviewActivity(GroupDetailsActivity.this);
      onButtonMenuOpenedClicked();
    }

    @Override public void onAudioClicked() {
      forceStaySocket = true;
      onButtonMenuOpenedClicked();
      if (ContextCompat.checkSelfPermission(GroupDetailsActivity.this,
          Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
          || ContextCompat.checkSelfPermission(GroupDetailsActivity.this,
          Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(GroupDetailsActivity.this, new String[] {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, Const.PermissionCode.MICROPHONE);
      } else if (ContextCompat.checkSelfPermission(GroupDetailsActivity.this,
          Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(GroupDetailsActivity.this,
            new String[] { Manifest.permission.RECORD_AUDIO }, Const.PermissionCode.MICROPHONE);
      } else if (ContextCompat.checkSelfPermission(GroupDetailsActivity.this,
          Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(GroupDetailsActivity.this,
            new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
            Const.PermissionCode.MICROPHONE);
      } else {
        RecordAudioActivity.starRecordAudioActivity(GroupDetailsActivity.this);
      }
    }

    private void onButtonMenuOpenedClicked() {
      if (buttonType == ChatActivity.ButtonType.IN_ANIMATION) {
        return;
      }
      buttonType = ChatActivity.ButtonType.IN_ANIMATION;

      menuManager.closeMenu();
    }

    @Override public void onFileClicked() {
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.setType("image/*");
      forceStaySocket = true;
      startActivityForResult(intent, Const.RequestCode.PICK_FILE);
      onButtonMenuOpenedClicked();
    }

    @Override public void onVideoClicked() {
      forceStaySocket = true;
      RecordVideoActivity.starVideoPreviewActivity(GroupDetailsActivity.this);
      onButtonMenuOpenedClicked();
    }

    @Override public void onLocationClicked() {
      forceStaySocket = true;
      onButtonMenuOpenedClicked();
      if (ContextCompat.checkSelfPermission(GroupDetailsActivity.this,
          Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(GroupDetailsActivity.this,
            new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
            Const.PermissionCode.LOCATION_MY);
      } else {
        LocationActivity.startLocationActivity(GroupDetailsActivity.this);
      }
    }

    @Override public void onGalleryClicked() {
      forceStaySocket = true;
      CameraPhotoPreviewActivity.starCameraFromGalleryPhotoPreviewActivity(
          GroupDetailsActivity.this);
      onButtonMenuOpenedClicked();
    }

    @Override public void onContactClicked() {
      if (ContextCompat.checkSelfPermission(GroupDetailsActivity.this,
          Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(GroupDetailsActivity.this,
            new String[] { Manifest.permission.READ_CONTACTS }, Const.PermissionCode.READ_CONTACTS);
      } else {
        requestContacts();
      }
      onButtonMenuOpenedClicked();
    }
  };
  // first time resume called
  private boolean firstTime = true;
  //message queue for unsent message when socket is not connected
  private List<Message> unSentMessageList = new ArrayList<>();
  //message queue for new message from latest api when listView is not at bottom
  private List<Message> unReadMessage = new ArrayList<>();
  private LocationModel tempLocationForPermission;
  protected MessageRecyclerViewAdapter.OnLastItemAndOnClickListener onLastItemAndClickItemListener =
      new MessageRecyclerViewAdapter.OnLastItemAndOnClickListener() {
        @Override public void onLastItem() {
          LogCS.e("LOG", "LAST ITEM");
          if (lastDataFromServer.size() < 50) {
            //no more paging
            LogCS.e("LOG", "NO MORE MESSAGES");
          } else {
            if (lastDataFromServer.size() > 0) {
              String lastMessageId = lastDataFromServer.get(lastDataFromServer.size() - 1)._id;
              boolean isInit = false;
              getMessages(isInit, lastMessageId);
            }
          }
        }

        private void downloadFile(Message item) {

          File file =
              new File(Tools.getDownloadFolderPath() + "/" + item.created + item.file.file.name);

          if (file.exists()) {
            OpenDownloadedFile.downloadedFileDialog(file, GroupDetailsActivity.this);
          } else {

            final DownloadFileDialog dialog =
                DownloadFileDialog.startDialog(GroupDetailsActivity.this);

            DownloadFileManager.downloadVideo(GroupDetailsActivity.this,
                Tools.getFileUrlFromId(item.file.file.id, GroupDetailsActivity.this), file,
                new DownloadFileManager.OnDownloadListener() {
                  @Override public void onStart() {
                    LogCS.d("LOG", "START UPLOADING");
                  }

                  @Override public void onSetMax(final int max) {
                    GroupDetailsActivity.this.runOnUiThread(new Runnable() {
                      @Override public void run() {
                        dialog.setMax(max);
                      }
                    });
                  }

                  @Override public void onProgress(final int current) {
                    GroupDetailsActivity.this.runOnUiThread(new Runnable() {
                      @Override public void run() {
                        dialog.setCurrent(current);
                      }
                    });
                  }

                  @Override public void onFinishDownload() {
                    GroupDetailsActivity.this.runOnUiThread(new Runnable() {
                      @Override public void run() {
                        dialog.fileDownloaded();
                      }
                    });
                  }

                  @Override public void onResponse(boolean isSuccess, final String path) {
                    GroupDetailsActivity.this.runOnUiThread(new Runnable() {
                      @Override public void run() {
                        dialog.dismiss();
                        OpenDownloadedFile.downloadedFileDialog(new File(path),
                            GroupDetailsActivity.this);
                      }
                    });
                  }
                });
          }
        }

        @Override public void onClickItem(final Message item) {
          if (item.deleted != -1 && item.deleted != 0) {
            return;
          }
          if (item.type == Const.MessageType.TYPE_FILE) {
            if (Tools.isMimeTypeImage(item.file.file.mimeType)) {
              PreviewPhotoDialog.startDialog(GroupDetailsActivity.this,
                  Tools.getFileUrlFromId(item.file.file.id, GroupDetailsActivity.this), item);
            } else if (Tools.isMimeTypeVideo(item.file.file.mimeType)) {
              PreviewVideoDialog.startDialog(GroupDetailsActivity.this, item.file);
            } else if (Tools.isMimeTypeAudio(item.file.file.mimeType)) {
              PreviewAudioDialog.startDialog(GroupDetailsActivity.this, item.file);
            } else {
              downloadFile(item);
            }
          } else if (item.type == Const.MessageType.TYPE_LOCATION) {
            forceStaySocket = true;
            if (ContextCompat.checkSelfPermission(GroupDetailsActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              tempLocationForPermission = item.location;
              ActivityCompat.requestPermissions(GroupDetailsActivity.this,
                  new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                  Const.PermissionCode.LOCATION_THEIR);
            } else {
              LocationActivity.startShowLocationActivity(GroupDetailsActivity.this,
                  item.location.lat, item.location.lng);
            }
          } else if (item.type == Const.MessageType.TYPE_CONTACT) {
            OpenDownloadedFile.selectedContactDialog(item.message, GroupDetailsActivity.this);
          } else {
            if (item.attributes != null && item.attributes.linkData != null) {
              Intent browserIntent =
                  new Intent(Intent.ACTION_VIEW, Uri.parse(item.attributes.linkData.url));
              startActivity(browserIntent);
            } else {
              // do nothing for now
            }
          }
        }

        private void openMessageInfoDialog(Message message) {
          PreviewMessageDialog.startDialog(GroupDetailsActivity.this, message, activeUser);
        }

        private void confirmDeleteMessage(final Message message) {
          NotifyDialog dialog = NotifyDialog.startConfirm(GroupDetailsActivity.this,
              getString(com.clover_studio.spikachatmodule.R.string.delete_message_title),
              getString(com.clover_studio.spikachatmodule.R.string.delete_message_text));
          dialog.setTwoButtonListener(new NotifyDialog.TwoButtonDialogListener() {
            @Override public void onOkClicked(NotifyDialog dialog) {
              dialog.dismiss();
              sendDeleteMessage(message._id);
            }

            @Override public void onCancelClicked(NotifyDialog dialog) {
              dialog.dismiss();
            }
          });
          //dialog.setButtonsText(getString(com.clover_studio.spikachatmodule.R.string.NO_CAPITAL), getString(com.clover_studio.spikachatmodule.R.string.YES_CAPITAL));
        }

        @Override public void onLongClick(Message item) {
          boolean showDelete = true;
          if (!activeUser.userID.equals(item.user.userID)
              || item.type == Const.MessageType.TYPE_NEW_USER
              || item.type == Const.MessageType.TYPE_USER_LEAVE) {

            showDelete = false;
          }

          boolean showCopy = true;
          if (item.type != Const.MessageType.TYPE_TEXT) {
            showCopy = false;
          }

          boolean showShare = false;
          if (item.type == Const.MessageType.TYPE_FILE && Tools.isMimeTypeImage(
              item.file.file.mimeType)) {
            showShare = true;
          }

          InfoMessageDialog.startDialogWithOptions(GroupDetailsActivity.this, item, activeUser,
              showCopy, showDelete, showShare, new InfoMessageDialog.OnInfoListener() {
                @Override public void onDeleteMessage(Message message, Dialog dialog) {
                  confirmDeleteMessage(message);
                }

                @Override public void onDetailsClicked(Message message, Dialog dialog) {
                  openMessageInfoDialog(message);
                }

                @Override public void onShareClicked(Message message, Dialog dialog) {
                  handleProgress(true);
                  File file = new File(
                      Tools.getImageFolderPath() + "/" + message.created + message.file.file.name);

                  if (file.exists()) {
                    Tools.shareImage(GroupDetailsActivity.this, file);
                  } else {
                    DownloadFileManager.downloadVideo(GroupDetailsActivity.this,
                        Tools.getFileUrlFromId(message.file.file.id, GroupDetailsActivity.this),
                        file, new DownloadFileManager.OnDownloadListener() {
                          @Override public void onStart() {
                          }

                          @Override public void onSetMax(int max) {
                          }

                          @Override public void onProgress(int current) {
                          }

                          @Override public void onFinishDownload() {
                          }

                          @Override public void onResponse(boolean isSuccess, String path) {
                            Tools.shareImage(GroupDetailsActivity.this, new File(path));
                          }
                        });
                  }
                }
              });
        }
      };

  private SocketManagerListener socketListener = new SocketManagerListener() {
    @Override public void onConnect() {
      LogCS.w("LOG", "CONNECTED TO SOCKET");
    }

    @Override public void onSocketFailed() {
      //GroupDetailsActivity.this.socketFailedDialog();
    }

    @Override public void onNewUser(Object... args) {
      Log.w("LOG", "new user, args" + args[0].toString());
    }

    @Override public void onLoginWithSocket() {
      GroupDetailsActivity.this.loginWithSocket();
    }

    @Override public void onUserLeft(User user) {
      GroupDetailsActivity.this.onUserLeft(user);
    }

    @Override public void onTyping(SendTyping typing) {
      GroupDetailsActivity.this.onTyping(typing);
    }

    @Override public void onMessageReceived(Message message) {
      GroupDetailsActivity.this.onMessageReceived(message);
    }

    @Override public void onMessagesUpdated(List<Message> messages) {

      GroupDetailsActivity.this.onMessagesUpdated(messages);
    }

    @Override public void onSocketError(int code) {
      GroupDetailsActivity.this.onSocketError(code);
    }
  };
  private BroadcastReceiverImplementation broadcastReceiverImplementation =
      new BroadcastReceiverImplementation();
  private Uri imageUri;
  private boolean isMentorGroup;
  private boolean isFailedGroup;
  private String mentorId;
  private Tracking mTracking;
  private boolean editMode = false;
  private MentorsCommentsEntity mComment;
  private IabHelper mHelper;
  private List<SubscriptionEntity> subscribeList = new ArrayList<>();
  private CountDownTimer mTimer;
  private CountDownTimer mCountDownGroupTimer;

  @OnClick(R.id.ivEditMentor) public void editMentorClicked() {

    AppsFlyerEventSender.sendEvent("af_edit_mode");

    editMode = !editMode;
    Timber.e("clicked edit " + editMode);
    membersAdapter.setEditMode(editMode);
  }

  @Override protected void onCreate(Bundle savedStateInstance) {
    setContentView(R.layout.activity_group_details);
    super.onCreate(savedStateInstance);
    Timber.e("deepLinks onCreate");

    Clear.clearCache(CustomPicasso.with(this));
    brokenGlassIds = SharedPreferenceHelper.getBrokenGlassIds();

    ivMentorAvatar = (ImageView) findViewById(R.id.ivMentorAvatar);

    tvCancel = (TextView) findViewById(R.id.tvCancel);

    tvCancel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        hideCommentsPanel();
      }
    });

    rlComments = (RelativeLayout) findViewById(R.id.rlCommentScreen);
    cvCommentContainer = (CardView) findViewById(R.id.cvCommentContainer);

    rlComments.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        hideCommentsPanel();
      }
    });
    cvCommentContainer.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

      }
    });

    attachButton = (ImageView) findViewById(R.id.attach_file);
    attachButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        menuManager.openMenu((ImageButton) attachButton);
        findViewById(R.id.viewForMenuBehind).setVisibility(View.VISIBLE);
      }
    });

    chatNotAvailable = (RelativeLayout) findViewById(R.id.chat_not_available);
    chatToHide = (RelativeLayout) findViewById(R.id.chat_to_hide);
    tvMySteps = (TextView) findViewById(R.id.tv_my_steps);

    linearInfo = (LinearLayout) findViewById(R.id.linear_group_stats);
    linearTimer = (LinearLayout) findViewById(R.id.linear_group_timer);

    tvEditGroup = (TextView) findViewById(R.id.tv_edit_group);
    tvEditGroup.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        AppsFlyerEventSender.sendEvent("af_edit_group");

        openAddMembersActivity();
      }
    });
    tvTimer = (TextView) findViewById(R.id.tv_timer);

    //sneekPeakTextView = (TextView) findViewById(R.id.sneekPeakTextView);

    client = new Retrofit.Builder().baseUrl(
        SingletonLikeApp.getInstance().getConfig(GroupDetailsActivity.this).apiBaseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    mPokeClient = new PokeClient();

    parentLayout = (FrameLayout) findViewById(R.id.parentLayout);

    setupUI(parentLayout, GroupDetailsActivity.this);

    borderline = findViewById(R.id.borderline);
    //hiddenContentContainer = (LinearLayout) findViewById(R.id.hiddenContentContainer);
    //if(!SharedPreferenceHelper.isChatEnabled())
    //hiddenContentContainer.setVisibility(View.GONE);
    location = new int[2];

    parentLayout.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          public void onGlobalLayout() {
            //Remove the listener before proceeding
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
              parentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
              parentLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            borderline.getLocationOnScreen(location);
            Log.e("BORDERLINE", "" + location[1]);
          }
        });

    View vCross = findViewById(R.id.iv_cross);
    RecyclerView rcvMembers = (RecyclerView) findViewById(R.id.rcv_group_members);
    vEdit = findViewById(R.id.iv_edit);
    tvGroupName = (TextView) findViewById(R.id.tv_group_name);
    tvAsses = (TextView) findViewById(R.id.tv_asses);
    tvSteps = (TextView) findViewById(R.id.tv_group_steps);
    ivGroupAvatar = (ImageView) findViewById(R.id.iv_group_avatar);

    rlChat = (RelativeLayout) findViewById(R.id.relative_chat);
    rlInfo = (RelativeLayout) findViewById(R.id.relative_info);
    // chatTestBtn = (Button) findViewById(R.id.chatTestButton);
    user = new User();
    user.roomID = "test"; // ->  id of room
    user.userID = SharedPreferenceHelper.getUserId(); // ->  id of user
    user.name = SharedPreferenceHelper.getUserName(); // ->  name of user
    user.avatarURL = SharedPreferenceHelper.getAvatar();//  ->  user avatar, this is optional

    activeUser = user;

    vCross.setOnClickListener(this);
    vEdit.setOnClickListener(this);

    trackingId = IntentExtractorHelper.getTrackingId(getIntent());

    isMentorGroup = getIntent().getBooleanExtra("isMentorGroup", false);
    mTrackingInfoClient = new GetTrackingInfoClient();
    mTrackingInfoClient.registerListener(this);
    mTrackingInfoClient.getTrackingInfo(trackingId);

    Timber.e("qqqq " + members.length);
    if (isMentorGroup) {

      pieChart.setVisibility(View.VISIBLE);
      groupSteps.setVisibility(View.GONE);
      tvTotalGroupSteps.setText(getResources().getString(R.string.success_rate));

      mentorId = getIntent().getStringExtra("mentorId");
      if (mentorId.equals(SharedPreferenceHelper.getUserId())) {
        amIMentor = true;
        ivEditMentor.setVisibility(View.VISIBLE);
      } else {
        ivCancelSubscription.setVisibility(View.VISIBLE);
      }
    } else {
      mentorId = "";
      pieChart.setVisibility(View.GONE);
      successRateStroke.setVisibility(View.GONE);
      groupSteps.setVisibility(View.VISIBLE);
      tvTotalGroupSteps.setText(getResources().getString(R.string.challenge_days));
    }

    isFailedGroup = getIntent().getBooleanExtra("isRescueGroup", false);
    if (isFailedGroup) {
      rescueCircle.setVisibility(View.VISIBLE);
      mTextViewRescueTimer.setVisibility(View.VISIBLE);

      tvTotalGroupSteps.setText(getResources().getString(R.string.challenge_days));
    }

    membersAdapter =
        new GroupMembersAdapter(getMvpDelegate(), this, this, trackingId, amIMentor, isMentorGroup,
            mentorId);
    rcvMembers.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    rcvMembers.setAdapter(membersAdapter);
    Timber.e("Mentor " + isMentorGroup + " " + mentorId);
    showProgressFragment();

    pathToSerialize = new File(getFilesDir() + "/" + trackingId);

    //CHAT****************************************************************************************
    SingletonLikeApp.getInstance().setApplicationState(this);

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
          Const.PermissionCode.CHAT_STORAGE);
    }

    if (getIntent().hasExtra("showchat")) {
      showChat = getIntent().getBooleanExtra("showchat", true);
    }
    if (getIntent().hasExtra("blockchat")) {
      blockChat = getIntent().getBooleanExtra("blockchat", false);
    }

    if (getIntent().hasExtra(Const.Extras.CONFIG)) {
      Config config = getIntent().getParcelableExtra(Const.Extras.CONFIG);
      SingletonLikeApp.getInstance().getSharedPreferences(this).setConfig(config);
      SingletonLikeApp.getInstance().setConfig(config);
    } else {
      Config config = new Config("", "");
      SingletonLikeApp.getInstance().getSharedPreferences(this).setConfig(config);
      SingletonLikeApp.getInstance().setConfig(null);
    }

    rvMessages = (RecyclerView) findViewById(R.id.rvMain);
    LinearLayoutManager llm = new LinearLayoutManager(this);
    llm.setStackFromEnd(true);
    rvMessages.setLayoutManager(llm);

    btnSend = (ImageButton) findViewById(R.id.btnSend);
    pbAboveSend = (ProgressBar) findViewById(R.id.loadingAboveSendButton);
    btnSend.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onSendMenuButtonClicked();
      }
    });

    etMessage = (EditText) findViewById(R.id.etMessage);
    etMessage.setImeOptions(EditorInfo.IME_ACTION_DONE);
    EditText.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
    etMessage.setOnFocusChangeListener(ofcListener);
    etMessage.addTextChangedListener(etMessageTextWatcher);
    etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          sendMessage();
        }
        return false;
      }
    });

    if (blockChat) {
      hideTabsAndSteps();
    }

    //tvTyping = (TextView) findViewById(R.id.toolbarSubtitle);

    menuManager = new MenuManager();
    menuManager.setMenuLayout(this, R.id.menuMain, onMenuManagerListener, onMenuButtonsListener);

    //check for user
    //        if (!getIntent().hasExtra(Const.Extras.USER)) {
    //            noUserDialog();
    //            return;
    //        } else {
    //            activeUser = getIntent().getParcelableExtra(Const.Extras.USER);
    //            if (activeUser == null) {
    //                noUserDialog();
    //                return;
    //            }
    //        }

    //tvTyping.setText(activeUser.userID);

    rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        lastVisibleItem =
            ((LinearLayoutManager) rvMessages.getLayoutManager()).findLastVisibleItemPosition();
        //                if (newMessagesButton.getVisibility() == View.VISIBLE) {
        //                    AnimUtils.fadeThenGoneOrVisible(newMessagesButton, 1, 0, 250);
        //                }
      }
    });

    //   newMessagesButton = (TextView) findViewById(R.id.newMessagesButton);
    //   newMessagesButton.setOnClickListener(new View.OnClickListener() {
    //       @Override
    //       public void onClick(View v) {
    //           scrollRecyclerToBottomWithAnimation();
    //       }
    //   });

    //for background state
    IntentFilter intentFilter = new IntentFilter(ApplicationStateManager.APPLICATION_PAUSED);
    intentFilter.addAction(ApplicationStateManager.APPLICATION_RESUMED);
    LocalBroadcastManager.getInstance(this)
        .registerReceiver(broadcastReceiverImplementation, intentFilter);

    rvMessages.setAdapter(
        new MessageRecyclerViewAdapter(new ArrayList<Message>(), activeUser, mentorId));

    try {
      FileInputStream fis = openFileInput(pathToSerialize.getName());
      ObjectInputStream is = null;
      is = new ObjectInputStream(fis);
      ArrayList<Message> messagesToRead = (ArrayList<Message>) is.readObject();
      Collections.reverse(messagesToRead);
      if (messagesToRead.size() > 0) {
        for (Message m : messagesToRead)
          Log.e("SERIALIZATION", m.message);
      }
      MessageRecyclerViewAdapter adapter = (MessageRecyclerViewAdapter) rvMessages.getAdapter();
      adapter.addMessages(messagesToRead, true);
      GroupDetailsActivity.this.onMessagesUpdated(messagesToRead);
      is.close();
      fis.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    //END CHAT ************************************************************************************************************

    setupInAppPurchase();
  }

  private void setUpPieChart() {
    int successCount = 0, totalCount = members.length - 1;

    for (Member m : members) {
      Timber.e("member qqqq" + m.getName() + " " + m.getSteps());
      if (!mentorId.equals(m.getId())) {
        if (m.getSteps() > 10000) {
          successCount++;
        }
      }
    }

    Timber.e("successCount:" + successCount);

    float successRateFloat = (float) successCount / (float) totalCount;
    Timber.e("successRateFloat " + successRateFloat);

    List<PieEntry> entries = new ArrayList<>();

    entries.add(new PieEntry(successRateFloat * 100f, ""));
    entries.add(new PieEntry(100 - successRateFloat * 100f, ""));

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
    pieChart.setData(data);
    pieChart.invalidate();
  }

  private void setupInAppPurchase() {
    mHelper = new IabHelper(this, com.sugarman.myb.constants.Config.BASE_64_ENCODED_PUBLIC_KEY);

    mHelper.startSetup(result -> {
      if (!result.isSuccess()) {
        Timber.e("In-app Billing setup failed: " + result);
      } else {
        Timber.e("In-app Billing is set up OK");
      }
    });
    mHelper.enableDebugLogging(true);
  }

  @OnClick(R.id.tvOk) public void tvOkClicked() {

    AppsFlyerEventSender.sendEvent("af_send_comment");

    mPresenter.sendComment(mentorId, mAppCompatRatingBarMentor.getRating(),
        mEditTextCommentBody.getText().toString());
  }

  private void hideCommentsPanel() {
    //AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.0f);
    //animation1.setDuration(5000);
    //animation1.setFillAfter(true);
    //rlComments.setAlpha(0.0f);
    //rlComments.startAnimation(animation1);
    //rlComments.setVisibility(View.GONE);

    AppsFlyerEventSender.sendEvent("af_cancel_comment");

    rlComments.animate().alpha(0.0f).setDuration(350).withEndAction(new Runnable() {
      @Override public void run() {
        rlComments.clearAnimation();
        rlComments.setVisibility(View.GONE);
      }
    }).start();
  }

  private void openCommentDialog() {
    // TODO: 03.11.2017 check if comments already exist for this mentor

    AppsFlyerEventSender.sendEvent("af_open_comment_screen");

    AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
    animation1.setDuration(350);
    animation1.setFillAfter(true);
    rlComments.setAlpha(1.0f);
    rlComments.startAnimation(animation1);
    rlComments.setVisibility(View.VISIBLE);
  }

  public void setupUI(View view, final Activity activity) {

    // Set up touch listener for non-text box views to hide keyboard.
    if (!(view instanceof EditText)) {
      view.setOnTouchListener(new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
          hideSoftKeyboard(activity);
          return false;
        }
      });
    }

    //If a layout container, iterate over children and seed recursion.
    if (view instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        View innerView = ((ViewGroup) view).getChildAt(i);
        setupUI(innerView, activity);
      }
    }
  }

  void hideTabsAndSteps() {

    tvSteps.setVisibility(View.GONE);
  }

  void hideSoftKeyboard(Activity activity) {

    InputMethodManager inputMethodManager =
        (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
  }

  private void scrollRecyclerToBottom() {
    rvMessages.scrollToPosition(rvMessages.getAdapter().getItemCount() - 1);
  }

  protected void socketFailedDialog() {
  }

  private void generateTypingString() {
    String typingText = "";
    for (User item : typingUsers) {
      typingText = typingText + item.name + ", ";
    }
    typingText = typingText.substring(0, typingText.length() - 2);

    if (typingUsers.size() > 1) {
      String typingTextSetText =
          typingText + " " + getString(com.clover_studio.spikachatmodule.R.string.are_typing);
      //  tvTyping.setText(typingTextSetText);
    } else {
      String typingTextSetText =
          typingText + " " + getString(com.clover_studio.spikachatmodule.R.string.is_typing);

      // tvTyping.setText(typingTextSetText);
    }
  }

  private void onUserLeft(User user) {
    if (typingUsers.contains(user)) {
      typingUsers.remove(user);
      runOnUiThread(new Runnable() {
        @Override public void run() {
          if (typingUsers.size() < 1) {
            //tvTyping.setText(activeUser.userID);
          } else {
            generateTypingString();
          }
        }
      });
    }
  }

  private void onMessageSent(Message sendMessage) {
    MessageRecyclerViewAdapter adapter = (MessageRecyclerViewAdapter) rvMessages.getAdapter();
    adapter.addSentMessage(sendMessage);
    sentMessages.add(sendMessage.localID);
    lastVisibleItem = adapter.getItemCount();
    scrollRecyclerToBottom();
  }

  private void onMessageReceived(final Message message) {
    final MessageRecyclerViewAdapter adapter = (MessageRecyclerViewAdapter) rvMessages.getAdapter();
    if (sentMessages.contains(message.localID)) {
      this.runOnUiThread(new Runnable() {
        @Override public void run() {
          adapter.setDeliveredMessage(message);
        }
      });
      sentMessages.remove(message.localID);
    } else {
      message.status = Const.MessageStatus.RECEIVED;
      this.runOnUiThread(new Runnable() {
        @Override public void run() {

          boolean toScrollBottom = false;
          LinearLayoutManager llManager = (LinearLayoutManager) rvMessages.getLayoutManager();
          if (llManager.findLastVisibleItemPosition()
              == rvMessages.getAdapter().getItemCount() - 1) {
            toScrollBottom = true;
          }

          adapter.addReceivedMessage(message);

          //if(toScrollBottom) {
          scrollRecyclerToBottom();
          // }else{
          //if(newMessagesButton.getVisibility() == View.GONE){
          //    AnimUtils.fadeThenGoneOrVisible(newMessagesButton, 0, 1, 250);
          //}
          //     }
        }
      });
      if (!message.user.userID.equals(activeUser.userID)) {
        sendOpenMessage(message._id);
      }
    }
    this.runOnUiThread(new Runnable() {
      @Override public void run() {
        lastVisibleItem = rvMessages.getAdapter().getItemCount();
      }
    });
  }

  private void onMessagesUpdated(final List<Message> messages) {

    Log.e("SERIALIZATION", "ZALOOOOOOPA");

    this.runOnUiThread(new Runnable() {
      @Override public void run() {
        // TODO: 23.08.2017 GET MESSAGES FROM CACHE HERE
        MessageRecyclerViewAdapter adapter = (MessageRecyclerViewAdapter) rvMessages.getAdapter();
        adapter.updateMessages(messages);
        scrollRecyclerToBottom();
      }
    });
  }

  public void sendUnSentMessages() {
    for (Message item : unSentMessageList) {
      JSONObject emitMessage = EmitJsonCreator.createEmitSendMessage(item);
      SocketManager.getInstance().emitMessage(Const.EmitKeyWord.SEND_MESSAGE, emitMessage);
    }
    unSentMessageList.clear();
  }

  private void onSocketError(final int code) {

    this.runOnUiThread(new Runnable() {
      @Override public void run() {
        NotifyDialog dialog = NotifyDialog.startInfo(GroupDetailsActivity.this,
            getString(com.clover_studio.spikachatmodule.R.string.error),
            ErrorHandle.getMessageForCode(code, getResources()));
      }
    });
  }

  private void onTyping(final SendTyping typing) {
    if (typing.user.userID.equals(activeUser.userID)) {
      return;
    }

    this.runOnUiThread(new Runnable() {
      @Override public void run() {
        if (typing.type == Const.TypingStatus.TYPING_OFF) {

          if (typingUsers.contains(typing.user)) {
            typingUsers.remove(typing.user);
          }

          if (typingUsers.size() < 1) {
            //tvTyping.setText(activeUser.userID);
          } else {
            generateTypingString();
          }
        } else {

          if (typingUsers.contains(typing.user)) {
            typingUsers.remove(typing.user);
          }

          typingUsers.add(typing.user);
          generateTypingString();
        }
      }
    });
  }

  @OnClick(R.id.ivCancelSubscription) void cancelSubscription() {
    DialogHelper.createSimpleDialog(getString(R.string.okay), getString(R.string.discard),
        getString(R.string.warning), getString(R.string.cancel_subscription_warning), this,
        //(dialogInterface, i) -> startCancelSubscribeFlow(),
        (dialogInterface, i) -> startCancelSubscribeFlowNewStyle(
            mTracking.getGroup().getOwner().getId()),
        (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
  }

  private void startCancelSubscribeFlowNewStyle(String mentorId) {
    mPresenter.getSlotToUnsubscribe(mentorId);
  }

  @Override public void unsubscribeMEntor(String slot) {
    mHelper.queryInventoryAsync(true, (result, inventory) -> {
      Timber.e(result.getMessage());
      Timber.e(inventory.getSkuDetails(slot).getTitle());
      Timber.e(inventory.getSkuDetails(slot).getSku());

      mPresenter.cancelSubscription(mentorId, inventory.getPurchase(slot));
    });
  }

  private void loginWithSocket() {
    JSONObject emitLogin = EmitJsonCreator.createEmitLoginMessage(activeUser);
    SocketManager.getInstance().emitMessage(Const.EmitKeyWord.LOGIN, emitLogin);

    GroupDetailsActivity.this.sendUnSentMessages();
  }

  private void scrollRecyclerToBottomWithAnimation() {
    rvMessages.smoothScrollToPosition(rvMessages.getAdapter().getItemCount() - 1);
  }

  /**
   * get retrofit client
   *
   * @return retrofit client
   */
  public Retrofit getRetrofit() {
    return client;
  }

  /**
   * show or hide loading progress
   *
   * @param showProgress to show loading progress
   */
  public void handleProgress(boolean showProgress) {

    if (doNotHideProgressNow) {
      doNotHideProgressNow = false;
      return;
    }

    if (doNotShowProgressNow) {
      doNotShowProgressNow = false;
      return;
    }

    try {

      if (showProgress) {

        if (dialog != null && dialog.isShowing()) {
          dialog.dismiss();
          dialog = null;
        }

        //dialog = new SimpleProgressDialog(this);
        //dialog.show();

      } else {

        if (dialog != null && dialog.isShowing()) {
          dialog.dismiss();
        }

        dialog = null;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void connectToSocket() {

    SocketManager.getInstance().setListener(socketListener);
    SocketManager.getInstance().connectToSocket(GroupDetailsActivity.this);
  }

  private void sendOpenMessage(String messageId) {
    List<String> messagesIds = new ArrayList<>();
    messagesIds.add(messageId);
    sendOpenMessage(messagesIds);
  }

  private void sendOpenMessage(List<String> messagesIds) {
    JSONObject emitOpenMessage =
        EmitJsonCreator.createEmitOpenMessage(messagesIds, activeUser.userID);
    SocketManager.getInstance().emitMessage(Const.EmitKeyWord.OPEN_MESSAGE, emitOpenMessage);
  }

  protected void sendDeleteMessage(String messageId) {
    JSONObject emitDeleteMessage =
        EmitJsonCreator.createEmitDeleteMessage(activeUser.userID, messageId);
    SocketManager.getInstance().emitMessage(Const.EmitKeyWord.DELETE_MESSAGE, emitDeleteMessage);
  }

  private void getMessages(final boolean isInit, String lastMessageId) {
    handleProgress(true);

    if (TextUtils.isEmpty(lastMessageId)) {
      lastMessageId = "0";
    }
    SpikaOSRetroApiInterface retroApiInterface =
        getRetrofit().create(SpikaOSRetroApiInterface.class);
    Call<GetMessagesModel> call = retroApiInterface.getMessages(activeUser.roomID, lastMessageId,
        SingletonLikeApp.getInstance().getSharedPreferences(GroupDetailsActivity.this).getToken());
    call.enqueue(new CustomResponse<GetMessagesModel>(GroupDetailsActivity.this, true, true) {

      @Override public void onCustomSuccess(Call<GetMessagesModel> call,
          Response<GetMessagesModel> response) {
        super.onCustomSuccess(call, response);
        lastDataFromServer.clear();
        lastDataFromServer.addAll(response.body().data.messages);
        MessageRecyclerViewAdapter adapter = (MessageRecyclerViewAdapter) rvMessages.getAdapter();
        if (isInit) {
          adapter.clearMessages();
          lastVisibleItem = response.body().data.messages.size();
        }
        boolean isPaging = !isInit;
        adapter.addMessages(response.body().data.messages, isPaging);

        ArrayList<Message> messagesToWrite = new ArrayList<>();
        messagesToWrite.addAll(response.body().data.messages);

        Log.e("SERIALIZATION", pathToSerialize.getAbsolutePath());
        try {
          FileOutputStream outStream = new FileOutputStream(pathToSerialize);
          ObjectOutputStream oos = new ObjectOutputStream(outStream);
          oos.writeObject(messagesToWrite);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }

        //                    if (isInit) {
        //                        scrollRecyclerToBottom();
        //                    } else {
        //                        int scrollToPosition = lastDataFromServer.size();
        //                        scrollRecyclerToPosition(scrollToPosition);
        //                    }

        List<String> unReadMessages =
            SeenByUtils.getUnSeenMessages(response.body().data.messages, activeUser);
        sendOpenMessage(unReadMessages);
      }
    });
  }

  private void login(User user) {

    handleProgress(true);
    SpikaOSRetroApiInterface retroApiInterface =
        getRetrofit().create(SpikaOSRetroApiInterface.class);
    Call<Login> call = retroApiInterface.login(user);
    call.enqueue(new CustomResponse<Login>(GroupDetailsActivity.this, true, true) {

      @Override public void onCustomSuccess(Call<Login> call, Response<Login> response) {
        doNotHideProgressNow = true;
        super.onCustomSuccess(call, response);
        SingletonLikeApp.getInstance()
            .getSharedPreferences(GroupDetailsActivity.this)
            .setToken(response.body().data.token);
        SingletonLikeApp.getInstance()
            .getSharedPreferences(GroupDetailsActivity.this)
            .setUserId(response.body().data.token);

        if (TextUtils.isEmpty(activeUser.avatarURL)) {
          activeUser.avatarURL = response.body().data.user.avatarURL;
        }
        connectToSocket();

        //progress is visible, and it is showed in login method
        if (firstTime) {
          doNotShowProgressNow = true;
          boolean isInit = true;
          String lastMessageId = null;
          getMessages(isInit, lastMessageId);

          firstTime = false;
        }
      }
    });
  }

  //    protected void noUserDialog() {
  //        NotifyDialog dialog = NotifyDialog.startInfo(this, getString(com.clover_studio.spikachatmodule.R.string.user_error_title), getString(com.clover_studio.spikachatmodule.R.string.user_error_not_sent));
  //        dialog.setOneButtonListener(new NotifyDialog.OneButtonDialogListener() {
  //            @Override
  //            public void onOkClicked(NotifyDialog dialog) {
  //                dialog.dismiss();
  //                finish();
  //            }
  //        });
  //    }

  private void sendLocation(String address, LatLng latLng) {

    Message message = new Message();
    message.fillMessageForSend(activeUser, address, Const.MessageType.TYPE_LOCATION, null, latLng);

    etMessage.setText("");

    if (SocketManager.getInstance().isSocketConnect()) {
      JSONObject emitMessage = EmitJsonCreator.createEmitSendMessage(message);
      SocketManager.getInstance().emitMessage(Const.EmitKeyWord.SEND_MESSAGE, emitMessage);
    } else {
      unSentMessageList.add(message);
    }

    onMessageSent(message);
  }

  public void requestContacts() {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
    forceStaySocket = true;
    startActivityForResult(intent, Const.RequestCode.CONTACT_CHOOSE);
  }

  protected void onSendMenuButtonClicked() {
    if (buttonType == ChatActivity.ButtonType.SEND) {
      onButtonSendClicked();
    } else if (buttonType == ChatActivity.ButtonType.MENU) {
      onButtonMenuClicked();
    } else if (buttonType == ChatActivity.ButtonType.MENU_OPENED) {
      onButtonMenuOpenedClicked();
    }
    InputMethodManager imm =
        (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(GroupDetailsActivity.this.getCurrentFocus().getWindowToken(), 0);
  }

  private void onButtonMenuOpenedClicked() {
    if (buttonType == ChatActivity.ButtonType.IN_ANIMATION) {
      return;
    }
    buttonType = ChatActivity.ButtonType.IN_ANIMATION;

    menuManager.closeMenu();
  }

  private void onButtonMenuClicked() {
    if (buttonType == ChatActivity.ButtonType.IN_ANIMATION) {
      return;
    }
    etMessage.setEnabled(false);
    buttonType = ChatActivity.ButtonType.IN_ANIMATION;

    menuManager.openMenu(btnSend);
    findViewById(R.id.viewForMenuBehind).setVisibility(View.VISIBLE);
  }

  protected void onButtonSendClicked() {
    sendMessage();
  }

  protected void sendMessage() {

    //***************************parse link*******************//
    boolean hasLink = false;
    String textMessage = etMessage.getText().toString();
    String checkForLink = Tools.checkForLink(textMessage);
    if (checkForLink != null) {
      hasLink = true;
      //set hasLink to true when attributes implements on api
    }

    final Message message = new Message();
    ivGroupAvatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onButtonMenuClicked();
      }
    });
    message.fillMessageForSend(activeUser, etMessage.getText().toString(),
        Const.MessageType.TYPE_TEXT, null, null);
    if (hasLink) {
      btnSend.setVisibility(View.INVISIBLE);
      pbAboveSend.setVisibility(View.VISIBLE);
      new ParseUrlLinkMetadata(checkForLink, new ParseUrlLinkMetadata.OnUrlParsed() {
        @Override public void onUrlParsed(ParsedUrlData data) {

          btnSend.setVisibility(View.VISIBLE);
          pbAboveSend.setVisibility(View.GONE);

          etMessage.setText("");

          Attributes att = new Attributes();
          att.linkData = data;
          message.attributes = att;

          if (SocketManager.getInstance().isSocketConnect()) {
            JSONObject emitMessage = EmitJsonCreator.createEmitSendMessage(message);
            SocketManager.getInstance().emitMessage(Const.EmitKeyWord.SEND_MESSAGE, emitMessage);
          } else {
            unSentMessageList.add(message);
          }

          onMessageSent(message);
        }
      }).execute();
    } else {
      etMessage.setText("");

      if (SocketManager.getInstance().isSocketConnect()) {
        JSONObject emitMessage = EmitJsonCreator.createEmitSendMessage(message);
        SocketManager.getInstance().emitMessage(Const.EmitKeyWord.SEND_MESSAGE, emitMessage);
      } else {
        unSentMessageList.add(message);
      }

      onMessageSent(message);
    }
  }

  private void animateSendButton(final boolean toSend) {
    if (toSend && buttonType == ChatActivity.ButtonType.SEND) {
      return;
    }
    if (toSend) {
      buttonType = ChatActivity.ButtonType.SEND;
    } else {
      buttonType = ChatActivity.ButtonType.MENU;
    }
    AnimUtils.fade(btnSend, 1, 0, 100, new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        if (toSend) {
          btnSend.setImageResource(R.drawable.send);
        } else {
          btnSend.setImageResource(R.drawable.attach);
        }
        AnimUtils.fade(btnSend, 0, 1, 100, null);
      }
    });
  }

  private void sendTypingType(int length) {
    if (length > 0 && typingType == ChatActivity.TypingType.BLANK) {
      setTyping(Const.TypingStatus.TYPING_ON);
      typingType = ChatActivity.TypingType.TYPING;
    } else if (length == 0 && typingType == ChatActivity.TypingType.TYPING) {
      setTyping(Const.TypingStatus.TYPING_OFF);
      typingType = ChatActivity.TypingType.BLANK;
    }
  }

  private void setTyping(int type) {
    JSONObject emitSendTyping = EmitJsonCreator.createEmitSendTypingMessage(activeUser, type);
    SocketManager.getInstance().emitMessage(Const.EmitKeyWord.SEND_TYPING, emitSendTyping);
  }

  @Override protected void onStart() {
    super.onStart();
    App.getEventBus().post(new DebugRequestStepsEvent(todaySteps));
    mPokeClient.registerListener(this);
  }

  @Override protected void onStop() {
    super.onStop();

    //SocketManager.getInstance().closeAndDisconnectSocket();

    mPokeClient.unregisterListener();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    SocketManager.getInstance().closeAndDisconnectSocket();
    mTrackingInfoClient.unregisterListener();
    if (mTimer != null) {
      mTimer.cancel();
    }
    if (mCountDownGroupTimer != null) {
      mCountDownGroupTimer.cancel();
      mCountDownGroupTimer = null;
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case Constants.ADD_MEMBER_ACTIVITY_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          onBackPressed();
        }
        break;
      case Const.RequestCode.PHOTO_CHOOSE:
        if (resultCode == RESULT_OK) {
          if (data != null && data.getExtras().containsKey(Const.Extras.UPLOAD_MODEL)) {
            UploadFileResult model = data.getExtras().getParcelable(Const.Extras.UPLOAD_MODEL);
            sendFile(model);
          }
        }
        break;
      case Const.RequestCode.PICK_FILE:
        if (resultCode == RESULT_OK) {
          getFile(data);
        }
        break;
      case Const.RequestCode.VIDEO_CHOOSE:
        if (resultCode == RESULT_OK) {
          if (data != null && data.getExtras().containsKey(Const.Extras.UPLOAD_MODEL)) {
            UploadFileResult model = data.getExtras().getParcelable(Const.Extras.UPLOAD_MODEL);
            sendFile(model);
          }
        }
        break;
      case Const.RequestCode.AUDIO_CHOOSE:
        if (resultCode == RESULT_OK) {
          if (data != null && data.getExtras().containsKey(Const.Extras.UPLOAD_MODEL)) {
            UploadFileResult model = data.getExtras().getParcelable(Const.Extras.UPLOAD_MODEL);
            sendFile(model);
          }
        }
        break;

      case TAKE_PICTURE:
        if (resultCode == Activity.RESULT_OK) {
          Uri selectedImage = imageUri;
          System.out.println("URI " + selectedImage.toString() + " " + data);
          if (selectedImage != null) {
            UploadFileResult model = data.getExtras().getParcelable(MediaStore.EXTRA_OUTPUT);
            sendFile(model);
          }
          final UploadFileDialog dialog = UploadFileDialog.startDialog(GroupDetailsActivity.this);

          UploadFileManagement tt = new UploadFileManagement();
          tt.new BackgroundUploader(
              SingletonLikeApp.getInstance().getConfig(GroupDetailsActivity.this).apiBaseUrl
                  + Const.Api.UPLOAD_FILE, new File(imageUri.getPath()),
              Const.ContentTypes.IMAGE_JPG, new UploadFileManagement.OnUploadResponse() {
            @Override public void onStart() {
              LogCS.d("LOG", "START UPLOADING");
            }

            @Override public void onSetMax(final int max) {
              GroupDetailsActivity.this.runOnUiThread(new Runnable() {
                @Override public void run() {
                  dialog.setMax(max);
                }
              });
            }

            @Override public void onProgress(final int current) {
              GroupDetailsActivity.this.runOnUiThread(new Runnable() {
                @Override public void run() {
                  dialog.setCurrent(current);
                }
              });
            }

            @Override public void onFinishUpload() {
              GroupDetailsActivity.this.runOnUiThread(new Runnable() {
                @Override public void run() {
                  dialog.fileUploaded();
                }
              });
            }

            @Override public void onResponse(final boolean isSuccess, final String result) {
              GroupDetailsActivity.this.runOnUiThread(new Runnable() {
                @Override public void run() {
                  dialog.dismiss();
                  if (!isSuccess) {
                    onResponseFailed();
                  } else {
                    onResponseFinish(result);
                  }
                }
              });
            }
          }).execute();
        }
        break;

      case Const.RequestCode.LOCATION_CHOOSE:
        if (resultCode == RESULT_OK) {
          if (data != null && data.getExtras().containsKey(Const.Extras.LATLNG)) {
            String address = null;
            if (data.getExtras().containsKey(Const.Extras.ADDRESS)) {
              address = data.getExtras().getString(Const.Extras.ADDRESS);
            }
            LatLng latLng = data.getExtras().getParcelable(Const.Extras.LATLNG);
            sendLocation(address, latLng);
          }
        }
        break;
      case Const.RequestCode.CONTACT_CHOOSE:
        if (resultCode == RESULT_OK) {
          Uri contactData = data.getData();
          Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
          try {
            int nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            if (cursor.moveToFirst()) {
              String name = cursor.getString(nameColumn);
              String lookupKey =
                  cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
              Uri uri =
                  Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
              AssetFileDescriptor fd;
              fd = getContentResolver().openAssetFileDescriptor(uri, "r");
              FileInputStream fis = fd.createInputStream();
              byte[] b = new byte[(int) fd.getDeclaredLength()];
              fis.read(b);
              String vCard = new String(b);

              sendContact(name, vCard);
            } else {
              NotifyDialog.startInfo(GroupDetailsActivity.this,
                  getString(com.clover_studio.spikachatmodule.R.string.contact_error_title),
                  getString(com.clover_studio.spikachatmodule.R.string.contact_error_select));
            }
            cursor.close();
          } catch (Exception ex) {
            cursor.close();
            NotifyDialog.startInfo(GroupDetailsActivity.this,
                getString(com.clover_studio.spikachatmodule.R.string.contact_error_title),
                getString(com.clover_studio.spikachatmodule.R.string.contact_error_select));
          }
        }
        break;
      default:
        break;
    }
  }

  private void getFile(Intent data) {
    Uri fileUri = data.getData();

    String fileName = null;
    String filePath = null;

    if (fileUri.getScheme().equals("content")) {

      String proj[];
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        proj = new String[] { MediaStore.Files.FileColumns.DISPLAY_NAME };
      } else {
        proj = new String[] {
            MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME
        };
      }
      Cursor cursor = getContentResolver().query(fileUri, proj, null, null, null);
      cursor.moveToFirst();

      int column_index_name = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
      int column_index_path = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);

      fileName = cursor.getString(column_index_name);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        try {
          new BuildTempFileAsync(this, fileName,
              new BuildTempFileAsync.OnTempFileCreatedListener() {
                @Override public void onTempFileCreated(String path, String name) {
                  if (TextUtils.isEmpty(path)) {
                    onFileSelected(RESULT_CANCELED, null, null);
                  } else {
                    onFileSelected(RESULT_OK, name, path);
                  }
                }
              }).execute(getContentResolver().openInputStream(fileUri));
          // async task initialized, exit
          cursor.close();
          return;
        } catch (FileNotFoundException ignored) {
          filePath = "";
        }
      } else {
        filePath = cursor.getString(column_index_path);
      }

      cursor.close();
    } else if (fileUri.getScheme().equals("file")) {

      File file = new File(URI.create(fileUri.toString()));
      fileName = file.getName();
      filePath = file.getAbsolutePath();

      if (!TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(filePath)) {
        onFileSelected(RESULT_OK, fileName, filePath);
      } else {
        onFileSelected(RESULT_CANCELED, null, null);
      }
    }
  }

  private void onFileSelected(int resultOk, String fileName, String filePath) {
    if (resultOk == RESULT_OK) {
      uploadFile(fileName, filePath);
    }
  }

  private void uploadFile(String fileName, String filePath) {

    String mimeType = Tools.getMimeType(filePath);
    if (TextUtils.isEmpty(mimeType)) {
      mimeType = Const.ContentTypes.OTHER;
    }

    final UploadFileDialog dialog = UploadFileDialog.startDialog(GroupDetailsActivity.this);

    UploadFileManagement tt = new UploadFileManagement();
    tt.new BackgroundUploader(
        SingletonLikeApp.getInstance().getConfig(GroupDetailsActivity.this).apiBaseUrl
            + Const.Api.UPLOAD_FILE, new File(filePath), mimeType,
        new UploadFileManagement.OnUploadResponse() {
          @Override public void onStart() {
            LogCS.d("LOG", "START UPLOADING");
          }

          @Override public void onSetMax(final int max) {
            GroupDetailsActivity.this.runOnUiThread(new Runnable() {
              @Override public void run() {
                dialog.setMax(max);
              }
            });
          }

          @Override public void onProgress(final int current) {
            GroupDetailsActivity.this.runOnUiThread(new Runnable() {
              @Override public void run() {
                dialog.setCurrent(current);
              }
            });
          }

          @Override public void onFinishUpload() {
            GroupDetailsActivity.this.runOnUiThread(new Runnable() {
              @Override public void run() {
                dialog.fileUploaded();
              }
            });
          }

          @Override public void onResponse(final boolean isSuccess, final String result) {
            GroupDetailsActivity.this.runOnUiThread(new Runnable() {
              @Override public void run() {
                dialog.dismiss();
                if (!isSuccess) {
                  onResponseFailed();
                } else {
                  onResponseFinish(result);
                }
              }
            });
          }
        }).execute();
  }

  private void onResponseFailed() {
    //NotifyDialog.startInfo(GroupDetailsActivity.this, getString(com.clover_studio.spikachatmodule.R.string.error), getString(com.clover_studio.spikachatmodule.R.string.file_not_found));
  }

  private void onResponseFinish(String result) {
    Gson gson = new Gson();

    UploadFileResult data = null;
    try {
      data = gson.fromJson(result, UploadFileResult.class);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (data != null) {
      sendFile(data);
    }
  }

  protected void sendFile(UploadFileResult result) {
    Message message = new Message();
    message.fillMessageForSend(activeUser, "", Const.MessageType.TYPE_FILE, result.data, null);

    etMessage.setText("");

    if (SocketManager.getInstance().isSocketConnect()) {
      JSONObject emitMessage = EmitJsonCreator.createEmitSendMessage(message);
      SocketManager.getInstance().emitMessage(Const.EmitKeyWord.SEND_MESSAGE, emitMessage);
    } else {
      unSentMessageList.add(message);
    }

    onMessageSent(message);
  }

  protected void sendContact(String name, String vCardLikeString) {
    Message message = new Message();
    message.fillMessageForSend(activeUser, vCardLikeString, Const.MessageType.TYPE_CONTACT, null,
        null);

    etMessage.setText("");

    if (SocketManager.getInstance().isSocketConnect()) {
      JSONObject emitMessage = EmitJsonCreator.createEmitSendMessage(message);
      SocketManager.getInstance().emitMessage(Const.EmitKeyWord.SEND_MESSAGE, emitMessage);
    } else {
      unSentMessageList.add(message);
    }

    onMessageSent(message);
  }

  @Override public void onBackPressed() {
    if (rlComments.getVisibility() == View.VISIBLE) {
      hideCommentsPanel();
    } else {
      Intent data = new Intent();
      data.putExtra(Constants.INTENT_TRACKING_ID, trackingId);
      setResult(RESULT_OK, data);
      startMainActivity();
    }
  }

  private void startMainActivity() {
    DataForMainActivity dataForMainActivity = SharedPreferenceHelper.getSavedDataForMainActivity();
    Intent intent = new Intent(GroupDetailsActivity.this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    if (dataForMainActivity != null) {
      intent.putExtra(Constants.INTENT_MY_TRACKINGS, dataForMainActivity.getTrackings());
      intent.putExtra(Constants.INTENT_MY_INVITES, dataForMainActivity.getInvites());
      intent.putExtra(Constants.INTENT_MY_REQUESTS, dataForMainActivity.getRequests());
      intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, dataForMainActivity.getOpenActivityCode());
      intent.putExtra(Constants.INTENT_FCM_TRACKING_ID, dataForMainActivity.getTrackingIdFromFcm());
      intent.putExtra(Constants.INTENT_MY_NOTIFICATIONS, dataForMainActivity.getNotifications());
    }

    startActivity(intent);
    setResult(Activity.RESULT_CANCELED);
    finishAffinity();
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.iv_edit:
        massPokeMember();
        break;
      case R.id.iv_cross:
        onBackPressed();
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
      case DialogConstants.API_GET_TRACKIN_INFO_FAILURE_ID:
        dialog.dismiss();
        onBackPressed();
        break;
      default:
        super.onClickDialog(dialog, button);
        break;
    }
  }

  @Override public void onApiGetTrackingInfoSuccess(Tracking tracking,
      List<MentorsCommentsEntity> commentsEntities, String successRate) {
    Timber.e("onApiGetTrackingInfoSuccess " + successRate);
    if (tracking != null) {
      mTracking = tracking;
      setUpPieChart();

      if (commentsEntities != null && commentsEntities.size() > 0) {
        mComment = commentsEntities.get(0);
      }
      if (mTracking.getRemainToFailUTCDate() != null) {
        mTimer = new CountDownTimer(
            mTracking.getRemainToFailUTCDate().getTime() - System.currentTimeMillis(), 1000) {
          @Override public void onTick(long l) {
            mTextViewRescueTimer.setText(String.format(getString(R.string.rescue_timer),
                Converters.timeFromMilliseconds(getApplicationContext(), l)));
          }

          @Override public void onFinish() {
            mTextViewRescueTimer.setText(String.format(getString(R.string.rescue_timer),
                Converters.timeFromMilliseconds(getApplicationContext(), 1L)));
          }
        }.start();
      }

      Group group = tracking.getGroup();
      members = tracking.getMembers();
      pendings = tracking.getPending();
      timestampCreate = tracking.getCreatedAtUTCDate().getTime();
      String groupId = tracking.getId();

      savers.clear();
      failers.clear();

      for (Member member : members) {
        if (member.getFailureStatus() == Member.FAIL_STATUS_SAVED) savers.add(member);
        if (member.getFailureStatus() == Member.FAIL_STATUS_FAILUER) failers.add(member);
      }

      tvSteps.setText("" + failers.size());

      //if (!thread.isAlive()) {
      //  thread = new Thread(new MyThread(tracking));
      //  thread.start();
      //}

      startCountDownTimer(tracking);

      if (mComment != null && rlComments.getVisibility() == View.GONE) {
        mEditTextCommentBody.setText(mComment.getAuthorsComment());
        mAppCompatRatingBarMentor.setRating(Float.valueOf(mComment.getAuthorsRating()));
      }

      lessThanYou.clear();
      for (Member member : members) {
        if (member.getId().equals(SharedPreferenceHelper.getUserId())) amIInGroup = true;
        if (member.getSteps() < SharedPreferenceHelper.getShowedSteps() && !member.getId()
            .equals(SharedPreferenceHelper.getUserId()) && member.getSteps() < 10000) {
          if (!mentorId.equals(member.getId())) lessThanYou.add(member);
          Log.e("LESS THAN YOU", "" + member.getName());
        }
      }
      if (amIInGroup) {
        chatToHide.setVisibility(View.VISIBLE);
        chatNotAvailable.setVisibility(View.GONE);
      } else {
        chatToHide.setVisibility(View.GONE);
        chatNotAvailable.setVisibility(View.VISIBLE);
      }

      final float day = (System.currentTimeMillis() - tracking.getStartUTCDate().getTime())
          / (float) Constants.MS_IN_DAY;
      if (day <= 0) {
      } else if (day - (int) day > 0) {
        tvSteps.setText(Integer.toString((int) (day + 1)) + "/21");
      } else {
        tvSteps.setText(Integer.toString((int) (day)) + "/21");
      }

      if (!activeUser.roomID.equals(groupId)) {
        activeUser.roomID = groupId;

        //rvMessages.setAdapter(new MessageRecyclerViewAdapter(new ArrayList<Message>(), activeUser));
        ((MessageRecyclerViewAdapter) rvMessages.getAdapter()).setLastItemListener(
            onLastItemAndClickItemListener);

        login(activeUser);
      }
      Log.d("GROUP ID", groupId);

      if (TextUtils.equals(tracking.getGroupOwnerId(), SharedPreferenceHelper.getUserId())) {
        Log.e("AM I ADMIN?", ""
            + TextUtils.equals(tracking.getGroupOwnerId(), SharedPreferenceHelper.getUserId())
            + "\n"
            + tracking.getGroupOwnerId()
            + "\n"
            + SharedPreferenceHelper.getUserId());
        vEdit.setVisibility(View.VISIBLE);
        long diff = System.currentTimeMillis() - tracking.getStartUTCDate().getTime();

        if (diff < 0 && tracking.getGroupOwnerId().equals(SharedPreferenceHelper.getUserId())) {
          isEditable = true;
        }
      }

      Date startedDate = tracking.getStartUTCDate();

      if (System.currentTimeMillis() < startedDate.getTime()) {
        linearInfo.setVisibility(View.GONE);
        linearTimer.setVisibility(View.VISIBLE);
        if (isEditable) {
          tvEditGroup.setVisibility(View.VISIBLE);
        } else {
          tvEditGroup.setVisibility(View.GONE);
        }
      } else {
        linearInfo.setVisibility(View.VISIBLE);
        linearTimer.setVisibility(View.GONE);
      }

      // A check to compare the dates of when the challenge started to the current date.
      // it is needed for showing pendings. In future this may be deleted as the server won't return pendings anymore.
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

      if (tracking.getStatus().equals("in progress")) {
        pendings =
            new Member[0];                                                       // We set pendings to none
      }

      membersAdapter.setMySteps(todaySteps);
      int stepsCount = SharedPreferenceHelper.getShowedSteps();
      String stepsCountFormatted = todaySteps < 1000 ? String.valueOf(todaySteps)
          : String.format(Locale.US, "%,d", todaySteps);
      tvMySteps.setText(stepsCountFormatted);
      membersAdapter.setValues(Arrays.asList(members), Arrays.asList(pendings));

      //membersAdapter.addMember(0, new GroupMember());

      groupName = group.getName();
      tvGroupName.setText(groupName);

      assesCount = 0;
      for (Member member : members) {
        assesCount += member.getKickCount();
      }

      String assesPlural =
          getResources().getQuantityString(R.plurals.asses_have_been_kicked, assesCount,
              assesCount);
      String assessFormatted =
          String.format(getString(R.string.plural_decimal_template), assesCount, assesPlural);
      tvAsses.setText(Integer.toString(assesCount));

      groupStepsWithoutMe = tracking.getGroupStepsCountWithoutMe();
      if (!isFailedGroup) setNumberOfUsers();
      membersAdapter.setMySteps(todaySteps);

      groupPictureUrl = group.getPictureUrl();
      if (TextUtils.isEmpty(groupPictureUrl)) {
        ivGroupAvatar.setImageResource(R.drawable.ic_group);
      } else {
        CustomPicasso.with(this)
            .load(groupPictureUrl)
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_group)
            .transform(new CropSquareTransformation())
            .transform(new MaskTransformation(this, R.drawable.profile_mask, false, 0xffffffff))
            .into(ivGroupAvatar, new Callback() {
              @Override public void onSuccess() {

                //   ivGroupAvatar.setBorderColor(0xffff0000);
                //   ivGroupAvatar.setBorderSize(25f);

              }

              @Override public void onError() {

              }
            });

        CustomPicasso.with(this)
            .load(groupPictureUrl)
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_group)
            .transform(new CropSquareTransformation())
            .transform(new MaskTransformation(this, R.drawable.profile_mask, false, 0xffffffff))
            .into(ivMentorAvatar);
      }

      ivGroupAvatar.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {

          if (isMentorGroup && !amIMentor) {
            openCommentDialog();
          }
        }
      });
      closeProgressFragment();
      handler.postDelayed(runnable, DELAY_REFRESH_GROUP_INFO);
    }
  }

  private void startCountDownTimer(Tracking tracking) {
    mCountDownGroupTimer = new CountDownTimer(tracking.getStartUTCDate().getTime(), 1000L) {
      @Override public void onTick(long l) {
        long startTimestamp = tracking.getStartUTCDate().getTime();
        long timeMs = startTimestamp - System.currentTimeMillis();

        NumberFormat timeFormatter = new DecimalFormat("00");
        float day = (System.currentTimeMillis() - tracking.getStartUTCDate().getTime())
            / (float) Constants.MS_IN_DAY;
        int days = (int) (timeMs / Constants.MS_IN_DAY);
        int hours = (int) ((timeMs % Constants.MS_IN_DAY) / Constants.MS_IN_HOUR);
        int minutes = (int) ((timeMs % Constants.MS_IN_HOUR) / Constants.MS_IN_MIN);
        int sec = (int) ((timeMs % Constants.MS_IN_MIN) / Constants.MS_IN_SEC);
        String timerTemplate = getString(R.string.timer_template);
        timeFormatted =
            String.format(timerTemplate, timeFormatter.format(days), timeFormatter.format(hours),
                timeFormatter.format(minutes), timeFormatter.format(sec));
        tvTimer.setText(timeFormatted);
      }

      @Override public void onFinish() {

      }
    }.start();
  }

  @Override public void onApiGetTrackingInfoFailure(String message) {
    if (DeviceHelper.isNetworkConnected()) {
      // new SugarmanDialog.Builder(this, DialogConstants.API_GET_TRACKIN_INFO_FAILURE_ID)
      //         .content(message)
      //         .show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onApiPokeSuccess() {
    DeviceHelper.vibrate();
    assesCount++;
    String assesPlural =
        getResources().getQuantityString(R.plurals.asses_have_been_kicked, assesCount, assesCount);
    String assessFormatted =
        String.format(getString(R.string.plural_decimal_template), assesCount, assesPlural);
    tvAsses.setText(Integer.toString(assesCount));
  }

  @Override public void onApiPokeFailure(String message) {
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.API_POKE_FAILURE_ID).content(message).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  public void massPokeMember() {
    boolean meFailuer = false;
    for (Member m : mTracking.getMembers()) {
      if (m.getId().equals(SharedPreferenceHelper.getUserId())
          && m.getFailureStatus() == Member.FAIL_STATUS_FAILUER) {
        meFailuer = true;
      }
    }
    if (meFailuer) {
      showToastMessage(R.string.failuer_can_not_poke_anyone);
    } else {
      if (amIInGroup) {

        AppsFlyerEventSender.sendEvent("af_kick_all");

        if (lessThanYou.size() > 0) {
          for (Member m : lessThanYou) {
            onPokeMember(new GroupMember(m));
          }
          new SugarmanDialog.Builder(this, "kicked_users").content(
              String.format(getString(R.string.kicked_users), (int) (lessThanYou.size()))).show();
          int id = new Random().nextInt(7);
          SoundHelper.play(brokenGlassIds[id]);
        } else {
          new SugarmanDialog.Builder(this, "you_cant_kick_anybody").content(
              getString(R.string.cant_kick_anybody)).show();
        }
      } else {
        new SugarmanDialog.Builder(this, "you_cant_kick_not_in_group").content(
            getString(R.string.cant_kick_not_in_group)).show();
      }
    }
  }

  @Override public void onPokeMember(GroupMember member) {
    if (member.getAction() == member.ACTION_LAZY) mPokeClient.poke(member.getId(), trackingId);
    /*    new SugarmanDialog.Builder(this, DialogConstants.YOU_CANT_KICK_PENDING_ID)
                .content(String.format(getString(R.string.kicked_user), member.getName()))
                .show();*/
  }

  @Override public void onPokePendingMember() {
    new SugarmanDialog.Builder(this, DialogConstants.YOU_CANT_KICK_PENDING_ID).content(
        R.string.you_cant_kick_pending).show();
  }

  @Override public void onPokeSelf() {
    new SugarmanDialog.Builder(this, DialogConstants.YOU_CANT_KICK_SELF_ID).content(
        R.string.you_cant_kick_self).show();
  }

  @Override public void onPokeCompletedDaily() {
    new SugarmanDialog.Builder(this, DialogConstants.THIS_USER_HAS_COMPLETED_DAILY_ID).content(
        R.string.this_user_has_completed_daily).show();
  }

  @Override public void onPokeSaver() {
    new SugarmanDialog.Builder(this, DialogConstants.THIS_USER_HAS_SAVED_THE_GROUP).content(
        R.string.this_user_has_saved_the_group).show();
  }

  @Override public void onPokeMentor() {
    new SugarmanDialog.Builder(this, DialogConstants.THIS_USER_HAS_SAVED_THE_GROUP).content(
        R.string.you_cant_kick_mentor).show();
  }

  @Override public void youCantPokeYourself() {
    showToastMessage(R.string.you_cant_kick_yourself);
  }

  @Override public void failuerCantPokeAnyone() {
    showToastMessage(R.string.failuer_can_not_poke_anyone);
  }

  @Override public void onPokeMoreThatSelf() {
    new SugarmanDialog.Builder(this, DialogConstants.YOU_CANT_KICK_MORE_THAT_SELF_ID).content(
        R.string.you_cant_kick_more_that_self).show();
  }

  @Override public void onPokeInForeignGroup() {
    new SugarmanDialog.Builder(this, DialogConstants.YOU_CANT_KICK_MORE_THAT_SELF_ID).content(
        R.string.you_cant_kick_foreign_users).show();
  }

  @Subscribe public void onEvent(DebugRefreshStepsEvent event) {
    todaySteps = event.getStartValue() + event.getRealSteps();

    mTrackingInfoClient = new GetTrackingInfoClient();
    mTrackingInfoClient.registerListener(this);
    mTrackingInfoClient.getTrackingInfo(trackingId);
  }

  @Subscribe public void onEvent(DebugRealStepAddedEvent event) {
    todaySteps = event.getStepsCalculated();
    setActualData();
    //setGroupSteps();
  }

  @Subscribe public void onEvent(RefreshTrackingsEvent event) {
    if (TextUtils.equals(event.getTrackingId(), trackingId) && mTrackingInfoClient != null) {
      showProgressFragment();
      mTrackingInfoClient.getTrackingInfo(trackingId);
    }
  }

  private void setActualData() {
    membersAdapter.setMySteps(todaySteps);
  }

  private void setNumberOfUsers() {
    //tvSteps.setText("" + mTracking.getMembers().length);
  }

  private void openAddMembersActivity() {
    Intent intent = new Intent(GroupDetailsActivity.this, AddMemberActivity.class);

    Member[] membersTemp = new Member[100];
    ArrayList<Member> members1 = new ArrayList<Member>(Arrays.asList(members));
    Collections.addAll(members1, pendings);
    membersTemp = members1.toArray(new Member[members1.size()]);
    //Timber.e("pending " + pendings[0].getName());
    //Timber.e("members " + members[0].getName());
    Timber.e("members1 " + members1.size());

    intent.putExtra(Constants.INTENT_MEMBERS, membersTemp);
    intent.putExtra(Constants.INTENT_PENDINGS, pendings);
    intent.putExtra(Constants.INTENT_TRACKING_ID, trackingId);
    intent.putExtra(Constants.INTENT_TIMESTAMP_CREATE, timestampCreate);
    intent.putExtra(Constants.INTENT_GROUP_NAME, groupName);
    intent.putExtra(Constants.INTENT_GROUP_PICTURE, groupPictureUrl);
    startActivityForResult(intent, Constants.ADD_MEMBER_ACTIVITY_REQUEST_CODE);
  }

  @Override public void closeDialog() {
    hideCommentsPanel();
  }

  @Override public void removeUser() {

  }

  @Override public void moveToMainActivity() {
    Intent intent = new Intent(GroupDetailsActivity.this, MainActivity.class);
    //intent.putExtra(IntroActivity.CODE_IS_OPEN_LOGIN_ACTIVITY, true);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  //private void startCancelSubscribeFlow() {
  //
  //  subscribeList = SharedPreferenceHelper.getListSubscriptionEntity();
  //  Timber.e("startUnSubscribeFlow size " + subscribeList.size());
  //  String slot = "";
  //  for (int i = 0; i < subscribeList.size(); i++) {
  //    if (subscribeList.get(i).getMentorId().equals(mTracking.getGroupOwnerId())) {
  //      slot = subscribeList.get(i).getSlot();
  //      Timber.e("startUnSubscribeFlow slot " + slot);
  //      String finalSlot = slot;
  //      mHelper.queryInventoryAsync(true, (result, inventory) -> {
  //        Timber.e(result.getMessage());
  //        Timber.e(inventory.getSkuDetails(finalSlot).getTitle());
  //        Timber.e(inventory.getSkuDetails(finalSlot).getSku());
  //
  //        mPresenter.cancelSubscription(inventory.getPurchase(finalSlot),
  //            inventory.getSkuDetails(finalSlot).getTitle(), mTracking.getGroupOwnerId(),
  //            finalSlot);
  //      });
  //    }
  //  }
  //}

  public enum ButtonType {
    MENU, SEND, MENU_OPENED, IN_ANIMATION;
  }

  public enum StickersType {
    CLOSED, OPENED, IN_ANIMATION;
  }

  public enum TypingType {
    TYPING, BLANK;
  }

  class MyFocusChangeListener implements View.OnFocusChangeListener {

    public void onFocusChange(View v, boolean hasFocus) {

      if (hasFocus) {

        InputMethodManager imm =
            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(GroupDetailsActivity.this.getCurrentFocus().getWindowToken(),
            0);
      }
    }
  }

  private class BroadcastReceiverImplementation extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(ApplicationStateManager.APPLICATION_PAUSED)) {
        //LogCS.e("******* PAUSE *******");
        //SocketManager.getInstance().closeAndDisconnectSocket();
        //pausedForSocket = true;
      } else if (intent.getAction().equals(ApplicationStateManager.APPLICATION_RESUMED)) {
        LogCS.e("******* RESUMED *******" + GroupDetailsActivity.this.getClass().getName());
        if (pausedForSocket) {
          SocketManager.getInstance().setListener(socketListener);
          SocketManager.getInstance().tryToReconnect(GroupDetailsActivity.this);
          pausedForSocket = false;
        }
      }
    }
  }

  class MyThread implements Runnable {
    Tracking tracking;

    public MyThread(Tracking tracking) {
      this.tracking = tracking;
    }

    @Override public void run() {
      while (true) {

        long startTimestamp = tracking.getStartUTCDate().getTime();
        long timeMs = startTimestamp - System.currentTimeMillis();

        NumberFormat timeFormatter = new DecimalFormat("00");
        float day = (System.currentTimeMillis() - tracking.getStartUTCDate().getTime())
            / (float) Constants.MS_IN_DAY;
        int days = (int) (timeMs / Constants.MS_IN_DAY);
        int hours = (int) ((timeMs % Constants.MS_IN_DAY) / Constants.MS_IN_HOUR);
        int minutes = (int) ((timeMs % Constants.MS_IN_HOUR) / Constants.MS_IN_MIN);
        int sec = (int) ((timeMs % Constants.MS_IN_MIN) / Constants.MS_IN_SEC);
        Timber.e("timer in groupDetails " + days + " " + hours + " " + minutes + " " + sec);
        Timber.e("timer in groupDetails milis :" + timeMs);
        Timber.e(
            "timer in groupDetails date from tracking :" + tracking.getStartUTCDate().getTime());
        String timerTemplate = getString(R.string.timer_template);
        timeFormatted =
            String.format(timerTemplate, timeFormatter.format(days), timeFormatter.format(hours),
                timeFormatter.format(minutes), timeFormatter.format(sec));

        runOnUiThread(new Runnable() {
          @Override public void run() {
            tvTimer.setText(timeFormatted);
          }
        });
        try {
          Thread.currentThread().sleep(1000);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }
    }
  }
  ///////////////////////////////////////////////////////////////////////////////////////////CHAT PART OF THE CODE

  //    RecyclerView rvChatWindow = (RecyclerView) findViewById(R.id.rvMain);
  //    MessageRecyclerViewAdapter adapter = new MessageRecyclerViewAdapter()
}
