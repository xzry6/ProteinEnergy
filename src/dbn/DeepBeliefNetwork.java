package dbn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dbn.classifier.Classifier;
import dbn.classifier.ClassifierFactory;
import dbn.util.ConfigUtil;
import dbn.util.Util;
import dbn.util.WriteUtil;


public class DeepBeliefNetwork {
	
	private int epoche = Integer.parseInt(ConfigUtil.getInstance().getMap().get("epoch"));
	private int[] hidLayer = Util.strA2intA(ConfigUtil.getInstance().getMap().get("hidden_layers").split(","));
	private int DBNlayer = hidLayer.length;//number of RBM layers

	private List<RestrictedBoltzmannMachine> rbmList;
	private Classifier c;
	
	
	
	public List<RestrictedBoltzmannMachine> getList() {
		return rbmList;
	}
	
	
	public Classifier getClassifier() {
		return c;
	}
	
	
	public DeepBeliefNetwork train(double[][] train, double[][] label) {
		
		rbmList = new ArrayList<RestrictedBoltzmannMachine>();
		c = ClassifierFactory.build(hidLayer[DBNlayer-1],label[0].length,train.length);
		
		preTrain(train);
		fineTune(train,label);
		return this;
	}
	
	
	public void test(double[][] test, double[][] label) {
		double[][] output = new double[label.length][label[0].length];
		for(int n=0; n<output.length; ++n) {
			double[] tempLayer = test[n];
			for(int l=0; l<DBNlayer; ++l) {
				RestrictedBoltzmannMachine rbm = rbmList.get(l);
				tempLayer = rbm.dropOutTest(tempLayer);
			}
			output[n] = c.test(tempLayer);
		}
		compare(output,label);
	}
	
	
	public void predict(double[][] test) {
		if(test==null||test.length==0) {
			Util.printError("test set has no data");
			return;
		} else if(c.getW().length==0||c.getB().length==0) {
			Util.printError("the model is not right");
			return;
		}
		else if(test[0].length!=rbmList.get(0).getW().length) {
			System.out.println(test[0].length);
			System.out.println(rbmList.get(0).getW().length);
			Util.printError("test set does not fit the model");
			return;
		}
		double[][] output = new double[test.length][c.getB().length];
		for(int n=0; n<output.length; ++n) {
			double[] tempLayer = test[n];
			for(int l=0; l<DBNlayer; ++l) {
				RestrictedBoltzmannMachine rbm = rbmList.get(l);
				tempLayer = rbm.dropOutTest(tempLayer);
			}
			output[n] = c.test(tempLayer);
		}
		WriteUtil.getInstance().writeResult(output).close();
	}
	
	
	public void writeModel() {
		WriteUtil.getInstance().writeModel(rbmList, c).close();
	}
	
	private void preTrain(double[][] train) {
		int num = train.length;
		int length = train[0].length;
		double[][] currentLayer = new double[num][length];
		double[][] nextLayer;
		for(int n=0; n<num; ++n)
			for(int i=0; i<length; ++i)
				currentLayer[n][i] = train[n][i];
		for(int l=0; l<DBNlayer; ++l) {
			RestrictedBoltzmannMachine rbm = new RestrictedBoltzmannMachine(currentLayer, hidLayer[l]);
			nextLayer = new double[num][hidLayer[l]];
			for(int e=0; e<epoche; ++e)
				for(int n=0; n<num; ++n)
					rbm.contrasiveDivergence(currentLayer[n]);
			for(int n=0; n<num; ++n)
				nextLayer[n] = rbm.generateNextLayer(currentLayer[n]);
			rbmList.add(rbm);
			currentLayer = nextLayer;
		}
	}
	
	
	private void fineTune(double[][] train, double[][] label) {
		int num = train.length;
		for(int e=0; e<epoche; ++e) {
			for(int n=0; n<num; ++n) {
				
				double[] tempLayer = train[n];
				List<double[]> sigLayers = new ArrayList<double[]>();
				sigLayers.add(tempLayer);
				
				for(int l=0; l<DBNlayer; ++l) {
					RestrictedBoltzmannMachine rbm = rbmList.get(l);
					tempLayer = rbm.dropOutTrain(tempLayer);
					sigLayers.add(tempLayer);
				}
				
				double[] error = c.update(tempLayer, label[n]);
				if(epoche>=10){
					error = passError(c.getW(),error,tempLayer);
					double[] nextError;
					for(int l=DBNlayer-1; l>-1; --l){
						RestrictedBoltzmannMachine rbm = rbmList.get(l);
						tempLayer = sigLayers.get(l);
						nextError = passError(rbm.getW(),error,tempLayer);
						rbm.update(error,tempLayer,num);
						error = nextError;
					}
				}
			}
		}
	}

	
	private double[] passError(double[][] W, double[] error, double[] instance){
		double[] nextError = new double[W.length];
		for(int i=0; i<W.length; ++i) {
			for(int j=0; j<W[0].length; ++j)
				nextError[i] += W[i][j]*error[j];
			nextError[i] *= instance[i]*(1-instance[i])*4*2;
		}
		return nextError;
	}
	
	
	private void compare(double[][] output, double[][] label) {
		WriteUtil.getInstance().writeDoubleResult(output, label);
		for(int j=0; j<label[0].length; ++j) {
			for(int i=0; i<label.length; ++i) {
				if(output[i][j]>=0.5&&label[i][j]==1) ConfusionTable.addTP();
				else if(output[i][j]<0.5&&label[i][j]==1) ConfusionTable.addFN();
				else if(output[i][j]<0.5) ConfusionTable.addTN();
				else ConfusionTable.addFP();
			}
		}
	}
	
	
	public DeepBeliefNetwork readModel(String fileName) {
		
		rbmList = new ArrayList<RestrictedBoltzmannMachine>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String tempString = null;
			while((tempString = reader.readLine()) != null) {
				if(tempString.length()==0) continue;
				if(tempString.charAt(0)=='~') break;
			}
			List<List<double[]>> Wlist = new ArrayList<List<double[]>>();
			List<double[]> tempList = new ArrayList<double[]>();
			while((tempString = reader.readLine()) != null) {
				if(tempString.charAt(0)=='~') break;
				if(tempString.charAt(0)=='#') {
					Wlist.add(tempList);
					tempList = new ArrayList<double[]>();
					continue;
				}
				tempList.add(Util.strA2douA(tempString.split("\t")));
			}
			tempList = new ArrayList<double[]>();
			while((tempString = reader.readLine()) != null) 
				tempList.add(Util.strA2douA(tempString.split("\t")));
			if(tempList.size()!=Wlist.size()) {
				Util.printError("read model failure: W and B doesn't fit");
				reader.close();
				return null;
			} 
			int i = 0;
			for(i=0; i<tempList.size()-1; ++i)
				rbmList.add(new RestrictedBoltzmannMachine(Wlist.get(i),tempList.get(i)));
			c = ClassifierFactory.read(Wlist.get(i),tempList.get(i));
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	
}
