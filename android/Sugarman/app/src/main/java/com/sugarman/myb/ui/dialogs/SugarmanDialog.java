package com.sugarman.myb.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sugarman.myb.R;
import com.sugarman.myb.listeners.SugarmanDialogListener;
import java.lang.ref.WeakReference;

public class SugarmanDialog extends Dialog implements View.OnClickListener {

  private static final String TAG = SugarmanDialog.class.getName();

  private WeakReference<SugarmanDialogListener> actionListener = new WeakReference<>(null);

  private TextView tvTitle;
  private TextView tvContent;
  private TextView tvPositiveText;
  private TextView tvNegativeText;
  private View vBtnsDivider;

  private final String id;

  private SugarmanDialog(Builder builder) {
    super(builder.mContext, R.style.SugarmanDialog);
    id = builder.id;

    requestWindowFeature(Window.FEATURE_NO_TITLE);

    LayoutInflater inflater = LayoutInflater.from(builder.mContext);
    setContentView(inflater.inflate(R.layout.layout_sugarman_dialog, null));

    init(builder);
  }

  @Override @UiThread public void show() {
    try {
      if (!isShowing()) {
        super.show();
      }
    } catch (WindowManager.BadTokenException e) {
      Log.e(TAG,
          "Bad window token, you cannot show a dialog before an Activity is created or after it's hidden.",
          e);
    }
  }

  @Override public void setContentView(@NonNull View view) {
    super.setContentView(view);

    tvContent = (TextView) findViewById(R.id.tv_content);
    tvTitle = (TextView) findViewById(R.id.tv_title);
    tvPositiveText = (TextView) findViewById(R.id.tv_positive);
    tvNegativeText = (TextView) findViewById(R.id.tv_negative);
    vBtnsDivider = findViewById(R.id.v_button_divider);

    tvNegativeText.setOnClickListener(this);
    tvPositiveText.setOnClickListener(this);
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.tv_positive:
        if (actionListener.get() != null) {
          actionListener.get().onClickDialog(this, DialogButton.POSITIVE);
        } else {
          dismiss();
        }
        break;
      case R.id.tv_negative:
        if (actionListener.get() != null) {
          actionListener.get().onClickDialog(this, DialogButton.NEGATIVE);
        } else {
          dismiss();
        }
        break;
      default:
        Log.d(TAG, "not processed view click: " + v.getResources().getResourceEntryName(id));
        break;
    }
  }

  public String getId() {
    return id;
  }

  private void init(Builder builder) {
    tvTitle.setText(builder.title);
    tvContent.setText(builder.content);
    tvPositiveText.setText(builder.positiveText);

    if (!TextUtils.isEmpty(builder.negativeText)) {
      tvNegativeText.setVisibility(View.VISIBLE);
      vBtnsDivider.setVisibility(View.VISIBLE);
      tvNegativeText.setText(builder.negativeText);

      LinearLayout.LayoutParams params =
          (LinearLayout.LayoutParams) tvPositiveText.getLayoutParams();
      params.width = 0;
      params.weight = 1f;
      tvPositiveText.setLayoutParams(params);
    }

    actionListener = new WeakReference<>(builder.actionListener.get());
  }

  public static class Builder {

    private WeakReference<SugarmanDialogListener> actionListener = new WeakReference<>(null);

    private final Context mContext;
    private final String id;
    private CharSequence title;
    private CharSequence content;
    private CharSequence positiveText;
    private CharSequence negativeText;

    public Builder(Context context, String id) {
      mContext = context;
      this.id = id;

      if (mContext != null) {
        title = mContext.getString(R.string.alert_default_title);
        positiveText = mContext.getString(R.string.okay);
      }
    }

    public Builder title(@NonNull CharSequence text) {
      title = text;
      return this;
    }

    public Builder title(@StringRes int textRes) {
      if (mContext != null) {
        title(mContext.getText(textRes));
      }
      return this;
    }

    public Builder content(@NonNull CharSequence text) {
      content = text;
      return this;
    }

    public Builder content(@StringRes int textRes) {
      if (mContext != null) {
        content(mContext.getText(textRes));
      }
      return this;
    }

    public Builder negativeText(@NonNull CharSequence text) {
      negativeText = text;
      return this;
    }

    public Builder negativeText(@StringRes int textRes) {
      if (mContext != null) {
        negativeText(mContext.getText(textRes));
      }
      return this;
    }

    public Builder positiveText(@NonNull CharSequence text) {
      positiveText = text;
      return this;
    }

    public Builder positiveText(@StringRes int textRes) {
      if (mContext != null) {
        positiveText(mContext.getText(textRes));
      }
      return this;
    }

    public Builder btnCallback(SugarmanDialogListener callback) {
      actionListener = new WeakReference<>(callback);
      return this;
    }

    @UiThread public SugarmanDialog build() {
      return new SugarmanDialog(this);
    }

    @UiThread public SugarmanDialog show() {
      SugarmanDialog dialog = build();
      dialog.show();
      return dialog;
    }
  }
}
