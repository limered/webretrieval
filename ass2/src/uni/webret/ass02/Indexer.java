package uni.webret.ass02;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Indexer {
	private Indexer(){}
	
	static String indexPath = "index";
	static String docsPath = "docs";
	
	static TokenStream tokenStream = null;
	
	static void index(){
		final File docDir = new File(docsPath);
	    if (!docDir.exists() || !docDir.canRead()) {
	      System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
	      System.exit(1);
	    }
		
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
			
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
//			iwc.setOpenMode(OpenMode.CREATE);
			
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDoc(writer, docDir);
						
			writer.close();
			
		}catch (IOException e) {
			System.out.println(" caught a " + e.getClass() +
					"\n with message: " + e.getMessage());
		}
	}
	
	static void indexDoc(IndexWriter writer, File file)
		throws IOException{
	if (file.canRead()) {
		if (file.isDirectory()) {
		  String[] files = file.list();
		  // an IO error could occur
		  if (files != null) {
		    for (int i = 0; i < files.length; i++) {
		      indexDoc(writer, new File(file, files[i]));
		    }
		  }
		} else {
			FileInputStream fis;
	        try {
	          fis = new FileInputStream(file);
	        } catch (FileNotFoundException fnfe) {
	          // at least on windows, some temporary files raise this exception with an "access denied" message
	          // checking if the file can be read doesn't help
	        	return;
			}
	        
	        try {
	        	
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
	        	
	        	String line = reader.readLine();
	        	
	        	reader.close();
	        	
	        	JSONParser parser = new JSONParser(); 
	        	
	        	Object o = null;
				try {
					o = parser.parse(line);
				} catch (ParseException e) {
					e.printStackTrace();
				}
	        	JSONObject jso = (JSONObject)o;
	        	
	        	Document doc = new Document();
	        	
	        	Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
	        	doc.add(pathField);
	        		        	
	        	if(jso.get("tweet") != null){
	        		//twitter file
	        		JSONObject twitter = (JSONObject)jso.get("tweet");
	        		
	        		Field typeField = new StringField("type", "twitter", Field.Store.YES);
	        		doc.add(typeField);
	        		
	        		Field linkField = new StringField("link", "http://www.twitter.com/" + twitter.get("user") + "/status/" + twitter.get("id"), Field.Store.YES);
	        		doc.add(linkField);
	        		
	        		Field linkTextField = new StringField("linkText", (String)twitter.get("user"), Field.Store.YES);
	        		doc.add(linkTextField);
	        		
	        		Field dateField = new LongField("date", (Long)twitter.get("time"), Field.Store.YES);
	        		doc.add(dateField);
	        		
	        		String content = (String)twitter.get("content");
	        		Field shortDesField = new StringField("shortDecr", (content.length() > 100) ? content.substring(0, 100) + "..." : content, Field.Store.YES);
	        		doc.add(shortDesField);
	        		
	        		content = content.replaceAll("#", " ");
	        		content = content.replaceAll("@", " ");
	        		
	        		Field searchField = new TextField("contents", new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)));
	        		doc.add(searchField);
	        		
	        	}else if (jso.get("reddit") != null){
	        		//reddit
	        		JSONObject reddit = (JSONObject)jso.get("reddit");
	        		
	        		Field typeField = new StringField("type", "reddit", Field.Store.YES);
	        		doc.add(typeField);
	        		
	        		Field cLinkField = new StringField("clink", "http://www.reddit.com" + reddit.get("clink"), Field.Store.YES);
	        		doc.add(cLinkField);
	        		
	        		Field linkField = new StringField("link", (String)reddit.get("link"), Field.Store.YES);
	        		doc.add(linkField);
	        		
	        		Field subField = new StringField("subreddit", (String)reddit.get("subreddit"), Field.Store.YES);
	        		doc.add(subField);
	        		
	        		Field linkTextField = new StringField("linkText", (String)reddit.get("title"), Field.Store.YES);
	        		doc.add(linkTextField);
	        		
	        		Field dateField = new LongField("date", new Double((double) reddit.get("time")).longValue(), Field.Store.YES);
	        		doc.add(dateField);
	        		
	        		Field searchField = new TextField("contents", new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)));
	        		doc.add(searchField);
	        	}else{
	        		//rss file
	        		JSONObject rss = (JSONObject)jso.get("rssfeed");
	        		
	        		Field typeField = new StringField("type", "rssfeed", Field.Store.YES);
	        		doc.add(typeField);
	        		
	        		Field linkField = new StringField("link", (String)rss.get("link"), Field.Store.YES);
	        		doc.add(linkField);
	        		
	        		Field titleField = new StringField("title", (String)rss.get("title"), Field.Store.YES);
	        		doc.add(titleField);
	        		
	        		Field feedField = new StringField("website", (String)rss.get("website"), Field.Store.YES);
	        		doc.add(feedField);
	        		
	        		String content = (rss.get("text") != null) ? (String)rss.get("text") : "";
	        		
	        		Field shortDesField = new StringField("shortDecr", (content.length() > 300) ? content.substring(0, 300) + "..." : content, Field.Store.YES);
	        		doc.add(shortDesField);
	        		
	        		Field searchField = new TextField("contents", new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)));
	        		doc.add(searchField);
	        	}
	        	
	        	doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));
	        	
	        	if(writer.getConfig().getOpenMode() == OpenMode.CREATE){
	        		writer.addDocument(doc);
	        	}else{
	        		writer.updateDocument(new Term("path", file.getPath()), doc);
	        	}
	        }catch (Exception e){
	        	e.printStackTrace();
	        }
	        finally{
	        	fis.close();
	        }
		}
	}
	}
}
