import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.features.DiscretizedProportion;
import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Mode;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Create_Propositionalized_PKDD_simpler {
}
/*
	
	NetworkInfo ntwInfo;

	
	public HashMap<Predicate,Dependency> getDependenciesForPropositionalizations(int discr_level,int subsampling_ratio) throws ConjunctionConstructionProblem, FeatureTypeException{
		HashMap<Predicate,Dependency> dep_for_propositionalization=new HashMap<Predicate, Dependency>();
		FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(3, 2);

		CreateDiscretizedPKDD discrPKdd=new CreateDiscretizedPKDD();
		NetworkInfo ntwInfo=discrPKdd.getDiscretizedPKDD(discr_level, subsampling_ratio);
		this.ntwInfo=ntwInfo;
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
		
		 
		//gender
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_gender=fGen.generateFeatures(gender, ntwInfo.getLiterals());
		List<Feature> fgender=new ArrayList<Feature>();
		fgender.add(features_gender.get(14));
		fgender.add(features_gender.get(15));
		fgender.add(features_gender.get(16));
		fgender.add(features_gender.get(17));
		fgender.add(features_gender.get(18));
		fgender.add(features_gender.get(19));
		fgender.add(features_gender.get(3));
		fgender.add(features_gender.get(6));
		fgender.add(features_gender.get(7));
		Dependency dep_gender_propos=new Dependency(gender,features_gender.toArray(new Feature[features_gender.size()]));
		dep_for_propositionalization.put(gender.getPredicate(), dep_gender_propos);

		//avgSalary
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSalary=fGen.generateFeatures(avgSalary,ntwInfo.getLiterals());
		List<Feature> favgSalary=new ArrayList<Feature>();
		favgSalary.add(features_avgSalary.get(0));
		favgSalary.add(features_avgSalary.get(2));
		favgSalary.add(features_avgSalary.get(3));
		favgSalary.add(features_avgSalary.get(4));
		Dependency dep_avgSalary_propos=new Dependency(avgSalary,favgSalary.toArray(new Feature[favgSalary.size()]));
		dep_for_propositionalization.put(avgSalary.getPredicate(), dep_avgSalary_propos);

		//clientDistrict
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_clientDistrict=fGen.generateFeatures(clientDistrict, ntwInfo.getLiterals());
		List<Feature> fclientDistrict=new ArrayList<Feature>();
		fclientDistrict.add(features_clientDistrict.get(0));
		fclientDistrict.add(features_clientDistrict.get(1));
		fclientDistrict.add(features_clientDistrict.get(2));
		fclientDistrict.add(features_clientDistrict.get(3));
		fclientDistrict.add(features_clientDistrict.get(11));
		fclientDistrict.add(features_clientDistrict.get(12));
		fclientDistrict.add(features_clientDistrict.get(13));
		fclientDistrict.add(features_clientDistrict.get(14));
		fclientDistrict.add(features_clientDistrict.get(15));
		fclientDistrict.add(features_clientDistrict.get(16));
		
		Dependency dep_clientDistrict_propos=new Dependency(clientDistrict,fclientDistrict.toArray(new Feature[fclientDistrict.size()]));
		dep_for_propositionalization.put(clientDistrict.getPredicate(), dep_clientDistrict_propos);
		
		
		//hasLoan
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_hasLoan=fGen.generateFeatures(hasLoan, ntwInfo.getLiterals());
		List<Feature> fhasLoan=new ArrayList<Feature>();
		fhasLoan.add(features_hasLoan.get(0));
		fhasLoan.add(features_hasLoan.get(1));
		fhasLoan.add(features_hasLoan.get(2));
		fhasLoan.add(features_hasLoan.get(3));
		fhasLoan.add(features_hasLoan.get(4));
		fhasLoan.add(features_hasLoan.get(5));
		fhasLoan.add(features_hasLoan.get(6));
		fhasLoan.add(features_hasLoan.get(7));
		fhasLoan.add(features_hasLoan.get(8));
		fhasLoan.add(features_hasLoan.get(9));
		fhasLoan.add(features_hasLoan.get(10));
		fhasLoan.add(features_hasLoan.get(22));
		Dependency dep_hasLoan_propos=new Dependency(hasLoan,fhasLoan.toArray(new Feature[fhasLoan.size()]));
		dep_for_propositionalization.put(hasLoan.getPredicate(), dep_hasLoan_propos);
		

		//loanAmount
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_loanAmount=fGen.generateFeatures(loanAmount, ntwInfo.getLiterals());
		List<Feature> floanAmount=new ArrayList<Feature>();
		floanAmount.add(features_loanAmount.get(2));
		floanAmount.add(features_loanAmount.get(3));
		floanAmount.add(features_loanAmount.get(4));
		floanAmount.add(features_loanAmount.get(8));
		floanAmount.add(features_loanAmount.get(9));
		floanAmount.add(features_loanAmount.get(10));
		floanAmount.add(features_loanAmount.get(11));
		floanAmount.add(features_loanAmount.get(12));
		floanAmount.add(features_loanAmount.get(13));
		Dependency dep_loanAmount_propos=new Dependency(loanAmount,floanAmount.toArray(new Feature[floanAmount.size()]));
		dep_for_propositionalization.put(loanAmount.getPredicate(), dep_loanAmount_propos);

		//loanStatus
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_loanStatus=fGen.generateFeatures(loanStatus, ntwInfo.getLiterals());
		List<Feature> floanStatus=new ArrayList<Feature>();
		floanStatus.add(features_loanStatus.get(2));
		floanStatus.add(features_loanStatus.get(3));
		floanStatus.add(features_loanStatus.get(4));
		floanStatus.add(features_loanStatus.get(8));
		floanStatus.add(features_loanStatus.get(9));
		floanStatus.add(features_loanStatus.get(10));
		floanStatus.add(features_loanStatus.get(11));
		floanStatus.add(features_loanStatus.get(12));
		floanStatus.add(features_loanStatus.get(13));
		
		Dependency dep_loanStatus_propos=new Dependency(loanStatus,floanStatus.toArray(new Feature[floanStatus.size()]));
		dep_for_propositionalization.put(loanStatus.getPredicate(), dep_loanStatus_propos);
		

		//monthlyPayments
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_monthlyPayments=fGen.generateFeatures(monthlyPayments, ntwInfo.getLiterals());
		List<Feature> fmonthlyPayments=new ArrayList<Feature>();
		fmonthlyPayments.add(features_monthlyPayments.get(2));
		fmonthlyPayments.add(features_monthlyPayments.get(3));
		fmonthlyPayments.add(features_monthlyPayments.get(4));
		fmonthlyPayments.add(features_monthlyPayments.get(8));
		fmonthlyPayments.add(features_monthlyPayments.get(9));
		fmonthlyPayments.add(features_monthlyPayments.get(10));
		fmonthlyPayments.add(features_monthlyPayments.get(11));
		fmonthlyPayments.add(features_monthlyPayments.get(12));
		fmonthlyPayments.add(features_monthlyPayments.get(13));
		
		
		Dependency dep_monthlyPayments_propos=new Dependency(monthlyPayments,fmonthlyPayments.toArray(new Feature[fmonthlyPayments.size()]));
		dep_for_propositionalization.put(monthlyPayments.getPredicate(), dep_monthlyPayments_propos);


		//clientAge(C,L)
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_clientAge=fGen.generateFeatures(clientAge, ntwInfo.getLiterals());
		List<Feature> fclientAge=new ArrayList<Feature>();
		fclientAge.add(features_clientAge.get(2));
		fclientAge.add(features_clientAge.get(5));
		fclientAge.add(features_clientAge.get(6));
		fclientAge.add(features_clientAge.get(7));
		fclientAge.add(features_clientAge.get(8));
		fclientAge.add(features_clientAge.get(10));
		fclientAge.add(features_clientAge.get(11));
		fclientAge.add(features_clientAge.get(19));
		fclientAge.add(features_clientAge.get(20));
		fclientAge.add(features_clientAge.get(21));
		fclientAge.add(features_clientAge.get(22));
		fclientAge.add(features_clientAge.get(23));
		fclientAge.add(features_clientAge.get(24));
		Dependency dep_clientAge_propos=new Dependency(clientAge,fclientAge.toArray(new Feature[fclientAge.size()]));
		dep_for_propositionalization.put(clientAge.getPredicate(), dep_clientAge_propos);

		
		//freq
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_freq=fGen.generateFeatures(frequency, ntwInfo.getLiterals());
		List<Feature> ffreq=new ArrayList<Feature>();
		ffreq.add(features_freq.get(2));
		ffreq.add(features_freq.get(3));
		ffreq.add(features_freq.get(4));
		ffreq.add(features_freq.get(5));
		ffreq.add(features_freq.get(6));
		ffreq.add(features_freq.get(7));
		ffreq.add(features_freq.get(27));
		ffreq.add(features_freq.get(28));
		ffreq.add(features_freq.get(9));
		ffreq.add(features_freq.get(10));
		ffreq.add(features_freq.get(11));
		Dependency dep_freq_propos=new Dependency(frequency,ffreq.toArray(new Feature[ffreq.size()]));
		dep_for_propositionalization.put(frequency.getPredicate(), dep_freq_propos);
		


		//avgNrWith
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgNrWith=fGen.generateFeatures(avgNrWith, ntwInfo.getLiterals());
		List<Feature> favgNrWith=new ArrayList<Feature>();
		favgNrWith.add(features_avgNrWith.get(2));
		favgNrWith.add(features_avgNrWith.get(3));
		favgNrWith.add(features_avgNrWith.get(4));
		favgNrWith.add(features_avgNrWith.get(5));
		favgNrWith.add(features_avgNrWith.get(6));
		favgNrWith.add(features_avgNrWith.get(7));
		favgNrWith.add(features_avgNrWith.get(9));
		favgNrWith.add(features_avgNrWith.get(10));
		favgNrWith.add(features_avgNrWith.get(11));
		favgNrWith.add(features_avgNrWith.get(28));
		favgNrWith.add(features_avgNrWith.get(27));
		Dependency dep_avgNrWith_propos=new Dependency(avgNrWith,favgNrWith.toArray(new Feature[favgNrWith.size()]));
		dep_for_propositionalization.put(avgNrWith.getPredicate(), dep_avgNrWith_propos);
	
		//avgSumOfInc
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSumOfInc=fGen.generateFeatures(avgSumOfInc, ntwInfo.getLiterals());
		List<Feature> favgSumOfInc=new ArrayList<Feature>();
		favgSumOfInc.add(features_avgSumOfInc.get(2));
		favgSumOfInc.add(features_avgSumOfInc.get(3));
		favgSumOfInc.add(features_avgSumOfInc.get(4));
		favgSumOfInc.add(features_avgSumOfInc.get(5));
		favgSumOfInc.add(features_avgSumOfInc.get(6));
		favgSumOfInc.add(features_avgSumOfInc.get(7));
		favgSumOfInc.add(features_avgSumOfInc.get(9));
		favgSumOfInc.add(features_avgSumOfInc.get(10));
		favgSumOfInc.add(features_avgSumOfInc.get(11));
		favgSumOfInc.add(features_avgSumOfInc.get(28));
		favgSumOfInc.add(features_avgSumOfInc.get(27));
		Dependency dep_avgSumOfInc_propos=new Dependency(avgSumOfInc,favgSumOfInc.toArray(new Feature[favgSumOfInc.size()]));
		dep_for_propositionalization.put(avgSumOfInc.getPredicate(), dep_avgSumOfInc_propos);
		
		//avgSumOfW
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSumOfW=fGen.generateFeatures(avgSumOfW, ntwInfo.getLiterals());
		List<Feature> favgSumOfW=new ArrayList<Feature>();
		favgSumOfW.add(features_avgSumOfW.get(2));
		favgSumOfW.add(features_avgSumOfW.get(3));
		favgSumOfW.add(features_avgSumOfW.get(4));
		favgSumOfW.add(features_avgSumOfW.get(5));
		favgSumOfW.add(features_avgSumOfW.get(6));
		favgSumOfW.add(features_avgSumOfW.get(7));
		favgSumOfW.add(features_avgSumOfW.get(9));
		favgSumOfW.add(features_avgSumOfW.get(10));
		favgSumOfW.add(features_avgSumOfW.get(11));
		favgSumOfW.add(features_avgSumOfW.get(28));
		favgSumOfW.add(features_avgSumOfW.get(27));
		Dependency dep_avgSumOfW_propos=new Dependency(avgSumOfW,favgSumOfW.toArray(new Feature[favgSumOfW.size()]));
		dep_for_propositionalization.put(avgSumOfW.getPredicate(), dep_avgSumOfW_propos);
		
		//stdMonthInc
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_stdMonthInc=fGen.generateFeatures(stdMonthInc, ntwInfo.getLiterals());
		List<Feature> fstdMonthInc=new ArrayList<Feature>();
		fstdMonthInc.add(features_stdMonthInc.get(2));
		fstdMonthInc.add(features_stdMonthInc.get(3));
		fstdMonthInc.add(features_stdMonthInc.get(4));
		fstdMonthInc.add(features_stdMonthInc.get(5));
		fstdMonthInc.add(features_stdMonthInc.get(6));
		fstdMonthInc.add(features_stdMonthInc.get(7));
		fstdMonthInc.add(features_stdMonthInc.get(9));
		fstdMonthInc.add(features_stdMonthInc.get(10));
		fstdMonthInc.add(features_stdMonthInc.get(11));
		fstdMonthInc.add(features_stdMonthInc.get(28));
		fstdMonthInc.add(features_stdMonthInc.get(27));
		Dependency dep_stdMonthInc_propos=new Dependency(stdMonthInc,fstdMonthInc.toArray(new Feature[fstdMonthInc.size()]));
		dep_for_propositionalization.put(stdMonthInc.getPredicate(), dep_stdMonthInc_propos);


		//stdMonthW
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_stdMonthW=fGen.generateFeatures(stdMonthW, ntwInfo.getLiterals());
		List<Feature> fstdMonthW=new ArrayList<Feature>();
		fstdMonthW.add(features_stdMonthW.get(2));
		fstdMonthW.add(features_stdMonthW.get(3));
		fstdMonthW.add(features_stdMonthW.get(4));
		fstdMonthW.add(features_stdMonthW.get(5));
		fstdMonthW.add(features_stdMonthW.get(6));
		fstdMonthW.add(features_stdMonthW.get(7));
		fstdMonthW.add(features_stdMonthW.get(9));
		fstdMonthW.add(features_stdMonthW.get(10));
		fstdMonthW.add(features_stdMonthW.get(11));
		fstdMonthW.add(features_stdMonthW.get(28));
		fstdMonthW.add(features_stdMonthW.get(27));
		Dependency dep_stdMonthW_propos=new Dependency(stdMonthW,fstdMonthW.toArray(new Feature[fstdMonthW.size()]));
		dep_for_propositionalization.put(stdMonthW.getPredicate(), dep_stdMonthW_propos);
		

		//ratUrbInhab
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_ratUrbInhab=fGen.generateFeatures(ratUrbInhab, ntwInfo.getLiterals());
		List<Feature> fratUrbInhab=new ArrayList<Feature>();
		fratUrbInhab.add(features_ratUrbInhab.get(2));
		fratUrbInhab.add(features_ratUrbInhab.get(0));
		fratUrbInhab.add(features_ratUrbInhab.get(3));
		fratUrbInhab.add(features_ratUrbInhab.get(4));

		Dependency dep_ratUrbInhab_propos=new Dependency(ratUrbInhab,fratUrbInhab.toArray(new Feature[fratUrbInhab.size()]));
		dep_for_propositionalization.put(ratUrbInhab.getPredicate(), dep_ratUrbInhab_propos);
			
		//hasAccount
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_hasAccount=fGen.generateFeatures(hasAccount, ntwInfo.getLiterals());
		List<Feature> fhasAccount=new ArrayList<Feature>();
		fhasAccount.add(features_hasAccount.get(2));
		fhasAccount.add(features_hasAccount.get(3));
		fhasAccount.add(features_hasAccount.get(5));
		fhasAccount.add(features_hasAccount.get(6));
		fhasAccount.add(features_hasAccount.get(7));
		fhasAccount.add(features_hasAccount.get(8));
		fhasAccount.add(features_hasAccount.get(9));
		fhasAccount.add(features_hasAccount.get(10));
		fhasAccount.add(features_hasAccount.get(11));
		fhasAccount.add(features_hasAccount.get(14));
		fhasAccount.add(features_hasAccount.get(15));
		fhasAccount.add(features_hasAccount.get(16));
		fhasAccount.add(features_hasAccount.get(17));
		fhasAccount.add(features_hasAccount.get(18));
		Dependency dep_hasAccount_propos=new Dependency(hasAccount,fhasAccount.toArray(new Feature[fhasAccount.size()]));
		dep_for_propositionalization.put(hasAccount.getPredicate(), dep_hasAccount_propos);
	

		return dep_for_propositionalization;

	}

	public NetworkInfo getNtwInfo() {
		return ntwInfo;
	}

}
*/