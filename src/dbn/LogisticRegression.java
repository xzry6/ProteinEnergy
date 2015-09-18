package dbn;

public class LogisticRegression {
	
	private int epoche = 100;
	private double learningRate = 0.1;
	private double[][] W;
	private double[] B;
	private int num;
	
	
	public LogisticRegression(int epoche, double learningRate) {
		this.epoche = epoche;
		this.learningRate = learningRate;
	}
	
	
	public void train(double[][] train, double[][] label) {
		if(validTrain(train,label)) return;

		int row = train[0].length;
		int column = label[0].length;
		W = new double[row][column];
		B = new double[column];
		num = train.length;
		
		for(int e=0; e<epoche; ++e) {
			for(int n=0; n<num; ++n) {
				double[] topError = new double[column];
				for(int j=0; j<column; ++j) {
					double temp = 0;
					for(int i=0; i<row; ++i)
						temp += train[n][i]*W[i][j];
					temp = 1.0/(1.0+Math.exp(-temp-B[j]));
					topError[j] = label[n][j]-temp;
				}
				//update top-layer theta then calculate previous layer error
				updateTheta(topError,train[n]);
			}
		}
	}
	
	
	public double[][] test(double[][] test, double[][] label) {
		if(validTest(test,label)) return null;
		
		double[][] output = new double[label.length][label[0].length];
		
		for(int n=0; n<test.length; ++n) {
			for(int j=0; j<label[0].length; ++j) {
				for(int i=0; i<test[0].length; ++i)
					output[n][j] += test[n][i]*W[i][j];
				output[n][j] = 1.0/(1.0+Math.exp(-output[n][j]-B[j]));
			}
		}
		return output;
	}
	
	
	private void updateTheta(double[] currentError, double[] sigLayer){
		for(int j=0; j<B.length; ++j){
			for(int i=0; i<W.length; ++i){
				W[i][j] += learningRate*currentError[j]*sigLayer[i]/num;
			}
			B[j] += learningRate*currentError[j]/num;
		}
	}
	
	
	private boolean validTrain(double[][] data, double[][] label) {
		if(data.length==0) {
			Util.printError("training data has no instance");
			return true;
		}
		if(label.length==0) {
			Util.printError("training label has no instance");
			return true;
		}
		if(data.length!=label.length) {
			Util.printError("number of instances between data and label is not equal");
			return true;
		}
		if(data[0].length==0) {
			Util.printError("training data has no feature");
			return true;
		}
		if(label[0].length==0) {
			Util.printError("training label has no column");
			return true;
		}
		return false;
	}
	
	
	private boolean validTest(double[][] data, double[][] label) {
		if(W==null||W.length==0||W[0].length==0||B.length==0) {
			Util.printError("model has not been trained");
			return true;
		}
		if(data.length==0) {
			Util.printError("test data has no instance");
			return true;
		}
		if(data[0].length!=W.length) {
			Util.printError("test data does not fit the model");
			return true;
		}
		if(label!=null) {
			if(label.length==0) {
				Util.printError("test label has no instance");
				return true;
			}
			if(data.length!=label.length) {
				Util.printError("number of instances between data and label is not equal");
				return true;
			}
			if(label[0].length!=W[0].length) {
				Util.printError("test label does not fit the model");
				return true;
			}
		}
		return false;
	}
}
