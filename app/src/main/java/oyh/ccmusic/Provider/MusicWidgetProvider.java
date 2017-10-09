package oyh.ccmusic.Provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import oyh.ccmusic.service.LocalMusicService;

/**
 * Created by yihong.ou on 17-10-9.
 */
public class MusicWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        context.startService(new Intent(context,LocalMusicService.class));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, LocalMusicService.class));
    }
}
