package uni.webret.ass02;

import twitter4j.TwitterException;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		try {
//			TwitterUpdater.update();
//		} catch (TwitterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
		Indexer.index();
//		
		try {
			WebServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
//		try {
//			Searcher.search(null);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
