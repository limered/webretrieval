package uni.webret.ass02;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WebServer {
	
	public static void start() throws Exception{
		
		Server server = new Server(8080);
		
		URL warURL = WebServer.class.getClassLoader().getResource("");
//		System.out.println(warURL.toExternalForm());
		String warUrlString = warURL.toExternalForm();
		WebAppContext webApp = new WebAppContext(warUrlString, "");
		server.setHandler(webApp);
		
		webApp.addServlet(HelloServlet.class, "/html/search");
		
		server.start();
        server.join();
	}
	
	@SuppressWarnings("serial")
	public static class HelloServlet extends HttpServlet{
		
		@SuppressWarnings("unchecked")
		@Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
			Map<String, String[]> parameters = request.getParameterMap();
			String[] querys = parameters.get("q")[0].split(" ");
			String[] filters = parameters.get("filter");
			boolean sorts = (parameters.get("sort") != null);
			
			StringBuilder query = new StringBuilder();
			for (String s : querys){
				if(s.contains("contents:")||s.contains("type:")||s.contains("date:"))
					query.append(s + " ");
				else
					query.append("contents:" + s + " ");
			}
			String inStr = request.getQueryString();
			inStr = URLDecoder.decode(inStr, "UTF-8");
			Vector<Document> result = null;
			try {
				 result = Searcher.search(query.toString(), filters, sorts);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            JSONObject resultObject = new JSONObject();
            resultObject.put("query", inStr);
            
            if(result.size() == 0){
            	resultObject.put("success", false);
            }else{
            	resultObject.put("success", true);
            }
            
            JSONArray items = new JSONArray();
            
            for (int i = 0; i < result.size(); i++){
            	items.add(makeResultItem(result.get(i)));
            }
            
            resultObject.put("items", items);
            
            out.append(resultObject.toString());
            
            response.setStatus(HttpServletResponse.SC_OK);
        }
		@SuppressWarnings("unchecked")
		private JSONObject makeResultItem(Document doc){
			switch (doc.get("type")){
			case "twitter":{
				JSONObject wrapper = new JSONObject();
				JSONObject content = new JSONObject();
				
				content.put("link", doc.get("link").toString());
				content.put("linkText", doc.get("linkText").toString());
				content.put("date", doc.get("date"));
				content.put("shortDecr", doc.get("shortDecr").toString());
				
				wrapper.put("twitter", content);
				
				return wrapper;
			}
			case "reddit":{
				JSONObject reddit = new JSONObject();
				JSONObject content = new JSONObject();
				
				content.put("link", doc.get("link").toString());
				content.put("linkText", doc.get("linkText").toString());
				content.put("date", doc.get("date"));
				content.put("subreddit", doc.get("subreddit").toString());
				content.put("clink", doc.get("clink").toString());
				
				reddit.put("reddit", content);
				
				return reddit;
			}
			case "rssfeed":{
				JSONObject wrapper = new JSONObject();
				JSONObject content = new JSONObject();
				
				content.put("link", doc.get("link").toString());
				content.put("title", doc.get("title").toString());
				content.put("website", doc.get("source"));
				content.put("date", doc.get("date"));
				content.put("shortDecr", doc.get("shortDecr").toString());
				
				wrapper.put("rssfeed", content);
				
				return wrapper;
			}
			case "youtube":{
				JSONObject wrapper = new JSONObject();
				JSONObject content = new JSONObject();
				
				content.put("link", doc.get("link").toString());
				content.put("title", doc.get("title").toString());
				content.put("website", doc.get("source"));
				content.put("date", doc.get("date"));
				content.put("shortDecr", doc.get("shortDecr").toString());
				
				wrapper.put("rssfeed", content);
				
				return wrapper;
			}
			}
			
			return null;
		}
	}
}
