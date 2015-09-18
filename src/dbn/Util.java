package dbn;

import java.text.SimpleDateFormat;
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
}
