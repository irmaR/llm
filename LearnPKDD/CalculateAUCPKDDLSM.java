import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CalculateAUCPKDDLSM {
	public static void main(String[] args) throws IOException{

		int nr_folds=10;
		String fold_name="Fold";
		int[] discrLevels=new int[]{2,4,6,8};
		String[] files=new String[]{"train","test"};

		for(int i=1;i<=nr_folds;i++){
			for(int k:discrLevels){
				String auc_file_path="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/LSM_results/PKDD_LSM_results_Tryout_Latest/experiment_quickLenPenalty0.04/pkddDiscrLevel"+k+"/Fold"+i+"/mln_fast_inference_constants/test/prob_files/auc/";
				String true_file="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/LSM_results/PKDD_LSM_results_Tryout_Latest/experiment_quickLenPenalty0.04/pkddDiscrLevel"+k+"/Fold"+i+"/mln_fast_inference_constants/test/prob_files/auc/pkdd_true.db";
				String path_to_auc_calc="/home/irma/Scripts/auc.pl";

				List<String> probability_file_names=new ArrayList<String>();
				probability_file_names.add("univ_prob_clientDistrict.prob");
				probability_file_names.add("univ_prob_freq.prob");
				probability_file_names.add("univ_prob_gender.prob");
				probability_file_names.add("univ_prob_hasAccount.prob");
				probability_file_names.add("univ_prob_hasLoan.prob");
				probability_file_names.add("univ_prob_loanStatus.prob");

				calculateAUCsLSM(auc_file_path,path_to_auc_calc,probability_file_names,true_file);


			}
		}

	}

	private static void calculateAUCsLSM(String auc_file_path,String path_to_auc_calc, List<String> probability_file_names, String true_file) throws IOException {
		for(String prob_name:probability_file_names){
			Runtime rt = Runtime.getRuntime();
			String command="perl "+path_to_auc_calc+" "+auc_file_path+"/"+prob_name+" "+true_file+" "+auc_file_path+"/"+"auc_"+prob_name.split("_")[2].replace(".prob", "");
			Process pr = rt.exec(command);
		}
		

	}
}
