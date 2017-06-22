package network;

import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.Renaming;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Mode;
import hybrid.features.Proportion;
import hybrid.features.ValueFt;
import hybrid.interpretations.NoCycles;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.Type;

import java.util.HashMap;

public class Hybrid_robot_network {
	
	public NetworkInfo getUniversityHybrid(int subsampling_ratio){
		Type objects=new Type("object");
		Logvar object=new Logvar("O",objects);
			
		GaussianPred current_x_pos_pred=new GaussianPred("current_x_pos",1);
		GaussianPred current_y_pos_pred=new GaussianPred("current_y_pos",1);
		GaussianPred next_x_pos_pred=new GaussianPred("next_x_pos",1);
		GaussianPred displacement_X_pred=new GaussianPred("displacement_X",1);
		GaussianPred displacement_Y_pred=new GaussianPred("displacement_Y",1);
		GaussianPred next_y_pos_pred=new GaussianPred("next_y_pos",1);
		Predicate action_pred=new CategoricalPred("action",1,new String[]{"push"});
		//maybe need affected later to denote when an object was touched or not
		//BooleanPred affected_pred=new BooleanPred("affected",1);
		//affected_pred.setSubsampleingProcedure(new TuPrologInterpretationCreator_NoSubsampling());


		Atom current_x_pos=new Atom(current_x_pos_pred, new Logvar[]{object});
		Atom current_y_pos=new Atom(current_y_pos_pred, new Logvar[]{object});
		Atom next_x_pos=new Atom(next_x_pos_pred, new Logvar[]{object});
		Atom next_y_pos=new Atom(next_y_pos_pred, new Logvar[]{object});
		Atom action=new Atom(action_pred, new Logvar[]{object});
		//Atom affected=new Atom(affected_pred, new Logvar[]{object});
		Atom displacement_X=new Atom(displacement_X_pred,new Logvar[]{object});
		Atom displacement_Y=new Atom(displacement_Y_pred,new Logvar[]{object});

		return new NetworkInfo(new Atom[]{current_x_pos,current_y_pos,next_x_pos,next_y_pos,action,displacement_X,displacement_Y},new Type[]{objects});	   
	}
}
