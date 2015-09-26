package dbn;

import java.util.List;
import java.util.Random;

import dbn.util.ConfigUtil;
import dbn.util.Util;

public class RestrictedBoltzmannMachine {

	private Random r = new Random(Long.parseLong(ConfigUtil.getInstance().getMap().get("preTrain_randomSeed")));
	private double learningRate = Double.parseDouble(ConfigUtil.getInstance().getMap().get("preTrain_learningRate"));
	private final double finetuneRate = Double.parseDouble(ConfigUtil.getInstance().getMap().get("fineTune_learningRate"));
	private int k = Integer.parseInt(ConfigUtil.getInstance().getMap().get("CD_k"));

	private String pro = ConfigUtil.getInstance().getMap().get("dropOut_standard");
	private String a = ConfigUtil.getInstance().getMap().get("dropOut_alpha");
	private String b = ConfigUtil.getInstance().getMap().get("dropOut_beta");
	
	private int num;
	private int visNum;
	private int hidNum;
	private double[][] W;
	private double[] visBias;
	private double[] hidBias;

	
	public RestrictedBoltzmannMachine(double[][] train, int hidNum){
		if(train==null) {
			Util.printError("training set is null");
			return;
		}
		this.hidNum = hidNum;
		this.num = train.length;
		this.visNum = train[0].length;
		this.W = new double[visNum][hidNum];
		for(int i=0; i<visNum; ++i)
			for(int j=0; j<hidNum; ++j)	W[i][j] = randomGaussian(0, 0.1);
		this.visBias = new double[visNum];
		this.hidBias = new double[hidNum];
	}	
	
	public RestrictedBoltzmannMachine(List<double[]> tempW, double[] B) {
		this.W = tempW.toArray(new double[tempW.size()][]);
		this.hidBias = B;
		this.hidNum = B.length;
		this.visNum = W.length;
		this.visBias = new double[visNum];
	}
	

	public void getRBM(RestrictedBoltzmannMachine rbm){
		this.num = rbm.num;
		this.visNum = rbm.visNum;
		this.hidNum = rbm.hidNum;
		this.r = rbm.r;
		this.W = new double[this.visNum][this.hidNum];
		for(int i=0; i<this.visNum; ++i)
			System.arraycopy(rbm.W[i], 0, this.W[i], 0, hidNum);
		this.visBias = new double[this.visNum];
		System.arraycopy(rbm.visBias, 0, this.visBias, 0, visNum);
		this.hidBias = new double[this.hidNum];
		System.arraycopy(rbm.hidBias, 0, this.hidBias, 0, hidNum);
	}
	
	
	
	public void contrasiveDivergence(double[] sV0){
		double[] pH0 = dropOutTrain(sV0);
		double[] pHn = new double[hidNum];
		double[] pVn = new double[visNum];
		double[] sHn = new double[hidNum];
		double[] sVn = new double[visNum];
		for(int i=0; i<visNum; ++i)
			sVn[i] = sV0[i];
		for(int j=0; j<hidNum; ++j)
			pHn[j] = pH0[j];
		for(int count=0; count<k; count++){
			sHn = sample(pHn);
			pVn = sigmoidVgivenH(sHn);
			sVn = sample(pVn);
			pHn = sigmoidHgivenV(sVn);
		}
		for(int i=0; i<visNum; ++i)
			for(int j=0; j<hidNum; ++j)	W[i][j] += (pH0[j]*sV0[i]-pHn[j]*sVn[i])/num*learningRate;
		for(int i=0; i<visNum; ++i)
			visBias[i] += (double) (sV0[i]-sVn[i])/num*learningRate;
		for(int j=0; j<hidNum; ++j)
			hidBias[j] += (pH0[j]-pHn[j])/num*learningRate;
	}
	
	public void update(double[] error, double[] instance, int num) {
		for(int j=0; j<hidBias.length; ++j) {
			for(int i=0; i<W.length; ++i)
				W[i][j] += finetuneRate*error[j]*instance[i]/num;
			hidBias[j] += finetuneRate*error[j]/num;
		}
	}
	
	public double[] generateNextLayer(double[] currentLayer) {
		return sample(sigmoidHgivenV(currentLayer));
	}
	
	
	
	public double[][] getW() {
		return W;
	}
	
	
	
	public int getHidNum() {
		return hidNum;
	}
	
	
	
	public int getVisNum() {
		return visNum;
	}
	
	
	
	public double[] getVisBias() {
		return visBias;
	}
	
	
	
	public double[] getHidBias() {
		return hidBias;
	}
	
	
	
	double[] sigmoidHgivenV(double[] V, double alpha, double beta){
		double[] p = new double[hidNum];
		for(int j=0; j<hidNum; ++j){
			double temp = 0;	
			for(int i=0; i<visNum; ++i)
				temp+=V[i]*(W[i][j]*alpha+beta);
			temp = temp+hidBias[j];
			p[j] = 1/(1+Math.exp(-temp));
		}
		return p;
	}
	

	
	double[] sigmoidHgivenV(double[] V){
		return sigmoidHgivenV(V,1,0);
	}
	
	
	
	double[] sigmoidVgivenH(double[] H){
		double[] p = new double[visNum];
		for(int i=0; i<visNum; ++i){
			double temp = 0;
			for(int j=0; j<hidNum; ++j)	
				temp += H[j]*W[i][j];
			temp = temp+visBias[i];
			p[i] = 1/(1+Math.exp(-temp));
		}
		return p;
	}
	

	
	private double randomGaussian(double mu, double sigma){
		double temp = 0;
		double N = 12;
		for(int i=0; i<N; ++i)
			temp += r.nextDouble();
		temp = mu+sigma*(temp-N/2.0)/Math.sqrt(N/12);
		return temp;
	}
	
	
	private double[] sample(double[] prob) {
		double sample[] = new double[prob.length];
		for(int i=0; i<prob.length; ++i)
			if(r.nextDouble()<prob[i]) sample[i] = 1;
		return sample;
	}
	
	
	private double sample(double prob) {
		if(r.nextDouble()<prob) return 1;
		return 0;
	}
	
	
	double[] dropOutTrain(double[] instance){
		double[] temp = sigmoidHgivenV(instance);
		if(!pro.equals("NaN")) {
			for(int i=0; i<temp.length; ++i)
				if(sample(Double.parseDouble(pro))==0) temp[i] = 0;
		} else if(!a.equals("NaN")&&!b.equals("NaN")) {
			double[] dropRate = sigmoidHgivenV(instance,Double.parseDouble(a),Double.parseDouble(b));
			dropRate = sample(dropRate);
			for(int i=0; i<temp.length; ++i)
				if(dropRate[i]==0) temp[i] = 0;
		}
		return temp;
	}
	
	
	double[] dropOutTest(double[] instance) {
		double[] temp = sigmoidHgivenV(instance);
		
		if(!pro.equals("NaN")) 
			for(int i=0; i<temp.length; ++i)
				temp[i] *= Double.parseDouble(pro);
		else if(!a.equals("NaN")&&!b.equals("NaN")) {
			double[] dropRate = sigmoidHgivenV(instance,Double.parseDouble(a),Double.parseDouble(b));
			for(int i=0; i<temp.length; ++i)
				temp[i] *= dropRate[i];
		}
		return temp;
	}
}
