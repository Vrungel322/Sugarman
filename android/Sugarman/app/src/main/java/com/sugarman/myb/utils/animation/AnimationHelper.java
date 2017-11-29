package com.sugarman.myb.utils.animation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yegoryeriomin on 11/29/17.
 */

public class AnimationHelper {

  public static final int WORKERS = 4;

  private AtomicBoolean done;
  private File imagesDir;
  private Stack<String> urls;

  public AnimationHelper(File imagesDir, List<String> urls) {
    this.imagesDir = imagesDir;
    this.urls = new Stack<>();
    this.urls.addAll(urls);
    done = new AtomicBoolean(false);
  }

  /**
   * @param callback is a callback that will be called with each work result when available
   */
  public void download(Callback callback) {
    for(Thread worker : newWorkers(callback)) {
      worker.start();
    }
  }

  private List<Thread> newWorkers(Callback callback) {
    ArrayList<Thread> workers = new ArrayList<>();

    for (int i = 0; i < WORKERS; i++) {
      workers.add(new Thread(newWorker(callback), "ConcurrentRequestSourceWorker-" + i));
    }

    return workers;
  }

  private Runnable newWorker(Callback callback) {
    return new Runnable() {
      @Override
      public void run() {
        while (!urls.empty()) {
          callback.onEach(AnimationHelper.this.doDownload(urls.pop()));
        }

        if(done.compareAndSet(false, true)) {
          callback.onDone(imagesDir);
        }

        System.out.println(String.format("Thread %s done", Thread.currentThread().getName()));
      }
    };
  }

  /**
   * Download and persist file on disk
   *
   * @return File pointing to downloaded file on disk
   */
  private File doDownload(String url) {
    try {
      Thread.sleep(10); //mock request time
    } catch (InterruptedException e) {
      //ignore
    }

    System.out.printf("Downloaded %s on thread: %s%n", url, Thread.currentThread().getName());
    return null; // file pointing to downloaded image on disk
  }

  public interface Callback {
    void onEach(File image);
    void onDone(File imagesDir);
  }

}
