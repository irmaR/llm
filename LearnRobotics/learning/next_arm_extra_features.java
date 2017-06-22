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
import hybrid.features.ComparisonFeatureContinuousOutput;
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
import hybrid.network.StringValue;
import hybrid.network.TestRandvarValue;
import hybrid.operators.Addition;
import hybrid.operators.Division;
import hybrid.operators.Multiplication;
import hybrid.operators.Subtraction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class next_arm_extra_features {
	public static List<Feature> getListOfFeatures(Atom at,NetworkInfo ntw) throws ConjunctionConstructionProblem, FeatureTypeException{
		List<Feature> tmp=new ArrayList<>();
		Atom object_current_x=ntw.getPredicateNameToAtom().get("pos_x_cur");
		Renaming ren=new Renaming();
		ren.addRenaming(object_current_x.getArgument(0).getType(),new Logvar("Obj1", object_current_x.getArgument(0).getType()));
		Atom object_current_x1=object_current_x.applyRenaming(ren).get(0);

		Atom arm_current_x=ntw.getPredicateNameToAtom().get("arm_x_cur");
		Atom arm_next_x=ntw.getPredicateNameToAtom().get("arm_x_next");

		Atom object_current_y=ntw.getPredicateNameToAtom().get("pos_y_cur");
		Atom object_current_y1=object_current_y.applyRenaming(ren).get(0);
		Atom arm_current_y=ntw.getPredicateNameToAtom().get("arm_y_cur");
		Atom arm_next_y=ntw.getPredicateNameToAtom().get("arm_y_next");

		Atom object_current_z=ntw.getPredicateNameToAtom().get("pos_z_cur");
		Atom object_current_z1=object_current_z.applyRenaming(ren).get(0);
		Atom arm_current_z=ntw.getPredicateNameToAtom().get("arm_z_cur");
		Atom arm_next_z=ntw.getPredicateNameToAtom().get("arm_z_next");

		HashMap<String,List<ComplexConjunction>> complex_conjunctions=new HashMap<String,List<ComplexConjunction>>();
		List<ComplexConjunction> next_x_complex_conjunctions=new ArrayList<ComplexConjunction>();
		Literal[] context_2=new Literal[]{new PosLiteral(object_current_x),new PosLiteral(object_current_x1)};

		//features: x_delta(Obj) | x_pos_before(Obj) op x_pos_before(Obj1) <- Obj=main_object,Obj1=secondary_object 
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(arm_current_x),new Addition()));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(arm_next_x),new Subtraction()));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(arm_current_x),new Subtraction()));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(arm_current_x),new Division()));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(arm_current_x),new Multiplication()));

		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(arm_current_y),new Addition()));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(arm_next_y),new Subtraction()));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(arm_current_y),new Subtraction()));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(arm_current_y),new Division()));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(arm_current_y),new Multiplication()));

		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_z),new PosLiteral(arm_current_z),new Addition()));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_z),new PosLiteral(arm_next_z),new Subtraction()));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_z),new PosLiteral(arm_current_z),new Subtraction()));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_z),new PosLiteral(object_current_z1),new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_z.getArgument(0), object_current_z1.getArgument(0))}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_z),new PosLiteral(arm_current_z),new Division()));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(object_current_z),new PosLiteral(arm_current_z),new Multiplication()));




		double min_threshold=-1.5;
		double max_threshold=1.5;

		HashMap<String,List<Feature>> additional_features=new HashMap<String, List<Feature>>();
		double jump=0.05;
		//double jump=1.0;
		List<Feature> fts=new ArrayList<Feature>();

		//add hybrid features
		for(ComplexConjunction c:next_x_complex_conjunctions){
			fts.add(new OperatorFeature(c,new Average()));
		}


		fts.add(new Average(new Standard_Conjunction(at,true,new PosLiteral(object_current_x))));
		//fts.add(new Min(new Standard_Conjunction(at,true,new PosLiteral(object_current_x))));
		//fts.add(new Max(new Standard_Conjunction(at,true,new PosLiteral(object_current_x))));

		fts.add(new Average(new Standard_Conjunction(at,true,new PosLiteral(object_current_y))));
		//fts.add(new Min(new Standard_Conjunction(at,true,new PosLiteral(object_current_y))));
		//fts.add(new Max(new Standard_Conjunction(at,true,new PosLiteral(object_current_y))));

		fts.add(new Average(new Standard_Conjunction(at,true,new PosLiteral(object_current_z))));
		//fts.add(new Min(new Standard_Conjunction(at,true,new PosLiteral(object_current_z))));
		//fts.add(new Max(new Standard_Conjunction(at,true,new PosLiteral(object_current_z))));


		//adding comparison features only
		int jump_finger=2000;
		for(double i=-30;i<6000;i+=jump_finger){
			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(ntw.getPredicateNameToAtom().get("arm_finger_cur"))),new Bigger(i),new Average()));
			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(ntw.getPredicateNameToAtom().get("arm_finger_cur"))),new InBetween(i-jump_finger,i),new Average()));
			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(ntw.getPredicateNameToAtom().get("arm_finger_cur"))),new Smaller(i),new Average()));
			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(ntw.getPredicateNameToAtom().get("arm_finger_next"))),new Bigger(i),new Average()));
			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(ntw.getPredicateNameToAtom().get("arm_finger_next"))),new InBetween(i-jump_finger,i),new Average()));
			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(ntw.getPredicateNameToAtom().get("arm_finger_next"))),new Smaller(i),new Average()));

		}
		for(double i=min_threshold;i<=max_threshold;i+=jump){
			BigDecimal bd = new BigDecimal(i).setScale(2, RoundingMode.HALF_EVEN);
			double thr = bd.doubleValue();
			for(ComplexConjunction c:next_x_complex_conjunctions){
				fts.add(new OperatorFeature(c, new Bigger(thr),new Average()));
				fts.add(new OperatorFeature(c, new Smaller(thr),new Average()));
			}
			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(object_current_x)),new Bigger(thr),new Average()));
			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(object_current_y)),new Bigger(thr),new Average()));
			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(object_current_z)),new Bigger(thr),new Average()));


			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(object_current_x)),new Smaller(thr),new Average()));
			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(object_current_y)),new Smaller(thr),new Average()));
			fts.add(new ComparisonFeature(new Standard_Conjunction(at,true,new PosLiteral(object_current_z)),new Smaller(thr),new Average()));
		}

		tmp.addAll(fts);
		return tmp;

	}
}

