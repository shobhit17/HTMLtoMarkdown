
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class Driver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Converter> files=new ArrayList<Converter>();
		files.add(new Converter("normal1"));
		files.add(new Converter("urlescaping"));
		ThreadPoolExecutor executor=(ThreadPoolExecutor) Executors.newFixedThreadPool(4);
		for(int i=0;i<files.size();i++){
			executor.execute(files.get(i));
		}
		executor.shutdown();
	}

}