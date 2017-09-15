package oyh.ccmusic.activity;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * 全局Application
 * Created by yihong.ou on 17-9-7.
 */
public class AppliContext extends Application{
    public static Context sContext;



    public static void setsContext(Context sContext) {
        AppliContext.sContext = sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

    }
}
