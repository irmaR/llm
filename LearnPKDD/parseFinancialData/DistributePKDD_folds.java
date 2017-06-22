package parseFinancialData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class DistributePKDD_folds {

	public static void main(String [] args) throws IOException{
		String path_to_files="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space/pkdd_added_predicates/";
		String path_to_folds="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space/pkdd_added_predicates/";
		HashMap<Integer,int[]> crossvalidationTrainingIndices=intitializeTrainingIndices();
		HashMap<Integer,int[]> crossvalidationValidationIndices=initializeValidationIndices();
        for(int i=1;i<=10;i++){
        	String train_fold=path_to_files+"/Fold"+i+"/train/";
        	String validate_fold=path_to_files+"/Fold"+i+"/validate/";
        	String test_fold=path_to_files+"/Fold"+i+"/test/";
        	if(new File(test_fold).exists()){
        		new File(test_fold).delete();
        	}
        	if(new File(test_fold+"/fold"+i+".pl").exists()){
        		new File(test_fold+"/fold"+i+".pl").delete();
        	}
        	new File(test_fold).mkdirs();
        	new File(train_fold).mkdirs();
        	new File(validate_fold).mkdirs();
        	
        	Files.copy(new File(path_to_folds+"/fold"+i+".pl").toPath(),new File(test_fold+"/fold"+i+".pl").toPath());
        	
        	//training files
        	int[] indices_training=crossvalidationTrainingIndices.get(i);
        	for(int j=0;j<indices_training.length;j++){
            	Files.copy(new File(path_to_folds+"/fold"+indices_training[j]+".pl").toPath(),new File(train_fold+"/fold"+indices_training[j]+".pl").toPath());
        	}
        	
        	//validation
        	int[] indices_validation=crossvalidationValidationIndices.get(i);

        	for(int j=0;j<indices_validation.length;j++){
            	Files.copy(new File(path_to_folds+"/fold"+indices_validation[j]+".pl").toPath(),new File(validate_fold+"/fold"+indices_validation[j]+".pl").toPath());
	
        	}
        }
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
		indices.put(2,new int[]{1, 10, 9, 8, 4, 3});
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