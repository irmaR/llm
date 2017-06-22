package networks;

public class Script_Learning {
}
/*
  public static void main(String args[]) throws Exception{
	  String input_path="/home/irma/workspace/Data/Antoine_data/continuous/cells/Fold";
	  String output_path="/home/irma/workspace/Results_HRDN/Cells/continuous/cells/mdl/Fold";
	  String penalty="mdl";
	  String input_path=args[0];
	  String output_path=args[1];
	  String penalty=args[2];
	  
	  for(int i=1;i<=10;i++){
		  String[] pars=new String[8];
		  pars[0]="-input";
		  pars[1]=input_path+i+"/";
		  pars[2]="-output";
		  pars[3]=output_path+i+"/";
		  pars[4]="-penalty_validation";
		  pars[5]=penalty;
		  pars[6]="-independent";
		  pars[7]="false";
		 // Hybrid_Cell_network learner=new Hybrid_Cell_network();
		  Discrete_Cell_network learner=new Discrete_Cell_network();
		  learner.main(pars);
	  }
  }
}
*/