package dbn;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class TrainManager {
	
	private static int CVfold = Integer.parseInt(ConfigUtil.getInstance().getMap().get("CV_fold"));
	private static double[][] data = ConfigUtil.getInstance().getData();
	private static double[][] label = ConfigUtil.getInstance().getLabel();
	
	private static LinkedBlockingQueue<Map<String,double[][]>> queue = new LinkedBlockingQueue<Map<String,double[][]>>(CVfold);
	private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
	
	public static void main(String[] args) {
		
		String fileName = args[0];

		try {
			ConfigUtil.getInstance().getConfig(fileName);
			CrossValidation cv = new CrossValidation(data,label).split(CVfold);
			for(int i=0; i<CVfold; ++i)
				queue.put(cv.build(i));
			

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
