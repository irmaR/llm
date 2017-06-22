package learning;

import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.features.Feature;
import hybrid.features.ValueFt;
import hybrid.interpretations.Data;
import hybrid.interpretations.DataType;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.parameters.Parameters;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.structureLearning.GreedySearch;
import hybrid.structureLearning.StructureLearner;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import network.Hybrid_robot_network;

public class estimate_parameters {

	private static  NetworkInfo ntw;
	
	public static void main(String[] args) throws Exception{
		AlgorithmParameters parameters=new AlgorithmParameters();
        ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS - added features to blocks functionality");
        getAlgorithmParameters.parseArgumentsHRDN(args);
        
        System.out.println("Parameters: "+parameters);
	    	
        Hybrid_robot_network hybrid_robotics_simple=new Hybrid_robot_network();
		ntw=hybrid_robotics_simple.getUniversityHybrid(1);
			
		//ESTIMATE PARAMETERS FOR DEPENDENCIES
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
	    Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/", "robot", "pl", ntw,DataType.training);
	    TuPrologQueryMachine training_data_machine=new TuPrologQueryMachine(d_training, AlgorithmParameters.getPenaltyType());
	    
	    //Next x position dependency
	    //next position dependens on the previous x and y position + x displacement
	    Atom next_x_position=ntw.getPredicateNameToAtom().get("next_x_pos");
	    Atom current_x_position=ntw.getPredicateNameToAtom().get("current_x_pos");
	    Atom current_y_position=ntw.getPredicateNameToAtom().get("current_y_pos");
	    Atom displacement_x_value=ntw.getPredicateNameToAtom().get("displacement_X");
	    ValueFt current_x_position_ft=new ValueFt(new Standard_Conjunction(next_x_position,new PosLiteral(current_x_position)));
	    ValueFt current_y_position_ft=new ValueFt(new Standard_Conjunction(next_x_position,new PosLiteral(current_y_position)));
	    ValueFt displacement_x_ft=new ValueFt(new Standard_Conjunction(next_x_position,new PosLiteral(displacement_x_value)));
	    Dependency dep_next_position=new Dependency(next_x_position,new Feature[]{current_x_position_ft,current_y_position_ft,displacement_x_ft});
	    System.out.println(dep_next_position);
	    Parameters pars=dep_next_position.getCpd().getCpdEvaluator().estimateParameters(training_data_machine.getQueryResults(dep_next_position));
	    
	    //output_file_for next_x_pos
	    FileWriter fw=new FileWriter(new File(AlgorithmParameters.output_path+"/parameters_next_x_pos.res"));
	    fw.append(pars.toString());
	    fw.close();
	    
	    
	}
	
}
