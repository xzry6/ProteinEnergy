package dbn.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ROC {
	private static List<double[]> readTestFile(String fileName) {
		List<double[]> list = new ArrayList<double[]>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String tempString = null;
			while((tempString = reader.readLine()) != null) {
				if(tempString.length()==0
						||tempString.charAt(0)=='#'
						||Character.isLetter(tempString.charAt(0)))
					continue;
				if(tempString.charAt(0)=='~') break;
			}
			while((tempString = reader.readLine()) != null) {
				if(tempString.charAt(0)=='~') break;
				list.add(Util.strA2douA(tempString.split("\t")));
			}
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println(list.size());
		return list;
	}
	
	private static double[][] drawROC(List<double[]> list, int num) {
		double[][] whatever = new double[num+1][2];
		for(int i=0; i<=num; i++) {
			int tp = 0;
			int fp = 0;
			int fn = 0;
			int tn = 0;
			double threshold = (double) i/num;
			System.out.println(i+"th iteration:");
			System.out.println("\tthredshold is "+threshold);
			for(double[] arr:list) {
				if(arr[0]>=threshold&&arr[1]==1) tp++;
				else if(arr[0]>=threshold&&arr[1]==0) fp++;
				else if(arr[0]<threshold&&arr[1]==1) fn++;
				else if(arr[0]<threshold&&arr[1]==0) tn++;
			}
			double sensitivity = (double) tp/(tp+fn);
			double specificity = (double) fp/(fp+tn);
			System.out.println("\tTP: "+tp);			
			System.out.println("\tFP: "+fp);
			System.out.println("\tFN: "+fn);
			System.out.println("\tTN: "+tn);
			System.out.println("\tsensitivity is: "+sensitivity);
			System.out.println("\tspecificity is: "+specificity);
			whatever[i][0] = sensitivity;
			whatever[i][1] = specificity;
		}
		double sum = 0;
		for(int i=0; i<whatever.length-1; ++i) {
			double triangle = (whatever[i][0]-whatever[i+1][0])*(whatever[i][1]-whatever[i+1][1])/2;
			double square = whatever[i+1][0]*(whatever[i][1]-whatever[i+1][1]);
			sum+=triangle+square;
		}

		System.out.println("AUC area is: "+sum);
		return whatever;
	}
	
	private static void write(double[][] whatever) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("ROC.txt"));
			for(double[] what:whatever) {
				out.write(what[0]+"\t");
				out.write(what[1]+"\n");
			}
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		write(ROC.drawROC(ROC.readTestFile("DBN_CV_result20,12"),100000));
//		int tp = 277;
//		int fp = 83;
//		int fn = 146;
//		int tn = 1094;
//		double precisiont = (double) tp/(tp+fp);
//		double precisionn = (double) tn/(tn+fn);
//		double specificity = (double) tn/(fp+tn);
//		double sensitivity = (double) tp/(tp+fn);
//		double accuracy = (double) (tp+tn)/(tp+tn+fp+fn);
//		double cc = (double)((tp*tn)-(fp*fn))/Math.sqrt((double)(tp+fn)*(tp+fp)*(tn+fn)*(tn+fp));
//		System.out.println("true precision is "+precisiont);
//		System.out.println("false precision is "+precisionn);
//		System.out.println("specificity is "+specificity);
//		System.out.println("sensitivity is "+sensitivity);
//		System.out.println("cc is "+cc);
//		System.out.println("accuracy is "+accuracy);
	}
}
