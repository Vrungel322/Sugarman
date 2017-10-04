package com.sugarman.myb.ui.views;

/**
 * Copyright (C) 2017 Wasabeef
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import com.squareup.picasso.Transformation;

public class CropCircleTransformation implements Transformation {

  int borderColor = 0x00000000;
  int radius = 0;

  public CropCircleTransformation(int borderColor, int radius) {
    this.borderColor = borderColor;
    this.radius = radius;
  }

  @Override public Bitmap transform(Bitmap source) {
    int size = Math.min(source.getWidth(), source.getHeight());

    int width = (source.getWidth() - size) / 2;
    int height = (source.getHeight() - size) / 2;

    Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

    Canvas canvas = new Canvas(bitmap);
    Paint paint = new Paint();
    BitmapShader shader =
        new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
    if (width != 0 || height != 0) {
      // source isn't square, move viewport to center
      Matrix matrix = new Matrix();
      matrix.setTranslate(-width, -height);
      shader.setLocalMatrix(matrix);
    }

    float r = size / 2f;
    paint.setAntiAlias(true);
    paint.setColor(borderColor);
    paint.setShader(null);
    canvas.drawCircle(r, r, r, paint);
    paint.setShader(shader);
    paint.setColor(0xff000000);
    //paint.setShadowLayer(10.0f,0,10,0xff000000);
    canvas.drawCircle(r, r, r - radius, paint);

    source.recycle();

    return bitmap;
  }

  @Override public String key() {
    return "CropCircleTransformation()";
  }
}