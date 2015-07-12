package miao1007.github.com.easynfc;

import android.app.Application;
import android.content.Context;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by leon on 7/12/15.
 */
public class GlobalContext extends Application {

  public static RefWatcher getRefWatcher(Context context) {
    GlobalContext application = (GlobalContext) context.getApplicationContext();
    return application.refWatcher;
  }

  private RefWatcher refWatcher;

  @Override public void onCreate() {
    super.onCreate();
    refWatcher = LeakCanary.install(this);
  }
}
