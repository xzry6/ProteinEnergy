package dbn.classifier;

import java.util.List;

public class SoftmaxRegression extends Classifier {

	SoftmaxRegression(List<double[]> W, double[] B) {
		super(W,B);
	}
	
	SoftmaxRegression(int trainlength, int labellength, int num) {
		super(trainlength, labellength, num);
	}

	@Override
	double[] getError(double[] instance, double[] label) {
		double[] topError = new double[label.length];
		for(int j=0; j<B.length; ++j){
			double temp = 0;
			for(int i=0; i<W.length; ++i){
				temp += instance[i]*W[i][j];
			}
			topError[j] = temp+B[j];
		}
		double max = 0;
		double sum = 0;
		for(double e:topError) max = Math.max(max, e);
		for(int j=0; j<label.length; ++j) {
			topError[j] = Math.exp(topError[j]-max);
			sum += topError[j];
		}
		for(int j=0; j<label.length; ++j)
			topError[j] = label[j]-topError[j]/sum;
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
		double max = 0;
		double sum = 0;
		for(double e:output) max = Math.max(max, e);
		for(int j=0; j<B.length; ++j) {
			output[j] = Math.exp(output[j]-max);
			sum += output[j];
		}
		for(int j=0; j<B.length; ++j)
			output[j] = output[j]/sum;
		return output;
	}

}
