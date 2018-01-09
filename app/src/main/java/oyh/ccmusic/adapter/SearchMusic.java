package oyh.ccmusic.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import oyh.ccmusic.domain.SearchResult;

/**
 *  解析在线数据
 *  Created by yihong.ou on 17-9-18.
 */
public class SearchMusic {
	private static final int SIZE = 20;
	private static final String URL =  "http://music.baidu.com" + "/search/song";
	private static SearchMusic sInstance;
	private OnSearchResultListener mListener;
	
    private ExecutorService mThreadPool;

	public synchronized static SearchMusic getInstance() {
		if (sInstance == null) {
			try {
				sInstance = new SearchMusic();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		
		return sInstance;
	}

	private SearchMusic() throws ParserConfigurationException {
		mThreadPool = Executors.newSingleThreadExecutor();
	}

	public SearchMusic setListener(OnSearchResultListener l) {
		mListener = l;
		return this;
	}

	public void search(final String key, final int page) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					if(mListener != null) mListener.onSearchResult((ArrayList<SearchResult>)msg.obj);
					break;
				case 2:
					if(mListener != null) mListener.onSearchResult(null);
					break;
				}
			}
		};
		
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				ArrayList<SearchResult> results = getMusicList(key, page);
				if(results == null) {
					handler.sendEmptyMessage(2);
					return;
				}
				
				handler.obtainMessage(1, results).sendToTarget();
			}
		});
	}
	
	private ArrayList<SearchResult> getMusicList(final String key, final int page){
		final String start = String.valueOf((page - 1) * SIZE);
		
		try {
			Document doc = Jsoup.connect(URL)
					  .data("key", key, "start", start, "size", String.valueOf(SIZE))
					  .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.22 Safari/537.36")
					  .timeout(60 * 1000).get();
			
			Elements songTitles = doc.select("div.song-item.clearfix");
			Elements songInfos;
			ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
			
			TAG:
			for(Element song : songTitles) {
				songInfos = song.getElementsByTag("a");
				SearchResult searchResult = new SearchResult();
				for(Element info : songInfos) {
					// 收费的歌曲
					if(info.attr("href").startsWith("http://y.baidu.com/song/")) {
						continue TAG;
					}
					
					// 跳转到百度音乐盒的歌曲
					if(info.attr("href").equals("#") && !TextUtils.isEmpty(info.attr("data-songdata"))) {
						continue TAG;
					}
					// 歌曲链接 http://music.baidu.com/song/554719861
					if(info.attr("href").startsWith("/song")) {
						searchResult.setMusicName(info.text());
						searchResult.setUrl(info.attr("href"));
					}

					// 歌手链接	http://music.baidu.com/data/artist/redirect?id=5669842
					if(info.attr("href").startsWith("/data/artist")) {
						searchResult.setArtist(info.text());
					}
					
					// 专辑链接	http://music.baidu.com/album/554719801
					if(info.attr("href").startsWith("/album")) {
						searchResult.setAlbum(info.text().replaceAll("《|》", ""));
					}

				}
				
				searchResults.add(searchResult);
			}
			return searchResults;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public interface OnSearchResultListener {
		public void onSearchResult(ArrayList<SearchResult> results);
	}
}
