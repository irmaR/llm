package hybrid.structureLearning;

import hybrid.experimenter.AlgorithmParameters;
import hybrid.utils.FileSearching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class NetworkWPLLStat {
	protected static FileWriter outFile;
	protected static PrintWriter out;
	protected static File file;

	public static PrintWriter getStream() {
		return out;
	}


	public static void close() {
		if(out!=null){
			out.close();
		}
	}

	public static void setOutputFile(String file_path) throws IOException {
		FileSearching fS=new FileSearching();
		//if learning is to be redone the new files are created
		if (AlgorithmParameters.isredoingExperiment()){
			file=new File(file_path);
			outFile = new FileWriter(file_path,true);
			out = new PrintWriter(outFile);
		}
		else{
			if(new File(file_path).isFile() && fS.containsString("WPLL_test", file_path)){
				System.out.println(" There is file in "+file_path+ "    ");
				return;
			}
			else{
			file=new File(file_path);
			outFile = new FileWriter(file_path,true);
			out = new PrintWriter(outFile);
			}
		}

	}

	public static void println(String predicateName, double wpll_score_test) throws IOException {
		if(out!=null){
			//first check if result already there
			out.write(predicateName+"\t"+wpll_score_test+"\n");
			System.out.println(file+ " "+file.exists());
			System.out.println(" WRITING WPLL STAT RESULTS: "+predicateName+"   "+wpll_score_test);

		}
	}


	public static void flush() {
		if(out!=null){
			out.flush();
		}

	}

}
