package hybrid.interpretations;

import hybrid.experimenter.AlgorithmParameters;
import hybrid.network.NetworkInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import alice.tuprolog.Prolog;

/**
 * This class loads input files for a specific network and outputs 
 * data consisting of a number of interpretations
 * @author irma
 *
 */

public class TuPrologDataLoader implements DataLoader {
	private InterpretationCreator d;

	
	public TuPrologDataLoader(InterpretationCreator e){
		this.d=e;
	}
	
	
	/**
	 * Loading data from prolog format. The boolean atoms are subsamples with a given ratio to true atoms.
	 * @param pathToFiles - path to directory containing files
	 * @param name - generic name of the files (e.g., interp, file...)
	 * @param extension - extension of the files to be loaded
	 * @param ntw - network
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public Data loadData(String pathToFiles, String name, String extension, NetworkInfo ntw,DataType data_type) throws IOException{
		List<File> listOfFilesDatabaseFiles=loadFilesSorted(pathToFiles,name,extension);
		File file=null;
		BufferedWriter fw=null;
		if(AlgorithmParameters.getDataLoadingFile()!=null){
			 file=AlgorithmParameters.getDataLoadingFile();
			 fw=new BufferedWriter(new FileWriter(file, true));
		}
	    
		//log files
		String dataType="";
		if(pathToFiles.contains("/train/") || data_type.equals(DataType.training)){
			if(fw!=null){
				fw.append(" Loading training data: "+pathToFiles+"\n");
			}
			dataType="training";
		}
		if(pathToFiles.contains("/validate/")|| data_type.equals(DataType.validation)){
			if(fw!=null){
				fw.append(" Loading validation data: "+pathToFiles+"\n");
			}
			dataType="validation";
		}
		if(pathToFiles.contains("/test/") || data_type.equals(DataType.test)){
			if(fw!=null){
				fw.append(" Loading test data: "+pathToFiles+"\n");
			}
			dataType="test";
		}
		System.out.println("****************************************** LOADING "+dataType+" ***************************************************");
		if(AlgorithmParameters.isDetailed_logging_flag()){
			hybrid.structureLearning.LearnedDepStatistics.println(dataType+"-------------------------------------------------------");
			for(File f:listOfFilesDatabaseFiles){
				hybrid.structureLearning.LearnedDepStatistics.println(f+",");
			}
			hybrid.structureLearning.LearnedDepStatistics.println();
		}	
		alice.tuprolog.Parser ps;	
		//create interpretations for the input files
		ArrayList<Interpretation> interpretations=new ArrayList<Interpretation>();
		try{
			for (File f:listOfFilesDatabaseFiles) {
				Prolog engine=new Prolog();
				//create a path to files
				String pathToFile=new String(pathToFiles + "/" +f.getName());
				//create interpretation
				//System.out.println(pathToFile);
				Interpretation interp=d.createInterpretation(ntw, pathToFile,dataType);
				if(fw!=null){
					fw.append(" Interpretation info:: "+interp.getInfo()+"\n");
				}
				interpretations.add(interp); 
			}
		}
		catch(NullPointerException e){
			e.printStackTrace();
			System.out.println("No interpretation created for: "+pathToFiles);
		}
		if(fw!=null){
			fw.close();
		}
		return new Data(interpretations);
	}
	
	/**
	 * Loading data from prolog format. The method will only load 
	 * interpretation with specific name given in names_with_extension
	 * argument.
	 * @param pathToFiles - path to folder containin interpretation files
	 * @param names_with_extension - the list of interpretation file names to be loaded
	 * @param ntw - network
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public Data loadData(String pathToFiles, String[] names_with_extension, NetworkInfo ntw) throws IOException{
		File file1=AlgorithmParameters.getDataLoadingFile();
	    BufferedWriter fw=new BufferedWriter(new FileWriter(file1, true));
		String dataType="";
		//log files
		System.out.println(pathToFiles);
		if(pathToFiles.contains("/train/")){
			if(fw!=null){
				fw.append(" Loading training data: "+pathToFiles+"\n");
			}
			dataType="training";
		}
		if(pathToFiles.contains("/validate/")){
			if(fw!=null){
				fw.append(" Loading validation data: "+pathToFiles+"\n");
			}
			dataType="validation";
		}
		if(pathToFiles.contains("/test/")){
			if(fw!=null){
				fw.append(" Loading test data: "+pathToFiles+"\n");
			}
			dataType="test";
		}
		System.out.println("****************************************** LOADING "+dataType+" ***************************************************");

		List<File> listOfFilesDatabaseFiles=new ArrayList<File>();
		for(String file_name:names_with_extension){
			File file = new File(pathToFiles+"/"+file_name);
			listOfFilesDatabaseFiles.add(file);
		}

		
		if(AlgorithmParameters.isDetailed_logging_flag()){
			hybrid.structureLearning.LearnedDepStatistics.println(dataType+"-------------------------------------------------------");
			for(File f:listOfFilesDatabaseFiles){
				hybrid.structureLearning.LearnedDepStatistics.println(f+",");

			}
			hybrid.structureLearning.LearnedDepStatistics.println();
		}	
		alice.tuprolog.Parser ps;	
		//create interpretations for the input files
		ArrayList<Interpretation> interpretations=new ArrayList<Interpretation>();
		try{
			for (File f:listOfFilesDatabaseFiles) {
				Prolog engine=new Prolog();
				//create a path to files
				String pathToFile=new String(pathToFiles + "/" +f.getName());
				//create interpretation
				System.out.println("Creating interpretation for: "+dataType);
				Interpretation interp=d.createInterpretation(ntw, pathToFile,dataType);
				if(fw!=null){
					fw.append(" Interpretation info:: "+interp.getInfo()+"\n");
				}
				interpretations.add(interp); 
			}

		}
		catch(NullPointerException e){
			e.printStackTrace();
			System.out.println("No interpretation created for: "+pathToFiles);
		}
		fw.close();
		return new Data(interpretations);
	}



	private List<File> loadFilesSorted(String pathToFiles,String name,String extension) throws IOException {
		File folder = new File(pathToFiles);
		File[] listOfFiles = folder.listFiles();
		//sort files
		if(listOfFiles==null){
			throw new IOException("No files found in :"+pathToFiles);
		}
		List<File> listFile = Arrays.asList(listOfFiles);
		
		Collections.sort(listFile);
		Collections.sort(listFile);
		ArrayList<File> listOfFilesDatabaseFiles = new ArrayList<File>();

		for(File f:listFile){
			if(f.getName().contains("."+extension) && !f.getName().contains("."+extension+"~")){
				listOfFilesDatabaseFiles.add(f);
			}
			/*if(f.getName().contains(name) && f.getName().contains("."+extension) && !f.getName().contains("."+extension+"~")){
				listOfFilesDatabaseFiles.add(f);
			}*/
		}
		return listOfFilesDatabaseFiles;
	}
}
