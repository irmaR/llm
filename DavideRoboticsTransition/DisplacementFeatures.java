import hybrid.comparators.Comparator;
import hybrid.comparators.ThresholdGenerator;
import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.featureGenerator.ComplexConjunction;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.featureGenerator.GetFeatures;
import hybrid.featureGenerator.Renaming;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features.Average;
import hybrid.features.ComparisonFeature;
import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.OperatorFeature;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.LogvarRestrictionLiteral;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.TestRandvarValue;
import hybrid.operators.Subtraction;
import hybrid.queryMachine.QueryMachine;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.ArffFile;
import hybrid.querydata.QueryData;
import hybrid.querydata.QueryDataFilter;
import hybrid.utils.Bin;
import hybrid.utils.DiscretizeFeatureValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import weka.core.Instances;
import hybrid.network.*;

public class DisplacementFeatures extends GetFeatures {

	private HashMap<Feature,Value> filter;
	
	public List<Feature> getListOfFeatures(Atom at, NetworkInfo ntw,QueryMachine trainingQueryMachine,HashMap<Feature,Value> filter) throws Exception {
		this.filter=filter;
		List<Feature> tmp=new ArrayList<>();
		int nrBins=AlgorithmParameters.getDiscretization_level();
		System.out.println("NR BINS: "+nrBins);
		Atom object_current_x=ntw.getPredicateNameToAtom().get("pos_x_cur");
		Renaming ren=new Renaming();
		ren.addRenaming(object_current_x.getArgument(0).getType(),new Logvar("Obj1", object_current_x.getArgument(0).getType()));
		Atom object_current_x1=object_current_x.applyRenaming(ren).get(0);
		Atom displacement=ntw.getPredicateNameToAtom().get("disp");
		Atom contdisplacement=ntw.getPredicateNameToAtom().get("displacement");

		Renaming ren1=new Renaming();
		ren1.addRenaming(displacement.getArgument(0).getType(),new Logvar("Obj1", displacement.getArgument(0).getType()));
		Atom displacement1=displacement.applyRenaming(ren1).get(0);

		Renaming ren2=new Renaming();
		ren1.addRenaming(contdisplacement.getArgument(0).getType(),new Logvar("Obj1", contdisplacement.getArgument(0).getType()));
		Atom contdisplacement1=contdisplacement.applyRenaming(ren1).get(0);


		Renaming rena=new Renaming();

		rena.addRenaming(displacement.getArgument(0).getType(),new Logvar("Obj1", displacement.getArgument(0).getType()));
		TestRandvarValue displacement_a=new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("a"));
		TestRandvarValue displacement_b=new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("b"));
		TestRandvarValue displacement_c=new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("c"));
		TestRandvarValue displacement_d=new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("d"));
		TestRandvarValue displacement_e=new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("e"));
		TestRandvarValue displacement_f=new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("f"));
		TestRandvarValue displacement_n=new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("n"));


		TestRandvarValue displacement_a1=new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("a"));
		TestRandvarValue displacement_b1=new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("b"));
		TestRandvarValue displacement_c1=new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("c"));
		TestRandvarValue displacement_d1=new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("d"));
		TestRandvarValue displacement_e1=new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("e"));
		TestRandvarValue displacement_f1=new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("f"));
		TestRandvarValue displacement_g1=new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("g"));
		TestRandvarValue displacement_h1=new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("h"));
		TestRandvarValue displacement_n1=new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("n"));

		Atom action=ntw.getPredicateNameToAtom().get("action");
		Renaming renact=new Renaming();
		ren1.addRenaming(action.getArgument(0).getType(),new Logvar("Obj1", action.getArgument(0).getType()));
		Atom action1=action.applyRenaming(ren1).get(0);
		TestRandvarValue action_push=new TestRandvarValue(action.getPredicate(),action.getArguments(),new StringValue("p"));
		TestRandvarValue action_tap=new TestRandvarValue(action.getPredicate(),action.getArguments(),new StringValue("t"));
		TestRandvarValue action_b=new TestRandvarValue(action.getPredicate(),action.getArguments(),new StringValue("b"));
		TestRandvarValue action_g=new TestRandvarValue(action.getPredicate(),action.getArguments(),new StringValue("g"));

		TestRandvarValue action_push1=new TestRandvarValue(action1.getPredicate(),action1.getArguments(),new StringValue("p"));
		TestRandvarValue action_tap1=new TestRandvarValue(action1.getPredicate(),action1.getArguments(),new StringValue("t"));
		TestRandvarValue action_b1=new TestRandvarValue(action1.getPredicate(),action1.getArguments(),new StringValue("b"));
		TestRandvarValue action_g1=new TestRandvarValue(action1.getPredicate(),action1.getArguments(),new StringValue("g"));


		Renaming ren3=new Renaming();
		ren.addRenaming(object_current_x.getArgument(0).getType(),new Logvar("Obj2", object_current_x.getArgument(0).getType()));
		Atom object_current_x2=object_current_x.applyRenaming(ren3).get(0);

		Atom arm_current_x=ntw.getPredicateNameToAtom().get("arm_x_cur");
		Atom arm_next_x=ntw.getPredicateNameToAtom().get("arm_x_next");

		Atom object_current_y=ntw.getPredicateNameToAtom().get("pos_y_cur");
		Atom object_current_y1=object_current_y.applyRenaming(ren).get(0);
		Atom object_current_y2=object_current_y.applyRenaming(ren2).get(0);

		Atom arm_current_y=ntw.getPredicateNameToAtom().get("arm_y_cur");
		Atom arm_next_y=ntw.getPredicateNameToAtom().get("arm_y_next");

		Atom object_current_z=ntw.getPredicateNameToAtom().get("pos_z_cur");
		Atom object_current_z1=object_current_z.applyRenaming(ren).get(0);
		Atom object_current_z2=object_current_z.applyRenaming(ren2).get(0);

		Atom arm_current_z=ntw.getPredicateNameToAtom().get("arm_z_cur");
		Atom arm_next_z=ntw.getPredicateNameToAtom().get("arm_z_next");

		HashMap<String,List<ComplexConjunction>> complex_conjunctions=new HashMap<String,List<ComplexConjunction>>();
		List<ComplexConjunction> next_x_complex_conjunctionsNoAggr=new ArrayList<ComplexConjunction>();
		List<ComplexConjunction> conjunctionsTobeAggregated=new ArrayList<ComplexConjunction>();

		Literal[] context_a=new Literal[]{new PosLiteral(new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("a")))};
		Literal[] context_b=new Literal[]{new PosLiteral(new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("b")))};
		Literal[] context_c=new Literal[]{new PosLiteral(new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("c")))};
		Literal[] context_d=new Literal[]{new PosLiteral(new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("d")))};
		Literal[] context_e=new Literal[]{new PosLiteral(new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("e")))};
		Literal[] context_f=new Literal[]{new PosLiteral(new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("f")))};
		Literal[] context_g=new Literal[]{new PosLiteral(new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("g")))};
		Literal[] context_h=new Literal[]{new PosLiteral(new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("h")))};
		Literal[] context_n=new Literal[]{new PosLiteral(new TestRandvarValue(displacement1.getPredicate(),displacement1.getArguments(),new StringValue("n")))};


		Literal[] context_action_p=new Literal[]{new PosLiteral(new TestRandvarValue(action1.getPredicate(),action1.getArguments(),new StringValue("p")))};
		Literal[] context_action_t=new Literal[]{new PosLiteral(new TestRandvarValue(action1.getPredicate(),action1.getArguments(),new StringValue("t")))};
		Literal[] context_action_g=new Literal[]{new PosLiteral(new TestRandvarValue(action1.getPredicate(),action1.getArguments(),new StringValue("g")))};
		Literal[] context_action_b=new Literal[]{new PosLiteral(new TestRandvarValue(action1.getPredicate(),action1.getArguments(),new StringValue("b")))};


		Literal[] context_a1=new Literal[]{new PosLiteral(new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("a")))};
		Literal[] context_b1=new Literal[]{new PosLiteral(new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("b")))};
		Literal[] context_c1=new Literal[]{new PosLiteral(new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("c")))};
		Literal[] context_d1=new Literal[]{new PosLiteral(new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("d")))};
		Literal[] context_e1=new Literal[]{new PosLiteral(new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("e")))};
		Literal[] context_f1=new Literal[]{new PosLiteral(new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("f")))};
		Literal[] context_g1=new Literal[]{new PosLiteral(new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("g")))};
		Literal[] context_h1=new Literal[]{new PosLiteral(new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("h")))};
		Literal[] context_n1=new Literal[]{new PosLiteral(new TestRandvarValue(displacement.getPredicate(),displacement.getArguments(),new StringValue("n")))};

		List<ComplexConjunction> contextFeatures=new ArrayList<ComplexConjunction>();
		List<ComplexConjunction> contextFeaturesNoAggr=new ArrayList<ComplexConjunction>();
		List<ComplexConjunction> contextFeaturesNoAggrTemplate=new ArrayList<ComplexConjunction>();
		List<ComplexConjunction> displacementToBeAggr=new ArrayList<ComplexConjunction>();

		//------------------------------------------------------------------------------------------
		conjunctionsTobeAggregated.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));


		if(AlgorithmParameters.getAggregatingDisplacement()){
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_a,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_b,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_c,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_d,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_e,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_f,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_g,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_h,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_n,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));

		}
		else{
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_a,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_b,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_c,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_d,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_e,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_f,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_g,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_h,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_n,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));

		}

		contextFeaturesNoAggrTemplate.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
		contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_action_p,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
		contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_action_t,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
		contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_action_g,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));
		contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_x),new PosLiteral(object_current_x1),context_action_b,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_x.getArgument(0), object_current_x1.getArgument(0))}));


		conjunctionsTobeAggregated.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));


		if(AlgorithmParameters.getAggregatingDisplacement()){
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_a,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_b,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_c,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_d,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_e,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_f,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_n,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_g,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			displacementToBeAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_h,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));

		}
		else{
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_a,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_b,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_c,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_d,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_e,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_f,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_n,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_g,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
			contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_h,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));

		}

		contextFeaturesNoAggrTemplate.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
		contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_action_p,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
		contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_action_t,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
		contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_action_g,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));
		contextFeaturesNoAggr.add(new ComplexConjunction(at,new PosLiteral(object_current_y),new PosLiteral(object_current_y1),context_action_b,new Subtraction(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", object_current_y.getArgument(0), object_current_y1.getArgument(0))}));



		HashMap<String,List<Feature>> additional_features=new HashMap<String, List<Feature>>();
		//double jump=1.0;
		List<Feature> fts=new ArrayList<Feature>();
		tmp.add(new ValueFt(new Standard_Conjunction<>(at,true,new PosLiteral(ntw.getPredicateNameToAtom().get("pos_x_cur")))));
		tmp.add(new ValueFt(new Standard_Conjunction<>(at,true,new PosLiteral(ntw.getPredicateNameToAtom().get("action")))));
		tmp.add(new ValueFt(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", action.getArgument(0), action1.getArgument(0))},new PosLiteral(action1))));

		tmp.add(new ValueFt(new Standard_Conjunction<>(at,true,new PosLiteral(ntw.getPredicateNameToAtom().get("pos_y_cur")))));
		tmp.add(new ValueFt(new Standard_Conjunction<>(at,true,new PosLiteral(ntw.getPredicateNameToAtom().get("displacement")))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(ntw.getPredicateNameToAtom().get("displacement")))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(displacement1))));
		tmp.add(new Average(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", contdisplacement.getArgument(0), contdisplacement1.getArgument(0))},new PosLiteral(contdisplacement1))));
		tmp.add(new Min(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", contdisplacement.getArgument(0), contdisplacement1.getArgument(0))},new PosLiteral(contdisplacement1))));
		tmp.add(new Max(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", contdisplacement.getArgument(0), contdisplacement1.getArgument(0))},new PosLiteral(contdisplacement1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", contdisplacement.getArgument(0), contdisplacement1.getArgument(0))},new PosLiteral(contdisplacement1))));

		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(displacement_a))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(displacement_b))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(displacement_c))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(displacement_d))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(displacement_e))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(displacement_f))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(displacement_n))));

		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(displacement))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(displacement_a1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(displacement_b1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(displacement_c1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(displacement_d1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(displacement_e1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(displacement_f1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(displacement_n1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(displacement_g1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(displacement_h1))));

		//action
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(action_push))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(action_tap))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(action_b))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new PosLiteral(action_g))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", action.getArgument(0), action1.getArgument(0))},new PosLiteral(action_push1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(action_tap1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(action_b1))));
		tmp.add(new Exist(new Standard_Conjunction<>(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", displacement.getArgument(0), displacement1.getArgument(0))},new PosLiteral(action_g1))));


		//COMPARISON FEATURES
		List<Standard_Conjunction> comparisonConjunction=new ArrayList<Standard_Conjunction>();
		comparisonConjunction.add(new Standard_Conjunction(at,true,new PosLiteral(object_current_x)));
		comparisonConjunction.add(new Standard_Conjunction(at,true,new PosLiteral(object_current_y)));
		comparisonConjunction.add(new Standard_Conjunction(at,true,new PosLiteral(contdisplacement)));
		comparisonConjunction.add(new Standard_Conjunction(at,true,new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", contdisplacement.getArgument(0), contdisplacement1.getArgument(0))},new PosLiteral(contdisplacement1)));

		//for each operator feature determine thresholds
		tmp.addAll(getDiscretizedFeatures(at,trainingQueryMachine,filter,nrBins,next_x_complex_conjunctionsNoAggr,contextFeaturesNoAggrTemplate,contextFeaturesNoAggr,conjunctionsTobeAggregated,displacementToBeAggr,comparisonConjunction));
		
		
		tmp.addAll(fts);
		List<Feature> extra_features_pos_x_next=new ArrayList<Feature>();
		List<Feature> extra_features_pos_y_next=new ArrayList<Feature>();
		List<Feature> extra_features_pos_z_next=new ArrayList<Feature>();
		if(AlgorithmParameters.getPredicate_names().contains("pos_x_next")){
			System.out.println("Generating Feature space for pos_x_next");
			List<Feature> fts1=next_arm_extra_features.getListOfFeaturesWithDisplacement(ntw.getPredicateNameToAtom().get("pos_x_next"), ntw,trainingQueryMachine);
			extra_features_pos_x_next.addAll(fts1);
			System.out.println("Finished ...");
		}
		
		if(AlgorithmParameters.getPredicate_names().contains("pos_y_next")){
			System.out.println("Generating Feature space for pos_y_next");
			extra_features_pos_y_next.addAll(next_arm_extra_features.getListOfFeaturesWithDisplacement(ntw.getPredicateNameToAtom().get("pos_y_next"), ntw,trainingQueryMachine));
			System.out.println("Finished ...");
		}
		if(AlgorithmParameters.getPredicate_names().contains("pos_z_next")){
			System.out.println("Generating Feature space for pos_z_next");
			extra_features_pos_z_next.addAll(next_arm_extra_features.getListOfFeaturesWithDisplacement(ntw.getPredicateNameToAtom().get("pos_z_next"), ntw,trainingQueryMachine));
			System.out.println("Finished ...");
		}
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
		
        List<Feature> res=fGen.generateFeatures(at, ntw.getAtomsAndEqualityConstraints());
		return res;
	}
	
	private static Collection<? extends Feature> getDiscretizedFeatures(Atom at,QueryMachine trainingQueryMachine, HashMap<Feature,Value> filter,int nrBins,List<ComplexConjunction> next_x_complex_conjunctionsNoAggr,List<ComplexConjunction> contextFeaturesNoAggrTemplate,List<ComplexConjunction> contextFeaturesNoAggr,List<ComplexConjunction> conjunctionsTobeAggregated,List<ComplexConjunction> displacementToBeAggr,List<Standard_Conjunction> comparisonConjunction) throws Exception {
		List<Feature> fts=new ArrayList<Feature>();
		DiscretizeFeatureValues discretize=new DiscretizeFeatureValues();
		ThresholdGenerator thrGenerator=new ThresholdGenerator();
		for(ComplexConjunction c:next_x_complex_conjunctionsNoAggr){
			fts.add(new OperatorFeature(c,new ValueFt()));
		}
		QueryDataFilter filterQ=new QueryDataFilter();
		
		for(ComplexConjunction c:contextFeaturesNoAggrTemplate){
			System.out.println("CONJUNCTION : "+c);
			//tmp.add(new OperatorFeature(c,new ValueFt()));
			trainingQueryMachine.setUseCache(false);
			List<Feature> ftsFilter=new ArrayList(filter.keySet());
			List<Feature> qAvgOperator=new ArrayList<Feature>();
			qAvgOperator.addAll(ftsFilter);
			qAvgOperator.add(new OperatorFeature(c, new ValueFt()));
			QueryData q=trainingQueryMachine.getQueryResults(new Dependency(at,qAvgOperator.toArray(new Feature[qAvgOperator.size()])));
			QueryData filteredValue=filterQ.filterQueryData(new Dependency(at,qAvgOperator.toArray(new Feature[qAvgOperator.size()])), filter, q);
			//System.out.println(q);
			ArffFile arff=new ArffFile(filteredValue);
			Instances inst=null;
			try {
				inst=arff.getQueryDataAsARFF_instances();
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<Bin> bins=discretize.getCutPoints(inst,0,nrBins);
			System.out.println(bins);
			List<Comparator> biggeThr=thrGenerator.getThresholdsBigger(bins);
			List<Comparator> smallerThr=thrGenerator.getThresholdsSmaller(bins);
			for(Comparator comp:biggeThr){
				//fts.add(new OperatorFeature(c, comp,new ValueFt()));
				for(ComplexConjunction c1:contextFeaturesNoAggr){
					fts.add(new OperatorFeature(c1, comp,new ValueFt()));
				}
			}
			for(Comparator comp:smallerThr){
				//fts.add(new OperatorFeature(c, comp,new ValueFt()));
				for(ComplexConjunction c1:contextFeaturesNoAggr){
					fts.add(new OperatorFeature(c1, comp,new ValueFt()));
				}
			}

		}
		for(ComplexConjunction c:conjunctionsTobeAggregated){
			System.out.println("TO BE AGGREGATED: "+c);
			List<Feature> ftsFilter=new ArrayList(filter.keySet());
			List<Feature> qAvgOperator=new ArrayList<Feature>();
			qAvgOperator.addAll(ftsFilter);
			qAvgOperator.add(new OperatorFeature(c, new Average()));
			QueryData qavg=trainingQueryMachine.getQueryResults(new Dependency(at,qAvgOperator.toArray(new Feature[qAvgOperator.size()])));
			
			List<Feature> qqminOperator=new ArrayList<Feature>();
			qqminOperator.addAll(ftsFilter);
			qqminOperator.add(new OperatorFeature(c, new Average()));
			QueryData qmin=trainingQueryMachine.getQueryResults(new Dependency(at,qqminOperator.toArray(new Feature[qAvgOperator.size()])));
			
			
			List<Feature> qmaxOperator=new ArrayList<Feature>();
			qmaxOperator.addAll(ftsFilter);
			qmaxOperator.add(new OperatorFeature(c, new Average()));
			QueryData qmax=trainingQueryMachine.getQueryResults(new Dependency(at,qmaxOperator.toArray(new Feature[qAvgOperator.size()])));
			
			
			QueryData filteredAvg=filterQ.filterQueryData(new Dependency(at,qAvgOperator.toArray(new Feature[qAvgOperator.size()])), filter, qavg);
			QueryData filteredMin=filterQ.filterQueryData(new Dependency(at,qqminOperator.toArray(new Feature[qAvgOperator.size()])), filter, qmin);
			QueryData filteredMax=filterQ.filterQueryData(new Dependency(at,qqminOperator.toArray(new Feature[qAvgOperator.size()])), filter, qmax);
			//System.out.println("Filtered average: "+filteredAvg);
			
			fts.add(new OperatorFeature(c,new Min()));
			fts.add(new OperatorFeature(c,new Max()));
			fts.add(new OperatorFeature(c,new Average()));
			System.out.println("Adding Average");
			fts.addAll(getAllAggregatesAverage(filteredAvg,c,nrBins,displacementToBeAggr));
			System.out.println("Adding Min");
			fts.addAll(getAllAggregatesMin(filteredMin,c,nrBins,displacementToBeAggr));
			System.out.println("Adding Max");
			fts.addAll(getAllAggregatesMax(filteredMax,c,nrBins,displacementToBeAggr));
		}

		//adding comparison features
		for(Standard_Conjunction c:comparisonConjunction){
			List<Feature> ftsFilter=new ArrayList(filter.keySet());
			List<Feature> qmaxOperator=new ArrayList<Feature>();
			qmaxOperator.addAll(ftsFilter);
			qmaxOperator.add(new ValueFt(c));
			QueryData q=trainingQueryMachine.getQueryResults(new Dependency(at,qmaxOperator.toArray(new Feature[qmaxOperator.size()])));
			QueryData filteredValue=filterQ.filterQueryData(new Dependency(at,qmaxOperator.toArray(new Feature[qmaxOperator.size()])), filter, q);
			//System.out.println(q);
			ArffFile arff=new ArffFile(filteredValue);
			Instances inst=null;
			try {
				inst=arff.getQueryDataAsARFF_instances();
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<Bin> bins=discretize.getCutPoints(inst,0,nrBins);
			//System.out.println(q.getDep());
			//System.out.println("Bins: "+bins);
			List<Comparator> biggeThr=thrGenerator.getThresholdsBigger(bins);
			List<Comparator> smallerThr=thrGenerator.getThresholdsSmaller(bins);
			//List<Comparator> inBetweenThr=thrGenerator.getThresholdsInBetween(bins);
			for(Comparator comp:biggeThr){
				fts.add(new ComparisonFeature(c, comp,new ValueFt()));
			}
			for(Comparator comp:smallerThr){
				fts.add(new ComparisonFeature(c, comp,new ValueFt()));
			}
			/*for(Comparator comp:inBetweenThr){
						fts.add(new ComparisonFeature(c, comp,new ValueFt()));
					}*/
		}
		return fts;

	}
	
	private static List<Feature> getAllAggregatesAverage(QueryData q, ComplexConjunction c,int nrBins, List<ComplexConjunction> contextFeatures) throws Exception {
		List<Feature> fts=new ArrayList<Feature>();
		ArffFile arff=new ArffFile(q);
		DiscretizeFeatureValues discretize=new DiscretizeFeatureValues();
		ThresholdGenerator thrGenerator=new ThresholdGenerator();
		Instances inst=null;
		try {
			inst=arff.getQueryDataAsARFF_instances();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Bin> bins=discretize.getCutPoints(inst,0,nrBins);
		System.out.println("CONJUNCTION : "+c);
		System.out.println("AVERAGE CONJ: "+bins);
		//System.out.println(q.getDep());
		//System.out.println("Bins: "+bins);
		List<Comparator> biggeThr=thrGenerator.getThresholdsBigger(bins);
		List<Comparator> smallerThr=thrGenerator.getThresholdsSmaller(bins);
		//List<Comparator> inBetweenThr=thrGenerator.getThresholdsInBetween(bins);
		for(Comparator comp:biggeThr){
			fts.add(new OperatorFeature(c, comp,new Average()));
			for(ComplexConjunction c1:contextFeatures){
				fts.add(new OperatorFeature(c1, comp,new Average()));

			}
		}
		for(Comparator comp:smallerThr){
			fts.add(new OperatorFeature(c, comp,new Average()));
			for(ComplexConjunction c1:contextFeatures){
				fts.add(new OperatorFeature(c1, comp,new Average()));
			}
		}
		return fts;
	}

	private static List<Feature> getAllAggregatesMax(QueryData q, ComplexConjunction c, int nrBins, List<ComplexConjunction> arrayList) throws Exception {
		List<Feature> fts=new ArrayList<Feature>();
		ArffFile arff=new ArffFile(q);
		DiscretizeFeatureValues discretize=new DiscretizeFeatureValues();
		ThresholdGenerator thrGenerator=new ThresholdGenerator();
		Instances inst=null;
		try {
			inst=arff.getQueryDataAsARFF_instances();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Bin> bins=discretize.getCutPoints(inst,0,nrBins);
		//System.out.println("CONJUNCTION : "+c);
		//System.out.println("MAX CONJ: "+bins);
		//System.out.println(q.getDep());
		//System.out.println("Bins: "+bins);
		List<Comparator> biggeThr=thrGenerator.getThresholdsBigger(bins);
		List<Comparator> smallerThr=thrGenerator.getThresholdsSmaller(bins);
		//List<Comparator> inBetweenThr=thrGenerator.getThresholdsInBetween(bins);
		for(Comparator comp:biggeThr){
			fts.add(new OperatorFeature(c, comp,new Max()));
			for(ComplexConjunction c1:arrayList){
				fts.add(new OperatorFeature(c1, comp,new Min()));
			}
		}
		for(Comparator comp:smallerThr){
			fts.add(new OperatorFeature(c, comp,new Max()));
			for(ComplexConjunction c1:arrayList){
				fts.add(new OperatorFeature(c1, comp,new Min()));
			}
		}
		/*for(Comparator comp:inBetweenThr){
			fts.add(new OperatorFeature(c, comp,new Max()));
		}*/
		return fts;
	}

	private static List<Feature> getAllAggregatesMin(QueryData q, ComplexConjunction c, int nrBins, List<ComplexConjunction> arrayList) throws Exception {
		List<Feature> fts=new ArrayList<Feature>();
		ArffFile arff=new ArffFile(q);
		DiscretizeFeatureValues discretize=new DiscretizeFeatureValues();
		ThresholdGenerator thrGenerator=new ThresholdGenerator();
		Instances inst=null;
		try {
			inst=arff.getQueryDataAsARFF_instances();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Bin> bins=discretize.getCutPoints(inst,0,nrBins);
		System.out.println("CONJUNCTION : "+c);
		System.out.println("MIN CONJ: "+bins);
		System.out.println(q.getDep());
		System.out.println("Bins: "+bins);
		List<Comparator> biggeThr=thrGenerator.getThresholdsBigger(bins);
		List<Comparator> smallerThr=thrGenerator.getThresholdsSmaller(bins);

		for(Comparator comp:biggeThr){
			fts.add(new OperatorFeature(c, comp,new Min()));
			for(ComplexConjunction c1:arrayList){
				fts.add(new OperatorFeature(c1, comp,new Min()));
			}
		}
		for(Comparator comp:smallerThr){
			fts.add(new OperatorFeature(c, comp,new Min()));
			for(ComplexConjunction c1:arrayList){
				fts.add(new OperatorFeature(c1, comp,new Min()));
			}
		}

		System.out.println("Made : "+fts.size()+" features");
		return fts;
	}



}
