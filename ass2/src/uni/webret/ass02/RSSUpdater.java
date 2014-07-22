package uni.webret.ass02;

import it.sauronsoftware.feed4j.FeedIOException;
import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.FeedXMLParseException;
import it.sauronsoftware.feed4j.UnsupportedFeedException;
import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedItem;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONObject;

public class RSSUpdater extends Thread {
	static String path = "docs/rss";
	static boolean running = true;
	
	@SuppressWarnings("unchecked")
	public void run(){
		while(running){
			
			String urlList[] = new String[5];
			urlList[0] = "http://www.gamespot.com/feeds/reviews/";
			urlList[1] = "http://www.gamesradar.com/all-platforms/news/rss/";
			
			for (int i = 0; i < 2; i++) {
				URL url = null;
				try {
					url = new URL(urlList[0]);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Feed feed = null;
				try {
					feed = FeedParser.parse(url);
				} catch (FeedIOException | FeedXMLParseException
						| UnsupportedFeedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				int items = feed.getItemCount();
				for (int j = 0; j < items; j++) {
					FeedItem item = feed.getItem(j);
					
					JSONObject rssfeed = new JSONObject();
					JSONObject contents = new JSONObject();

					contents.put("website", urlList[i]);
					contents.put("title", item.getTitle());
					contents.put("text", item.getDescriptionAsText());
					contents.put("link",item.getLink());
					rssfeed.put("rssfeed", contents);
					
					String id = String.format("%04d", j);
					try {
						PrintWriter out = new PrintWriter(path + "/rssfedd" + i+""+id
								+ ".txt");
						out.println(rssfeed.toString());
						out.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				
			}
				
			try{
				Indexer.index();
				sleep(900000);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}
