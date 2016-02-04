package dbn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Za {
	public static void main(String[] args) {
		//String[][] train = readDataAndLabel(1615,1615);
		String[][] data = readDataAndLabel(388,388);
//		String[][] data = readData(65,"DBN_test");
//		System.out.println(Arrays.toString(data[0]));
//		System.out.println(Arrays.toString(train[0]));
//		List<String[]> list = new ArrayList<String[]>();
//		int count = 0;
//		for(String dat[]:data) {
//			System.out.println(dat[140]);
//			String d = dat[140];
//			if(Double.parseDouble(d) > 0) count++;
//		}
//		System.out.println("positive number is: "+ count);
//		System.out.println("negative number is: "+ (65-count));
//		
//		int count = 0;
//		Loop:for(int i=0; i<train.length; ++i) {
//			for(int j=0; j<data.length; ++j) {
//				if(Arrays.equals(train[i], data[j])) {
//					System.out.println("line "+i+" equals to data "+j+": "+Arrays.toString(train[i]));
//					count++;
//					continue Loop;
//				}
//			}
//			list.add(train[i]);
//		}
//		System.out.println("repeated numbers: "+count);
//		System.out.println("list size: "+list.size());
		try {
				BufferedWriter out = new BufferedWriter(new FileWriter("data&labels388"));
				for(String[] instance:data) {
					for(String num:instance) 
						out.write(num+"\t");
					out.write("\n");
				}
				out.close();
			} catch (IOException e) {
		}
//		parseOriginData("mupro");
		
	}
	
	private static String[][] readDataAndLabel(int num, int actual) {
		String[][] data = new String[actual][141];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("input"+num+".txt")));
			String tempString = null;
			int count = 0;
			while ((tempString = reader.readLine()) != null && count<actual) {
				String[] sa = tempString.split(" ");
				for(int i=0; i<sa.length; ++i) 
					data[count][i] = sa[i];
				count ++;
			}
			
			reader = new BufferedReader(new FileReader(new File("labelC"+num+".txt")));
			tempString = null;
			count = 0;
			while ((tempString = reader.readLine()) != null && count<actual) {
				data[count][140] = tempString;
				count ++;
			}
			reader.close();
		}
		catch(Exception e) {
			
		}
		
		return data;
	}
	
	private static String[][] readData(int num, String fileName) {
		String[][] data = new String[num][141];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String tempString = null;
			int count = 0;
			while ((tempString = reader.readLine()) != null) {
				String[] sa = tempString.split("\t");
				for(int i=0; i<sa.length; ++i) 
					data[count][i] = sa[i];
				count ++;
			}
		} catch(Exception e) {
			
		}
		return data;
	}
	
	private static void parseOriginData(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			BufferedWriter out = new BufferedWriter(new FileWriter("muprodata"));
			String tempString = null;
			int count = 1;
			while ((tempString = reader.readLine()) != null) {
				if(count++%10 == 9) {
					System.out.println(tempString);
					out.write(tempString+"\n");
				}
			}
			reader.close();
			out.close();
		} catch(Exception e) {
			
		}
	}
}
