package uni.webret.ass02;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import org.json.simple.JSONObject;

import com.github.jreddit.submissions.Submission;
import com.github.jreddit.submissions.Submissions;
import com.github.jreddit.user.User;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;

public class RedditUpdater extends Thread {
	static String path = "docs/reddit";
	static boolean running = true;
	static int sleeptime = 5 * 60 * 1000;
	
	static String[] subreddits = new String[8];
	
	RedditUpdater(){
		subreddits[0] = "gaming";
		subreddits[1] = "Games";
		subreddits[2] = "gamernews";
		subreddits[3] = "truegaming";
		subreddits[4] = "Gaming4Gamers";
		subreddits[5] = "IndieGaming";
		subreddits[6] = "gamingpc";
		subreddits[7] = "WebGames";
	}
	
	@SuppressWarnings("unchecked")
	private void loadAndSaveSub(RestClient rest, User user, String subreddit){
		Submissions subsObject = new Submissions(rest);
        List<Submission> subs = null;
        try{
	        subs = subsObject.getSubmissions(subreddit,
	                Submissions.Popularity.HOT, Submissions.Page.FRONTPAGE, user);
        }catch (Exception e){
        	e.printStackTrace();
        }
		for (Submission sub : subs) {
			JSONObject submission = new JSONObject();
			JSONObject contents = new JSONObject();
			
			contents.put("time", sub.getCreatedUTC());
			contents.put("clink", sub.getPermalink());
			contents.put("link", sub.getURL());
			contents.put("title", sub.getTitle());
			contents.put("subreddit", sub.getSubreddit());
			
			submission.put("reddit", contents);
			
			try {
			PrintWriter out = new PrintWriter(path + "/reddit" + sub.getCreatedUTC() + ".txt");
			out.println(submission.toString());
			out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void run(){
		while(running){
			System.out.println("Reddit update...");
			RestClient rest = new HttpRestClient();
			User user = new User(rest, "limered", "tempwebretrieval");
			try{
				user.connect();
			}catch (Exception e){
				e.printStackTrace();
			}
			
	        for(String subreddit : subreddits){
	        	loadAndSaveSub(rest, user, subreddit);
	        }
			
	        System.out.println("Reddit update done...indexing");
	        
			try{
				Indexer.index();
				System.out.println("Reddit indexing done...sleeping");
				sleep(sleeptime);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}
