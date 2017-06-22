package learning;

import hybrid.comparators.Bigger;
import hybrid.comparators.InBetween;
import hybrid.comparators.Smaller;
import hybrid.featureGenerator.ComplexConjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.Renaming;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features.Average;
import hybrid.features.ComparisonFeature;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.OperatorFeature;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.LogvarRestrictionLiteral;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.RangeDiscrete;
import hybrid.network.StringValue;
import hybrid.network.TestRandvarValue;
import hybrid.operators.Addition;
import hybrid.operators.Division;
import hybrid.operators.Multiplication;
import hybrid.operators.Subtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class bogdan_extra_features {
	public static List<Feature> getListOfFeatures(Atom at,NetworkInfo ntw) throws ConjunctionConstructionProblem, FeatureTypeException{
		//add special features for x_pos_a  
		Renaming ren_object=new Renaming();
		
		Atom action_par=ntw.getPredicateNameToAtom().get("action_parameter");
		
		Atom x_pos_b=ntw.getPredicateNameToAtom().get("x_pos_b");
		ren_object=new Renaming();
		ren_object.addRenaming(x_pos_b.getArgument(0).getType(), new Logvar("Obj_1",x_pos_b.getArgument(0).getType()));
	    Atom x_pos_b1=x_pos_b.applyRenaming(ren_object).get(0);
		
		Atom x_pos_a=ntw.getPredicateNameToAtom().get("x_pos_a");
		ren_object=new Renaming();
		ren_object.addRenaming(x_pos_a.getArgument(0).getType(), new Logvar("Obj_1",x_pos_a.getArgument(0).getType()));
		Atom x_pos_a1=x_pos_a.applyRenaming(ren_object).get(0);
		
		Atom main_object=ntw.getPredicateNameToAtom().get("main_object");
	    ren_object=new Renaming();
	    ren_object.addRenaming(main_object.getArgument(0).getType(), new Logvar("Obj_1",main_object.getArgument(0).getType()));
	    Atom main_object_1=main_object.applyRenaming(ren_object).get(0);
	    Logvar classical_logvar=main_object.getArgument(0);
	    Logvar renamed_logvar=new Logvar("Obj_1",main_object.getArgument(0).getType());
	    
	    Atom secondary_object=ntw.getPredicateNameToAtom().get("secondary_object");
	    ren_object=new Renaming();
	    ren_object.addRenaming(secondary_object.getArgument(0).getType(), new Logvar("Obj_1",secondary_object.getArgument(0).getType()));
	    Atom secondary_object_1=secondary_object.applyRenaming(ren_object).get(0);
		
		Literal[] context_2=new Literal[]{new PosLiteral(main_object),new PosLiteral(secondary_object_1)};
	    Literal[] context_3=new Literal[]{new PosLiteral(main_object_1),new PosLiteral(secondary_object)};
	    Literal[] context_4=new Literal[]{new PosLiteral(new TestRandvarValue(action_par.getPredicate(),action_par.getArguments(),new StringValue("ten")))};
	    Literal[] context_5=new Literal[]{new PosLiteral(new TestRandvarValue(action_par.getPredicate(),action_par.getArguments(),new StringValue("fifteen")))};
	    Literal[] context_6=new Literal[]{new PosLiteral(new TestRandvarValue(action_par.getPredicate(),action_par.getArguments(),new StringValue("twenty")))};
	    Literal[] context_7=new Literal[]{new PosLiteral(new TestRandvarValue(action_par.getPredicate(),action_par.getArguments(),new StringValue("twentyfive")))};
	    List<ComplexConjunction> x_pos_a_conj=new ArrayList<ComplexConjunction>();
	   
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b),new PosLiteral(x_pos_b1),new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b),new PosLiteral(x_pos_b1),new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b),new PosLiteral(x_pos_b1),new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b),new PosLiteral(x_pos_b1),new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
	    
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_4,new Addition()));
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_4,new Subtraction()));
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_5,new Addition()));
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_5,new Subtraction()));
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_6,new Addition()));
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_6,new Subtraction()));
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_7,new Addition()));
	    x_pos_a_conj.add(new ComplexConjunction(at,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_7,new Subtraction()));

	    
	  //add thresholds for distance features
	  		double min_threshold=-12;
	  		double max_threshold=27;
	  		
	  		
	  		HashMap<String,List<Feature>> additional_features=new HashMap<String, List<Feature>>();
	  		double jump=0.5;
	  		List<Feature> fts=new ArrayList<Feature>();
	  		for(ComplexConjunction c:x_pos_a_conj){
	  			    fts.add(new OperatorFeature(c,new Average()));
	  			    //fts.add(new Operator_Feature(c,new Min()));
	  			    //fts.add(new Operator_Feature(c,new Max()));
	  				for(double i=min_threshold;i<max_threshold;i+=jump){
	  				fts.add(new OperatorFeature(c, new InBetween(i,i-jump),new Average()));
	  				fts.add(new OperatorFeature(c, new Bigger(i),new Average()));
	  				fts.add(new OperatorFeature(c, new Smaller(i),new Average()));
	  				//fts.add(new Operator_Feature(c, new InBetween(i,i-jump),new Min()));
	  				//fts.add(new Operator_Feature(c, new InBetween(i,i-jump),new Max()));
	  				}
	  		}
	  		
	  		return fts;
	    
		}

}
