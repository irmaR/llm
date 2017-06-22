
public class Script_arff_PKDD_hybrid {
}
/*

	public static void main(String[] args){
		String path_to_results=args[0];
		String path_to_data=args[1];
		int nr_runs=1;
		Create_ARFF_hybrid arff_creator=new Create_ARFF_hybrid();
		//String[] predicates=new String[]{"avgSalary","gender","clientDistrict","hasLoan","loanAmount","loanStatus","monthlyPayments","clientAge","freq","avgNrWith","avgSumOfW","avgSumOfInc","stdMonthInc","stdMonthW","ratUrbInhab","hasAccount"};
        String[] predicates=new String[]{"clientDistrict"};
        
			for(int i=1;i<=nr_runs;i++){
				try{
				String input_train_data=path_to_data+"/Fold"+i+"/train/";
				String input_test_data=path_to_data+"/Fold"+i+"/test/";
				String output_train_data=path_to_results+"/Fold"+i+"/train/";
				String output_test_data=path_to_results+"/Fold"+i+"/test/";
				for(String predicate:predicates){
					try {
						//train data
						arff_creator.main(new String[]{"-input",input_train_data,"-output",output_train_data,"-predicate",predicate,"-type_of_data","train"});
						//test_data
						//arff_creator.main(new String[]{"-input",input_test_data,"-output",output_test_data,"-predicate",predicate,"-type_of_data","test"});
					} catch (Exception e) {
						System.out.println(e);
						e.printStackTrace();
					}
				}
				}
				catch(NullPointerException e){
					System.out.println(" problem? ");
					continue;
				}
			}
		}
	

}
*/