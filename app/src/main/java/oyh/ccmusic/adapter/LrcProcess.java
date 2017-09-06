package oyh.ccmusic.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import oyh.ccmusic.domain.LrcContent;


/**
 * Created by yihong.ou on 17-8-28.
 */
public class LrcProcess {

    private List<LrcContent> lrcList;	//List集合存放歌词内容对象
    private LrcContent mLrcContent;		//声明一个歌词内容对象
    //mp3时间
    private long currentTime;
    //MP3对应时间的内容
    private String currentContent;



    private InputStream inputStream;

    /**
     * 无参构造函数用来实例化对象
     */
    public LrcProcess() {
        mLrcContent = new LrcContent();
        lrcList = new ArrayList<LrcContent>();
    }



    /**
     * 读取歌词
     * @param path
     * @return
     */
    public String readLRC(String path) {
        //定义一个StringBuilder对象，用来存放歌词内容
        StringBuilder stringBuilder = new StringBuilder();
        File f = new File(path.replace(".mp3", ".lrc"));

        try {
            //创建一个文件输入流对象
            FileInputStream fis = new FileInputStream(f);
//            InputStreamReader isr = new InputStreamReader(fis, "GB2312");
//            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            InputStreamReader isr = new InputStreamReader(fis, "gbk");
            BufferedReader br = new BufferedReader(isr);
            String s = "";
            while((s = br.readLine()) != null) {
                //替换字符
                s = s.replace("[", "");
                s = s.replace("]", "@");

                //分离“@”字符
                String splitLrcData[] = s.split("@");
                if(splitLrcData.length > 1) {
                    mLrcContent.setLrcStr(splitLrcData[1]);

                    //处理歌词取得歌曲的时间
                    int lrcTime = time2Str(splitLrcData[0]);

                    mLrcContent.setLrcTime(lrcTime);

                    //添加进列表数组
                    lrcList.add(mLrcContent);

                    //新创建歌词内容对象
                    mLrcContent = new LrcContent();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            stringBuilder.append("歌词文件丢啦～～");
        } catch (IOException e) {
            e.printStackTrace();
            stringBuilder.append("没有找到歌词哦");
        }
        return stringBuilder.toString();
    }



    /**
     * 解析歌词时间
     * @param timeStr
     * @return
     */
    public int time2Str(String timeStr) {
        timeStr = timeStr.replace(":", ".");
        timeStr = timeStr.replace(".", "@");

        String timeData[] = timeStr.split("@");	//将时间分隔成字符串数组

        //分离出分、秒并转换为整型
        int minute = Integer.parseInt(timeData[0]);
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);

        //计算上一行与下一行的时间转换为毫秒数
        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
        return currentTime;
    }
    public List<LrcContent> getLrcList() {
        return lrcList;
    }
}
