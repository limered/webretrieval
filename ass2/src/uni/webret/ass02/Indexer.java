package uni.webret.ass02;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.Analyzer;
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

public class Indexer {
	private Indexer(){}
	
	static String indexPath = "index";
	static String docsPath = "docs";	
	
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
			
			//iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			iwc.setOpenMode(OpenMode.CREATE);
			
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
	        	Document doc = new Document();
	        	
	        	Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
	        	doc.add(pathField);
	        	
	        	doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));
	        	
	        	doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))));
	        	
	        	if(writer.getConfig().getOpenMode() == OpenMode.CREATE){
	        		writer.addDocument(doc);
	        	}else{
	        		writer.updateDocument(new Term("path", file.getPath()), doc);
	        	}
	        }finally{
	        	fis.close();
	        }
		}
	}
	}
}
