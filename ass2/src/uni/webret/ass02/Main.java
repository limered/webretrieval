package uni.webret.ass02;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Indexer.index();
		try {
			Searcher.search(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
