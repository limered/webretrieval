package uni.webret.ass02;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import org.json.simple.JSONObject;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterUpdater extends Thread{
	static String path = "docs/twitter";
	static boolean running = true;
	
	static int updateTime = 15 * 60 * 1000;
	
	@SuppressWarnings("unchecked")
	public void run(){
		while(running){
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey("pgJ4qXmL3gQDogVc6w5JQ8eiu")
			  .setOAuthConsumerSecret("Thrqj3kWa2ckuIARAhv8qQqyaLM5m917KNDdxtPDZYxdbkuGOJ")
			  .setOAuthAccessToken("49376044-vvVudJAKzjr3MIimlzE3U2I7HplQ871dDdb6gnQBu")
			  .setOAuthAccessTokenSecret("eVXKgezjignYlChnwgILAhxgao2lVhHr8rwV8c2DQcglJ");
			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();
			
			for (int i = 1; i <= 2; i++){
				List<Status> stati = null;
				try{
					stati = twitter.getHomeTimeline(new Paging(i));
				}catch (TwitterException e){
					e.getMessage();
					e.printStackTrace();
				}
				for (Status st : stati){
					JSONObject tweet = new JSONObject();
					JSONObject contents = new JSONObject();
					
					contents.put("id", st.getId());
					contents.put("time", st.getCreatedAt().getTime());
					contents.put("user", st.getUser().getScreenName());
					contents.put("content", st.getText());
					tweet.put("tweet", contents);
					try {
						PrintWriter out = new PrintWriter(path + "/tweet" + st.getId() + ".txt");
						out.println(tweet.toString());
						out.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			try{
				Indexer.index();
				sleep(updateTime);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}
