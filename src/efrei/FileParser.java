package efrei;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class FileParser {
	
	public static double p1;
	public static double p2;
	public static double uctime;
	public static double disktime;
	public static double tapetime;
	public static double lambda;
	public static int ordre;
	public static int count;
	public static double diskusage;

	public FileParser(String[] args) {
		String filename = "para.p";
		if(args.length > 0) filename = args[0];
		Scanner scanner;
		try {
			scanner = new Scanner(new FileReader(filename));
			 p1 =  stringTodouble(scanner.next());
			 p2 =  stringTodouble(scanner.next());
			 uctime =  stringTodouble(scanner.next());
			 disktime = stringTodouble(scanner.next());
			 tapetime = stringTodouble(scanner.next());
			 lambda = stringTodouble(scanner.next());
			 ordre = Integer.parseInt(scanner.next());
			 count = Integer.parseInt(scanner.next());
			 diskusage = stringTodouble(scanner.next());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private double stringTodouble(String s) {
		if(!s.contains("/")) return Double.parseDouble(s);
		String str[]=s.split("/");
		return ((double)Integer.parseInt(str[0])/Integer.parseInt(str[1]));
	}

}
