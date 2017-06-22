import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.HashMap;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class DistributeIMDBsToFolds {

	
	public static void main(String[] args) throws IOException{
		ArgumentParser parser = ArgumentParsers.newArgumentParser("prog")
				.description("Process paths.");

		parser.addArgument("-path_to_folds")
		.dest("path_to_folds")
		.type(String.class)
		.required(true)
		;

		parser.addArgument("-path_to_distributed")
		.dest("path_to_distributed")
		.type(String.class)
		.required(true)
		;
		
		Namespace res=null;

		try {
			res = parser.parseArgs(args);
			System.out.println(" RES " +res);
		} catch (ArgumentParserException e) {
			System.out.println(" Didn't succeed to parse the parameters");
			parser.handleError(e);
			System.exit(0);
		}
		
		String path_to_interpretations=res.getString("path_to_folds");
		String path_to_database=res.getString("path_to_distributed");
		
		for(int i=1;i<=10;i++){
		   File train_fold=new File(path_to_database+"/Fold"+i,"/train");	
		   train_fold.mkdirs();
		   File validate_fold=new File(path_to_database+"/Fold"+i,"/validate");
		   validate_fold.mkdirs();
		   File test_fold=new File(path_to_database+"/Fold"+i,"/test");	
		   test_fold.mkdirs();
		   try{
		   
		   Files.copy(new File(path_to_interpretations+"/fold"+i+".pl").toPath(), new File(test_fold+"/fold"+i+".pl").toPath());
		   }
		   catch(FileAlreadyExistsException e){
			   continue;
		   }
		   copyTrainingFiles(path_to_interpretations,train_fold,i);
		   copyValidationFiles(path_to_interpretations,validate_fold,i);
		   

		}
		
		
	}
	
	
	
	
	private static void copyValidationFiles(String path_to_interpretations,File validate_fold,int fold_nr) throws IOException {
		HashMap<Integer,int[]> trainingIndices=intitializeTrainingValidationIndices();
		int[] indices=trainingIndices.get(fold_nr);
		for(int j:indices){
			 Files.copy(new File(path_to_interpretations+"/fold"+j+".pl").toPath(), new File(validate_fold+"/fold"+j+".pl").toPath());
		}
	}




	private static void copyTrainingFiles(String path_to_interpretations,File train_fold,int fold_nr) throws IOException {
		HashMap<Integer,int[]> trainingIndices=intitializeTrainingValidationIndices();
		int[] indices=trainingIndices.get(fold_nr);
		for(int j:indices){
			 Files.copy(new File(path_to_interpretations+"/fold"+j+".pl").toPath(), new File(train_fold+"/fold"+j+".pl").toPath());
		}
	}


	private static HashMap<Integer, int[]> intitializeTrainingValidationIndices() {
		HashMap<Integer,int[]> indices=new HashMap<Integer,int[]>();
		indices.put(1,new int[]{2,10,3,6,7,5,8,4,9});
		indices.put(2,new int[]{6,7,5,1,10,9,8,4,3});
		indices.put(3,new int[]{9,2,6,7,5,10,8,1,4});
		indices.put(4,new int[]{5,7,3,10,8,1,6,9,2});
		indices.put(5,new int[]{2,3,10,6,1,9,7,8,4});
		indices.put(6,new int[]{3,5,1,2,10,4,7,9,8});
		indices.put(7,new int[]{3,9,4,5,6,2,10,8,1});
		indices.put(8,new int[]{3,5,1,10,2,9,4,6,7});
		indices.put(9,new int[]{7,10,5,6,8,3,2,1,4});
		indices.put(10,new int[]{1,9,5,6,2,4,7,8,3});
		return indices;
	}	

	private static HashMap<Integer, int[]> initializeValidationIndices() {
		HashMap<Integer,int[]> indices=new HashMap<Integer,int[]>();
		indices.put(1,new int[]{2,10,3});
		indices.put(2,new int[]{6,7,5});
		indices.put(3,new int[]{9,2,6});
		indices.put(4,new int[]{5,7,3});
		indices.put(5,new int[]{2,3,10});
		indices.put(6,new int[]{3,5,1});
		indices.put(7,new int[]{3,9,4});
		indices.put(8,new int[]{3,5,1});
		indices.put(9,new int[]{7,10,5});
		indices.put(10,new int[]{1,9,5});
		return indices;
	}

	private static HashMap<Integer, int[]> intitializeTrainingIndices() {
		HashMap<Integer,int[]> indices=new HashMap<Integer,int[]>();
		indices.put(1,new int[]{6,7,5,8,4,9});
		indices.put(2,new int[]{1,10,9,8,4,3});
		indices.put(3,new int[]{7,5,10,8,1,4});
		indices.put(4,new int[]{10,8,1,6,9,2});
		indices.put(5,new int[]{6,1,9,7,8,4});
		indices.put(6,new int[]{2,10,4,7,9,8});
		indices.put(7,new int[]{5,6,2,10,8,1});
		indices.put(8,new int[]{10,2,9,4,6,7});
		indices.put(9,new int[]{6,8,3,2,1,4});
		indices.put(10,new int[]{6,2,4,7,8,3});
		return indices;
	}	
	
	
}
