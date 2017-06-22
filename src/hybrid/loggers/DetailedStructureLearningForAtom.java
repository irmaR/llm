package hybrid.loggers;

import hybrid.experimenter.AlgorithmParameters;
import hybrid.network.Atom;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class DetailedStructureLearningForAtom{

	protected static FileWriter outFile;
	protected static PrintWriter out;
	protected static boolean writing_permission;
	private static Atom atom;

	public static void setOutFile(Atom set_atom,String str) throws IOException {
		if(AlgorithmParameters.isDetailed_logging_flag()){
			atom=set_atom;
			outFile = new FileWriter(str+set_atom.getPredicate().getPredicateName());
			out = new PrintWriter(outFile);
		}
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

	public static void setLoggingPermission(boolean b) {
		writing_permission=b;

	}

	public static void flush() {
		if(out!=null){
			out.flush();
		}

	}




}
