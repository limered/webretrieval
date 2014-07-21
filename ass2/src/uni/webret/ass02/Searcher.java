package uni.webret.ass02;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {
	static String index = "index";
	static String field = "contents";
	
	public static void search(String[] args) throws Exception{
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		
		QueryParser parser = new QueryParser(Version.LUCENE_4_9, field, analyzer);
		
		String queries = null;
		String queryString = null;
		
		while (true){
			if(queries == null && queryString == null){
				System.out.println("Enter query: ");
			}
			
			String line = queryString != null ? queryString : in.readLine();
			
			if (line == null || line.length() == -1) {
				break;
			}
			
			line = line.trim();
			if(line.length() == 0){
				break;
			}
			
			Query query = parser.parse(line);
			
//			searcher.search(query, null, 100);
			
			doPagingSearch(in, searcher, query, 100, false, false);
			
			if(queryString != null){
				break;
			}
		}
		reader.close();
	}
	
	public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, 
			int hitsPerPage, boolean raw, boolean interactive) throws IOException{
		
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;
		
		int nuTotalHits = results.totalHits;
		
		int start = 0;
		int end = Math.min(nuTotalHits, hitsPerPage);
		
		while (true){
			for (int i = start; i < end; i++) {
				if (raw) {                              // output raw format
					System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
					continue;
				}
				
				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get("path");
				if (path != null) {
					System.out.println((i+1) + ". " + path);
					String title = doc.get("title");
					if (title != null) {
						System.out.println("   Title: " + doc.get("title"));
					}
				} else {
					System.out.println((i+1) + ". " + "No path for this document");
			    }
				              
			}
			if (!interactive || end == 0) {
				break;
			}
		}
	}
}
