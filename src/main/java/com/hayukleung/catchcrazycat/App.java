package com.hayukleung.catchcrazycat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Message;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import com.mdroid.Library;
import com.mdroid.PausedHandler;
import com.mdroid.lifecycle.LifecycleDispatcher;
import java.lang.ref.WeakReference;

public class App extends MultiDexApplication {

  private static App mInstance;
  protected Activity mActivity;
  protected boolean mVisible = false;
  protected PausedHandler mHandler;
//  private Gson mGson;

//  private RefWatcher mRefWatcher;

  private boolean mShowTimeOut0;
  private boolean mShowTimeOut1;

  public static App Instance() {
    return mInstance;
  }

  public static void setShowTimeOut0(boolean show) {
    mInstance.mShowTimeOut0 = show;
  }

  public static void setShowTimeOut1(boolean show) {
    mInstance.mShowTimeOut1 = show;
  }

  public static boolean isShowTimeOut() {
    return mInstance.mShowTimeOut0 || mInstance.mShowTimeOut1;
  }

  public static PausedHandler getMainHandler() {
    return mInstance.mHandler;
  }

  public static boolean isVisible() {
    return mInstance.mVisible;
  }

//  public static synchronized Gson getGson() {
//    if (mInstance.mGson == null) {
//      mInstance.mGson = new GsonBuilder().registerTypeAdapterFactory(
//          TypeAdapters.newFactory(int.class, Integer.class, new IntegerAdapter())).create();
//    }
//    return mInstance.mGson;
//  }

  private static SharedPreferences getPreferences() {
    return mInstance.getSharedPreferences("configuration.pref", Context.MODE_MULTI_PROCESS);
  }

  private static SharedPreferences getSharePreferences() {
    return mInstance.getSharedPreferences("shared.pref",
        Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
  }

//  public static RefWatcher getRefWatcher() {
//    return mInstance.mRefWatcher;
//  }

  @Override public void onCreate() {

    super.onCreate();

    mInstance = this;
    init();
  }

  private void init() {
    Library.init(this);

    mHandler = new Handler(this);

    if (BuildConfig.DEBUG && false) {
      StrictMode.setThreadPolicy(
          new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDialog().build());
      StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        builder.detectLeakedRegistrationObjects()
            //                        .detectActivityLeaks()
            .detectCleartextNetwork().detectFileUriExposure();
      }
      builder.detectLeakedClosableObjects()
          .detectLeakedSqlLiteObjects()
          .penaltyDeath()
          .penaltyLog();
      StrictMode.setVmPolicy(builder.build());
    }

    // Share sdk
//    ShareSDK.initSDK(getApplicationContext());

    ActivityLifecycle lifecycle = new ActivityLifecycle();
    lifecycle.setVisibleListener(new ActivityLifecycle.VisibleListener() {
      @Override public void statusChange(boolean visible) {
        if (visible) {
          mHandler.resume();
        } else {
          mHandler.pause();
        }
      }
    });
    LifecycleDispatcher.registerActivityLifecycleCallbacks(this, lifecycle);

    Toost.initialize(getApplicationContext());
  }

  @Override public void onTerminate() {
    Toost.terminate();
    super.onTerminate();
  }

  private static class Handler extends PausedHandler {
    private WeakReference<App> mApp;

    public Handler(App app) {
      mApp = new WeakReference<>(app);
    }

    @Override protected void processMessage(Message message) {
    }
  }
}
