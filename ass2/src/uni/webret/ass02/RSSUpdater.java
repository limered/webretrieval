package uni.webret.ass02;

import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedItem;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;

import org.json.simple.JSONObject;

public class RSSUpdater extends Thread {
	static String path = "docs/rss";
	static boolean running = true;
	static int sleeptime = 5 * 60 * 1000;
	
	static String urlList[] = new String[11];
	
	public RSSUpdater() {
		urlList[0] = "http://www.gamespot.com/feeds/reviews/";
		urlList[1] = "http://www.gamesradar.com/all-platforms/news/rss/";
		urlList[2] = "http://feeds.feedburner.com/GamasutraFeatureArticles";
		urlList[3] = "http://feeds.feedburner.com/GamasutraNews";
		urlList[4] = "http://feeds.feedburner.com/RockPaperShotgun";
		urlList[5] = "http://feeds.gawker.com/kotaku/full";
		urlList[6] = "http://www.gamespot.com/feeds/news";
		urlList[7] = "http://n4g.com/rss/news?channel=&sort=latest";
		urlList[8] = "http://www.polygon.com/rss/index.xml";
		urlList[9] = "http://www.joystiq.com/tag/@news/rss.xml";
		urlList[10] = "http://www.joystiq.com/tag/@reviews/rss.xml";
	}
	
	@SuppressWarnings("unchecked")
	private void loadAndSaveFeed(String urlString){
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Feed feed = null;
		try {
			feed = FeedParser.parse(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(feed == null){
			try{
				sleep(500);
			}catch(Exception e){
				System.err.println(urlString + " failed!");
//				e.printStackTrace();
			}
			return;
		}
		int items = feed.getItemCount() | 0;
		for (int j = 0; j < items; j++) {
			FeedItem item = feed.getItem(j);
			
			JSONObject rssfeed = new JSONObject();
			JSONObject contents = new JSONObject();

			contents.put("website", urlString);
			contents.put("title", item.getTitle());
			contents.put("text", item.getDescriptionAsText());
			contents.put("link",item.getLink().toString());
			
			rssfeed.put("rssfeed", contents);
			
//			String id = String.format("%04d", j);
			try {
				PrintWriter out = new PrintWriter(path + "/rssfeed" + item.getTitle().hashCode() + ".txt");
				out.println(rssfeed.toString());
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try{
				sleep(500);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void run(){
		while(running){
			System.out.println("RSS update...");
			for (String uri : urlList) {
				loadAndSaveFeed(uri);
			}
			System.out.println("RSS update done...indexing");
			try{
				Indexer.index();
				System.out.println("RSS indexing done...sleeping");
				sleep(sleeptime);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}
