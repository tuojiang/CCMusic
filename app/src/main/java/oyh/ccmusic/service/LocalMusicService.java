package oyh.ccmusic.service;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import oyh.ccmusic.Provider.DBHelper;
import oyh.ccmusic.Provider.MusicWidgetProvider;
import oyh.ccmusic.Provider.PlayListContentProvider;
import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.adapter.LrcProcess;
import oyh.ccmusic.domain.LrcContent;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.MusicUtils;

/**
 * 本地音乐服务类
 * Created by yihong.ou on 17-9-8.
 */
public class LocalMusicService extends Service{
    private ArrayList<Music> musicPathLists;
    private ArrayList<Music> musicplaylist;
    private ContentResolver mResolver;
    private MediaPlayer mPlayer;
    private int currentPos=0;         // 记录当前正在播放的音乐
    private int currentMLPosition;         // 记录我喜欢列表当前正在播放的音乐
    private int nextPlay;
    private LrcProcess mLrcProcess; //歌词处理
    private List<LrcContent> lrcList = new ArrayList<LrcContent>(); //存放歌词列表对象
    private int index = 0;          //歌词检索值
    private int currentTime;		//当前播放进度
    private int duration;			//播放长度
    private static final String MUSIC_PLAY_ACTION="appwidget.action.musicplay";
    private static final String MUSIC_NEXT_ACTION="appwidget.action.musicnext";
    private static final String MUSIC_PRE_ACTION="appwidget.action.musicpre";
    private MyBinder myBinder=new MyBinder();

    /**
     * 音乐播放回调接口
     */
    public interface CallBack{
        boolean isPlayerMusic();
        boolean isPlay();
        int callTotalDate();
        int callCurrentTime();
        int play(int position);
        void playNet(String file);
        int itemPlay(int position);
        int mlovePlay(int index,int position);
        int next();
        int itemNext();
        int mloveNext();
        int pre();
        int itemPre();
        int mlovePre();
        void start();
        void stop();
        int playMyLove(int position);
        void isSeekto(int m_send);
        void isPlayPre();
        void isPlayNext();
        boolean isPlayering();
        void initLrc();
        void initLrcMlove();
        int lrcIndex();
        String getTitle();
        String getArtist();
        int pause();
        void addplaylist(Music music);
        void delplaylist(String name);
        void delplaysdcard(String filename);
        ArrayList<LrcContent> initLrcx(ArrayList<LrcContent> list,int index);
        ArrayList<LrcContent> initLrcMlove(ArrayList<LrcContent> list,int index);
    }

    //private MyBinder mBinder = new MyBinder();
    public class MyBinder extends Binder implements CallBack{
        /**
         * 播放音乐
         * @return
         */
        @Override
        public boolean isPlayerMusic() {
            return playerMusic();
        }

        @Override
        public boolean isPlay() {
            if (mPlayer!=null){
                return true;
            }else {
                return false;
            }
        }

        /**
         * 歌曲总的时间
         * @return
         */
        @Override
        public int callTotalDate() {
            if (mPlayer!=null){
                return mPlayer.getDuration();
            }else {
                return 0;
            }
        }

        /**
         * 歌曲当前的时间
         * @return
         */
        @Override
        public int callCurrentTime() {
            if (mPlayer!=null){
                return mPlayer.getCurrentPosition();
            }else {
                return 0;
            }
        }

        @Override
        public int play(int position) {
            final int isRepeat = (int) MusicUtils.get(AppliContext.sContext,"isRepeat", 0);

            initLrc();
            if(position < 0) position = 0;
            if(position >= MusicUtils.sMusicList.size()) position = MusicUtils.sMusicList.size() - 1;

            try {
                mPlayer.reset();
                mPlayer.setDataSource(MusicUtils.sMusicList.get(position).getMusicPath());
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 装载完毕回调
                        start();
                        updateAppWidget();
                    }
                });
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (isRepeat==1){
                            mPlayer.setLooping(true);//单曲循环播放
                        }else if (isRepeat==0){
                            next();

                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            currentPos = position-1;
            MusicUtils.put("position", position);
            return currentPos;
        }

