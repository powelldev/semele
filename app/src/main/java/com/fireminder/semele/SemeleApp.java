package com.fireminder.semele;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.fireminder.semele.util.DbDumperPlugin;
import com.fireminder.semele.util.HrmDumperPlugin;

import timber.log.Timber;

public class SemeleApp extends Application {

  private static final String TAG = "Semele";
  static Application sApplication;

  @Override
  public void onCreate() {
    super.onCreate();
    sApplication = this;
    Timber.plant(new Timber.DebugTree() {
      @Override
      protected String createStackElementTag(StackTraceElement element) {
        return TAG + " " + super.createStackElementTag(element) + ": " + element.getMethodName() + ":" + element.getLineNumber();
      }
    });
    Stetho.initialize(Stetho.newInitializerBuilder(this)
        .enableDumpapp(new DumperPluginsProvider() {
          @Override
          public Iterable<DumperPlugin> get() {
            return new Stetho.DefaultDumperPluginsBuilder(sApplication)
                .provide(new HrmDumperPlugin())
                .provide(new DbDumperPlugin())
                .finish();
          }
        })
        .build());
  }

  public static Context get() {
    return sApplication;
  }
}
