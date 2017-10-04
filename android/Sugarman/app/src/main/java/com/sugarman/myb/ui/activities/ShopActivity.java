package com.sugarman.myb.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sugarman.myb.R;
import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {

  ImageView backButton;
  List<TextView> allTexts;
  List<LinearLayout> containers;
  LinearLayout mainLayout;
  Typeface tfDin;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shop);
    tfDin = Typeface.createFromAsset(getAssets(), "din_medium.ttf");
    allTexts = new ArrayList<>();
    containers = new ArrayList<>();

    containers.add((LinearLayout) findViewById(R.id.card_container_1));
    containers.add((LinearLayout) findViewById(R.id.card_container_2));
    containers.add((LinearLayout) findViewById(R.id.card_container_3));

    for (LinearLayout ll : containers) {
      ll.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {

          int id = view.getId();
          int productId = -1;

          switch (id) {
            case R.id.card_container_1:
              productId = 1;
              break;
            case R.id.card_container_2:
              productId = 2;
              break;
            case R.id.card_container_3:
              productId = 3;
              break;
          }

          Intent intent = new Intent(ShopActivity.this, ProductDetailsActivity.class);

          intent.putExtra("productId", productId);
          startActivity(intent);
        }
      });
    }

    mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

    backButton = (ImageView) findViewById(R.id.iv_back);
    backButton.setOnClickListener(this);

    allTexts = getTextViews(mainLayout);

    for (TextView v : allTexts)
      v.setTypeface(tfDin);

    Log.e("SHOP", "" + allTexts.size());
  }

  @Override public void onClick(View view) {
    int id = view.getId();
    switch (id) {
      case R.id.iv_back:
        closeActivity();
        break;
    }
    return;
  }

  private ArrayList<TextView> getTextViews(ViewGroup root) {
    ArrayList<TextView> views = new ArrayList<>();
    for (int i = 0; i < root.getChildCount(); i++) {
      View v = root.getChildAt(i);
      if (v instanceof TextView) {
        views.add((TextView) v);
      } else if (v instanceof ViewGroup) {
        views.addAll(getTextViews((ViewGroup) v));
      }
    }
    return views;
  }

  private void closeActivity() {
    finish();
  }
}
