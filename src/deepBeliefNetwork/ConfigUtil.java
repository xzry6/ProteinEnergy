package deepBeliefNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dbn.util.Util;

public class ConfigUtil {
	public static void main(String[] args) {
		List<double[]> input = new ArrayList<double[]>();
		List<Double> label = new ArrayList<Double>();
		try {
			BufferedReader reader1 = new BufferedReader(new FileReader(new File("input388.txt")));
			BufferedReader reader2 = new BufferedReader(new FileReader(new File("label388.txt")));
			String tempString = null;
			while((tempString=reader1.readLine())!=null) 
				input.add(Util.strA2douA(tempString.split(" ")));
			reader1.close();
			while((tempString=reader2.readLine())!=null) 
				label.add(Double.parseDouble(tempString));
			reader2.close();
			System.out.println(input.size()+"\t"+label.size());
			BufferedWriter put = new BufferedWriter(new FileWriter("DBN_input"));
			for(int i=0; i<input.size(); ++i) {
				for(double d:input.get(i)) put.write(d+"\t");
				double temp = label.get(i)>=0?1:0;
				put.write(temp+"\n");
			}
			put.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
