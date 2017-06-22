import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.DataType;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import network.TransitionDisplacementNetwork;


public class FeatureCalculation {
		private static  NetworkInfo ntw;

		public static int[] makeSequence(int begin, int end) {
			int[] ret = new int[end - begin + 1];
			for(int i = begin; i <= end; i++){
				ret[i]=i;
			}
			return ret;  
		}

		public static void main(String[] args) throws Exception{
			AlgorithmParameters parameters=new AlgorithmParameters();
			ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS - added features to blocks functionality");
			getAlgorithmParameters.parseArgumentsHRDN(args);
			AlgorithmParameters.setDetailed_logging_flag(false);
			AlgorithmParameters.setNrFolds(5);
			System.out.println("Parameters: "+parameters);
			System.out.println("Running box learning - decision trees");
			TransitionDisplacementNetwork hybrid_robotics_simple=new TransitionDisplacementNetwork();
			ntw=hybrid_robotics_simple.getNetwork(1);
			
			if(AlgorithmParameters.getScriptOutput()!=null){
				File theDir = new File(AlgorithmParameters.getScriptOutput());
				// if the directory does not exist, create it
				System.out.println("Output script: "+AlgorithmParameters.getScriptOutput());
				System.out.println("Already exists? "+theDir.exists());
				if (AlgorithmParameters.getScriptOutput()!=null && !theDir.exists()) {
				    System.out.println("creating directory: " + AlgorithmParameters.getScriptOutput());
				    boolean result = false;
				    try{
				        theDir.mkdirs();
				        result = true;
				    } 
				    catch(SecurityException se){
				        //handle it
				    }        
				    if(result) {    
				        System.out.println("DIR created");  
				    }
				}
			}

			//LOAD DATA
			TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
			TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
			Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/","data", "txt", ntw,DataType.training);
			Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/", "data", "txt",ntw,DataType.validation);
			Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "data", "txt", ntw,DataType.test);
			

			//query machines
			TuPrologQueryMachine training_data_machine=new TuPrologQueryMachine(d_training, AlgorithmParameters.getPenaltyType());
			TuPrologQueryMachine validation_machine=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
			TuPrologQueryMachine test_machine=new TuPrologQueryMachine(d_test, AlgorithmParameters.getPenaltyType());
			
			
			List<Feature> extra_features_pos_x_next=new ArrayList<Feature>();
			List<Feature> extra_features_pos_y_next=new ArrayList<Feature>();
			List<Feature> extra_features_pos_z_next=new ArrayList<Feature>();
			
			if(AlgorithmParameters.getPredicate_names().contains("pos_x_next")){
				System.out.println("Generating Feature space for pos_x_next");
				System.out.println("Training data has: "+training_data_machine.getData().getNrGroundingsInData(ntw.getPredicateNameToAtom().get("pos_x_next"))+" data points");
				List<Feature> fts=next_arm_extra_features.getSimpleFeatures(ntw.getPredicateNameToAtom().get("pos_x_next"), ntw,training_data_machine);
				extra_features_pos_x_next.addAll(fts);
				System.out.println("Finished ...");
			}
			
			if(AlgorithmParameters.getPredicate_names().contains("pos_y_next")){
				System.out.println("Generating Feature space for pos_y_next");
				System.out.println("Training data has: "+training_data_machine.getData().getNrGroundingsInData(ntw.getPredicateNameToAtom().get("pos_y_next"))+" data points");
				extra_features_pos_y_next.addAll(next_arm_extra_features.getSimpleFeatures(ntw.getPredicateNameToAtom().get("pos_y_next"), ntw,training_data_machine));
				System.out.println("Finished ...");
			}
			if(AlgorithmParameters.getPredicate_names().contains("pos_z_next")){
				System.out.println("Generating Feature space for pos_z_next");
				System.out.println("Training data has: "+training_data_machine.getData().getNrGroundingsInData(ntw.getPredicateNameToAtom().get("pos_z_next"))+" data points");
				extra_features_pos_z_next.addAll(next_arm_extra_features.getSimpleFeatures(ntw.getPredicateNameToAtom().get("pos_z_next"), ntw,training_data_machine));
				System.out.println("Finished ...");
			}
			
			
			//set feature generator
			FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(AlgorithmParameters.feature_length, AlgorithmParameters.nr_logvar_renamings);
			fGen.setAdditionalFeatures(extra_features_pos_x_next, ntw.getPredicateNameToAtom().get("pos_x_next"));
			fGen.setAdditionalFeatures(extra_features_pos_y_next, ntw.getPredicateNameToAtom().get("pos_y_next"));
	     	fGen.setAdditionalFeatures(extra_features_pos_z_next, ntw.getPredicateNameToAtom().get("pos_z_next"));

