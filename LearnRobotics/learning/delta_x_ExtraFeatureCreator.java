package learning;

import hybrid.comparators.Bigger;
import hybrid.comparators.InBetween;
import hybrid.comparators.Smaller;
import hybrid.featureGenerator.ComplexConjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.Renaming;
import hybrid.features.Average;
import hybrid.features.OperatorFeature;
import hybrid.features.Feature;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.StringValue;
import hybrid.network.TestRandvarValue;
import hybrid.operators.Addition;
import hybrid.operators.Division;
import hybrid.operators.Multiplication;
import hybrid.operators.Subtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class delta_x_ExtraFeatureCreator {

	
	public static List<Feature> getListOfFeatures(Atom at,NetworkInfo ntw) throws ConjunctionConstructionProblem{
		List<Feature> tmp=new ArrayList<>();
		 Atom x_pos_b=ntw.getPredicateNameToAtom().get("x_pos_b");
		 Renaming ren_object=new Renaming();
		 ren_object.addRenaming(x_pos_b.getArgument(0).getType(), new Logvar("Obj_1",x_pos_b.getArgument(0).getType()));
		 Atom x_pos_b1=x_pos_b.applyRenaming(ren_object).get(0);
		 
		 //logvar of main object
		 Atom main_object=ntw.getPredicateNameToAtom().get("main_object");
		 ren_object=new Renaming();
		 ren_object.addRenaming(main_object.getArgument(0).getType(), new Logvar("Obj_1",main_object.getArgument(0).getType()));
		 Atom main_object_1=main_object.applyRenaming(ren_object).get(0);
		    
         //logvar of secondary object		
		 Atom secondary_object=ntw.getPredicateNameToAtom().get("secondary_object");
		 ren_object=new Renaming();
		 ren_object.addRenaming(secondary_object.getArgument(0).getType(), new Logvar("Obj_1",secondary_object.getArgument(0).getType()));
		 Atom secondary_object_1=secondary_object.applyRenaming(ren_object).get(0);
		 
		 //create randvar values for action parameter
		 Logvar renamed_logvar_dim=new Logvar("Obj_1",main_object.getArgument(0).getType());
		 ren_object=new Renaming();
		 ren_object.addRenaming(ntw.getPredicateNameToAtom().get("action_parameter").getArgument(0).getType(),renamed_logvar_dim);
		 Atom action_parameter_renamed=ntw.getPredicateNameToAtom().get("action_parameter").applyRenaming(ren_object).get(0);
		 TestRandvarValue dimension_10=new TestRandvarValue(ntw.getPredicateNameToAtom().get("action_parameter").getPredicate(), action_parameter_renamed.getArguments(), new StringValue("ten"));
		 TestRandvarValue dimension_15=new TestRandvarValue(ntw.getPredicateNameToAtom().get("action_parameter").getPredicate(), action_parameter_renamed.getArguments(), new StringValue("fifteen"));
		 TestRandvarValue dimension_20=new TestRandvarValue(ntw.getPredicateNameToAtom().get("action_parameter").getPredicate(), action_parameter_renamed.getArguments(), new StringValue("twenty"));
		 TestRandvarValue dimension_25=new TestRandvarValue(ntw.getPredicateNameToAtom().get("action_parameter").getPredicate(), action_parameter_renamed.getArguments(), new StringValue("twentyfive"));

		 
		 //add specific context
		 Literal[] context_2=new Literal[]{new PosLiteral(main_object_1),new PosLiteral(secondary_object)};
		 Literal[] context_3=new Literal[]{new PosLiteral(main_object_1),new PosLiteral(secondary_object)};
		 Literal[] context_4=new Literal[]{new PosLiteral(dimension_10)};
		 Literal[] context_5=new Literal[]{new PosLiteral(dimension_15)};
		 Literal[] context_6=new Literal[]{new PosLiteral(dimension_20)};
		 Literal[] context_7=new Literal[]{new PosLiteral(dimension_25)};
		 
		 
		 HashMap<String,List<ComplexConjunction>> complex_conjunctions=new HashMap<String,List<ComplexConjunction>>();
		 List<ComplexConjunction> x_delta_complex_conjunctions=new ArrayList<ComplexConjunction>();
		 
		 
		//features: x_delta(Obj) | x_pos_before(Obj) op x_pos_before(Obj1) <- Obj=main_object,Obj1=secondary_object 
		 
		 
		 
		 
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_2,new Addition()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_2,new Subtraction()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_2,new Division()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_2,new Multiplication()));
		 
		 
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_4,new Addition()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_4,new Subtraction()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_4,new Division()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_4,new Multiplication()));
		 
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_5,new Addition()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_5,new Subtraction()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_5,new Division()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_5,new Multiplication()));
		 
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_6,new Addition()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_6,new Subtraction()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_6,new Division()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_6,new Multiplication()));
		 
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_7,new Addition()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_7,new Subtraction()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_7,new Division()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_7,new Multiplication()));
		 
		 //features: x_delta(Obj) | threshold 1 < x_pos_before(Obj) op x_pos_before(Obj1) < threshold 2<- Obj=secondary_object,Obj1=main_bject 
		 //x_delta_complex_conjunctions.add(new ComplexConjunction(x_delta,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_3,new Addition()));
		// x_delta_complex_conjunctions.add(new ComplexConjunction(x_delta,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_3,new Subtraction()));
		 //x_delta_complex_conjunctions.add(new ComplexConjunction(x_delta,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_3,new Division()));
		// x_delta_complex_conjunctions.add(new ComplexConjunction(x_delta,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_3,new Multiplication()));
		 
		 //Make normal features (continuous features, no thresholds)
		 for(ComplexConjunction c:x_delta_complex_conjunctions){
			 tmp.add(new OperatorFeature(c,new Average()));
			 tmp.add(new OperatorFeature(c,new Min()));
			 tmp.add(new OperatorFeature(c,new Max()));
		 }
		 
		 x_delta_complex_conjunctions=new ArrayList<ComplexConjunction>();
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_2,new Addition()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_2,new Subtraction()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_2,new Division()));
		 x_delta_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_2,new Multiplication()));
		 
		 
		//add thresholds for distance features
		double min_threshold=-12;
		double max_threshold=27;
        
		HashMap<String,List<Feature>> additional_features=new HashMap<String, List<Feature>>();
		double jump=4;
			List<Feature> fts=new ArrayList<Feature>();
			for(ComplexConjunction c:x_delta_complex_conjunctions){
				for(double i=min_threshold;i<max_threshold;i+=jump){
				fts.add(new OperatorFeature(c, new InBetween(i-jump,i),new Average()));
				fts.add(new OperatorFeature(c, new InBetween(i-jump,i),new Min()));
				fts.add(new OperatorFeature(c, new InBetween(i-jump,i),new Max()));
				fts.add(new OperatorFeature(c, new Bigger(i),new Max()));
				fts.add(new OperatorFeature(c, new Bigger(i),new Min()));
				fts.add(new OperatorFeature(c, new Bigger(i),new Average()));
				fts.add(new OperatorFeature(c, new Smaller(i),new Max()));
				fts.add(new OperatorFeature(c, new Smaller(i),new Min()));
				fts.add(new OperatorFeature(c, new Smaller(i),new Average()));
				}
			}
		tmp.addAll(fts);
		return tmp;

	}
	
	
}
