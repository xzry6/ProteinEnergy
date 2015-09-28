package dbn.classifier;

import java.util.List;

import dbn.util.ConfigUtil;

public class ClassifierFactory {
	
	private static final String classifier = ConfigUtil.getInstance().getMap().get("classifier");
	
	public static Classifier build(int trainlength, int labellength, int num) {
		Classifier c;
		if(classifier.equals("softmaxRegression"))
			c = new SoftmaxRegression(trainlength,labellength,num);
		else
			c = new LogisticRegression(trainlength,labellength,num);
		return c;
	}
	
	public static Classifier read(List<double[]> W, double[] B) {
		Classifier c;
		if(classifier.equals("softmaxRegression"))
			c = new SoftmaxRegression(W,B);
		else
			c = new LogisticRegression(W,B);
		return c;
	}
}
