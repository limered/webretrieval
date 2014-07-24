package uni.webret.ass02;

public class Main {

	public static void main(String[] args) {
		
		TwitterUpdater twitter = new TwitterUpdater();
		twitter.start();
		
		RedditUpdater reddit = new RedditUpdater();
		reddit.start();
		
		YRSSReader rss = new YRSSReader();
		rss.start();

//		Indexer.index();
		
		try {
			WebServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
