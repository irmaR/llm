package process_data;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.HashMap;

public class Distribute_folds {

	public static void main(String[] args) throws IOException{

	/*	String output="/home/irma/workspace/Data/Antoine_data/continuous/cells/";
		String path_to_data="/home/irma/workspace/Data/Antoine_data/continuous/folds/";*/
		String path_to_data=args[0];
		String output=args[1];
		

		for(int i=1;i<=10;i++){
			File train_fold=new File(output+"/Fold"+i,"/train");	
			train_fold.mkdirs();
			File validate_fold=new File(output+"/Fold"+i,"/validate");
			validate_fold.mkdirs();
			File test_fold=new File(output+"/Fold"+i,"/test");	
			test_fold.mkdirs();
			try{

				Files.copy(new File(path_to_data+"/fold"+i+".pl").toPath(), new File(test_fold+"/fold"+i+".pl").toPath());
			}
			catch(FileAlreadyExistsException e){
				continue;
			}
			copyTrainingFiles(path_to_data,train_fold,i);
			copyValidationFiles(path_to_data,validate_fold,i);
		}

	}

	private static void copyValidationFiles(String path_to_interpretations,File validate_fold,int fold_nr) throws IOException {
		HashMap<Integer,int[]> trainingIndices=initializeValidationIndices();
		int[] indices=trainingIndices.get(fold_nr);
		for(int j:indices){
			Files.copy(new File(path_to_interpretations+"/fold"+j+".pl").toPath(), new File(validate_fold+"/fold"+j+".pl").toPath());
		}
	}




	private static void copyTrainingFiles(String path_to_interpretations,File train_fold,int fold_nr) throws IOException {
		HashMap<Integer,int[]> trainingIndices=intitializeTrainingIndices();
		int[] indices=trainingIndices.get(fold_nr);
		for(int j:indices){
			Files.copy(new File(path_to_interpretations+"/fold"+j+".pl").toPath(), new File(train_fold+"/fold"+j+".pl").toPath());
		}
	}


	private static HashMap<Integer, int[]> initializeValidationIndices() {
		HashMap<Integer,int[]> indices=new HashMap<Integer,int[]>();
		indices.put(1,new int[]{2});
		indices.put(2,new int[]{6});
		indices.put(3,new int[]{9});
		indices.put(4,new int[]{5});
		indices.put(5,new int[]{3});
		indices.put(6,new int[]{1});
		indices.put(7,new int[]{4});
		indices.put(8,new int[]{3});
		indices.put(9,new int[]{7});
		indices.put(10,new int[]{1});
		return indices;
	}

	private static HashMap<Integer, int[]> intitializeTrainingIndices() {
		HashMap<Integer,int[]> indices=new HashMap<Integer,int[]>();
		indices.put(1,new int[]{6,7,5,8,4,9,10,3});
		indices.put(2,new int[]{1,10,9,8,4,3,7,5});
		indices.put(3,new int[]{7,5,10,8,1,4,2,6});
		indices.put(4,new int[]{10,8,1,6,9,2,7,3});
		indices.put(5,new int[]{6,1,9,7,8,4,2,10});
		indices.put(6,new int[]{2,10,4,7,9,8,3,5});
		indices.put(7,new int[]{5,6,2,10,8,1,3,9});
		indices.put(8,new int[]{10,2,9,4,6,7,5,1});
		indices.put(9,new int[]{6,8,3,2,1,4,10,5});
		indices.put(10,new int[]{6,2,4,7,8,3,9,5});
		return indices;
	}	

}
