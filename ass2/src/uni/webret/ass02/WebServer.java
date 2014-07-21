package uni.webret.ass02;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.servlet.ServletHandler;

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
			if(request.getQueryString() == null){
				response.setContentType("text/html");
	            response.setStatus(HttpServletResponse.SC_OK);
	            PrintWriter out = response.getWriter();
	            out.append("hahahahahahahahaha</br> bjbajbajb");
	            out.println("<h1>Your search:</h1>");
	            out.println("<button type='submit' onclick='send();'>Your search:</button>");
			}
			
			System.out.println(request.getQueryString());
			

        }
	}
}
