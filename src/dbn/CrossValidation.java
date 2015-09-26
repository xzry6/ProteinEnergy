package dbn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dbn.util.Util;

public class CrossValidation {
	
	private double[][] data;
	private double[][] label;
	
	private int size;
	private int datalength;
	private int labellength;
	
	private List<double[][]> datalist = new ArrayList<double[][]>();
	private List<double[][]> labellist = new ArrayList<double[][]>();
	/**
	 * @author Sean
	 */
	public CrossValidation(double[][] data, double[][] label) {
		this.data = data;
		this.label = label;
		this.size = data.length;
		this.datalength = data[0].length;
		this.labellength = label[0].length;
	}
	/**
	 * 
	 * @param fold 
	 */
	public CrossValidation split(int fold) {
		if(fold>data.length) {
			Util.printError("too many cross-validation folds");
			return null;
		}
		int piece = size/fold;
		int piecetemp = piece;
		for(int i=0; i<fold; ++i) {
			if(i==fold-1) piecetemp = size-piece*(fold-1);
			double[][] datatemp = new double[piecetemp][datalength];
			double[][] labeltemp = new double[piecetemp][labellength];
			for(int j=0; j<piecetemp; ++j) {
				System.arraycopy(data[i*piece+j],0,datatemp[j],0,datalength);
				System.arraycopy(label[i*piece+j],0,labeltemp[j],0,labellength);
			}
			datalist.add(datatemp);
			labellist.add(labeltemp);
		}
		return this;
	}
	
	
	/**
	 * 
	 * @param number number must be in a range of [0,fold); 
	 * @return a map of train and test data and label for following work;<br>
	 * grab training data using map.get("traindata");<br>
	 * grab training label using map.get("trainlabel");<br>
	 * grab test data using map.get("testdata");<br>
	 * grab test label using map.get("testlabel");<br>
	 */
	public Map<String,double[][]> build(int number) {
		
		if(number>=datalist.size()||number<0) {
			Util.printError("number is smaller than 0 or bigger than cv fold");
			return null;
		}
		
		Map<String,double[][]> map = new HashMap<String,double[][]>();
		int trainsize = size-datalist.get(number).length;
		double[][] traindata = new double[trainsize][datalength];
		double[][] trainlabel = new double[trainsize][labellength];
		
		int row = 0;
		for(int i=0; i<datalist.size(); ++i) {
			if(i==number) continue;
			double[][] tempdata = datalist.get(i);
			double[][] templabel = labellist.get(i);
			for(int j=0; j<tempdata.length; ++j) {
				System.arraycopy(tempdata[j],0,traindata[row],0,datalength);
				System.arraycopy(templabel[j],0,trainlabel[row++],0,labellength);
			}
		}
		
		map.put("traindata",traindata);
		map.put("trainlabel",trainlabel);
		map.put("testdata",datalist.get(number));
		map.put("testlabel",labellist.get(number));
		return map;
	}
	
	
	public static void main(String[] args) {
		double[][] data = {{1,1},{2,2},{3,3},{4,4},{5,5}};
		double[][] label = {{-1},{-2},{-3},{-4},{-5}};
		
		CrossValidation cv = new CrossValidation(data,label).split(3);
		Map<String,double[][]> map = cv.build(0);
		System.out.println(Arrays.deepToString(map.get("traindata")));
		System.out.println(Arrays.deepToString(map.get("trainlabel")));
		System.out.println(Arrays.deepToString(map.get("testdata")));
		System.out.println(Arrays.deepToString(map.get("testlabel")));
	}
}
