import java.util.ArrayList;



public class Script_ARFF_hybrid {
}
/*
	
	//Argument1: path to resultse e.g.: /cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/HRDN_New_Implementation_results/PKDD/PKDD_Arff_Simple/
	//Argument2: path containing data: /cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space/
	//Argument3: discretization level e.g. 2

	public static void main(String[] args){
		String path_to_results=args[0];
		String path_to_data=args[1];
		ArrayList<Integer> runs=new ArrayList<Integer>();
		String feature_space="intrinsic";
        
		if(args.length==3){
			System.out.println(" Specific run set. ...");
			runs.add(Integer.valueOf(args[2]).intValue());
		}
		else{
			for(int i=1;i<=10;i++){
				runs.add(i);
			}
		}

		ARFF_Creator arff_creator=new ARFF_Creator();
		String[] predicates=new String[]{"cell_doublingtime","cell_lengthatbirth","cell_avglength","cell_lengthgrowthrate","cell_age","cell_oldpole","parent"};

		for(int i=0;i<runs.size();i++){
			try{
				String input_train_data=path_to_data+"/Fold"+runs.get(i)+"/train/";
				String input_test_data=path_to_data+"/Fold"+runs.get(i)+"/test/";
				String output_train_data=path_to_results+"/"+feature_space+"_ft_space"+"/Fold"+runs.get(i)+"/train/";
				String output_test_data=path_to_results+"/"+feature_space+"_ft_space"+"/Fold"+runs.get(i)+"/test/";
				for(String predicate:predicates){
					try {
						//train data
						arff_creator.main(new String[]{"-input",input_train_data,"-output",output_train_data,"-predicate",predicate,"-type_of_data","train","-features",feature_space});
						//test_data
						arff_creator.main(new String[]{"-input",input_test_data,"-output",output_test_data,"-predicate",predicate,"-type_of_data","test","-features",feature_space});
					} catch (Exception e) {
						System.out.println(e);
						e.printStackTrace();
					}
				}
			}
			catch(NullPointerException e){
				continue;
			}
		}
	}

}
*/