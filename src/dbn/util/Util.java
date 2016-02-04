package dbn.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Util {
	
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static String getTime() {
		return "["+df.format(new Date())+"]: ";
	}
	
	public static void print(String info) {
		System.out.println(getTime()+info);
	}
	
	public static void printError(String info) {
		System.out.println(getTime()+"ERROR!!! "+info);
	}
	
	public static int[] strA2intA(String[] string) {
		int[] intArray = new int[string.length];
		for(int i=0; i<string.length; ++i)
			intArray[i] = Integer.parseInt(string[i]);
		//System.out.println(Arrays.toString(intArray));
		return intArray;
	}

	public static double[] strA2douA(String[] string) {
		double[] doubleArray = new double[string.length];
		for(int i=0; i<string.length; ++i) 
			doubleArray[i] = Double.parseDouble(string[i]);
		return doubleArray;
	}
}
