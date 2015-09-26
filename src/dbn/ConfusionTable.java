package dbn;


public class ConfusionTable {
	
	private static int TP = 0;
	private static int FP = 0;
	private static int FN = 0;
	private static int TN = 0;
	
	public static void addTP() {
		TP++;
	}
	

	public static void addFP() {
		FP++;
	}
	

	public static void addFN() {
		FN++;
	}
	

	public static void addTN() {
		TN++;
	}
	
	public static int getTP() {
		return TP;
	}
	
	public static int getFP() {
		return FP;
	}
	
	public static int getFN() {
		return FN;
	}
	
	public static int getTN() {
		return TN;
	}
}
