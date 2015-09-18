package dbn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigUtil {
	
	private static ConfigUtil instance = new ConfigUtil();
	
	private Map<String,String> map = new HashMap<String,String>() {
		{
			put("preTrain_learningRate","0.1");	
			put("fineTune_learningRate","1.5");
			put("CD_k","1");
			put("epoch","1000");
			put("hidden_layers","60,20,12");
			put("CV_fold","10");
			put("dropOut_standard","0.5");
			put("dropOut_alpha","0");
			put("dropOut_beta","0");
			put("preTrain_randomSeed","325325");
			put("label_number","1");
		}
	};
	
	private static double[][] train;
	private static double[][] label;
	
	
	private ConfigUtil() {
		
	}
	
	public static ConfigUtil getInstance() {
		return instance;
	}
	
	public double[][] getData() {
		return train;
	}
	
	public double[][] getLabel() {
		return label;
	}
	
	public Map<String,String> getMap() {
		return map;
	}
	/**
	 * 
	 * @param fileName configuration file must keep the following format;<br><br>
	 * 		  '<strong>#</strong>' is the annotation of file, a line begin with a '<strong>#</strong>' will be skipped;<br><br>
	 * 		  use '<strong>=</strong>' to modify default value of parameters, space is not accepted, exp."RBM_learningRate=1.0";<br><br>
	 * 		  training data must be under a line which begins with one or several '<strong>-</strong>';<br><br>
	 * 		  training data could be a int like "1" or a double like "1.0", attribute values should be seperated with a '<strong>\t</strong>';
	 * @author Sean
	 *
	 */
	public ConfigUtil getConfig(String fileName) {
		List<double[]> list = new ArrayList<double[]>();
	    try {
			File file = new File(fileName);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			boolean bool = false;
			while ((tempString = reader.readLine()) != null) {
				if(tempString.charAt(0)=='#') continue;
				if(tempString.charAt(0)=='-') {
					bool = true;
					continue;
				}
				tempString = tempString.split("#")[0];
				if(bool) 
					list.add(strA2douA(tempString.split("\t")));
				else {
					String[] keymap = tempString.split("=");
					if(keymap.length==2) getInstance().getMap().put(keymap[0], keymap[1]);
				}
			}
			reader.close();
			parseData(list);
		} catch (Exception e) {
			Util.printError("unable to read configuration file");
			e.printStackTrace();
		}
	    return instance;
	}
	
	private void parseData(List<double[]> list) {
		int labelNumber = Integer.parseInt(getInstance().getMap().get("label_number"));
	    if(list.size()!=0) {
			train = new double[list.size()][list.get(0).length-labelNumber];
			label = new double[list.size()][labelNumber];
	    		for(int i=0; i<list.size(); ++i) {
	    			double[] instance = list.get(i);
	    			for(int j=0; j<list.get(0).length; ++j) {
	    				if(j<train[0].length) train[i][j] = instance[j];
	    				else label[i][j-train[0].length] = instance[j];
	    			}
	    		}
	    }
	}
	
	
	private double[] strA2douA(String[] string) {
		double[] doubleArray = new double[string.length];
		for(int i=0; i<string.length; ++i) 
			doubleArray[i] = Double.parseDouble(string[i]);
		return doubleArray;
	}
	
	
	public static void main(String[] args) {
		Set<String> keyset = getInstance().getConfig("config.txt").getMap().keySet();
		Iterator<String> iterator = keyset.iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			System.out.println(key+"\t"+getInstance().getMap().get(key));
		}
		for(double[] d:train) {
			for(double dd:d)
				System.out.print(dd+"\t");
			System.out.println();
		}
		for(double[] d:label) {
			for(double dd:d)
				System.out.print(dd+"\t");
			System.out.println();
		}
	}
	
}
