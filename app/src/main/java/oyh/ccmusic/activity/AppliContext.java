package oyh.ccmusic.activity;

import android.app.Application;
import android.content.Context;

/**
 * Created by yihong.ou on 17-9-7.
 */
public class AppliContext extends Application{
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }
}
