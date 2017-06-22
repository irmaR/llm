import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

import hybrid.network.NetworkInfo;


public class CopyFilesMLN {

	public static void main(String[] args) throws IOException{

		int nr_folds=10;
		String fold_name="Fold";
		int[] discrLevels=new int[]{2,4,6,8};
		String[] files=new String[]{"train","test"};

		for(int i=1;i<=nr_folds;i++){
			for(int k:discrLevels){
				String input_path="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space_MLN_no_type_atoms/pkddDiscrLevel"+k+"/Fold"+i+"/test/fold"+i+".db";
				String output_path_general="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/LSM_results/PKDD_LSM_results_Tryout_Latest/experiment_quickLenPenalty0.04/pkddDiscrLevel"+k+"/Fold"+i+"/mln_fast_inference_constants/test/";
				String output_path="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/LSM_results/PKDD_LSM_results_Tryout_Latest/experiment_quickLenPenalty0.04/pkddDiscrLevel"+k+"/Fold"+i+"/mln_fast_inference_constants/test/prob_files/auc/pkdd_true.db";
				if(!new File(output_path).exists()){
					new File(output_path).getParentFile().mkdirs();
					Files.copy(new File(input_path).toPath(), new File(output_path).toPath());
				}
				
				List<String> probability_file_names=new ArrayList<String>();
				probability_file_names.add("univ_prob_clientDistrict.prob");
				probability_file_names.add("univ_prob_freq.prob");
				probability_file_names.add("univ_prob_gender.prob");
				probability_file_names.add("univ_prob_hasAccount.prob");
				probability_file_names.add("univ_prob_hasLoan.prob");
				probability_file_names.add("univ_prob_loanStatus.prob");
				copyFilesProbabilities(output_path_general,probability_file_names);
			  
		
			    }
		}

	}

	private static void copyFilesProbabilities(String output_path,List<String> probability_file_names) throws IOException {
		for(String prb_file_name:probability_file_names){
			File copy_from=new File(output_path+"/prob_files/"+prb_file_name);
			File copy_to=new File(output_path+"/prob_files/auc/"+prb_file_name);
			try{
			Files.copy(copy_from.toPath(), copy_to.toPath());
			}
			catch(FileAlreadyExistsException e){
				continue;
			}
			catch(NoSuchFileException e){
				continue;
			}
		}
		
	}
	
	
	
}
