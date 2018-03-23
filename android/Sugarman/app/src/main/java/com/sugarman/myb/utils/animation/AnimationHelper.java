package com.sugarman.myb.utils.animation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import timber.log.Timber;

/**
 * Created by yegoryeriomin on 11/29/17.
 */

public class AnimationHelper {

  public int workers = Runtime.getRuntime().availableProcessors() + 1;

  private AtomicBoolean done;
  private File imagesDir;
  private Stack<String> urls;

  public AnimationHelper(File imagesDir, List<String> urls, int workersThread) {
    this.imagesDir = imagesDir;
    this.urls = new Stack<>();
    this.urls.addAll(urls);
    this.workers = workersThread;
    done = new AtomicBoolean(false);
    Timber.e("workers " + workers);
  }

  /**
   * @param callback is a callback that will be called with each work result when available
   */
  public void download(Callback callback) {
    for (Thread worker : newWorkers(callback)) {
      worker.start();
    }
  }

  private List<Thread> newWorkers(Callback callback) {
    ArrayList<Thread> workers = new ArrayList<>();

    for (int i = 0; i < this.workers; i++) {
      workers.add(new Thread(newWorker(callback), "ConcurrentRequestSourceWorker-" + i));
    }

    return workers;
  }

  private synchronized Runnable newWorker(Callback callback) {
    return new Runnable() {
      @Override public void run() {
        while (!urls.empty()) {
          callback.onEach(AnimationHelper.this.doDownload(urls.pop()));
        }

        if (done.compareAndSet(false, true)) {
          callback.onDone(imagesDir);
        }

        System.out.println(String.format("Thread %s done", Thread.currentThread().getName()));
      }
    };
  }

  public static String getFilenameFromURL(URL url) {
    return new File(url.getPath().toString()).getName();
  }

  /**
   * Download and persist file on disk
   *
   * @return File pointing to downloaded file on disk
   */
  private File doDownload(String url) {
    URL url1 = null;
    try {
      url1 = new URL(url);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    File f = new File(imagesDir.getAbsolutePath() + "/" + getFilenameFromURL(url1));
    try {
      Thread.sleep(10); //mock request time
      try {
        FileOutputStream out = new FileOutputStream(f);

        Bitmap bmp = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
        //bmp.compress(Bitmap.CompressFormat.PNG,80,out);
        bmp.compress(Bitmap.CompressFormat.PNG, 0, out);
        out.flush();
        out.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (InterruptedException e) {
      //ignore
    }

    System.out.printf("Downloaded %s on thread: %s%n", url, Thread.currentThread().getName());
    return f; // file pointing to downloaded image on disk
  }

  public interface Callback {
    void onEach(File image);

    void onDone(File imagesDir);
  }
}
