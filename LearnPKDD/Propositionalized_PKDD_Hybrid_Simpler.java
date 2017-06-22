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


public class Propositionalized_PKDD_Hybrid_Simpler extends PropositionalNetworkCreator {

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
		List<Feature> features_gender=fGen.generateFeatures(gender, ntwInfo.getLiterals());
		List<Feature> fgender=new ArrayList<Feature>();
		fgender.add(features_gender.get(24));
		fgender.add(features_gender.get(25));
		fgender.add(features_gender.get(26));
		fgender.add(features_gender.get(27));
		fgender.add(features_gender.get(28));
		fgender.add(features_gender.get(29));
		fgender.add(features_gender.get(30));
		fgender.add(features_gender.get(31));
		fgender.add(features_gender.get(32));
		fgender.add(features_gender.get(33));
		fgender.add(features_gender.get(34));
		fgender.add(features_gender.get(35));
		fgender.add(features_gender.get(36));
		fgender.add(features_gender.get(37));
		fgender.add(features_gender.get(38));
		fgender.add(features_gender.get(39));
		fgender.add(features_gender.get(40));
		fgender.add(features_gender.get(41));
		fgender.add(features_gender.get(12));
		fgender.add(features_gender.get(15));
		
		for(Feature f:features_gender){
	    	System.out.println(f);
	    }
		
