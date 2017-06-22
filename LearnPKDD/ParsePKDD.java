

import hybrid.interpretations.DiscretizedRangeExtractor;
import hybrid.network.Atom;
import hybrid.network.Logvar;
import hybrid.network.MinMaxValue;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.Type;
import hybrid.util.ParseToMLNFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import alice.tuprolog.Term;

public class ParsePKDD {
}
/*

	public static void main(String[] args) throws FileNotFoundException{
		int nr_folds=10;
		String fold_name="Fold";
		int[] discrLevels=new int[]{2,4,6,8};
		String[] files=new String[]{"train","test"};

		String generic_dir_name="data800x125x125";



		for(int i=1;i<=nr_folds;i++){
			for(int k:discrLevels){
				String input_path="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space/pkddDiscrLevel"+k+"/Fold"+i+"/";
				String output_path="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space_MLN/pkddDiscrLevel"+k+"/Fold"+i+"/";
				String non_discretized="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space/pkdd/Fold"+i+"/train/";
				CreateDiscretizedPKDD discrPKDD=new CreateDiscretizedPKDD();
				NetworkInfo pkkDiscretized=discrPKDD.getDiscretizedPKDD(k,1.0);

				DiscretizedRangeExtractor extractRange=new DiscretizedRangeExtractor();
				HashMap<Predicate,MinMaxValue> minMax=extractRange.getMinMaxValuesForPredicates(non_discretized, pkkDiscretized.getDiscretizedAtoms().toArray(new Atom[pkkDiscretized.getDiscretizedAtoms().size()]));
				System.out.println(minMax);
				String configuration_file="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space_MLN/pkddDiscrLevel"+k+"/Fold"+i+"/pkdd_domain_"+k;

				HashMap<String,String> mapAtomsToValueAccronym=new HashMap<String, String>();
				mapAtomsToValueAccronym.put("loanAmount", "");
				mapAtomsToValueAccronym.put("monthlyPayments", "");
				mapAtomsToValueAccronym.put("clientAge", "");
				mapAtomsToValueAccronym.put("avgNrWith", "");
				mapAtomsToValueAccronym.put("avgSumOfW", "");
				mapAtomsToValueAccronym.put("avgSumOfInc", "");
				mapAtomsToValueAccronym.put("stdMonthInc", "");
				mapAtomsToValueAccronym.put("stdMonthW", "");
				mapAtomsToValueAccronym.put("avgSalary", "");
				mapAtomsToValueAccronym.put("ratUrbInhab", "");
				mapAtomsToValueAccronym.put("ratUrbInhab", "");
				mapAtomsToValueAccronym.put("gender", "Gen");

				
				
				for(String f:files){
					String read_file_from=input_path+f+"/";
					String write_file_to=output_path+f+"/";

					File folder = new File(read_file_from);
					System.out.println(" Read file from: "+read_file_from);
					File[] listOfFiles = folder.listFiles();
					for(File fi:listOfFiles){
						try {
							String just_name=fi.getName().split("\\.")[0];
							String[] booleanPredNames=new String[]{"client","loan","account","hasLoan","hasAccont"};

							HashMap<String,String> mapLogVarsDomain=new HashMap<String, String>();
							HashMap<String,Integer> valueIndex=new HashMap<String, Integer>();
							ParseToMLNFormat.parseFile(read_file_from+"/"+fi.getName(), booleanPredNames,write_file_to,just_name+".db", configuration_file,mapAtomsToValueAccronym,valueIndex,mapLogVarsDomain);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

		}
	}
}
*/