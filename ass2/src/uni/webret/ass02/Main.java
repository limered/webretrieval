package uni.webret.ass02;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			WebServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		Indexer.index();
//		try {
//			Searcher.search(null);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
