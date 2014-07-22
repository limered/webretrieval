package uni.webret.ass02;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebServer {
	
	public static void start() throws Exception{
		
		Server server = new Server(8080);
		
		URL warURL = WebServer.class.getClassLoader().getResource("");
		System.out.println(warURL.toExternalForm());
		String warUrlString = warURL.toExternalForm();
		WebAppContext webApp = new WebAppContext(warUrlString, "");
		server.setHandler(webApp);
		
		webApp.addServlet(HelloServlet.class, "/html/q");
		
		server.start();
        server.join();
	}
	
	@SuppressWarnings("serial")
	public static class HelloServlet extends HttpServlet{
		
		@Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
			String inStr = request.getQueryString();
			inStr = URLDecoder.decode(inStr, "UTF-8");
			Vector<Document> result = null;
			try {
				 result = Searcher.search(inStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.append("<h2>Results for: " + inStr + "</h2>");
            
            for (int i = 0; i < result.size(); i++){
            	out.append(makeResultItem(result.get(i)));
            }
            
            response.setStatus(HttpServletResponse.SC_OK);
        }
		private String makeResultItem(Document doc){
			StringBuilder sb = new StringBuilder();
			sb.append("<div class='result'>");
			sb.append("<a class='doc-path' href='" + doc.get("path") + "' target='_blank'>" + doc.get("path") + "</a>");
			sb.append("<div class='doc-modified'>" + doc.get("test") + "</div>");
			sb.append("<div class='doc-content'>" + doc.get("modified") + "</div>");
			sb.append("</div>");
			return sb.toString();
		}
	}
}
