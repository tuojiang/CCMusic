package oyh.ccmusic.activity;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * 全局Application
 * Created by yihong.ou on 17-9-7.
 */
public class AppliContext extends Application{
    public static Context sContext;
    public static int sScreenWidth;
    public static int sScreenHeight;

    public static void setsContext(Context sContext) {
        AppliContext.sContext = sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        sScreenWidth = dm.widthPixels;
        sScreenHeight = dm.heightPixels;
    }
}
