package dbn;

import java.util.ArrayList;
import java.util.List;


public class DeepBeliefNetwork {
	
	private double[][] train = ConfigUtil.getInstance().getData();
	private int epoche = Integer.parseInt(ConfigUtil.getInstance().getMap().get("epoch"));
	private int[] hidLayer = str2intA(ConfigUtil.getInstance().getMap().get("hidden_layers"));
	private int num = train.length;//number of training samples
	private int length = train[0].length;//number of visible units
	private int DBNlayer = hidLayer.length;//number of RBM layers
	
	private List<RestrictedBoltzmannMachine> rbmList;
	
	public List<RestrictedBoltzmannMachine> getModel() {
		return rbmList;
	}
	
	
	public DeepBeliefNetwork pretrain() {
		rbmList = new ArrayList<RestrictedBoltzmannMachine>();
		double[][] currentLayer = new double[num][length];
		double[][] nextLayer;
		for(int n=0; n<num; ++n)
			for(int i=0; i<length; ++i)
				currentLayer[n][i] = train[n][i];
		for(int l=0; l<DBNlayer; ++l){
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
		return this;
	}
	
	private int[] str2intA(String string) {
		String[] stringInstance = string.split(",");
		int[] intInstance = new int[stringInstance.length];
		for(int i=0; i<stringInstance.length; ++i)
			intInstance[i] = Integer.parseInt(stringInstance[i]);
		return intInstance;
	}
}
