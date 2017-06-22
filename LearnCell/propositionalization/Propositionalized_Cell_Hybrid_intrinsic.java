package propositionalization;

import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Feature;
import hybrid.features.ValueFt;
import hybrid.interpretations.NoCycles;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.RelationType;
import hybrid.network.Type;

import java.util.*;

public class Propositionalized_Cell_Hybrid_intrinsic {
}
/*
	NetworkInfo ntw;

	public HashMap<Predicate, Dependency> getDependenciesForPropositionalizations(int i) throws ConjunctionConstructionProblem {
		HashMap<Predicate,Dependency> dep_for_propositionalization=new HashMap<Predicate, Dependency>();

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


		Atom cell_oldpole=new Atom(cell_oldpole_pred, new Logvar[]{cell});
		Atom cell_oldpole1=new Atom(cell_oldpole_pred, new Logvar[]{cell1});

		Atom parent=new Atom(parent_pred, new Logvar[]{cell,cell1});
		parent.setRelationType(RelationType.INTERNAL);

		Atom cell_age=new Atom(cell_age_pred, new Logvar[]{cell});
		Atom cell_age1=new Atom(cell_age_pred, new Logvar[]{cell1});

		Atom cell_doublingtime=new Atom(cell_doublingtime_pred, new Logvar[]{cell});
		Atom cell_doublingtime1=new Atom(cell_doublingtime_pred, new Logvar[]{cell1});

		Atom cell_lengthatbirth=new Atom(cell_lengthatbirth_pred, new Logvar[]{cell});
		Atom cell_lengthatbirth1=new Atom(cell_lengthatbirth_pred, new Logvar[]{cell1});

		Atom cell_avglength=new Atom(cell_avglength_pred, new Logvar[]{cell});
		Atom cell_avglength1=new Atom(cell_avglength_pred, new Logvar[]{cell1});

		Atom cell_lengthgrowthrate=new Atom(cell_lengthgrowthrate_pred, new Logvar[]{cell});
		Atom cell_lengthgrowthrate1=new Atom(cell_lengthgrowthrate_pred, new Logvar[]{cell1});

		this.ntw=new NetworkInfo(new Atom[]{parent,cell_oldpole,cell_age,cell_doublingtime,cell_avglength,cell_lengthatbirth,cell_lengthgrowthrate},new Type[]{cells});

		//parent
		ValueFt val_cell_age_Cell=new ValueFt(new Conjunction(parent,new PosLiteral(cell_age)));
		ValueFt val_cell_age_Cell1=new ValueFt(new Conjunction(parent,new PosLiteral(cell_age1)));
		ValueFt val_cell_doublingtime_Cell=new ValueFt(new Conjunction(parent,new PosLiteral(cell_doublingtime)));
		ValueFt val_cell_doublingtime_Cell1=new ValueFt(new Conjunction(parent,new PosLiteral(cell_doublingtime1)));
		ValueFt val_cell_lengthatbirth_Cell=new ValueFt(new Conjunction(parent,new PosLiteral(cell_lengthatbirth)));
		ValueFt val_cell_lengthatbirth_Cell1=new ValueFt(new Conjunction(parent,new PosLiteral(cell_lengthatbirth1)));
		ValueFt val_cell_avglength_Cell=new ValueFt(new Conjunction(parent,new PosLiteral(cell_avglength)));
		ValueFt val_cell_avglength_Cell1=new ValueFt(new Conjunction(parent,new PosLiteral(cell_avglength1)));
		ValueFt val_cell_lengthgrowthrate_Cell=new ValueFt(new Conjunction(parent,new PosLiteral(cell_lengthgrowthrate)));
		ValueFt val_cell_lengthgrowthrate_Cell1=new ValueFt(new Conjunction(parent,new PosLiteral(cell_lengthgrowthrate1)));
		ValueFt cell_oldpole_Cell=new ValueFt(new Conjunction(parent,new PosLiteral(cell_oldpole)));
		ValueFt cell_oldpole_Cell1=new ValueFt(new Conjunction(parent,new PosLiteral(cell_oldpole1)));
		List<Feature> parent_features=new ArrayList<Feature>();
		parent_features.add(val_cell_age_Cell);
		parent_features.add(val_cell_age_Cell1);
		parent_features.add(val_cell_doublingtime_Cell);
		parent_features.add(val_cell_doublingtime_Cell1);
		parent_features.add(val_cell_lengthatbirth_Cell);
		parent_features.add(val_cell_lengthatbirth_Cell1);
		parent_features.add(val_cell_avglength_Cell);
		parent_features.add(val_cell_avglength_Cell1);
		parent_features.add(val_cell_lengthgrowthrate_Cell);
		parent_features.add(val_cell_lengthgrowthrate_Cell1);
		parent_features.add(cell_oldpole_Cell);
		parent_features.add(cell_oldpole_Cell1);
		Dependency dep_parent=new Dependency(parent,parent_features.toArray(new Feature[parent_features.size()]));
		dep_for_propositionalization.put(parent.getPredicate(), dep_parent);


		//cell_age
		ValueFt val_cell_doublingtime_Cell_age=new ValueFt(new Conjunction(cell_age,new PosLiteral(cell_doublingtime)));
		ValueFt val_cell_avglength_age=new ValueFt(new Conjunction(cell_age,new PosLiteral(cell_avglength)));
		ValueFt val_cell_lengthatbirth_age=new ValueFt(new Conjunction(cell_age,new PosLiteral(cell_lengthatbirth)));
		ValueFt val_cell_lengthgrowthrate_age=new ValueFt(new Conjunction(cell_age,new PosLiteral(cell_lengthgrowthrate)));
		ValueFt val_cell_oldpole_age=new ValueFt(new Conjunction(cell_age,new PosLiteral(cell_oldpole)));
		List<Feature> cell_age_features=new ArrayList<Feature>();
		cell_age_features.add(val_cell_doublingtime_Cell_age);
		cell_age_features.add(val_cell_avglength_age);
		cell_age_features.add(val_cell_lengthatbirth_age);
		cell_age_features.add(val_cell_lengthgrowthrate_age);
		cell_age_features.add(val_cell_oldpole_age);
		Dependency dep_cell_age=new Dependency(cell_age,cell_age_features.toArray(new Feature[cell_age_features.size()]));
		dep_for_propositionalization.put(cell_age.getPredicate(), dep_cell_age);


		//cell_doubling_time
		ValueFt val_cell_age_Cell_dt=new ValueFt(new Conjunction(cell_doublingtime,new PosLiteral(cell_age)));
		ValueFt val_cell_avglength_Cell_dt=new ValueFt(new Conjunction(cell_doublingtime,new PosLiteral(cell_avglength)));
		ValueFt val_cell_lengthatbirth_Cell_dt=new ValueFt(new Conjunction(cell_doublingtime,new PosLiteral(cell_lengthatbirth)));
		ValueFt val_cell_lengthgrowthrate_Cell_dt=new ValueFt(new Conjunction(cell_doublingtime,new PosLiteral(cell_lengthgrowthrate)));
		ValueFt val_cell_oldpole_Cell_dt=new ValueFt(new Conjunction(cell_doublingtime,new PosLiteral(cell_oldpole)));

		List<Feature> cell_doublingtime_features=new ArrayList<Feature>();
		cell_doublingtime_features.add(val_cell_age_Cell_dt);
		cell_doublingtime_features.add(val_cell_avglength_Cell_dt);
		cell_doublingtime_features.add(val_cell_lengthatbirth_Cell_dt);
		cell_doublingtime_features.add(val_cell_lengthgrowthrate_Cell_dt);
		cell_doublingtime_features.add(val_cell_oldpole_Cell_dt);
		Dependency dep_cell_doubling_time=new Dependency(cell_doublingtime,cell_doublingtime_features.toArray(new Feature[cell_doublingtime_features.size()]));
		dep_for_propositionalization.put(cell_doublingtime.getPredicate(), dep_cell_doubling_time);

		//cell_length_atbirth
		ValueFt val_cell_age_Cell_lab=new ValueFt(new Conjunction(cell_lengthatbirth,new PosLiteral(cell_age)));
		ValueFt val_cell_doublingtime_Cell_lab=new ValueFt(new Conjunction(cell_lengthatbirth,new PosLiteral(cell_doublingtime)));
		ValueFt val_cell_avglength_Cell_lab=new ValueFt(new Conjunction(cell_lengthatbirth,new PosLiteral(cell_avglength)));
		ValueFt val_cell_lengthgrowthrate_Cell_lab=new ValueFt(new Conjunction(cell_lengthatbirth,new PosLiteral(cell_lengthgrowthrate)));
		ValueFt val_cell_oldpole_Cell_lab=new ValueFt(new Conjunction(cell_lengthatbirth,new PosLiteral(cell_oldpole)));

		List<Feature> cell_lengthatbirth_features=new ArrayList<Feature>();
		cell_lengthatbirth_features.add(val_cell_age_Cell_lab);
		cell_lengthatbirth_features.add(val_cell_doublingtime_Cell_lab);
		cell_lengthatbirth_features.add(val_cell_avglength_Cell_lab);
		cell_lengthatbirth_features.add(val_cell_lengthgrowthrate_Cell_lab);
		cell_lengthatbirth_features.add(val_cell_oldpole_Cell_lab);
		Dependency dep_cell_length_atbirth=new Dependency(cell_lengthatbirth,cell_lengthatbirth_features.toArray(new Feature[cell_lengthatbirth_features.size()]));
		dep_for_propositionalization.put(cell_lengthatbirth.getPredicate(), dep_cell_length_atbirth);


		//cell_avglength
		ValueFt val_cell_age_Cell_al=new ValueFt(new Conjunction(cell_avglength,new PosLiteral(cell_age)));
		ValueFt val_cell_doublingtime_Cell_al=new ValueFt(new Conjunction(cell_avglength,new PosLiteral(cell_doublingtime)));
		ValueFt val_cell_lengthatbirth_Cell_al=new ValueFt(new Conjunction(cell_avglength,new PosLiteral(cell_lengthatbirth)));
		ValueFt val_cell_lengthgrowthrate_al=new ValueFt(new Conjunction(cell_avglength,new PosLiteral(cell_lengthgrowthrate)));
		ValueFt val_cell_oldpole_al=new ValueFt(new Conjunction(cell_avglength,new PosLiteral(cell_oldpole)));

		List<Feature> cell_avglength_features=new ArrayList<Feature>();
		cell_avglength_features.add(val_cell_age_Cell_al);
		cell_avglength_features.add(val_cell_doublingtime_Cell_al);
		cell_avglength_features.add(val_cell_lengthatbirth_Cell_al);
		cell_avglength_features.add(val_cell_lengthgrowthrate_al);
		cell_avglength_features.add(val_cell_oldpole_al);

		Dependency dep_cell_avglength=new Dependency(cell_avglength,cell_avglength_features.toArray(new Feature[cell_avglength_features.size()]));
		dep_for_propositionalization.put(cell_avglength.getPredicate(), dep_cell_avglength);


		//cell_lengthgrowthrate
		ValueFt val_cell_age_Cell_gr=new ValueFt(new Conjunction(cell_lengthgrowthrate,new PosLiteral(cell_age)));
		ValueFt val_cell_doublingtime_Cell_gr=new ValueFt(new Conjunction(cell_lengthgrowthrate,new PosLiteral(cell_doublingtime)));
		ValueFt val_cell_lengthatbirth_Cell_gr=new ValueFt(new Conjunction(cell_lengthgrowthrate,new PosLiteral(cell_lengthatbirth)));
		ValueFt val_cell_avglength_Cell_gr=new ValueFt(new Conjunction(cell_lengthgrowthrate,new PosLiteral(cell_avglength)));
		ValueFt val_cell_oldpole_Cell_gr=new ValueFt(new Conjunction(cell_lengthgrowthrate,new PosLiteral(cell_oldpole)));

		List<Feature> cell_lengthgrowthrate_features=new ArrayList<Feature>();
		cell_lengthgrowthrate_features.add(val_cell_age_Cell_gr);
		cell_lengthgrowthrate_features.add(val_cell_doublingtime_Cell_gr);
		cell_lengthgrowthrate_features.add(val_cell_lengthatbirth_Cell_gr);
		cell_lengthgrowthrate_features.add(val_cell_avglength_Cell_gr);
		cell_lengthgrowthrate_features.add(val_cell_oldpole_Cell_gr);
		Dependency dep_cell_lengthgrowthrate=new Dependency(cell_lengthgrowthrate,cell_lengthgrowthrate_features.toArray(new Feature[cell_lengthgrowthrate_features.size()]));
		dep_for_propositionalization.put(cell_lengthgrowthrate.getPredicate(), dep_cell_lengthgrowthrate);


		//cell_oldpole
		ValueFt val_cell_age_Cell_op=new ValueFt(new Conjunction(cell_oldpole,new PosLiteral(cell_age)));
		ValueFt val_cell_doublingtime_Cell_op=new ValueFt(new Conjunction(cell_oldpole,new PosLiteral(cell_doublingtime)));
		ValueFt val_cell_lengthatbirth_Cell_op=new ValueFt(new Conjunction(cell_oldpole,new PosLiteral(cell_lengthatbirth)));
		ValueFt val_cell_avglength_Cell_op=new ValueFt(new Conjunction(cell_oldpole,new PosLiteral(cell_avglength)));
		ValueFt val_cell_lengthgrowthrate_Cell_op=new ValueFt(new Conjunction(cell_oldpole,new PosLiteral(cell_lengthgrowthrate)));

		List<Feature> cell_oldpole_features=new ArrayList<Feature>();
		cell_oldpole_features.add(val_cell_age_Cell_op);
		cell_oldpole_features.add(val_cell_doublingtime_Cell_op);
		cell_oldpole_features.add(val_cell_lengthatbirth_Cell_op);
		cell_oldpole_features.add(val_cell_avglength_Cell_op);
		cell_oldpole_features.add(val_cell_lengthgrowthrate_Cell_op);
		Dependency dep_cell_oldpole=new Dependency(cell_oldpole,cell_oldpole_features.toArray(new Feature[cell_oldpole_features.size()]));
		dep_for_propositionalization.put(cell_oldpole.getPredicate(), dep_cell_oldpole);


		return dep_for_propositionalization;
	}

	public NetworkInfo getNtwInfo() {
		return this.ntw;
	}

}
*/