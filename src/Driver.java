import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class Driver {

	public static void main(String[] args) {
		List<String> files=new ArrayList<String>();
		files.add("normal1");
		files.add("normal2");
		files.add("normal3");
		files.add("urlescaping");
		ThreadPoolExecutor executor=(ThreadPoolExecutor) Executors.newFixedThreadPool(4);  //4 Thread will run multiple files concurrently
		for(int i=0;i<files.size();i++){
			executor.execute(new Converter(files.get(i)));
		}
		executor.shutdown();
	}

}
