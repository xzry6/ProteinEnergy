package dbn;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import dbn.util.ConfigUtil;
import dbn.util.Util;
import dbn.util.WriteUtil;

public class DBNManager {
	
	private static String mode;
	private static Map<String,String> map;
	private static double[][] data;
	private static double[][] label;
	private static int CVfold;
		
	public static void main(String[] args) {
		
		String fileName = args[0];
		String testName = null;
		if(args.length>1) 
			testName = args[1];
		
		ConfigUtil.getInstance().getConfig(fileName,testName);
		map = ConfigUtil.getInstance().getMap();
		mode = map.get("mode");
		data = ConfigUtil.getInstance().getData();
		label = ConfigUtil.getInstance().getLabel();
		WriteUtil.getInstance().getMode(mode).writeMap(map);
		
		if(mode.contains("CV_")) CVmanager(fileName);
		else if(mode.equals("test")) 
			new DeepBeliefNetwork().readModel(fileName)
								   .predict(data);
		else new DeepBeliefNetwork().train(data,label)
									.writeModel();;
	}
	
	public static void CVmanager(String fileName) {
		String[] sa = mode.split("_");
		CVfold = Integer.parseInt(sa[sa.length-1]);
		LinkedBlockingQueue<Map<String,double[][]>> queue = new LinkedBlockingQueue<Map<String,double[][]>>(CVfold);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
		try {
			CrossValidation cv = new CrossValidation(data,label).split(CVfold);
			WriteUtil.getInstance().writeCVheader(label);
			for(int i=0; i<CVfold; ++i)
				queue.put(cv.build(i));
			executor.execute(new CV2DBNadaptor(queue,"dbn 1"));
			executor.execute(new CV2DBNadaptor(queue,"dbn 2"));
			executor.execute(new CV2DBNadaptor(queue,"dbn 3"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		executor.shutdown();
	}
	
	
	private static class CV2DBNadaptor implements Runnable {
		
		private BlockingQueue<Map<String,double[][]>> queue;
		
		private String id;
		
		private static int finishedfold = 0;
		
		public CV2DBNadaptor(BlockingQueue<Map<String,double[][]>> queue, String id) {
			super();
			this.queue = queue;
			this.id = id;
		}
		
		@Override
		public void run() {
			while(!queue.isEmpty()) {
				Map<String,double[][]> map = queue.poll();
				Util.print(id+" begin training a new fold!");
				new DeepBeliefNetwork()
									.train(map.get("traindata"),map.get("trainlabel"))
									.test(map.get("testdata"),map.get("testlabel"));
				Util.print((double)++finishedfold/DBNManager.CVfold*100+"% has finished");
				if(finishedfold==DBNManager.CVfold)
					WriteUtil.getInstance().writeTable().close();
			}
		}
		
		
	}
}
