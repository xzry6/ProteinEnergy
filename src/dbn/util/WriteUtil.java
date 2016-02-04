package dbn.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dbn.ConfusionTable;
import dbn.RestrictedBoltzmannMachine;
import dbn.classifier.Classifier;

public class WriteUtil {
	
	private static WriteUtil instance = new WriteUtil();
	
	private BufferedWriter out;
	
	private String mode;
	
	private WriteUtil() {
		
	}
	
	public static WriteUtil getInstance() {
		return instance;
	}
	
	public WriteUtil getMode(String mode) {
		this.mode = mode;
		try {
			if(mode.contains("CV_"))
				out = new BufferedWriter(new FileWriter("DBN_CV_result"));
			else if(mode.equals("predict"))
				out = new BufferedWriter(new FileWriter("DBN_predict_result"));
			else if(mode.equals("test"))
				out = new BufferedWriter(new FileWriter("DBN_test_result"));
			else out = new BufferedWriter(new FileWriter("DBN_model"));
		} catch(IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	
	public void close() {
		try {
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public WriteUtil writeMap(Map<String,String> map, String TestName) {
		try {
			out.write("#####PARAMETERS#####\n");
			if(mode.equals("train")) {
				if(TestName==null)
					map.put("mode","predict");
				else map.put("mode", "test");
			}
				if(Double.parseDouble(map.get("dropOut_standard"))!=0
					||(Double.parseDouble(map.get("dropOut_alpha"))==0
					&&Double.parseDouble(map.get("dropOut_beta"))==0)) {
					map.put("dropOut_alpha","NaN");
					map.put("dropOut_beta","NaN");
				} 
			Iterator<String> i = map.keySet().iterator();
			while(i.hasNext()) {
				String key = i.next();
				out.write(key+"="+map.get(key)+"\n");
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	
	public WriteUtil writeTable() {
		try {
			int TP = ConfusionTable.getTP();
			int FP = ConfusionTable.getFP();
			int FN = ConfusionTable.getFN();
			int TN = ConfusionTable.getTN();
			writeWave();
			out.write("Accuracy="+(double)(TP+TN)/(TP+TN+FP+FN)+"\n");
			out.write("TP="+TP+"\n");
			out.write("FP="+FP+"\n");
			out.write("FN="+FN+"\n");
			out.write("TN="+TN+"\n");
		} catch(IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public void writeCVheader(double[][] label) {
		try {
			writeNNN();
			out.write(mode+" RESULT\n");
			out.write(label.length+" instances\n");
			out.write("left "+label[0].length+" columns are outputs\n");
			out.write("right "+label[0].length+" columns are original lables\n");
			writeWave();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public synchronized WriteUtil writeDoubleResult(double[][] output, double[][] label) {
		try {
			for(int n=0; n<output.length; ++n) {
				for(int j=0; j<output[0].length; ++j)
					out.write(output[n][j]+"\t");
				for(int j=0; j<label[0].length; ++j)
					out.write(label[n][j]+"\t");
				out.write("\n");
			}
		} catch(IOException e) {
			//e.printStackTrace();
			Util.printError("out closed");
		}
		return this;
	}


	public WriteUtil writeResult(double[][] label) {
		try {
			writeNNN();
			out.write(mode+" RESULT\n");
			out.write(label.length+" instances\n");
			out.write("label has "+label[0].length+" attributes\n");
			writeWave();
			for(double[] row:label) {
				for(double cell:row)
					out.write(cell+"\t");
				out.write("\n");
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	
	public WriteUtil writeModel(List<RestrictedBoltzmannMachine> rbmList, Classifier c) {
		try {
			writeNNN().writeWave();
			for(RestrictedBoltzmannMachine rbm:rbmList)
				writeW(rbm.getW());
			writeW(c.getW()).writeWave();
			for(RestrictedBoltzmannMachine rbm:rbmList)
				writeB(rbm.getHidBias());
			writeB(c.getB());
		} catch(IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	private WriteUtil writeW(double[][] W) throws IOException {
		for(double[] Wrow:W) {
			for(double Wcell:Wrow)
				out.write(Wcell+"\t");
			out.write("\n");
		}          
		writePound();
		return this;
	}
	
	
	private WriteUtil writeB(double[] B) throws IOException {
		for(double Bcell:B) 
			out.write(Bcell+"\t");
		out.write("\n");
		return this;
	}
	
	private WriteUtil writeWave() throws IOException {
		out.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		return this;
	}
	
	private WriteUtil writePound() throws IOException {
		out.write("############################################\n");
		return this;
	}
	
	private WriteUtil writeNNN() throws IOException {
		out.write("\n\n\n");
		return this;
	}
}
