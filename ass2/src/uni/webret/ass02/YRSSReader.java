package uni.webret.ass02;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpURL;
import org.json.simple.JSONObject;

import yarfraw.core.datamodel.ChannelFeed;
import yarfraw.core.datamodel.ItemEntry;
import yarfraw.core.datamodel.YarfrawException;
import yarfraw.io.FeedReader;

public class YRSSReader extends Thread {
	static String path = "docs/rss";
	static boolean running = true;
	static int sleeptime = 5 * 60 * 1000;
	
	static String urlList[] = new String[11];
	
	public YRSSReader() {
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
		FeedReader cr = null;
		try {
			cr = new FeedReader(new HttpURL(urlString));
			
			ChannelFeed feed = cr.readChannel();
			
			List<ItemEntry> items = feed.getItems();
			
			if(items.size() == 0){
				return;
			}
			for (ItemEntry item : items){
				String pub = item.getPubDate() != null ? item.getPubDate() : feed.getLastBuildOrUpdatedDate();
				pub = pub.substring(5, 25);
				DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
				Date tdate;
				try {
					tdate = (Date)formatter.parse(pub);
				} catch (ParseException e1) {
					tdate = new Date();
				}
				
				JSONObject wrapper = new JSONObject();
				JSONObject content = new JSONObject();
				content.put("source", feed.getTitleText());
				content.put("date", tdate.getTime());
				content.put("link", item.getLinks().get(0).getHref());
				content.put("title", item.getTitleText());
				String text = (item.getContent() != null) 
						? item.getContent().getContentText().get(0) 
						: item.getDescriptionOrSummaryText();
				String nohtml = text.replaceAll("\\<.*?\\>", "");
				nohtml = nohtml.replaceAll("\n", "");
				content.put("text", nohtml);
				
				wrapper.put("rssfeed", content);
				
				try {
					PrintWriter out = new PrintWriter(path + "/rssfeed" + item.getTitleText().hashCode() + ".txt");
					out.println(wrapper.toString());
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}	
			}
			
		} catch (YarfrawException | IOException e) {
			e.printStackTrace();
			return;
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

