package hybrid.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;

import hybrid.interpretations.Data;
import hybrid.interpretations.Interpretation;
import hybrid.network.NetworkInfo;

/**
 * By using tuProlog the files will be transformed to a MLN input file
 * - that is, all constants will be capitalized
 * @author irma
 *
 */
public class ParseToMLNFormat {

	public static void parseFile(String pathToFile, String[] booleanPredNames, String output_file, String file_name, String configuration_file, HashMap<String, String> mapAtomsToValueAccronym,HashMap<String, Integer> valueIndex, HashMap<String, String> mapLogvarsToDomainName) throws IOException {
		Prolog eng=new Prolog();
		
		HashMap<String,HashSet<String>> domain=new HashMap<String, HashSet<String>>();
		
		for(String s:mapAtomsToValueAccronym.keySet()){
			domain.put(s,new HashSet<String>());
		}
		
		if(!pathToFile.contains(".pl")){
			System.out.println(file_name+" file name doesn't contain the right extension!");
			throw new IOException();
		}
		
		Path pathToFileLALA = Paths.get(output_file+"/"+file_name);
		
        Files.createDirectories(pathToFileLALA.getParent());
		
		try{
			Files.createFile(pathToFileLALA);
		}
		catch(FileAlreadyExistsException e){

		}
		
		BufferedWriter writer=new BufferedWriter(new FileWriter(pathToFileLALA.toFile()));
		InputStream tmp1=null;
		
		
		
		
		try {
	      tmp1 = new FileInputStream(pathToFile);
		
		} catch (FileNotFoundException e) {
			System.out.println(" file not found!");
			e.printStackTrace();
		}
		try {
			eng.setTheory(new Theory(tmp1));
		} catch (InvalidTheoryException e) {
            
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        
		alice.tuprolog.Parser parser=new alice.tuprolog.Parser(eng.getTheory().toString());
		Iterator<Term> it=parser.iterator();	

		while(it.hasNext()){
			Struct t=(Struct)it.next();
			String term_name=t.getName();
			Term[] arguments=new Term[t.getArity()];
			String suffix="";
			
			if(mapAtomsToValueAccronym.containsKey(term_name)){
				suffix=mapAtomsToValueAccronym.get(term_name);
			}
			
			if(t.getArity()==1){
				arguments[0]=Term.createTerm(t.getArg(0).toString().substring(0, 1).toUpperCase() + t.getArg(0).toString().substring(1));
				    try{
				      domain.get(term_name).add(arguments[0].toString());
				    }
				    catch(NullPointerException e){
				    
				    }
			}
			
			if(t.getArity()==2){
				arguments[0]=Term.createTerm(t.getArg(0).toString().substring(0, 1).toUpperCase() + t.getArg(0).toString().substring(1));
				arguments[1]=Term.createTerm(t.getArg(1).toString().substring(0, 1).toUpperCase() + t.getArg(1).toString().substring(1)+suffix);
				    try{
				     domain.get(term_name).add(arguments[1].toString());
				    }
				    catch(NullPointerException e){
				    }
			}
			//value atom
			if(t.getArity()==3){
				for(int i=0;i<t.getArity()-1;i++){
					String output = t.getArg(i).toString().substring(0, 1).toUpperCase() + t.getArg(i).toString().substring(1);
					arguments[i]=Term.createTerm(output);
				}	
				arguments[t.getArity()-1]=Term.createTerm(t.getArg(t.getArity()-1).toString().substring(0, 1).toUpperCase() + t.getArg(t.getArity()-1).toString().substring(1)+suffix);
			    try{
			    	domain.get(term_name).add(arguments[t.getArity()-1].toString());
			    }
			    catch(NullPointerException e){

			    }
			}

			Struct newS=new Struct(t.getName(),arguments);
			writer.write(newS+"\n");

		}
		appendDomainToConfigurationFile(domain,configuration_file,mapLogvarsToDomainName);
		System.out.println(" Closing the writer! for "+writer.toString());
		writer.close();
	}

	private static void appendDomainToConfigurationFile(HashMap<String, HashSet<String>> domain, String configuration_file, HashMap<String, String> mapLogvarsToDomainName) throws IOException {
		File f=new File(configuration_file);
		FileWriter fw = new FileWriter(f,false); //the true will append the new data		
		for(String s:domain.keySet()){
			fw.write(s+"= {");
			Object[] array = domain.get(s).toArray();
			for(int i=0;i<array.length-1;i++){
				fw.write(array[i]+", ");
			}
			fw.write(array[array.length-1]+" }\n");
		}
	    
	    fw.close();
		
	}

}
