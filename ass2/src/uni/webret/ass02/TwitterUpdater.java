package uni.webret.ass02;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterUpdater {
	static String path = "docs/twitter";
	
	public static void update() throws TwitterException{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("*****************")
		  .setOAuthConsumerSecret("************************")
		  .setOAuthAccessToken("**************************************")
		  .setOAuthAccessTokenSecret("********************************");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		for (int i = 1; i <= 5; i++){
			List<Status> stati = twitter.getHomeTimeline(new Paging(i));
			for (Status st : stati){
				try {
					PrintWriter out = new PrintWriter(path + "/tweet" + st.getId() + ".txt");
					out.println("<tweet><time>" + st.getCreatedAt() + "</time>");
					out.println("<user>" + st.getUser().getScreenName() + "</user>");
					out.println("<content>" + st.getText() + "</content></tweet>");
					out.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