		for(Feature f:fgender){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features gender: "+fgender.size());
		
		Dependency dep_gender_propos=new Dependency(gender,fgender.toArray(new Feature[fgender.size()]));
		dep_for_propositionalization.put(gender.getPredicate(), dep_gender_propos);



		//avgSalary(D)
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSalary=fGen.generateFeatures(avgSalary, ntwInfo.getLiterals());
		List<Feature> favgSalary=new ArrayList<Feature>();
		favgSalary.add(features_avgSalary.get(0));
		favgSalary.add(features_avgSalary.get(2));
		favgSalary.add(features_avgSalary.get(3));
		favgSalary.add(features_avgSalary.get(4));
		favgSalary.add(features_avgSalary.get(5));
		favgSalary.add(features_avgSalary.get(6));

		
		for(Feature f:features_avgSalary){
	    	System.out.println(f);
	    }
		
		for(Feature f:favgSalary){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features avgSalary: "+favgSalary.size());
		
		
		Dependency dep_avgSalary_propos=new Dependency(avgSalary,favgSalary.toArray(new Feature[favgSalary.size()]));
		dep_for_propositionalization.put(avgSalary.getPredicate(), dep_avgSalary_propos);

		////clientDistrict
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_clientDistrict=fGen.generateFeatures(clientDistrict, ntwInfo.getLiterals());
		List<Feature> fclientDistrict=new ArrayList<Feature>();
		fclientDistrict.add(features_clientDistrict.get(0));
		fclientDistrict.add(features_clientDistrict.get(3));
		fclientDistrict.add(features_clientDistrict.get(4));
		fclientDistrict.add(features_clientDistrict.get(5));
		fclientDistrict.add(features_clientDistrict.get(17));
		fclientDistrict.add(features_clientDistrict.get(18));
		fclientDistrict.add(features_clientDistrict.get(19));
		fclientDistrict.add(features_clientDistrict.get(20));
		fclientDistrict.add(features_clientDistrict.get(21));
		fclientDistrict.add(features_clientDistrict.get(22));
		fclientDistrict.add(features_clientDistrict.get(23));
		fclientDistrict.add(features_clientDistrict.get(24));
		fclientDistrict.add(features_clientDistrict.get(25));
		fclientDistrict.add(features_clientDistrict.get(26));
		fclientDistrict.add(features_clientDistrict.get(27));
		fclientDistrict.add(features_clientDistrict.get(28));
		fclientDistrict.add(features_clientDistrict.get(29));
		fclientDistrict.add(features_clientDistrict.get(30));
		fclientDistrict.add(features_clientDistrict.get(31));

		Dependency dep_clientDistrict_propos=new Dependency(clientDistrict,fclientDistrict.toArray(new Feature[fclientDistrict.size()]));
		dep_for_propositionalization.put(clientDistrict.getPredicate(), dep_clientDistrict_propos);
		
		for(Feature f:features_clientDistrict){
	    	System.out.println(f);
	    }
		
		for(Feature f:fclientDistrict){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features clientDistrict: "+fclientDistrict.size());
		
		
		
		//hasLoan
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_hasLoan=fGen.generateFeatures(hasLoan, ntwInfo.getLiterals());
		List<Feature> fhasLoan=new ArrayList<Feature>();
		fhasLoan.add(features_hasLoan.get(38));
		fhasLoan.add(features_hasLoan.get(0));
		fhasLoan.add(features_hasLoan.get(1));
		fhasLoan.add(features_hasLoan.get(2));
		fhasLoan.add(features_hasLoan.get(17));
		fhasLoan.add(features_hasLoan.get(18));
		fhasLoan.add(features_hasLoan.get(19));
		fhasLoan.add(features_hasLoan.get(6));
		fhasLoan.add(features_hasLoan.get(7));
		fhasLoan.add(features_hasLoan.get(8));
		fhasLoan.add(features_hasLoan.get(9));
		fhasLoan.add(features_hasLoan.get(10));
		fhasLoan.add(features_hasLoan.get(11));
		fhasLoan.add(features_hasLoan.get(12));
		
		
		Dependency dep_hasLoan_propos=new Dependency(hasLoan,fhasLoan.toArray(new Feature[fhasLoan.size()]));
		dep_for_propositionalization.put(hasLoan.getPredicate(), dep_hasLoan_propos);
		
		for(Feature f:features_hasLoan){
	    	System.out.println(f);
	    }
		
		for(Feature f:fhasLoan){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features hasLoan: "+fhasLoan.size());
		
		
		
		//loanAmount
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_loanAmount=fGen.generateFeatures(loanAmount, ntwInfo.getLiterals());
		List<Feature> floanAmount=new ArrayList<Feature>();
		floanAmount.add(features_loanAmount.get(2));
		floanAmount.add(features_loanAmount.get(3));
		floanAmount.add(features_loanAmount.get(11));
		floanAmount.add(features_loanAmount.get(12));
		floanAmount.add(features_loanAmount.get(13));
		floanAmount.add(features_loanAmount.get(14));
		floanAmount.add(features_loanAmount.get(17));
		floanAmount.add(features_loanAmount.get(20));
		floanAmount.add(features_loanAmount.get(23));
		floanAmount.add(features_loanAmount.get(26));
		floanAmount.add(features_loanAmount.get(29));
		
		Dependency dep_loanAmount_propos=new Dependency(loanAmount,floanAmount.toArray(new Feature[floanAmount.size()]));
		dep_for_propositionalization.put(loanAmount.getPredicate(), dep_loanAmount_propos);

		for(Feature f:features_loanAmount){
	    	System.out.println(f);
	    }
		
		for(Feature f:floanAmount){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features loanAmount: "+floanAmount.size());
		

		//loanStatus
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_loanStatus=fGen.generateFeatures(loanStatus, ntwInfo.getLiterals());
		List<Feature> floanStatus=new ArrayList<Feature>();
		floanStatus.add(features_loanStatus.get(2));
		floanStatus.add(features_loanStatus.get(3));
		floanStatus.add(features_loanStatus.get(13));
		floanStatus.add(features_loanStatus.get(14));
		floanStatus.add(features_loanStatus.get(15));
		floanStatus.add(features_loanStatus.get(16));
		floanStatus.add(features_loanStatus.get(19));
		floanStatus.add(features_loanStatus.get(22));
		floanStatus.add(features_loanStatus.get(25));
		floanStatus.add(features_loanStatus.get(28));
		floanStatus.add(features_loanStatus.get(31));

		Dependency dep_loanStatus_propos=new Dependency(loanStatus,floanStatus.toArray(new Feature[floanStatus.size()]));
		dep_for_propositionalization.put(loanStatus.getPredicate(), dep_loanStatus_propos);

		for(Feature f:features_loanStatus){
	    	System.out.println(f);
	    }
		
		for(Feature f:floanStatus){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features loanStatus: "+floanStatus.size());
		
		
		//monthlyPayments
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_monthlyPayments=fGen.generateFeatures(monthlyPayments, ntwInfo.getLiterals());
		List<Feature> fmonthlyPayments=new ArrayList<Feature>();

		fmonthlyPayments.add(features_monthlyPayments.get(2));
		fmonthlyPayments.add(features_monthlyPayments.get(3));
		fmonthlyPayments.add(features_monthlyPayments.get(11));
		fmonthlyPayments.add(features_monthlyPayments.get(12));
		fmonthlyPayments.add(features_monthlyPayments.get(13));
		fmonthlyPayments.add(features_monthlyPayments.get(14));
		fmonthlyPayments.add(features_monthlyPayments.get(17));
		fmonthlyPayments.add(features_monthlyPayments.get(20));
		fmonthlyPayments.add(features_monthlyPayments.get(23));
		fmonthlyPayments.add(features_monthlyPayments.get(26));
		fmonthlyPayments.add(features_monthlyPayments.get(29));
		
		Dependency dep_monthlyPayments_propos=new Dependency(monthlyPayments,fmonthlyPayments.toArray(new Feature[fmonthlyPayments.size()]));
		dep_for_propositionalization.put(monthlyPayments.getPredicate(), dep_monthlyPayments_propos);

		for(Feature f:features_monthlyPayments){
	    	System.out.println(f);
	    }
		
		for(Feature f:fmonthlyPayments){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features monthlyPayments: "+fmonthlyPayments.size());
	

		//clientAge(C,L)
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_clientAge=fGen.generateFeatures(clientAge,  ntwInfo.getLiterals());
		List<Feature> fclientAge=new ArrayList<Feature>();
		fclientAge.add(features_clientAge.get(2));
		fclientAge.add(features_clientAge.get(27));
		fclientAge.add(features_clientAge.get(30));
		fclientAge.add(features_clientAge.get(33));
		fclientAge.add(features_clientAge.get(36));
		fclientAge.add(features_clientAge.get(39));
		fclientAge.add(features_clientAge.get(42));
		fclientAge.add(features_clientAge.get(13));
		fclientAge.add(features_clientAge.get(16));
		fclientAge.add(features_clientAge.get(5));
		fclientAge.add(features_clientAge.get(6));
		fclientAge.add(features_clientAge.get(7));
		
		Dependency dep_clientAge_propos=new Dependency(clientAge,fclientAge.toArray(new Feature[fclientAge.size()]));
		dep_for_propositionalization.put(clientAge.getPredicate(), dep_clientAge_propos);

		for(Feature f:features_clientAge){
	    	System.out.println(f);
	    }
		
		for(Feature f:fclientAge){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features clientAge: "+fclientAge.size());
		
	
		//freq
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_frequency=fGen.generateFeatures(frequency, ntwInfo.getLiterals());
		List<Feature> ffrequency=new ArrayList<Feature>();
		ffrequency.add(features_frequency.get(2));
		ffrequency.add(features_frequency.get(53));
		ffrequency.add(features_frequency.get(3));
		ffrequency.add(features_frequency.get(4));
		ffrequency.add(features_frequency.get(5));
		ffrequency.add(features_frequency.get(6));
		ffrequency.add(features_frequency.get(7));
		ffrequency.add(features_frequency.get(11));
		ffrequency.add(features_frequency.get(15));
		ffrequency.add(features_frequency.get(12));
		ffrequency.add(features_frequency.get(16));
		ffrequency.add(features_frequency.get(17));
		ffrequency.add(features_frequency.get(18));
		
		
		
		Dependency dep_frequency_propos=new Dependency(frequency,ffrequency.toArray(new Feature[ffrequency.size()]));
		dep_for_propositionalization.put(frequency.getPredicate(), dep_frequency_propos);
		
		for(Feature f:features_frequency){
	    	System.out.println(f);
	    }
		
		for(Feature f:ffrequency){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features frequency: "+ffrequency.size());
		
		
		//avgNrWith
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgNrWith=fGen.generateFeatures(avgNrWith,ntwInfo.getLiterals());
		List<Feature> favgNrWith=new ArrayList<Feature>();
		favgNrWith.add(features_avgNrWith.get(2));
		favgNrWith.add(features_avgNrWith.get(3));
		favgNrWith.add(features_avgNrWith.get(4));
		favgNrWith.add(features_avgNrWith.get(5));
		favgNrWith.add(features_avgNrWith.get(6));
		favgNrWith.add(features_avgNrWith.get(7));
		favgNrWith.add(features_avgNrWith.get(11));
		favgNrWith.add(features_avgNrWith.get(12));
		favgNrWith.add(features_avgNrWith.get(15));
		favgNrWith.add(features_avgNrWith.get(49));
		favgNrWith.add(features_avgNrWith.get(16));
		favgNrWith.add(features_avgNrWith.get(17));
		favgNrWith.add(features_avgNrWith.get(18));
		

		
		Dependency dep_avgNrWith_propos=new Dependency(avgNrWith,favgNrWith.toArray(new Feature[favgNrWith.size()]));
		dep_for_propositionalization.put(avgNrWith.getPredicate(), dep_avgNrWith_propos);

		
		for(Feature f:features_avgNrWith){
	    	System.out.println(f);
	    }
		
		for(Feature f:favgNrWith){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features avgNrWith: "+favgNrWith.size());
		
		
		
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
		favgSumOfInc.add(features_avgSumOfInc.get(11));
		favgSumOfInc.add(features_avgSumOfInc.get(12));
		favgSumOfInc.add(features_avgSumOfInc.get(15));
		favgSumOfInc.add(features_avgSumOfInc.get(49));
		favgSumOfInc.add(features_avgSumOfInc.get(16));
		favgSumOfInc.add(features_avgSumOfInc.get(17));
		favgSumOfInc.add(features_avgSumOfInc.get(18));
		
		Dependency dep_avgSumOfInc_propos=new Dependency(avgSumOfInc,favgSumOfInc.toArray(new Feature[favgSumOfInc.size()]));
		dep_for_propositionalization.put(avgSumOfInc.getPredicate(), dep_avgSumOfInc_propos);

		for(Feature f:features_avgSumOfInc){
	    	System.out.println(f);
	    }
		
		for(Feature f:favgSumOfInc){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features avgSumOfInc: "+favgSumOfInc.size());
		
		

		////avgSumOfW
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSumOfW=fGen.generateFeatures(avgSumOfW,  ntwInfo.getLiterals());
		List<Feature> favgSumOfW=new ArrayList<Feature>();
		favgSumOfW.add(features_avgSumOfW.get(2));
		favgSumOfW.add(features_avgSumOfW.get(3));
		favgSumOfW.add(features_avgSumOfW.get(4));
		favgSumOfW.add(features_avgSumOfW.get(5));
		favgSumOfW.add(features_avgSumOfW.get(6));
		favgSumOfW.add(features_avgSumOfW.get(7));
		favgSumOfW.add(features_avgSumOfW.get(11));
		favgSumOfW.add(features_avgSumOfW.get(12));
		favgSumOfW.add(features_avgSumOfW.get(15));
		favgSumOfW.add(features_avgSumOfW.get(49));
		favgSumOfW.add(features_avgSumOfW.get(16));
		favgSumOfW.add(features_avgSumOfW.get(17));
		favgSumOfW.add(features_avgSumOfW.get(18));
		
		Dependency dep_avgSumOfW_propos=new Dependency(avgSumOfW,favgSumOfW.toArray(new Feature[favgSumOfW.size()]));
		dep_for_propositionalization.put(avgSumOfW.getPredicate(), dep_avgSumOfW_propos);

		
		for(Feature f:features_avgSumOfW){
	    	System.out.println(f);
	    }
		
		for(Feature f:favgSumOfW){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features favgSumOfW: "+favgSumOfW.size());
		
		
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
		fstdMonthInc.add(features_stdMonthInc.get(11));
		fstdMonthInc.add(features_stdMonthInc.get(12));
		fstdMonthInc.add(features_stdMonthInc.get(15));
		fstdMonthInc.add(features_stdMonthInc.get(49));
		fstdMonthInc.add(features_stdMonthInc.get(16));
		fstdMonthInc.add(features_stdMonthInc.get(17));
		fstdMonthInc.add(features_stdMonthInc.get(18));
		
		Dependency dep_stdMonthInc_propos=new Dependency(stdMonthInc,fstdMonthInc.toArray(new Feature[fstdMonthInc.size()]));
		dep_for_propositionalization.put(stdMonthInc.getPredicate(), dep_stdMonthInc_propos);
		
		for(Feature f:features_stdMonthInc){
	    	System.out.println(f);
	    }
		
		for(Feature f:fstdMonthInc){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features favgSumOfW: "+fstdMonthInc.size());
		
		
		//stdMonthW
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_stdMonthW=fGen.generateFeatures(stdMonthW,  ntwInfo.getLiterals());
		List<Feature> fstdMonthW=new ArrayList<Feature>();
		fstdMonthW.add(features_stdMonthW.get(2));
		fstdMonthW.add(features_stdMonthW.get(3));
		fstdMonthW.add(features_stdMonthW.get(4));
		fstdMonthW.add(features_stdMonthW.get(5));
		fstdMonthW.add(features_stdMonthW.get(6));
		fstdMonthW.add(features_stdMonthW.get(7));
		fstdMonthW.add(features_stdMonthW.get(11));
		fstdMonthW.add(features_stdMonthW.get(12));
		fstdMonthW.add(features_stdMonthW.get(15));
		fstdMonthW.add(features_stdMonthW.get(49));
		fstdMonthW.add(features_stdMonthW.get(16));
		fstdMonthW.add(features_stdMonthW.get(17));
		fstdMonthW.add(features_stdMonthW.get(18));
		Dependency dep_stdMonthW_propos=new Dependency(stdMonthW,fstdMonthW.toArray(new Feature[fstdMonthW.size()]));
		dep_for_propositionalization.put(stdMonthW.getPredicate(), dep_stdMonthW_propos);
		
		//ratUrbInhab	
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_ratUrbInhab=fGen.generateFeatures(ratUrbInhab, ntwInfo.getLiterals());
		List<Feature> fratUrbInhab=new ArrayList<Feature>();
		fratUrbInhab.add(features_ratUrbInhab.get(0));
		fratUrbInhab.add(features_ratUrbInhab.get(2));
		fratUrbInhab.add(features_ratUrbInhab.get(3));
		fratUrbInhab.add(features_ratUrbInhab.get(4));
		fratUrbInhab.add(features_ratUrbInhab.get(5));
		fratUrbInhab.add(features_ratUrbInhab.get(6));	

		
		Dependency dep_ratUrbInhab_propos=new Dependency(ratUrbInhab,fratUrbInhab.toArray(new Feature[fratUrbInhab.size()]));
		dep_for_propositionalization.put(ratUrbInhab.getPredicate(), dep_ratUrbInhab_propos);
		
		for(Feature f:features_ratUrbInhab){
	    	System.out.println(f);
	    }
		
		for(Feature f:fratUrbInhab){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features fratUrbInhab: "+fratUrbInhab.size());
		
		
		//hasAccount
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_hasAccount=fGen.generateFeatures(hasAccount,  ntwInfo.getLiterals());
		List<Feature> fhasAccount=new ArrayList<Feature>();
		fhasAccount.add(features_hasAccount.get(2));
		fhasAccount.add(features_hasAccount.get(4));
		fhasAccount.add(features_hasAccount.get(33));
		fhasAccount.add(features_hasAccount.get(8));
		fhasAccount.add(features_hasAccount.get(9));
		fhasAccount.add(features_hasAccount.get(10));
		fhasAccount.add(features_hasAccount.get(11));
		fhasAccount.add(features_hasAccount.get(12));
		fhasAccount.add(features_hasAccount.get(13));
		fhasAccount.add(features_hasAccount.get(20));
		fhasAccount.add(features_hasAccount.get(23));
		fhasAccount.add(features_hasAccount.get(26));
		fhasAccount.add(features_hasAccount.get(30));
		fhasAccount.add(features_hasAccount.get(27));
		
		
		Dependency dep_hasAccount_propos=new Dependency(hasAccount,fhasAccount.toArray(new Feature[fhasAccount.size()]));
		dep_for_propositionalization.put(hasAccount.getPredicate(), dep_hasAccount_propos);
		
		for(Feature f:features_hasAccount){
	    	System.out.println(f);
	    }
		
		for(Feature f:fhasAccount){
	    	System.out.println(f);
	    }
		
		System.out.println(" Nr features fhasAccount: "+fhasAccount.size());
	
		return dep_for_propositionalization;

	}
	public NetworkInfo getNtwInfo() {
		return ntwInfo;
	}
}
*/