package dbn.classifier;

import java.util.List;

public class LogisticRegression extends Classifier {

	LogisticRegression(List<double[]> W, double[] B) {
		super(W,B);
	}
	
	LogisticRegression(int trainlength, int labellength, int num) {
		super(trainlength, labellength, num);
	}

	@Override
	double[] getError(double[] instance, double[] label) {
		double[] topError = new double[label.length];
		for(int j=0; j<B.length; ++j){
			double temp = 0;
			for(int i=0; i<W.length; ++i)
				temp += instance[i]*W[i][j];
			topError[j] = label[j]-1.0/(1.0+Math.exp(-temp-B[j]));
		}
		return topError;
	}

	@Override
	public double[] test(double[] instance) {
		double[] output = new double[B.length];
		for(int j=0; j<B.length; ++j){
			for(int i=0; i<W.length; ++i)
				output[j] += instance[i]*W[i][j];
			output[j] = 1.0/(1.0+Math.exp(-output[j]-B[j]));
		}
		return output;
	}

}
