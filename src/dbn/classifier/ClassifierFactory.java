package dbn.classifier;

import java.util.List;

import dbn.util.ConfigUtil;

public class ClassifierFactory {
	
	private static final String classifier = ConfigUtil.getInstance().getMap().get("classifier");
	
	public static Classifier build(int trainlength, int labellength, int num) {
		Classifier c;
		switch(classifier) {
		case "softmaxRegression":
			c = new SoftmaxRegression(trainlength,labellength,num);
			break;
		default:
			c = new LogisticRegression(trainlength,labellength,num);
			break;
		}
		return c;
	}
	
	public static Classifier read(List<double[]> W, double[] B) {
		Classifier c;
		switch(classifier) {
		case "softmaxRegression":
			c = new SoftmaxRegression(W,B);
			break;
		default:
			c = new LogisticRegression(W,B);
			break;
		}
		return c;
	}
}
