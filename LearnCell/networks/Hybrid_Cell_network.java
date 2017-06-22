package networks;

import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.NoCycles;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.RelationType;
import hybrid.network.Type;
import hybrid.queryMachine.MDLPenalty;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.structure_learning.GreedySearch;
import hybrid.structure_learning.StructureLearner;

import java.util.List;

public class Hybrid_Cell_network {
}
/*

	private static  NetworkInfo ntw;
	
	public static void main(String[] args) throws Exception{

		AlgorithmParameters parameters=new AlgorithmParameters();
		ParseArguments getAlgorithmParameters=new ParseArguments();
		getAlgorithmParameters.parseArgumentsHRDN(args);


		Type cells=new Type("cell_id");
		Logvar cell=new Logvar("cell",cells);
		Logvar cell1=new Logvar("cell1",cells);
		
		BooleanPred parent_pred=new BooleanPred("parent",2);
		parent_pred.setSubsampleingProcedure(new TuPrologSubSample(new NoCycles(),0.7));
		
		
		CategoricalPred cell_oldpole_pred=new CategoricalPred("cell_oldpole", 1);
		CategoricalPred cell_age_pred=new CategoricalPred("cell_age", 1);
		GaussianPred cell_doublingtime_pred=new GaussianPred("cell_doublingtime", 1,9,37);
		GaussianPred cell_lengthatbirth_pred=new GaussianPred("cell_lengthatbirth", 1,10,60);
		GaussianPred cell_avglength_pred=new GaussianPred("cell_avglength", 1,10,60);
		GaussianPred cell_lengthgrowthrate_pred=new GaussianPred("cell_lengthgrowthrate", 1,0,0.5);

		

		Atom parent=new Atom(parent_pred, new Logvar[]{cell,cell1});
		parent.setRelationType(RelationType.INTERNAL);
		Atom cell_age=new Atom(cell_age_pred, new Logvar[]{cell});
		Atom cell_oldpole=new Atom(cell_oldpole_pred, new Logvar[]{cell});
		Atom cell_doublingtime=new Atom(cell_doublingtime_pred, new Logvar[]{cell});
		Atom cell_lengthatbirth=new Atom(cell_lengthatbirth_pred, new Logvar[]{cell});
		Atom cell_avglength=new Atom(cell_avglength_pred, new Logvar[]{cell});
		Atom cell_lengthgrowthrate=new Atom(cell_lengthgrowthrate_pred, new Logvar[]{cell});
		
		ntw=new NetworkInfo(new Atom[]{parent,cell_age,cell_oldpole,cell_doublingtime,cell_lengthatbirth,cell_avglength,cell_lengthatbirth,cell_lengthgrowthrate},new Type[]{cells});
		
		//LOAD DATA
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());

		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator());
	    Data d_training=dataLoader.loadData(parameters.input_path+"/train/", "fold", "pl", ntw);
	    Data d_validation=dataLoader.loadData(parameters.input_path+"/validate/","fold", "pl",ntw);
	    Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path + "/test/","fold", "pl",ntw);
			 
	    
	    
	      
	    TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d_training, new MDLPenalty());
	    TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
	    TuPrologQueryMachine tuPrologQueryMachine_test=new TuPrologQueryMachine(d_test, AlgorithmParameters.getPenaltyType());
			
	  //ADD DISCRETIZED RANDVARVALUE TESTS
	  		if(!ntw.areRandVarValueTestsSetForAllPreds()){
	  			ntw.initializeRandvarTests();
	  		}
		FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(AlgorithmParameters.feature_length, AlgorithmParameters.nr_logvar_renamings);
        List<Feature> fts=fGen.generateFeatures(cell_age, ntw.getAtomsAndEqualityConstraints());
	  	Dependency test=new Dependency(cell_age,new Feature[]{fts.get(6),fts.get(30),fts.get(2),fts.get(8)});	
	  	test.getCpd().getCpdEvaluator().estimateParameters(tuPrologQueryMachine_training.getQueryResults(test));
	    System.out.println(test.getCpd().getParameters());
	  	System.out.println(" Interpretation created ... ");

		GreedySearch greedySearch=new GreedySearch();
		StructureLearner str_learner=new StructureLearner();	
		List<Feature> features=fGen.generateFeatures(parent, ntw.getAtomsAndEqualityConstraints());
		
		Atom[] learning_atoms=null;
		learning_atoms=ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]);
		str_learner.learnStructureAndEvaluate(learning_atoms,ntw, tuPrologQueryMachine_training, tuPrologQueryMachine_validation,tuPrologQueryMachine_test,fGen,greedySearch);
		
	}

}
*/