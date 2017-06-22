import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Propositionalized_PKDD_Hybrid_Full extends PropositionalNetworkCreator{

	@Override
	public HashMap<Predicate, Dependency> getDependenciesForPropositionalizations(
			int i) throws ConjunctionConstructionProblem, FeatureTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NetworkInfo getNtwInfo() {
		// TODO Auto-generated method stub
		return null;
	}
}
/*

	NetworkInfo ntwInfo;


	public HashMap<Predicate,Dependency> getDependenciesForPropositionalizations(int subsampling_ratio) throws ConjunctionConstructionProblem, FeatureTypeException{
		HashMap<Predicate,Dependency> dep_for_propositionalization=new HashMap<Predicate, Dependency>();
		FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(3, 2);

		PKDDHybridNetwork hybrid_university=new PKDDHybridNetwork();
		NetworkInfo ntw=hybrid_university.getHybridPKDD(1);
		this.ntwInfo=ntw;
		//Atoms
		Atom clientDistrict=ntwInfo.getAtom("clientDistrict");
		Atom gender=ntwInfo.getAtom("gender");
		Atom hasLoan=ntwInfo.getAtom("hasLoan");
		Atom loanAmount=ntwInfo.getAtom("loanAmount");
		Atom loanStatus=ntwInfo.getAtom("loanStatus");
		Atom monthlyPayments=ntwInfo.getAtom("monthlyPayments");
		Atom clientAge=ntwInfo.getAtom("clientAge");
		Atom frequency=ntwInfo.getAtom("freq");
		Atom avgNrWith=ntwInfo.getAtom("avgNrWith");
		Atom avgSumOfW=ntwInfo.getAtom("avgSumOfW");
		Atom avgSumOfInc=ntwInfo.getAtom("avgSumOfInc");
		Atom stdMonthInc=ntwInfo.getAtom("stdMonthInc");
		Atom stdMonthW=ntwInfo.getAtom("stdMonthW");
		Atom avgSalary=ntwInfo.getAtom("avgSalary");
		Atom ratUrbInhab=ntwInfo.getAtom("ratUrbInhab");
		Atom hasAccount=ntwInfo.getAtom("hasAccount");        

		//gender(client)
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_gender=fGen.generateFeatures(gender, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fgender=new ArrayList<Feature>();
		fgender.addAll(features_gender);
		Dependency dep_gender_propos=new Dependency(gender,fgender.toArray(new Feature[fgender.size()]));
		dep_for_propositionalization.put(gender.getPredicate(), dep_gender_propos);



		//avgSalary(D)
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSalary=fGen.generateFeatures(avgSalary, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> favgSalary=new ArrayList<Feature>();
		favgSalary.addAll(features_avgSalary);
		Dependency dep_avgSalary_propos=new Dependency(avgSalary,favgSalary.toArray(new Feature[favgSalary.size()]));
		dep_for_propositionalization.put(avgSalary.getPredicate(), dep_avgSalary_propos);

		////clientDistrict
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_clientDistrict=fGen.generateFeatures(clientDistrict, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fclientDistrict=new ArrayList<Feature>();
		fclientDistrict.addAll(features_clientDistrict);
		Dependency dep_clientDistrict_propos=new Dependency(clientDistrict,fclientDistrict.toArray(new Feature[fclientDistrict.size()]));
		dep_for_propositionalization.put(clientDistrict.getPredicate(), dep_clientDistrict_propos);
		
		//hasLoan
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_hasLoan=fGen.generateFeatures(hasLoan, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fhasLoan=new ArrayList<Feature>();
		fhasLoan.addAll(features_hasLoan);
		Dependency dep_hasLoan_propos=new Dependency(hasLoan,fhasLoan.toArray(new Feature[fhasLoan.size()]));
		dep_for_propositionalization.put(hasLoan.getPredicate(), dep_hasLoan_propos);
		
		//loanAmount
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_loanAmount=fGen.generateFeatures(loanAmount, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> floanAmount=new ArrayList<Feature>();
		floanAmount.addAll(features_loanAmount);
		Dependency dep_loanAmount_propos=new Dependency(loanAmount,floanAmount.toArray(new Feature[floanAmount.size()]));
		dep_for_propositionalization.put(loanAmount.getPredicate(), dep_loanAmount_propos);

		//loanStatus
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_loanStatus=fGen.generateFeatures(loanStatus, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> floanStatus=new ArrayList<Feature>();
		floanStatus.addAll(features_loanStatus);
		Dependency dep_loanStatus_propos=new Dependency(loanStatus,floanStatus.toArray(new Feature[floanStatus.size()]));
		dep_for_propositionalization.put(loanStatus.getPredicate(), dep_loanStatus_propos);

		//monthlyPayments
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_monthlyPayments=fGen.generateFeatures(monthlyPayments, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fmonthlyPayments=new ArrayList<Feature>();
		fmonthlyPayments.addAll(features_monthlyPayments);
		Dependency dep_monthlyPayments_propos=new Dependency(monthlyPayments,fmonthlyPayments.toArray(new Feature[fmonthlyPayments.size()]));
		dep_for_propositionalization.put(monthlyPayments.getPredicate(), dep_monthlyPayments_propos);


		//clientAge(C,L)
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_clientAge=fGen.generateFeatures(clientAge,  ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fclientAge=new ArrayList<Feature>();
		fclientAge.addAll(features_clientAge);	
		Dependency dep_clientAge_propos=new Dependency(clientAge,fclientAge.toArray(new Feature[fclientAge.size()]));
		dep_for_propositionalization.put(clientAge.getPredicate(), dep_clientAge_propos);

		//freq
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_frequency=fGen.generateFeatures(frequency,ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> ffrequency=new ArrayList<Feature>();
		ffrequency.addAll(features_frequency);
		Dependency dep_frequency_propos=new Dependency(frequency,ffrequency.toArray(new Feature[ffrequency.size()]));
		dep_for_propositionalization.put(frequency.getPredicate(), dep_frequency_propos);
		
		
		//avgNrWith
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgNrWith=fGen.generateFeatures(avgNrWith,ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> favgNrWith=new ArrayList<Feature>();
		favgNrWith.addAll(features_avgNrWith);
		Dependency dep_avgNrWith_propos=new Dependency(avgNrWith,favgNrWith.toArray(new Feature[favgNrWith.size()]));
		dep_for_propositionalization.put(avgNrWith.getPredicate(), dep_avgNrWith_propos);

		//avgSumOfInc
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSumOfInc=fGen.generateFeatures(avgSumOfInc,ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> favgSumOfInc=new ArrayList<Feature>();
		favgSumOfInc.addAll(features_avgSumOfInc);
		Dependency dep_avgSumOfInc_propos=new Dependency(avgSumOfInc,favgSumOfInc.toArray(new Feature[favgSumOfInc.size()]));
		dep_for_propositionalization.put(avgSumOfInc.getPredicate(), dep_avgSumOfInc_propos);

		////avgSumOfW
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSumOfW=fGen.generateFeatures(avgSumOfW,ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> favgSumOfW=new ArrayList<Feature>();
		favgSumOfW.addAll(features_avgSumOfW);
		Dependency dep_avgSumOfW_propos=new Dependency(avgSumOfW,favgSumOfW.toArray(new Feature[favgSumOfW.size()]));
		dep_for_propositionalization.put(avgSumOfW.getPredicate(), dep_avgSumOfW_propos);

	
		//stdMonthInc
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_stdMonthInc=fGen.generateFeatures(stdMonthInc,ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fstdMonthInc=new ArrayList<Feature>();
		fstdMonthInc.addAll(features_stdMonthInc);
		Dependency dep_stdMonthInc_propos=new Dependency(stdMonthInc,fstdMonthInc.toArray(new Feature[fstdMonthInc.size()]));
		dep_for_propositionalization.put(stdMonthInc.getPredicate(), dep_stdMonthInc_propos);
			
		//stdMonthW
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_stdMonthW=fGen.generateFeatures(stdMonthW,ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fstdMonthW=new ArrayList<Feature>();
		fstdMonthW.addAll(features_stdMonthW);
		Dependency dep_stdMonthW_propos=new Dependency(stdMonthW,fstdMonthW.toArray(new Feature[fstdMonthW.size()]));
		dep_for_propositionalization.put(stdMonthW.getPredicate(), dep_stdMonthW_propos);
		
		//ratUrbInhab	
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_ratUrbInhab=fGen.generateFeatures(ratUrbInhab,ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fratUrbInhab=new ArrayList<Feature>();
		fratUrbInhab.addAll(features_ratUrbInhab);
		Dependency dep_ratUrbInhab_propos=new Dependency(ratUrbInhab,fratUrbInhab.toArray(new Feature[fratUrbInhab.size()]));
		dep_for_propositionalization.put(ratUrbInhab.getPredicate(), dep_ratUrbInhab_propos);
	
		//hasAccount
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_hasAccount=fGen.generateFeatures(hasAccount,ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fhasAccount=new ArrayList<Feature>();
		fhasAccount.addAll(features_hasAccount);
		Dependency dep_hasAccount_propos=new Dependency(hasAccount,fhasAccount.toArray(new Feature[fhasAccount.size()]));
		dep_for_propositionalization.put(hasAccount.getPredicate(), dep_hasAccount_propos);
		
	
		return dep_for_propositionalization;

	}
	public NetworkInfo getNtwInfo() {
		return ntwInfo;
	}

}
*/