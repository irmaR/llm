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

public class Bogdan_network {
	public NetworkInfo getNetwork(int subsampling_ratio){
		Type objects=new Type("object");
		Logvar object=new Logvar("Obj",objects);
		Logvar object1=new Logvar("Obj1",objects);
		Predicate main_object_pred=new BooleanPred("main_object",1);
		Predicate secondary_object_pred=new BooleanPred("secondary_object",1);
		
		CategoricalPred object_size_pred=new CategoricalPred("obj_size",1,new String[]{"one","two","three"});
		CategoricalPred action_parameter_pred=new CategoricalPred("action_parameter",1,new String[]{"ten","fifteen","twenty","twentyfive"});
		
		GaussianPred x_pos_before_pred=new GaussianPred("x_pos_b",1);
		GaussianPred x_pos_before_pred_1=new GaussianPred("x_pos_b",1);
		
		GaussianPred x_pos_after_pred=new GaussianPred("x_pos_a",1);
		GaussianPred x_pos_after_pred_1=new GaussianPred("x_pos_a",1);
		
		GaussianPred y_pos_before_pred=new GaussianPred("y_pos_b",1);
		GaussianPred y_pos_before_pred_1=new GaussianPred("y_pos_b",1);
		
		GaussianPred y_pos_after_pred=new GaussianPred("y_pos_a",1);
		GaussianPred y_pos_after_pred_1=new GaussianPred("y_pos_a",1);
		
		GaussianPred x_delta_pred_main=new GaussianPred("delta_x_main",1);
		GaussianPred y_delta_pred_main=new GaussianPred("delta_y_main",1);
		GaussianPred x_delta_pred_second=new GaussianPred("delta_x_sec",1);
		GaussianPred y_delta_pred_second=new GaussianPred("delta_y_sec",1);
		GaussianPred rel_delta_x_pred=new GaussianPred("rel_delta_x",2);
		GaussianPred rel_delta_y_pred=new GaussianPred("rel_delta_y",2);
			
		Atom object_size=new Atom(object_size_pred, new Logvar[]{object});
		Atom main_object=new Atom(main_object_pred, new Logvar[]{object});
		Atom secondary_object=new Atom(secondary_object_pred, new Logvar[]{object});
		Atom x_pos_before=new Atom(x_pos_before_pred, new Logvar[]{object});
		Atom action_parameter=new Atom(action_parameter_pred, new Logvar[]{object});
		Atom x_pos_after=new Atom(x_pos_after_pred, new Logvar[]{object});
		Atom y_pos_before=new Atom(y_pos_before_pred, new Logvar[]{object});
		Atom y_pos_after=new Atom(y_pos_after_pred, new Logvar[]{object});
		Atom x_delta_main=new Atom(x_delta_pred_main, new Logvar[]{object});
		Atom y_delta_main=new Atom(y_delta_pred_main, new Logvar[]{object});
		Atom x_delta_sec=new Atom(x_delta_pred_second, new Logvar[]{object});
		Atom y_delta_sec=new Atom(y_delta_pred_second, new Logvar[]{object});
		Atom rel_delta_x=new Atom(rel_delta_x_pred, new Logvar[]{object,object});
		Atom rel_delta_y=new Atom(rel_delta_y_pred, new Logvar[]{object,object});
		return new NetworkInfo(new Atom[]{action_parameter,object_size,main_object,secondary_object,x_pos_before,x_pos_after,y_pos_before,y_pos_after,x_delta_main,x_delta_sec,y_delta_main,y_delta_sec,rel_delta_x,rel_delta_y},new Type[]{objects});	   		
		}
	}