			HashMap<Atom,List<Predicate>> exclusion_predicates=new HashMap<Atom,List<Predicate>>();
			List<Predicate> exclusion_pos_x_next=new ArrayList<Predicate>();
			exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
			exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
			exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
			exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("yaw_next").getPredicate());
			exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("roll_next").getPredicate());
			exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pitch_next").getPredicate());
			exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("yaw_cur").getPredicate());
			exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("roll_cur").getPredicate());
			exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pitch_cur").getPredicate());
			
			List<Predicate> exclusion_pos_y_next=new ArrayList<Predicate>();
			exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
			exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
			exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
			exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("yaw_next").getPredicate());
			exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("roll_next").getPredicate());
			exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pitch_next").getPredicate());
			exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("yaw_cur").getPredicate());
			exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("roll_cur").getPredicate());
			exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pitch_cur").getPredicate());
			
			List<Predicate> exclusion_pos_z_next=new ArrayList<Predicate>();
			exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
			exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
			exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
			exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("yaw_next").getPredicate());
			exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("roll_next").getPredicate());
			exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pitch_next").getPredicate());
			exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("yaw_cur").getPredicate());
			exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("roll_cur").getPredicate());
			exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pitch_cur").getPredicate());
			
			exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_x_next"), exclusion_pos_x_next);
			exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_y_next"), exclusion_pos_y_next);
			exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_z_next"), exclusion_pos_z_next);
			fGen.setExclusionPredicates(exclusion_predicates);

			fGen.do_not_generate_features();

			List<Feature> fts=fGen.generateFeatures(ntw.getPredicateNameToAtom().get(AlgorithmParameters.predicates.get(0)), ntw.getLiterals());
			
			BufferedWriter fw=new BufferedWriter(new FileWriter(new File(AlgorithmParameters.getOutput_path()+"/"+ntw.getPredicateNameToAtom().get(AlgorithmParameters.predicates.get(0)).getPredicate().getPredicateName()+".csv")));
			BufferedWriter fw_fts_type=new BufferedWriter(new FileWriter(new File(AlgorithmParameters.getOutput_path()+"/"+AlgorithmParameters.predicates.get(0)+"_feature_types"+".info")));
			BufferedWriter fw_features=new BufferedWriter(new FileWriter(new File(AlgorithmParameters.getOutput_path()+"/"+AlgorithmParameters.predicates.get(0)+"_features"+".info")));
			for(Feature f:fts){
	        	fw_features.append(f.toString()+"\n");
	        	String type=null;
	        	if(f.isContinuousOutput()){
	        		type="cont";
	        	}
	        	else if(f.isDiscreteOutput() && f.getRange().isBooleanRange()){
	        		type="boolean";
	        	}
	        	else{
	        		type="categ";
	        	}
	        }
	        fw_fts_type.close();
	        fw_features.close();
			HashMap<Feature,QueryData> map=new HashMap<Feature,QueryData>();
			Integer data_points=null;
			int counter=0;
			for(Feature f:fts){
				counter++;
				Dependency dep=new Dependency(ntw.getPredicateNameToAtom().get(AlgorithmParameters.predicates.get(0)),new Feature[]{f});
				QueryData qd=training_data_machine.getQueryResults(dep);
				System.out.println("Queried "+counter+"th feature out of: "+fts.size());
				if(data_points==null){
					data_points=qd.getNr_groundings_for_head();
				}
				map.put(f,qd);
			}
			System.out.println("Nr data points: "+data_points);
			for(int i=0;i<data_points;i++){
			   System.out.println("Writing "+i+"th row out of: "+data_points);
			   String interpretation_path=null;
			   String headValue=null;
			   String fts_string="";
			   String row_string="";
			   for(Feature f:fts){
				    if(interpretation_path==null){
				    	interpretation_path=map.get(f).getFlatData().get(i).getPathToInterpretation();
				    }
				    if(headValue==null){
				    	headValue=map.get(f).getFlatData().get(i).getHead().getValue().toString();
				    }
					fts_string+=map.get(f).getFlatData().get(i).getFeatureValues().get(f).toString()+",";	
				}
			   // System.out.println(" Row "+i+" "+headValue);
			    row_string+=interpretation_path+","+headValue+","+fts_string+"\n";
				fw.append(row_string);
			}
			fw.close();
		}
}
