package oyh.ccmusic.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import oyh.ccmusic.Provider.DBHelper;
import oyh.ccmusic.Provider.PlayListContentProvider;
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
    private int currentPos;         // 记录当前正在播放的音乐
    private int currentMLPosition;         // 记录我喜欢列表当前正在播放的音乐
    private int nextPlay;
    private LrcProcess mLrcProcess; //歌词处理
    private List<LrcContent> lrcList = new ArrayList<LrcContent>(); //存放歌词列表对象
    private int index = 0;          //歌词检索值
    private int currentTime;		//当前播放进度
    private int duration;			//播放长度
    private MyBinder myBinder=new MyBinder();

    /**
     * 音乐播放回调接口
     */
    public interface CallBack{
        boolean isPlayerMusic();
        int callTotalDate();
        int callCurrentTime();
        int play(int position);
        int next();
        int pre();
        void start();
        int playMyLove(int position);
        void isSeekto(int m_send);
        void isPlayPre();
        void shPlayPre();
        void isPlayNext();
        void shPlayNext();
        boolean isPlayering();
        void currentList();
        void toggleShuffle();
        void cycleRepeat();
        void initLrc();
        void initLrcMlove();
        int lrcIndex();
        String getTitle();
        String getArtist();
        int pause();
        void addplaylist(Music music);
        void delplaylist(String name);
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
//            currentPos = position;
//            MusicUtils.put("position", position);
//            initMusic();
//            playerMusic();
            if(position < 0) position = 0;
            if(position >= MusicUtils.sMusicList.size()) position = MusicUtils.sMusicList.size() - 1;

            try {
                mPlayer.reset();
                mPlayer.setDataSource(MusicUtils.sMusicList.get(position).getMusicPath());
                mPlayer.prepare();

                start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            currentPos = position;
            MusicUtils.put("position", position);

            return currentPos;
        }

        @Override
        public int next() {
            if(currentPos >= MusicUtils.sMusicList.size() - 1) {
                return play(0);
            }
            return play(currentPos + 1);
        }

        @Override
        public int pre() {
            if(currentPos <= 0) {
                return play(MusicUtils.sMusicList.size() - 1);
            }

            return play(currentPos - 1);
        }

        /**
         * 开始播放
         */
        @Override
        public void start() {
            mPlayer.start();
        }

        @Override
        public int playMyLove(int position) {
            currentMLPosition = position;
            MusicUtils.put("mlposition", currentMLPosition);
//            initMLMusic();
            playerMusic();
//            if(position < 0) position = 0;
//            if(position >= MusicUtils.sMusicList.size()) position = MusicUtils.sMusicList.size() - 1;
//
//            try {
//                mPlayer.reset();
//                Log.e("playMyLove","reset");
//                mPlayer.setDataSource(MusicUtils.sMusicSQlList.get(currentMLPosition).getMusicPath());
//                Log.e("playMyLove","list="+MusicUtils.sMusicSQlList.get(currentMLPosition).getMusicPath());
//
//                mPlayer.prepare();
//                Log.e("playMyLove","prepare");
//
//                mPlayer.start();
//                Log.e("playMyLove","start");

//                start();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

//            currentMLPosition = position;
//            MusicUtils.put("mlposition", currentMLPosition);
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
            play(currentPos);
        }

        /**
         * 随机播放上一首
         */
        @Override
        public void shPlayPre() {
            initLrc();
            shuffleMusic();
            playerMusic();
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
            initMusic();
            playerMusic();
        }

        /**
         * 随机播放下一首
         */
        @Override
        public void shPlayNext() {
            initLrc();
            shuffleMusic();
            playerMusic();
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
         * 顺序播放
         */
        @Override
        public void currentList() {
            initLrc();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    currentPos++;
                    if (currentPos>=MusicUtils.sMusicList.size()){
                        currentPos=0;
                    }
                    initMusic();
                    playerMusic();
                }
            });
        }

        /**
         * 随机播放
         */
        @Override
        public void toggleShuffle() {
            initLrc();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    shuffleMusic();
                    playerMusic();

                }
            });
        }

        /**
         * 单曲循环播放
         */
        @Override
        public void cycleRepeat() {
            initLrc();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {

                    repeatMusic();
                    playerMusic();
                }
            });
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
            //读取歌词文件
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

        @Override
        public void addplaylist(Music music) {
            addPlayListInner(music, true);
        }

        @Override
        public void delplaylist(String name) {
            dellPlayListInner(name);
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
            //TODO   进行播放
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
     * 随机播放
     */
    private void shuffleMusic() {
        int min=0;
        int max=musicPathLists.size();
        Random random = new Random();

        final int s = random.nextInt(max-min+1) + min;
        currentPos=s;
        mPlayer.reset();
        try {
            mPlayer.setDataSource(MusicUtils.sMusicList.get(currentPos).getMusicPath());
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentPos=s;


                    shuffleMusic();
                    playerMusic();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 单曲播放
     */
    private void repeatMusic() {

        mPlayer.reset();

        try {

            mPlayer.setDataSource(MusicUtils.sMusicList.get(currentPos).getMusicPath());
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextPlay=currentPos;


                    repeatMusic();
                    playerMusic();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化音乐数据
     */
    public void initMusic(){
        mPlayer.reset();
        try{
            mPlayer.setDataSource(MusicUtils.sMusicList.get(currentPos).getMusicPath());
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    currentPos++;
                    if (currentPos>=MusicUtils.sMusicList.size()){
                        currentPos=0;
                    }
                    initMusic();
                    myBinder.initLrc();
                    playerMusic();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 初始化音乐数据
     */
    public void initMLMusic(){
        mPlayer.reset();
        try{
            mPlayer.setDataSource(MusicUtils.sMusicSQlList.get(currentMLPosition).getMusicPath());
            Log.e("service","initMLMusic"+MusicUtils.sMusicSQlList.size());
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    currentMLPosition++;
                    if (currentMLPosition>=MusicUtils.sMusicList.size()){
                        currentMLPosition=0;
                    }
                    initMLMusic();
//                    myBinder.initLrc();
                    playerMusic();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
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
//        currentPos = intent.getIntExtra("CURRENT_POSITION", 0);
        myBinder.initLrc();
        initMusic();
//        playerMusic();


        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("LocalMusicService", "onCreate");
        mPlayer = new MediaPlayer();
        mResolver = getContentResolver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("LocalMusicService", "onBind");
        return new MyBinder();
    }

}