        @Override
        public void playNet(String file) {
            try {
                mPlayer.reset();
                mPlayer.setDataSource(file);
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 装载完毕回调
                        start();
                        updateAppWidget();
                    }
                });
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mPlayer.setLooping(true);//单曲循环播放
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public int itemPlay(int position) {
            if(position < 0) position = 0;
            if(position >= MusicUtils.itemCommonList.size()) position = MusicUtils.itemCommonList.size() - 1;

            try {
                mPlayer.reset();
                mPlayer.setDataSource(MusicUtils.itemCommonList.get(position).getMusicPath());
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 装载完毕回调
                        start();
                    }
                });
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        itemNext();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            currentPos = position;
            MusicUtils.put("itemposition", position);
            return currentPos;
        }

        @Override
        public int mlovePlay(int index,int position) {
            if (mPlayer!=null){
                mPlayer.stop();
            }
            if(position < 0) position = 0;
            if(position >= MusicUtils.sMusicList.size()) position = MusicUtils.sMusicList.size() - 1;
            try {
                mPlayer.reset();
                mPlayer.setDataSource(MusicUtils.sMusicList.get(index).getMusicPath());
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 装载完毕回调
                        start();
                    }
                });
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mloveNext();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            currentMLPosition = position;
            MusicUtils.put("mlposition", position);
            return currentMLPosition;
        }


        @Override
        public int next() {
            initLrc();
            if(currentPos >= MusicUtils.sMusicList.size() - 1) {
                return play(0);
            }
            return play(currentPos + 1);
        }

        @Override
        public int itemNext() {
            if(currentPos >= MusicUtils.itemCommonList.size() - 1) {
                return itemPlay(0);
            }
            return itemPlay(currentPos + 1);
        }

        @Override
        public int mloveNext() {
           int index = MusicUtils.queryMLoveToList(currentMLPosition+1);
            if(currentMLPosition >= MusicUtils.sMusicSQlList.size() - 1) {
           int indexinit = MusicUtils.queryMLoveToList(0);
                MusicUtils.put("indexNext", indexinit);
                return mlovePlay(indexinit,0);
            }
                MusicUtils.put("indexNext", index);
            return mlovePlay(index,currentMLPosition+1);
        }


        @Override
        public int pre() {
            initLrc();
            if(currentPos <= 0) {
                return play(MusicUtils.sMusicList.size() - 1);
            }
            return play(currentPos - 1);
        }

        @Override
        public int itemPre() {
            if(currentPos <= 0) {
                return itemPlay(MusicUtils.itemCommonList.size() - 1);
            }
            return itemPlay(currentPos - 1);
        }

        @Override
        public int mlovePre() {
            int index = MusicUtils.queryMLoveToList(currentMLPosition-1);
            if(currentMLPosition <= 0) {
                int indexinit = MusicUtils.queryMLoveToList(currentMLPosition-1);
                MusicUtils.put("indexPre", indexinit);
                return mlovePlay(indexinit,MusicUtils.sMusicSQlList.size() - 1);
            }
            MusicUtils.put("indexPre", index);
            return mlovePlay(index,currentMLPosition-1);
        }

        /**
         * 开始播放
         */
        @Override
        public void start() {
            mPlayer.start();
            updateAppWidget();
        }

        @Override
        public void stop() {
            mPlayer.stop();
            mPlayer.release();
        }

        @Override
        public int playMyLove(int position) {
            currentMLPosition = position;
            MusicUtils.put("mlposition", currentMLPosition);
            playerMusic();
            return currentMLPosition;
        }

        /**
         * 进度条拖动
         * @param m_send
         */
        @Override
        public void isSeekto(int m_send) {
            if (mPlayer!=null){
                mPlayer.seekTo(m_send);
            }
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }

        /**
         * 播放上一首
         */
        @Override
        public void isPlayPre() {
            if (--currentPos<0){
                currentPos=0;
            }
            initLrc();
            play(currentPos+1);
//            pre();
        }

        /**
         * 播放下一首
         */
        @Override
        public void isPlayNext() {
            if (++currentPos>MusicUtils.sMusicList.size()-1){
                currentPos=MusicUtils.sMusicList.size()-1;
            }
            initLrc();
            next();
        }


        /**
         * 判读是否正在播放
         * @return
         */
        @Override
        public boolean isPlayering() {
            if(mPlayer.isPlaying()){
                return true;
            }else{
                return false;
            }
        }



        /**
         * 初始化歌词
         */
        @Override
        public void initLrc() {
            mLrcProcess = new LrcProcess();
            //读取歌词文件
            mLrcProcess.readLRC(MusicUtils.sMusicList.get(currentPos).getMusicPath());
            //传回处理后的歌词文件
            lrcList = mLrcProcess.getLrcList();
            //发送广播
            MusicUtils.initLrcList(AppliContext.sContext,lrcList);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            intent.putParcelableArrayListExtra("LRC_LIST", (ArrayList<? extends Parcelable>) lrcList);
            intent.putExtra("SIZE",lrcList.size());
            intent.setAction("yihong.lrc");
            sendBroadcast(intent);
        }

        /**
         * 初始化我喜欢列表歌词   currentMLPosition
         */
        @Override
        public void initLrcMlove() {
            mLrcProcess = new LrcProcess();
            //读取歌词文件
            mLrcProcess.readLRC(MusicUtils.sMusicSQlList.get(currentMLPosition).getMusicPath());
            //传回处理后的歌词文件
            lrcList = mLrcProcess.getLrcList();
        }

        /**
         * 初始化歌词
         */
        @Override
        public ArrayList<LrcContent> initLrcx(ArrayList<LrcContent> list,int index) {
            mLrcProcess = new LrcProcess();
            //
            if (index>=MusicUtils.sMusicList.size()){
                index = MusicUtils.sMusicList.size() - 1;
            }
            mLrcProcess.readLRC(MusicUtils.sMusicList.get(index).getMusicPath());
            Log.e("init",MusicUtils.sMusicList.get(index).getMusicPath());
            //传回处理后的歌词文件
            lrcList = mLrcProcess.getLrcList();
            list= (ArrayList<LrcContent>) lrcList;

            return list;
        }
        /**
         * 初始化我喜欢列表歌词
         */
        @Override
        public ArrayList<LrcContent> initLrcMlove(ArrayList<LrcContent> list, int index) {
            mLrcProcess = new LrcProcess();
            //读取歌词文件
            mLrcProcess.readLRC(MusicUtils.sMusicSQlList.get(index).getMusicPath());
            //传回处理后的歌词文件
            lrcList = mLrcProcess.getLrcList();
            list= (ArrayList<LrcContent>) lrcList;
            return list;
        }

        /**
         * 获取歌词位置
         * @return
         */
        @Override
        public int lrcIndex() {
            if(mPlayer.isPlaying()) {
                currentTime = mPlayer.getCurrentPosition();
                duration = mPlayer.getDuration();
            }
            if(currentTime < duration) {
                for (int i = 0; i < lrcList.size(); i++) {
                    if (i < lrcList.size() - 1) {
                        if (currentTime < lrcList.get(i).getLrcTime() && i == 0) {
                            index = i;
                        }
                        if (currentTime > lrcList.get(i).getLrcTime()
                                && currentTime < lrcList.get(i + 1).getLrcTime()) {
                            index = i;
                        }
                    }
                    if (i == lrcList.size() - 1
                            && currentTime > lrcList.get(i).getLrcTime()) {
                        index = i;
                    }
                }
            }
            return index;
        }

        /**
         * 获取当前歌曲名
         * @return
         */
        @Override
        public String getTitle() {
            return null;
        }

        /**
         * 获取当前歌手名
         * @return
         */
        @Override
        public String getArtist() {
            return null;
        }

        @Override
        public int pause() {
                if(!isPlayering()) return -1;

                mPlayer.pause();
                return currentPos;
        }

        /**
         * 添加到当前列表
         * @param music
         */
        @Override
        public void addplaylist(Music music) {
            addPlayListInner(music, true);
        }

        /**
         * 从列表中删除
         * @param name
         */
        @Override
        public void delplaylist(String name) {
            dellPlayListInner(name);
        }

        /**
         * 从sd卡删除
         * @param path
         */
        @Override
        public void delplaysdcard(String path) {
            File file = new File(path);
            if(file.isFile()){
                file.delete();
            }
            file.exists();
        }


    }

    /**
     * 从播放列表删除歌曲
     * @param name
     */
    private void dellPlayListInner(String name){
        int delete=mResolver.delete(PlayListContentProvider.CONTENT_SONGS_URI,"name=?",new String[] { name });

    }
    /**
     * 添加歌曲到播放列表中
     * @param music
     * @param needplay
     */
    private void addPlayListInner(Music music,boolean needplay){
        musicplaylist=new ArrayList<>();
        if (musicplaylist.contains(music)){
            return;
        }
        musicplaylist.add(0,music);

        insertMusicItemToContentProvider(music);

        if(needplay) {
        }
    }
    private void insertMusicItemToContentProvider(Music music){
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.NAME,music.getTitle());
        cv.put(DBHelper.ARTIST,music.getArtist());
        cv.put(DBHelper.DURATION,music.getLength());
        cv.put(DBHelper.ALBUM_URI,music.getImage());
        cv.put(DBHelper.SONG_URI,music.getMusicPath());
        Uri uri=mResolver.insert(PlayListContentProvider.CONTENT_SONGS_URI,cv);
    }

    /**
     * 播放音乐
     * @return
     */
    public boolean playerMusic(){
        if (mPlayer.isPlaying()){
            mPlayer.pause();
            return false;
        }else {

            mPlayer.start();
            return true;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myBinder.initLrc();
        //更新appWidget
        updateAppWidget();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("LocalMusicService", "onCreate");
//        MusicUtils.initMusicList(AppliContext.sContext.getApplicationContext());
        mPlayer = new MediaPlayer();
        mResolver = getContentResolver();
        //注册广播
        IntentFilter filter=new IntentFilter(MUSIC_PLAY_ACTION);
        filter.addAction(MUSIC_NEXT_ACTION);
        filter.addAction(MUSIC_PRE_ACTION);
        registerReceiver(new MusicReceiver(), filter);
    }
    /**
     * 更新appWidget
     */
    public void updateAppWidget()
    {
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(this);

        RemoteViews remoteViews=new RemoteViews(this.getPackageName(),R.layout.music_app_widget);

        String artistAndSong=MusicUtils.sMusicList.get(currentPos+1).getTitle()+"-"+MusicUtils.sMusicList.get(currentPos+1).getArtist();
        //设置歌曲名和歌手
        remoteViews.setTextViewText(R.id.tv_widget_music_name,artistAndSong);
        Bitmap icon = BitmapFactory.decodeFile(MusicUtils.sMusicList.get(currentPos+1).getImage());
        if (icon==null){
            icon =BitmapFactory.decodeResource(getResources(), R.mipmap.img);
        }
        Intent playIntent=new Intent(MUSIC_PLAY_ACTION);
        if (mPlayer!=null)
        {
            //根据音乐是否播放，更改imageView的图片
            remoteViews.setImageViewBitmap(R.id.iv_widget_image_thumb, icon);
            //根据音乐是否播放，更改btn的图片
            remoteViews.setInt(R.id.widget_play_btn, "setBackgroundResource", mPlayer.isPlaying() ? R.mipmap.ic_pause : R.mipmap.ic_play);

        }
        //点击开启广播
        PendingIntent playBroad=PendingIntent.getBroadcast(this,0,playIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.widget_play_btn, playBroad);

        Intent nextIntent=new Intent(MUSIC_NEXT_ACTION);
        PendingIntent nextBroad= PendingIntent.getBroadcast(this,0,nextIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.widget_next_btn, nextBroad);

        Intent preIntent=new Intent(MUSIC_PRE_ACTION);
        PendingIntent preBroad= PendingIntent.getBroadcast(this,0,preIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.widget_pre_btn, preBroad);

        ComponentName componentName=new ComponentName(this,MusicWidgetProvider.class);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }
    /**
     * 接收广播，处理对应请求
     */
    public class MusicReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MUSIC_PLAY_ACTION))
            {
                //当mediaPlayer为null时，表示当前未播放音乐
                if (mPlayer==null)
                {
                    myBinder.play(currentPos+1);
                }else {
                    //当音乐正在播放时，暂停播放
                    if (mPlayer.isPlaying())
                    {
                        mPlayer.pause();
                    }else {
                        mPlayer.start();
                    }
                }

            }else if (intent.getAction().equals(MUSIC_NEXT_ACTION)) {

                myBinder.isPlayNext();
            }else if (intent.getAction().equals(MUSIC_PRE_ACTION)){

                myBinder.isPlayPre();
            }
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("LocalMusicService", "onBind");
        return new MyBinder();
    }

    @Override
    public void onDestroy() {
        if(mPlayer != null)
            mPlayer.release();
            mPlayer = null;
        super.onDestroy();
        unregisterReceiver(new MusicReceiver());
    }
}
