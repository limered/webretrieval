package uni.webret.ass02;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

public class RSSUpdater extends Thread {
	static String path = "docs/rss";
	static boolean running = true;
	
	@SuppressWarnings("unchecked")
	public void run(){
		while(running){
			
			JSONObject tweet = new JSONObject();
			JSONObject contents = new JSONObject();
			
			contents.put("time", "bla");
			contents.put("user", "blo");
			contents.put("content", "nana");
			tweet.put("tweet", contents);
			try {
				PrintWriter out = new PrintWriter(path + "/tweet" + null + ".txt");
				out.println(tweet.toString());
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
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
