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

public class YouTubeUpdater extends Thread {
	static String path = "docs/youtube";
	static boolean running = true;
	static int sleeptime = 5 * 60 * 1000;
	
	static String urlList[] = new String[20];
	
	public YouTubeUpdater() {
		/*Youtube Stuff*/
		urlList[0] = "http://gdata.youtube.com/feeds/videos?q=gaming&max-results=50&search_sort=video_date_uploaded";
		urlList[1] = "http://gdata.youtube.com/feeds/videos?q=gaming&start-index=51&max-results=50&search_sort=video_date_uploaded";
		
		urlList[2] = "http://gdata.youtube.com/feeds/videos?q=game%20review&max-results=50&search_sort=video_date_uploaded";
		urlList[3] = "http://gdata.youtube.com/feeds/videos?q=game%20review&start-index=51&max-results=50&search_sort=video_date_uploaded";
		urlList[4] = "http://gdata.youtube.com/feeds/videos?q=game%20review&start-index=101&max-results=50&search_sort=video_date_uploaded";
		
		urlList[5] = "http://gdata.youtube.com/feeds/videos?q=game%20review%20gamestar&max-results=50&search_sort=video_date_uploaded";
		urlList[6] = "http://gdata.youtube.com/feeds/videos?q=game%20review%20gamestar&max-results=50&search_sort=video_date_uploaded";
		
		urlList[7] = "http://gdata.youtube.com/feeds/videos?q=inside%20gaming%20daily&max-results=50&search_sort=video_date_uploaded";
		
		urlList[8] = "http://gdata.youtube.com/feeds/videos?q=game%20news&max-results=50&search_sort=video_date_uploaded";
		urlList[9] = "http://gdata.youtube.com/feeds/videos?q=game%20news&start-index=51&max-results=50&search_sort=video_date_uploaded";
		
		urlList[10] = "http://gdata.youtube.com/feeds/videos?q=WTF%20is&max-results=50&search_sort=video_date_uploaded";
		urlList[11] = "http://gdata.youtube.com/feeds/videos?q=WTF%20is&start-index=51&max-results=50&search_sort=video_date_uploaded";
		
		urlList[12] = "http://gdata.youtube.com/feeds/videos?q=IGN%20News&max-results=50&search_sort=video_date_uploaded";
		urlList[13] = "http://gdata.youtube.com/feeds/videos?q=IGN%20News&start-index=51&max-results=50&search_sort=video_date_uploaded";
		
		urlList[14] = "http://gdata.youtube.com/feeds/videos?q=athenewins%20News&max-results=50&search_sort=video_date_uploaded";
		urlList[15] = "http://gdata.youtube.com/feeds/videos?q=athenewins%20News&start-index=51&max-results=50&search_sort=video_date_uploaded";
		
		urlList[16] = "http://gdata.youtube.com/feeds/videos?q=IGN%20Reviews&max-results=50&search_sort=video_date_uploaded";
		urlList[17] = "http://gdata.youtube.com/feeds/videos?q=IGN%20Reviews&start-index=51&max-results=50&search_sort=video_date_uploaded";
		
		urlList[18] = "http://gdata.youtube.com/feeds/videos?q=Inside%20Gaming%20Review&max-results=50&search_sort=video_date_uploaded";
		urlList[19] = "http://gdata.youtube.com/feeds/videos?q=Inside%20Gaming%20Review&start-index=51&max-results=50&search_sort=video_date_uploaded";
	}
	
	@SuppressWarnings("unchecked")
	private void loadAndSaveFeed(String urlString){
		FeedReader cr = null;
		try {
			cr = new FeedReader(new HttpURL(urlString));
			
			ChannelFeed feed = cr.readChannel();
			
			List<ItemEntry> items = feed.getItems();
			
			if(items == null || items.size() == 0){
				return;
			}
			for (ItemEntry item : items){
				String pub = item.getPubDate() != null ? item.getPubDate() : feed.getLastBuildOrUpdatedDate();
				
				DateFormat formatter = null;
				formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

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
				
				wrapper.put("youtube", content);
				
				try {
					PrintWriter out = new PrintWriter(path + "/youtube" + item.getTitleText().hashCode() + ".txt");
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
			System.out.println("Youtube update...");
			for (String uri : urlList) {
				loadAndSaveFeed(uri);
			}
			System.out.println("Youtube update done...indexing");
			try{
				Indexer.index();
				System.out.println("Youtube indexing done...sleeping");
				sleep(sleeptime);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}

