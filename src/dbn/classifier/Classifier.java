package dbn.classifier;

import java.util.List;

import dbn.util.ConfigUtil;
import dbn.util.Util;

public abstract class Classifier {
	
	final double learningRate = Double.parseDouble(ConfigUtil.getInstance().getMap().get("fineTune_learningRate"));

	double[][] W;
	double[] B;
	int num;
	
	Classifier(int trainlength, int labellength, int num) {
		this.W = new double[trainlength][labellength];
		this.B = new double[labellength];
		this.num = num;
	}
	
	Classifier(List<double[]> W, double[] B) {
		this.W = W.toArray(new double[W.size()][]);
		this.B = B;
	}
	
	public double[][] getW() {
		return W;
	}
	
	public double[] getB() {
		return B;
	}
	
	abstract double[] getError(double[] instance, double[] label);
	
	public double[] update(double[] instance, double[] label) {
		if(instance.length!=W.length||label.length!=B.length) {
			System.out.println(W.length+" "+B.length);
			System.out.println(instance.length+" "+label.length);
			Util.printError("data doesn't fit the classifier");
			return null;
		}
		double[] error = getError(instance,label);
		for(int j=0; j<B.length; ++j) {
			for(int i=0; i<W.length; ++i)
				W[i][j] += learningRate*error[j]*instance[i]/num;
			B[j] += learningRate*error[j]/num;
		}
		return error;
	}
	
	public abstract double[] test(double[] instance);
}
