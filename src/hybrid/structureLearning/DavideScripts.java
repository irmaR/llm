package hybrid.structureLearning;

import hybrid.converters.ParConverterFromCSV;
import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.Value;
import hybrid.parameters.AssignmentKey;
import hybrid.querydata.QueryData;
import hybrid.tocsvmodule.DataToCSV;
import hybrid.utils.DavideOutputFiles;
import hybrid.utils.MakeDependencyName;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
public class DavideScripts {
    
	private String[] parameters;
	private String scriptName;
	private String output_directory;
	private Atom a;
	
	
	
	public DavideScripts(String[] parameters, String scriptName,String output_directory, Atom a) {
		super();
		this.parameters = parameters;
		this.scriptName = scriptName;
		this.output_directory = output_directory;
		this.a = a;
	}

	public double call(QueryData filtered_est_training,QueryData filteredTestData,Dependency dep_temp,HashMap<Feature,Value> filter,AssignmentKey key) throws IOException{
		List<String> pars=new ArrayList<String>();
		ParConverterFromCSV parconverter=new ParConverterFromCSV();
		DataToCSV dataToCSVConverting=new DataToCSV();
		double extractedScore=0;

		pars.addAll(Arrays.asList(this.parameters));
		pars.add(AlgorithmParameters.getExternalScript()+scriptName);
		//if head atom is discrete add argument having the number of values for head

		pars.add(output_directory+"/data_"+this.a.getPredicate().getPredicateName()+".csv");
		pars.add(output_directory+"/test_"+this.a.getPredicate().getPredicateName()+".csv");
		pars.add(output_directory+"/model_"+this.a.getPredicate().getPredicateName()+".csv");
		pars.add("Ridge");
		
		
		parconverter.setPathToCSVFile(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");

		if(new File(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv").exists()){
			new File(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv").delete();
		}
		if(new File(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv").exists()){
			new File(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv").delete();
		}
		if(new File(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv").exists()){
			new File(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv").delete();
		}
		long timeWritingParams=System.nanoTime();
		FileWriter fw = new FileWriter(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.append(dataToCSVConverting.dataToCSVFile(filtered_est_training,dep_temp,filter,true));
		bw.close();
		fw = new FileWriter(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv");
		bw = new BufferedWriter(fw);
		bw.append(dataToCSVConverting.dataToCSVFile(filteredTestData,dep_temp,filter,true));
		bw.close();
		try{  
			System.out.println("Running script");
			System.out.println(pars);
			long initTime=System.nanoTime();
			 ProcessBuilder pb = new ProcessBuilder(pars);
			 Process p = pb.start();
			 p.waitFor();
			//Process proc = Runtime.getRuntime().exec(pars.toArray(new String[pars.size()]));			
			//int exitVal = proc.waitFor();
			//System.out.println("Process exitValue: " + exitVal);
			System.out.println("Done...");
			System.out.println("Took : "+TimeUnit.SECONDS.convert((System.nanoTime()-initTime),TimeUnit.NANOSECONDS)+" seconds");
		} catch (Throwable t)
		{
			t.printStackTrace();
		}
		System.out.println("Extracting scores ...");
		FileReader fr = new FileReader(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
		double pll=Double.NaN;
		if(a.getPredicate().isDiscrete()){
			extractedScore=extractCVScoreLR(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
		}
		else{
			extractedScore=extractCVScore(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
		}
		System.out.println("Extracted");
		
		dep_temp.getCpd().getParameters().getCoefficients().convert(parconverter,key,dep_temp);
		
		if(AlgorithmParameters.getOutputScriptResults()){
			/*Files.copy(new File(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv").toPath(),new File(output_directory+"/model_"+a.getPredicate().getPredicateName()+"_"+MakeDependencyName.makeName(dep_temp, filter)+".csv").toPath());
			Files.copy(new File(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv").toPath(),new File(output_directory+"/data_"+a.getPredicate().getPredicateName()+"_"+MakeDependencyName.makeName(dep_temp, filter)+".csv").toPath());
			Files.copy(new File(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv").toPath(),new File(output_directory+"/test_"+a.getPredicate().getPredicateName()+"_"+MakeDependencyName.makeName(dep_temp, filter)+".csv").toPath());
*/
			DavideOutputFiles.addData(MakeDependencyName.makeName(dep_temp, filter),fileToString(new File(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv")));
			DavideOutputFiles.addModel(MakeDependencyName.makeName(dep_temp, filter),fileToString(new File(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv")));
			DavideOutputFiles.addTest(MakeDependencyName.makeName(dep_temp, filter),fileToString(new File(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv")));

		}
		return extractedScore;
	}
	
	private String fileToString(File file) throws IOException{
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();
		String str = new String(data, "UTF-8");
		return str;
	}
	
	private Double extractCVScore(String string) throws IOException {
		File csvFile=new File(string);
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = br.readLine();
		Double result;
		int lineNumber=0;
		Double score=null;
		while (line != null) {
			lineNumber++;
			if(lineNumber==3){ //read regression coefficients
				score=Double.valueOf(line.split(",")[1]);
			}	
			line = br.readLine();
		}
		return score;
	}
	
	private Double extractCVScoreLR(String string) throws IOException {
		File csvFile=new File(string);
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = br.readLine();
		Double result;
		int lineNumber=0;
		Double score=null;
		while (line != null) {
			lineNumber++;
			if(line.startsWith("sum loglikelihood CV train")){ //read regression coefficients
				score=Double.valueOf(line.split(",")[1]);
			}	
			line = br.readLine();
		}
		return score;
	}
	
}
