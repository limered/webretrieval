package uni.webret.ass02;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FieldCacheTermsFilter;
import org.apache.lucene.search.FieldValueFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.Sort;


public class Searcher {
	static String index = "index";
	static String field = "contents";
	static String dateField = "date";
	static String typeField = "type";
	
	static String[] fields = new String[3];

	public static Vector<Document> search(String searchTerm, String[] filters, boolean sorts) throws Exception{
		
		fields[0] = "contents";
		fields[1] = "date";
		fields[2] = "type";
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		
		QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_4_9, fields, analyzer);
//		QueryParser parser = new QueryParser(Version.LUCENE_4_9, field, analyzer);

		Query query = parser.parse(searchTerm);
		
//		System.out.println(query.toString());
		
		Sort sorter = null;
		if(sorts)
			sorter = new Sort(new SortField("date", SortField.Type.LONG, true));
		
		Filter filter = null;
		if(filters != null && filters.length > 0){
			StringBuilder filterString = new StringBuilder();
			for (String s : filters){
				filterString.append(s + " ");
			}
			filter = new FieldCacheTermsFilter("type", filters);
		}
		
		Vector<Document> docs = doPagingSearch(in, searcher, query, 100, sorter, filter);
		
		reader.close();
		
		return docs;
	}
	
	public static Vector<Document> doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, 
			int hitsPerPage, Sort sorter, Filter filter) throws IOException{
		
		Vector<Document> result = new Vector<Document>();
		
		TopDocs results = null;
		
		if(sorter != null && filter != null){
			results = searcher.search(query, filter, hitsPerPage, sorter, true, true);
		}else if (sorter != null && filter == null){
			results = searcher.search(query, hitsPerPage, sorter);
		}else if (sorter == null && filter != null){
			results = searcher.search(query, filter, hitsPerPage);
		}else{
			results = searcher.search(query, hitsPerPage);
		}
		
		ScoreDoc[] hits = results.scoreDocs;
		
		int nuTotalHits = results.totalHits;
		
		int start = 0;
		int end = Math.min(nuTotalHits, hitsPerPage);
		
		
		for (int i = start; i < end; i++) {
//			if (raw) {                              // output raw format
//				System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
//				continue;
//			}
			
//			Document doc = searcher.doc(hits[i].doc);
//			String path = doc.get("path");
//			if (path != null) {
//				System.out.println((i+1) + ". " + path);
//				String title = doc.get("title");
//				if (title != null) {
//					System.out.println("   Title: " + doc.get("title"));
//				}
//			} else {
//				System.out.println((i+1) + ". " + "No path for this document");
//		    }
			result.add(searcher.doc(hits[i].doc));
//			System.out.println(hits[i].doc);
		}
		
		return result;

	}
}
