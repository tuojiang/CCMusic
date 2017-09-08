package oyh.ccmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private MediaPlayer mPlayer;
    private int currentPos;         // 记录当前正在播放的音乐
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
        int lrcIndex();
        String getTitle();
        String getArtist();
    }

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

        /**
         * 播放上一首
         */
        @Override
        public void isPlayPre() {
            if (--currentPos<0){
                currentPos=0;
            }
            playerMusic();
        }

        /**
         * 随机播放上一首
         */
        @Override
        public void shPlayPre() {
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
            initMusic();
            playerMusic();
        }

        /**
         * 随机播放下一首
         */
        @Override
        public void shPlayNext() {
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
        currentPos = intent.getIntExtra("CURRENT_POSITION", 0);
        initMusic();
        playerMusic();


        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        MusicUtils.initMusicList();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }
}
