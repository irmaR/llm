package learning;

import hybrid.comparators.Bigger;
import hybrid.comparators.InBetween;
import hybrid.comparators.Smaller;
import hybrid.featureGenerator.ComplexConjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.Renaming;
import hybrid.features.Average;
import hybrid.features.Feature;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class next_x_ExtraFeatureCreator {


	public static List<Feature> getListOfFeatures(Atom at,NetworkInfo ntw) throws ConjunctionConstructionProblem{
		List<Feature> tmp=new ArrayList<>();
		Atom current_x=ntw.getPredicateNameToAtom().get("current_x");
		Atom moved_object=ntw.getPredicateNameToAtom().get("moved");

		//logvar of main object
		Atom main_object=ntw.getPredicateNameToAtom().get("moved");
		Renaming ren_object=new Renaming();
		Logvar classical_logvar=main_object.getArgument(0);
		Logvar renamed_logvar=new Logvar("Obj_1",main_object.getArgument(0).getType());
		ren_object.addRenaming(main_object.getArgument(0).getType(),renamed_logvar );
		Atom moved_object1=main_object.applyRenaming(ren_object).get(0);

		//dimension randvar value tests
		//rename dimension
		Logvar renamed_logvar_dim=new Logvar("Obj_1",main_object.getArgument(0).getType());
		ren_object=new Renaming();
		ren_object.addRenaming(ntw.getPredicateNameToAtom().get("dim").getArgument(0).getType(),renamed_logvar );
		Atom dimension_renamed=ntw.getPredicateNameToAtom().get("dim").applyRenaming(ren_object).get(0);
		TestRandvarValue dimension_small=new TestRandvarValue(ntw.getPredicateNameToAtom().get("dim").getPredicate(), dimension_renamed.getArguments(), new StringValue("small"));
		TestRandvarValue dimension_medium=new TestRandvarValue(ntw.getPredicateNameToAtom().get("dim").getPredicate(), dimension_renamed.getArguments(), new StringValue("medium"));
		TestRandvarValue dimension_big=new TestRandvarValue(ntw.getPredicateNameToAtom().get("dim").getPredicate(), dimension_renamed.getArguments(), new StringValue("big"));

		ren_object=new Renaming();
		ren_object.addRenaming(main_object.getArgument(0).getType(), new Logvar("Obj_1",main_object.getArgument(0).getType()));
		System.out.println(current_x);
		Atom current_x_1=current_x.applyRenaming(ren_object).get(0);

		//add specific context
		Literal[] context_2=new Literal[]{new PosLiteral(moved_object1)};
		Literal[] context_3=new Literal[]{new PosLiteral(moved_object)};
		Literal[] context_5_small_dimension=new Literal[]{new PosLiteral(moved_object),new PosLiteral(dimension_small)};
		Literal[] context_6_medium_dimension=new Literal[]{new PosLiteral(moved_object),new PosLiteral(dimension_medium)};
		Literal[] context_7_big_dimension=new Literal[]{new PosLiteral(moved_object),new PosLiteral(dimension_big)};

		HashMap<String,List<ComplexConjunction>> complex_conjunctions=new HashMap<String,List<ComplexConjunction>>();
		List<ComplexConjunction> next_x_complex_conjunctions=new ArrayList<ComplexConjunction>();


		//features: x_delta(Obj) | x_pos_before(Obj) op x_pos_before(Obj1) <- Obj=main_object,Obj1=secondary_object 
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_5_small_dimension,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_5_small_dimension,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_5_small_dimension,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_5_small_dimension,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_6_medium_dimension,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_6_medium_dimension,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_6_medium_dimension,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_6_medium_dimension,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_7_big_dimension,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_7_big_dimension,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_7_big_dimension,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_7_big_dimension,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		//features: x_delta(Obj) | threshold 1 < x_pos_before(Obj) op x_pos_before(Obj1) < threshold 2<- Obj=secondary_object,Obj1=main_bject 
		//x_delta_complex_conjunctions.add(new ComplexConjunction(x_delta,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_3,new Addition()));
		// x_delta_complex_conjunctions.add(new ComplexConjunction(x_delta,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_3,new Subtraction()));
		//x_delta_complex_conjunctions.add(new ComplexConjunction(x_delta,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_3,new Division()));
		// x_delta_complex_conjunctions.add(new ComplexConjunction(x_delta,new PosLiteral(ntw.getPredicateNameToAtom().get("x_pos_b")),new PosLiteral(x_pos_b1),context_3,new Multiplication()));
		//add thresholds for distance features
		double min_threshold=-3;
		double max_threshold=3;

		HashMap<String,List<Feature>> additional_features=new HashMap<String, List<Feature>>();
		double jump=0.05;
		List<Feature> fts=new ArrayList<Feature>();
		for(ComplexConjunction c:next_x_complex_conjunctions){
			for(double i=min_threshold;i<max_threshold;i+=jump){
				fts.add(new OperatorFeature(c, new InBetween(i-jump,i),new Average()));
				//fts.add(new Operator_Feature(c, new InBetween(i-jump,i),new Min()));
				//fts.add(new Operator_Feature(c, new InBetween(i-jump,i),new Max()));
				//fts.add(new Operator_Feature(c, new Bigger(i),new Max()));
				//fts.add(new Operator_Feature(c, new Bigger(i),new Min()));
				//fts.add(new Operator_Feature(c, new Bigger(i),new Average()));
				//fts.add(new Operator_Feature(c, new Smaller(i),new Max()));
				//fts.add(new Operator_Feature(c, new Smaller(i),new Min()));
				//fts.add(new Operator_Feature(c, new Smaller(i),new Average()));
			}
		}
		tmp.addAll(fts);
		return tmp;

	}

	public static List<Feature> getListOfFeatures_general_dimension_dimension_continuous(Atom at,NetworkInfo ntw) throws ConjunctionConstructionProblem{
		List<Feature> tmp=new ArrayList<>();
		Atom current_x=ntw.getPredicateNameToAtom().get("current_x");
		Atom moved_object=ntw.getPredicateNameToAtom().get("moved");

		//logvar of main object
		Atom main_object=ntw.getPredicateNameToAtom().get("moved");
		Renaming ren_object=new Renaming();
		Logvar classical_logvar=main_object.getArgument(0);
		Logvar renamed_logvar=new Logvar("Obj_1",main_object.getArgument(0).getType());
		ren_object.addRenaming(main_object.getArgument(0).getType(),renamed_logvar );
		Atom moved_object1=main_object.applyRenaming(ren_object).get(0);

		ren_object=new Renaming();
		ren_object.addRenaming(main_object.getArgument(0).getType(), new Logvar("Obj_1",main_object.getArgument(0).getType()));
		Atom current_x_1=current_x.applyRenaming(ren_object).get(0);

		//add specific context
		Literal[] context_2=new Literal[]{new PosLiteral(moved_object1)};
		Literal[] context_3=new Literal[]{new PosLiteral(moved_object)};

		HashMap<String,List<ComplexConjunction>> complex_conjunctions=new HashMap<String,List<ComplexConjunction>>();
		List<ComplexConjunction> next_x_complex_conjunctions=new ArrayList<ComplexConjunction>();


		//features: x_delta(Obj) | x_pos_before(Obj) op x_pos_before(Obj1) <- Obj=main_object,Obj1=secondary_object 
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		double min_threshold=-3;
		double max_threshold=3;

		HashMap<String,List<Feature>> additional_features=new HashMap<String, List<Feature>>();
		double jump=0.25;
		List<Feature> fts=new ArrayList<Feature>();
		for(ComplexConjunction c:next_x_complex_conjunctions){
			for(double i=min_threshold;i<max_threshold;i+=jump){
				fts.add(new OperatorFeature(c, new InBetween(i-jump,i),new Average()));
				//fts.add(new Operator_Feature(c, new InBetween(i-jump,i),new Min()));
				//fts.add(new Operator_Feature(c, new InBetween(i-jump,i),new Max()));
				//fts.add(new Operator_Feature(c, new Bigger(i),new Max()));
				//fts.add(new Operator_Feature(c, new Bigger(i),new Min()));
				//fts.add(new Operator_Feature(c, new Bigger(i),new Average()));
				//fts.add(new Operator_Feature(c, new Smaller(i),new Max()));
				//fts.add(new Operator_Feature(c, new Smaller(i),new Min()));
				//fts.add(new Operator_Feature(c, new Smaller(i),new Average()));
			}
		}
		tmp.addAll(fts);
		return tmp;

	}


	public static List<Feature> getListOfFeatures_general_dimension(Atom at,NetworkInfo ntw) throws ConjunctionConstructionProblem{
		List<Feature> tmp=new ArrayList<>();
		Atom current_x=ntw.getPredicateNameToAtom().get("current_x");
		Atom current_z=ntw.getPredicateNameToAtom().get("current_z");
		Atom moved_object=ntw.getPredicateNameToAtom().get("moved");

		//logvar of main object
		Atom main_object=ntw.getPredicateNameToAtom().get("moved");
		Renaming ren_object=new Renaming();
		Logvar classical_logvar=main_object.getArgument(0);
		Logvar renamed_logvar=new Logvar("Obj_1",main_object.getArgument(0).getType());
		ren_object.addRenaming(main_object.getArgument(0).getType(),renamed_logvar );
		Atom moved_object1=main_object.applyRenaming(ren_object).get(0);

		//dimension randvar value tests
		//rename dimension
		Logvar renamed_logvar_dim=new Logvar("Obj_1",main_object.getArgument(0).getType());
		ren_object=new Renaming();
		ren_object.addRenaming(ntw.getPredicateNameToAtom().get("dim").getArgument(0).getType(),renamed_logvar );
		Atom dimension_renamed=ntw.getPredicateNameToAtom().get("dim").applyRenaming(ren_object).get(0);
		TestRandvarValue dimension_small=new TestRandvarValue(ntw.getPredicateNameToAtom().get("dim").getPredicate(), dimension_renamed.getArguments(), new StringValue("small"));
		TestRandvarValue dimension_medium=new TestRandvarValue(ntw.getPredicateNameToAtom().get("dim").getPredicate(), dimension_renamed.getArguments(), new StringValue("medium"));
		TestRandvarValue dimension_big=new TestRandvarValue(ntw.getPredicateNameToAtom().get("dim").getPredicate(), dimension_renamed.getArguments(), new StringValue("big"));

		ren_object=new Renaming();
		ren_object.addRenaming(main_object.getArgument(0).getType(), new Logvar("Obj_1",main_object.getArgument(0).getType()));
		System.out.println(current_x);
		Renaming ren_object_1=new Renaming();
		ren_object_1.addRenaming(main_object.getArgument(0).getType(), new Logvar("Obj_1",main_object.getArgument(0).getType()));

		Atom current_x_1=current_x.applyRenaming(ren_object).get(0);
		Atom current_z_1=current_z.applyRenaming(ren_object_1).get(0);

		//add specific context
		Literal[] context_2=new Literal[]{new PosLiteral(moved_object1)};
		Literal[] context_3=new Literal[]{new PosLiteral(moved_object)};
		Literal[] context_5_small_dimension=new Literal[]{new PosLiteral(moved_object1),new PosLiteral(dimension_small)};
		Literal[] context_6_medium_dimension=new Literal[]{new PosLiteral(moved_object1),new PosLiteral(dimension_medium)};
		Literal[] context_7_big_dimension=new Literal[]{new PosLiteral(moved_object1),new PosLiteral(dimension_big)};

		HashMap<String,List<ComplexConjunction>> complex_conjunctions=new HashMap<String,List<ComplexConjunction>>();
		List<ComplexConjunction> next_x_complex_conjunctions=new ArrayList<ComplexConjunction>();


		// x coordinate features: x_delta(Obj) | x_pos_before(Obj) op x_pos_before(Obj1) <- Obj=main_object,Obj1=secondary_object 
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_2,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		/*next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_3,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_5_small_dimension,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_5_small_dimension,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_5_small_dimension,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_5_small_dimension,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_6_medium_dimension,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_6_medium_dimension,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_6_medium_dimension,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_6_medium_dimension,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_7_big_dimension,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_7_big_dimension,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_7_big_dimension,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_x),new PosLiteral(current_x_1),context_7_big_dimension,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
*/


		//features  z coordinate
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_2,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_2,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_2,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_2,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		/*next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_3,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_3,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_3,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_3,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_5_small_dimension,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_5_small_dimension,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_5_small_dimension,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_5_small_dimension,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_6_medium_dimension,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_6_medium_dimension,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_6_medium_dimension,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_6_medium_dimension,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));

		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_7_big_dimension,new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_7_big_dimension,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		//next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_7_big_dimension,new Division(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
		next_x_complex_conjunctions.add(new ComplexConjunction(at,new PosLiteral(current_z),new PosLiteral(current_z_1),context_7_big_dimension,new Multiplication(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", classical_logvar, renamed_logvar)}));
*/


		//features: x_delta(Obj) | threshold 1 < x_pos_before(Obj) op x_pos_before(Obj1) < threshold 2<- Obj=secondary_object,Obj1=main_bject 
		for(ComplexConjunction c:next_x_complex_conjunctions){
			tmp.add(new OperatorFeature(c,new Average()));
			//tmp.add(new Operator_Feature(c,new Min()));
			//tmp.add(new Operator_Feature(c,new Max()));
		}
		//add thresholds for distance features
		double min_threshold=-10;
		double max_threshold=10;

		HashMap<String,List<Feature>> additional_features=new HashMap<String, List<Feature>>();
		double jump=10;
		List<Feature> fts=new ArrayList<Feature>();
		for(ComplexConjunction c:next_x_complex_conjunctions){
			for(double i=min_threshold;i<max_threshold;i+=jump){
				fts.add(new OperatorFeature(c, new InBetween(i-jump,i),new Average()));
				//fts.add(new Operator_Feature(c, new InBetween(i-jump,i),new Min()));
				//fts.add(new Operator_Feature(c, new InBetween(i-jump,i),new Max()));
				//fts.add(new Operator_Feature(c, new Bigger(i),new Max()));
				//fts.add(new Operator_Feature(c, new Bigger(i),new Min()));
				fts.add(new OperatorFeature(c, new Bigger(i),new Average()));
				//fts.add(new Operator_Feature(c, new Smaller(i),new Max()));
				//fts.add(new Operator_Feature(c, new Smaller(i),new Min()));
				fts.add(new OperatorFeature(c, new Smaller(i),new Average()));
			}
		}
		tmp.addAll(fts);
		return tmp;

	}

}
