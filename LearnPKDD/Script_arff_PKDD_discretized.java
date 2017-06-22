/**
 * Propositionalize PKDD data accoring do a predetermined set of parents
 * @author irma
 *
 */

public class Script_arff_PKDD_discretized {
}
/*

	//Argument1: path to resultse e.g.: /cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/HRDN_New_Implementation_results/PKDD/PKDD_Arff_Simple/
	//Argument2: path containing data: /cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space/
	//Argument3: discretization level e.g. 2

	public static void main(String[] args){
		String path_to_results=args[0];
		String path_to_data=args[1];
		int discretization=Integer.valueOf(args[2]);
		int[] discretizations=new int[]{discretization};
		int nr_runs=10;
		Create_ARFF_discretized arff_creator=new Create_ARFF_discretized();
		//String[] predicates=new String[]{"avgSalary","gender","clientDistrict","hasLoan","loanAmount","loanStatus","monthlyPayments","clientAge","freq","avgNrWith","avgSumOfW","avgSumOfInc","stdMonthInc","stdMonthW","ratUrbInhab","hasAccount"};
		String[] predicates=new String[]{"freq"};

		for(int k=0;k<discretizations.length;k++){
			for(int i=1;i<=nr_runs;i++){
				try{
					String input_train_data=path_to_data+"pkddDiscrLevel"+discretizations[k]+"/Fold"+i+"/train/";
					String input_test_data=path_to_data+"pkddDiscrLevel"+discretizations[k]+"/Fold"+i+"/test/";
					String output_train_data=path_to_results+"pkddDiscrLevel"+discretizations[k]+"/Fold"+i+"/train/";
					String output_test_data=path_to_results+"pkddDiscrLevel"+discretizations[k]+"/Fold"+i+"/test/";
					for(String predicate:predicates){
						System.out.println("Extracting: "+predicate);
						try {
							//train data
							arff_creator.main(new String[]{"-input",input_train_data,"-output",output_train_data,"-predicate",predicate,"-discretization",String.valueOf(discretizations[k]),"-type_of_data","train"});
							//test_data
							arff_creator.main(new String[]{"-input",input_test_data,"-output",output_test_data,"-predicate",predicate,"-discretization",String.valueOf(discretizations[k]),"-type_of_data","test"});
						} catch (Exception e) {
							System.out.println(e);
							e.printStackTrace();
						}
					}
				}
				catch(NullPointerException e){
					System.out.println("Something happende: "+e);
					continue;
				}
			}
		}
	}
}

*/