package hybrid.structureLearning;

import hybrid.network.Atom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * used for writing the statistics 
 * @author irma
 *
 */
public class LearnedDepStatistics{
	
	protected static FileWriter outFile;
	protected static PrintWriter out;

	
	public static void setOutFile(String path,String file_name) throws IOException{
			File file=new File(path+file_name);
			outFile = new FileWriter(path+file_name);
			out = new PrintWriter(outFile);
		}
	
	public static void println(String print) {
		if(out!=null){
		out.println(print);
		}
	}

	public static PrintWriter getStream() {
		return out;
	}

	public static void print(String print) {
		if(out!=null){
		out.print(print);
		}
	}

	public static void println() {
		if(out!=null){
		out.println();
		}

	}

	public static void close() {
		if(out!=null){
		out.close();
		}
	}
	
	

	


	
}
