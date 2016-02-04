package dbn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import dbn.util.ConfigUtil;
import dbn.util.Util;

public class CrossValidation {
	
	private double[][] data;
	private double[][] label;
	
	private int size;
	private int datalength;
	private int labellength;
	
	private List<double[][]> datalist = new ArrayList<double[][]>();
	private List<double[][]> labellist = new ArrayList<double[][]>();

	
	private Random r = new Random(Long.parseLong(ConfigUtil.getInstance().getMap().get("preTrain_randomSeed")));
	
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
		int[] index = randomIndex(data.length);
		for(int i=0; i<fold; ++i) {
			//if(i==fold-1) piecetemp = size-piece*(fold-1);
			double[][] datatemp = new double[piecetemp][datalength];
			double[][] labeltemp = new double[piecetemp][labellength];
			for(int j=0; j<piecetemp; ++j) {
				//System.out.print(index[i*piece+j]+" ");
				System.arraycopy(data[index[i*piece+j]],0,datatemp[j],0,datalength);
				System.arraycopy(label[index[i*piece+j]],0,labeltemp[j],0,labellength);
			}
			//System.out.println();
			datalist.add(datatemp);
			labellist.add(labeltemp);
		}
		double[][] redundantData = new double[size-piece*fold][datalength];
		double[][] redundantLabel = new double[size-piece*fold][labellength];
		for(int j=0; j<size-piece*fold; ++j) {
			//System.out.print(index[piece*fold+j]+" ");
			System.arraycopy(data[index[piece*fold+j]],0,redundantData[j],0,datalength);
			System.arraycopy(label[index[piece*fold+j]],0,redundantLabel[j],0,labellength);
		}
		datalist.add(redundantData);
		labellist.add(redundantLabel);
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
		//System.out.println(trainsize);
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
	
	private int[] randomIndex(int num) {
		Random r = new Random(32553532);
		int[] index = new int[num];
		boolean[] bool = new boolean[num];
		int temp = 0;
		for(int n=0; n<num; ++n){
			do{
				temp = r.nextInt(num);
			} while(bool[temp]);
			bool[temp] = true;
			index[n] = temp;
		}
		return index;
	}
	
	public static void main(String[] args) {
		double[][] data = new double[11][2];
		double[][] label = new double[11][1];
		CrossValidation cv = new CrossValidation(data,label);
		cv.split(5);
	}
}
