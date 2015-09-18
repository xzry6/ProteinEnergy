package dbn;

import java.util.Random;

public class RestrictedBoltzmannMachine {
	
	private Random r = new Random(Long.parseLong(ConfigUtil.getInstance().getMap().get("preTrain_randomSeed")));
	private double learningRate = Double.parseDouble(ConfigUtil.getInstance().getMap().get("preTrain_learningRate"));
	private int k = Integer.parseInt(ConfigUtil.getInstance().getMap().get("CD_k"));
	private double pro = Double.parseDouble(ConfigUtil.getInstance().getMap().get("dropOut_standard"));
	private double a = Double.parseDouble(ConfigUtil.getInstance().getMap().get("dropOut_alpha"));
	private double b = Double.parseDouble(ConfigUtil.getInstance().getMap().get("dropOut_beta"));

	
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
		double[] pH0 = sigmoidHgivenV(sV0,1,0);
		if(pro!=0)
			dropOut(pH0, simpleRate(pro), true);
		else if(a!=0)
			dropOut(pH0, sigmoidHgivenV(sV0,a,b), true);
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
			pHn = sigmoidHgivenV(sVn,1,0);
		}
		for(int i=0; i<visNum; ++i)
			for(int j=0; j<hidNum; ++j)	W[i][j] += (pH0[j]*sV0[i]-pHn[j]*sVn[i])/num*learningRate;
		for(int i=0; i<visNum; ++i)
			visBias[i] += (double) (sV0[i]-sVn[i])/num*learningRate;
		for(int j=0; j<hidNum; ++j)
			hidBias[j] += (pH0[j]-pHn[j])/num*learningRate;
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
	
	
	
	private void dropOut(double[] prob, double[] dropRate, boolean flag){
		if(flag){
			double[] sample = sample(dropRate);
			for(int i=0; i<prob.length; ++i)
				if(sample[i]==0) prob[i] = 0;
		} else{
			for(int i=0; i<prob.length; ++i)
				prob[i] *= dropRate[i];
		}
	}
	
	
	
	private double[] sample(double[] prob){
		double sample[] = new double[prob.length];
		for(int i=0; i<prob.length; ++i)
			if(r.nextDouble()<prob[i]) sample[i] = 1;
		return sample;
	}
	
	
	
	private double[] simpleRate(double rate){
		double[] dropRate = new double[hidNum];
		for(int i=0; i<dropRate.length; ++i)
			dropRate[i] = rate;
		return dropRate;
	}
	
	public static void main(String[] args) {
		ConfigUtil.getInstance().getConfig("config.txt");
		RestrictedBoltzmannMachine rbm = new RestrictedBoltzmannMachine(null,1);
		System.out.println(rbm.learningRate);
		System.out.println(ConfigUtil.getInstance().getMap().get("preTrain_randomSeed"));
	}
}
