import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.features.Average;
import hybrid.features.DiscretizedProportion;
import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.Mode;
import hybrid.features.Proportion;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PropositionalizedPKDD_hybrid {
	
}/*

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
		fgender.add(features_gender.get(2));
		fgender.add(features_gender.get(3));
		fgender.add(features_gender.get(4));
		fgender.add(features_gender.get(8));
		fgender.add(features_gender.get(9));
		fgender.add(features_gender.get(10));
		fgender.add(features_gender.get(11));
		fgender.add(features_gender.get(12));
		fgender.add(features_gender.get(13));
		fgender.add(features_gender.get(21));
		fgender.add(features_gender.get(22));
		fgender.add(features_gender.get(23));
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
		
		Dependency dep_gender_propos=new Dependency(gender,fgender.toArray(new Feature[fgender.size()]));
		dep_for_propositionalization.put(gender.getPredicate(), dep_gender_propos);



		//avgSalary(D)
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSalary=fGen.generateFeatures(avgSalary, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> favgSalary=new ArrayList<Feature>();
		favgSalary.add(features_avgSalary.get(0));
		favgSalary.add(features_avgSalary.get(1));
		favgSalary.add(features_avgSalary.get(2));
		favgSalary.add(features_avgSalary.get(3));
		favgSalary.add(features_avgSalary.get(4));
		favgSalary.add(features_avgSalary.get(5));
		favgSalary.add(features_avgSalary.get(6));
		favgSalary.add(features_avgSalary.get(7));
		favgSalary.add(features_avgSalary.get(8));
		favgSalary.add(features_avgSalary.get(9));
		favgSalary.add(features_avgSalary.get(10));
		favgSalary.add(features_avgSalary.get(11));
		
		Dependency dep_avgSalary_propos=new Dependency(avgSalary,favgSalary.toArray(new Feature[favgSalary.size()]));
		dep_for_propositionalization.put(avgSalary.getPredicate(), dep_avgSalary_propos);

		////clientDistrict
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_clientDistrict=fGen.generateFeatures(clientDistrict, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fclientDistrict=new ArrayList<Feature>();
		fclientDistrict.add(features_clientDistrict.get(0));
		fclientDistrict.add(features_clientDistrict.get(1));
		fclientDistrict.add(features_clientDistrict.get(2));
		fclientDistrict.add(features_clientDistrict.get(3));
		fclientDistrict.add(features_clientDistrict.get(4));
		fclientDistrict.add(features_clientDistrict.get(5));
		fclientDistrict.add(features_clientDistrict.get(14));
		fclientDistrict.add(features_clientDistrict.get(15));
		fclientDistrict.add(features_clientDistrict.get(16));
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
		
		//hasLoan
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_hasLoan=fGen.generateFeatures(hasLoan, ntwInfo.getAtomsAndEqualityConstraints());
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
		fhasLoan.add(features_hasLoan.get(11));
		fhasLoan.add(features_hasLoan.get(12));
		
		
		Dependency dep_hasLoan_propos=new Dependency(hasLoan,fhasLoan.toArray(new Feature[fhasLoan.size()]));
		dep_for_propositionalization.put(hasLoan.getPredicate(), dep_hasLoan_propos);
		

		
		
		//loanAmount
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_loanAmount=fGen.generateFeatures(loanAmount, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> floanAmount=new ArrayList<Feature>();
		floanAmount.add(features_loanAmount.get(2));
		floanAmount.add(features_loanAmount.get(3));
		floanAmount.add(features_loanAmount.get(12));
		floanAmount.add(features_loanAmount.get(13));
		floanAmount.add(features_loanAmount.get(14));
		floanAmount.add(features_loanAmount.get(15));
		floanAmount.add(features_loanAmount.get(16));
		floanAmount.add(features_loanAmount.get(17));
		floanAmount.add(features_loanAmount.get(18));
		floanAmount.add(features_loanAmount.get(19));
		floanAmount.add(features_loanAmount.get(20));
		floanAmount.add(features_loanAmount.get(21));
		floanAmount.add(features_loanAmount.get(22));
		floanAmount.add(features_loanAmount.get(23));
		floanAmount.add(features_loanAmount.get(24));
		floanAmount.add(features_loanAmount.get(25));
		floanAmount.add(features_loanAmount.get(26));
		floanAmount.add(features_loanAmount.get(27));
		floanAmount.add(features_loanAmount.get(28));
		floanAmount.add(features_loanAmount.get(29));
		
		Dependency dep_loanAmount_propos=new Dependency(loanAmount,floanAmount.toArray(new Feature[floanAmount.size()]));
		dep_for_propositionalization.put(loanAmount.getPredicate(), dep_loanAmount_propos);

		

		//loanStatus
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_loanStatus=fGen.generateFeatures(loanStatus, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> floanStatus=new ArrayList<Feature>();
		floanStatus.add(features_loanStatus.get(2));
		floanStatus.add(features_loanStatus.get(3));
		floanStatus.add(features_loanStatus.get(10));
		floanStatus.add(features_loanStatus.get(11));
		floanStatus.add(features_loanStatus.get(12));
		floanStatus.add(features_loanStatus.get(13));
		floanStatus.add(features_loanStatus.get(14));
		floanStatus.add(features_loanStatus.get(15));
		floanStatus.add(features_loanStatus.get(16));
		floanStatus.add(features_loanStatus.get(17));
		floanStatus.add(features_loanStatus.get(18));
		floanStatus.add(features_loanStatus.get(19));
		floanStatus.add(features_loanStatus.get(20));
		floanStatus.add(features_loanStatus.get(21));
		floanStatus.add(features_loanStatus.get(22));
		floanStatus.add(features_loanStatus.get(23));
		floanStatus.add(features_loanStatus.get(24));
		floanStatus.add(features_loanStatus.get(25));
		floanStatus.add(features_loanStatus.get(26));
		floanStatus.add(features_loanStatus.get(27));

		Dependency dep_loanStatus_propos=new Dependency(loanStatus,floanStatus.toArray(new Feature[floanStatus.size()]));
		dep_for_propositionalization.put(loanStatus.getPredicate(), dep_loanStatus_propos);

	
		
		//monthlyPayments
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_monthlyPayments=fGen.generateFeatures(monthlyPayments, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fmonthlyPayments=new ArrayList<Feature>();

		fmonthlyPayments.add(features_monthlyPayments.get(2));
		fmonthlyPayments.add(features_monthlyPayments.get(3));
		fmonthlyPayments.add(features_monthlyPayments.get(12));
		fmonthlyPayments.add(features_monthlyPayments.get(13));
		fmonthlyPayments.add(features_monthlyPayments.get(14));
		fmonthlyPayments.add(features_monthlyPayments.get(15));
		fmonthlyPayments.add(features_monthlyPayments.get(16));
		fmonthlyPayments.add(features_monthlyPayments.get(17));
		fmonthlyPayments.add(features_monthlyPayments.get(18));
		fmonthlyPayments.add(features_monthlyPayments.get(19));
		fmonthlyPayments.add(features_monthlyPayments.get(20));
		fmonthlyPayments.add(features_monthlyPayments.get(21));
		fmonthlyPayments.add(features_monthlyPayments.get(22));
		fmonthlyPayments.add(features_monthlyPayments.get(23));
		fmonthlyPayments.add(features_monthlyPayments.get(24));
		fmonthlyPayments.add(features_monthlyPayments.get(25));
		fmonthlyPayments.add(features_monthlyPayments.get(26));
		fmonthlyPayments.add(features_monthlyPayments.get(27));
		fmonthlyPayments.add(features_monthlyPayments.get(28));
		fmonthlyPayments.add(features_monthlyPayments.get(29));
		
		Dependency dep_monthlyPayments_propos=new Dependency(monthlyPayments,fmonthlyPayments.toArray(new Feature[fmonthlyPayments.size()]));
		dep_for_propositionalization.put(monthlyPayments.getPredicate(), dep_monthlyPayments_propos);

	

		//clientAge(C,L)
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_clientAge=fGen.generateFeatures(clientAge, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fclientAge=new ArrayList<Feature>();
		fclientAge.add(features_clientAge.get(0));
		fclientAge.add(features_clientAge.get(1));
		fclientAge.add(features_clientAge.get(2));
		fclientAge.add(features_clientAge.get(3));
		fclientAge.add(features_clientAge.get(4));
		fclientAge.add(features_clientAge.get(77));
		fclientAge.add(features_clientAge.get(78));
		fclientAge.add(features_clientAge.get(79));
		fclientAge.add(features_clientAge.get(80));
		fclientAge.add(features_clientAge.get(81));
		fclientAge.add(features_clientAge.get(82));
		fclientAge.add(features_clientAge.get(83));
		fclientAge.add(features_clientAge.get(84));
		fclientAge.add(features_clientAge.get(85));
		fclientAge.add(features_clientAge.get(86));
		fclientAge.add(features_clientAge.get(87));
		fclientAge.add(features_clientAge.get(88));
		fclientAge.add(features_clientAge.get(89));
		fclientAge.add(features_clientAge.get(90));
		fclientAge.add(features_clientAge.get(91));
		fclientAge.add(features_clientAge.get(92));
		
		Dependency dep_clientAge_propos=new Dependency(clientAge,fclientAge.toArray(new Feature[fclientAge.size()]));
		dep_for_propositionalization.put(clientAge.getPredicate(), dep_clientAge_propos);

	
		//freq
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_frequency=fGen.generateFeatures(frequency, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> ffrequency=new ArrayList<Feature>();
		ffrequency.add(features_frequency.get(56));
		ffrequency.add(features_frequency.get(57));
		ffrequency.add(features_frequency.get(58));
		ffrequency.add(features_frequency.get(0));
		ffrequency.add(features_frequency.get(9));
		ffrequency.add(features_frequency.get(10));
		ffrequency.add(features_frequency.get(11));
		ffrequency.add(features_frequency.get(12));
		ffrequency.add(features_frequency.get(13));
		ffrequency.add(features_frequency.get(14));
		ffrequency.add(features_frequency.get(15));
		ffrequency.add(features_frequency.get(16));
		ffrequency.add(features_frequency.get(17));
		ffrequency.add(features_frequency.get(18));
		ffrequency.add(features_frequency.get(19));
		ffrequency.add(features_frequency.get(20));
		ffrequency.add(features_frequency.get(21));
		ffrequency.add(features_frequency.get(22));
		ffrequency.add(features_frequency.get(23));
		ffrequency.add(features_frequency.get(24));
		ffrequency.add(features_frequency.get(25));
		ffrequency.add(features_frequency.get(26));
		ffrequency.add(features_frequency.get(27));
		ffrequency.add(features_frequency.get(28));
		ffrequency.add(features_frequency.get(29));
		ffrequency.add(features_frequency.get(30));
		ffrequency.add(features_frequency.get(31));
		ffrequency.add(features_frequency.get(32));
		Dependency dep_frequency_propos=new Dependency(frequency,ffrequency.toArray(new Feature[ffrequency.size()]));
		dep_for_propositionalization.put(frequency.getPredicate(), dep_frequency_propos);
		
		//avgNrWith
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgNrWith=fGen.generateFeatures(avgNrWith, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> favgNrWith=new ArrayList<Feature>();
		favgNrWith.add(features_avgNrWith.get(2));
		favgNrWith.add(features_avgNrWith.get(3));
		favgNrWith.add(features_avgNrWith.get(4));
		favgNrWith.add(features_avgNrWith.get(5));
		favgNrWith.add(features_avgNrWith.get(6));
		favgNrWith.add(features_avgNrWith.get(7));
		favgNrWith.add(features_avgNrWith.get(12));
		favgNrWith.add(features_avgNrWith.get(13));
		favgNrWith.add(features_avgNrWith.get(14));
		favgNrWith.add(features_avgNrWith.get(15));
		favgNrWith.add(features_avgNrWith.get(16));
		favgNrWith.add(features_avgNrWith.get(17));
		favgNrWith.add(features_avgNrWith.get(18));
		favgNrWith.add(features_avgNrWith.get(75));
		favgNrWith.add(features_avgNrWith.get(76));
		favgNrWith.add(features_avgNrWith.get(77));
		favgNrWith.add(features_avgNrWith.get(78));
		
		Dependency dep_avgNrWith_propos=new Dependency(avgNrWith,favgNrWith.toArray(new Feature[favgNrWith.size()]));
		dep_for_propositionalization.put(avgNrWith.getPredicate(), dep_avgNrWith_propos);

		
		//avgSumOfInc
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSumOfInc=fGen.generateFeatures(avgSumOfInc, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> favgSumOfInc=new ArrayList<Feature>();
		favgSumOfInc.add(features_avgSumOfInc.get(2));
		favgSumOfInc.add(features_avgSumOfInc.get(3));
		favgSumOfInc.add(features_avgSumOfInc.get(4));
		favgSumOfInc.add(features_avgSumOfInc.get(5));
		favgSumOfInc.add(features_avgSumOfInc.get(6));
		favgSumOfInc.add(features_avgSumOfInc.get(7));
		favgSumOfInc.add(features_avgSumOfInc.get(8));
		favgSumOfInc.add(features_avgSumOfInc.get(12));
		favgSumOfInc.add(features_avgSumOfInc.get(13));
		favgSumOfInc.add(features_avgSumOfInc.get(14));
		favgSumOfInc.add(features_avgSumOfInc.get(15));
		favgSumOfInc.add(features_avgSumOfInc.get(16));
		favgSumOfInc.add(features_avgSumOfInc.get(17));
		favgSumOfInc.add(features_avgSumOfInc.get(18));
		favgSumOfInc.add(features_avgSumOfInc.get(75));
		favgSumOfInc.add(features_avgSumOfInc.get(76));
		favgSumOfInc.add(features_avgSumOfInc.get(77));
		favgSumOfInc.add(features_avgSumOfInc.get(78));
		
		Dependency dep_avgSumOfInc_propos=new Dependency(avgSumOfInc,favgSumOfInc.toArray(new Feature[favgSumOfInc.size()]));
		dep_for_propositionalization.put(avgSumOfInc.getPredicate(), dep_avgSumOfInc_propos);


		////avgSumOfW
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSumOfW=fGen.generateFeatures(avgSumOfW, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> favgSumOfW=new ArrayList<Feature>();
		favgSumOfW.add(features_avgSumOfW.get(2));
		favgSumOfW.add(features_avgSumOfW.get(3));
		favgSumOfW.add(features_avgSumOfW.get(4));
		favgSumOfW.add(features_avgSumOfW.get(5));
		favgSumOfW.add(features_avgSumOfW.get(6));
		favgSumOfW.add(features_avgSumOfW.get(7));
		favgSumOfW.add(features_avgSumOfW.get(8));
		favgSumOfW.add(features_avgSumOfW.get(12));
		favgSumOfW.add(features_avgSumOfW.get(13));
		favgSumOfW.add(features_avgSumOfW.get(14));
		favgSumOfW.add(features_avgSumOfW.get(15));
		favgSumOfW.add(features_avgSumOfW.get(16));
		favgSumOfW.add(features_avgSumOfW.get(17));
		favgSumOfW.add(features_avgSumOfW.get(18));
		favgSumOfW.add(features_avgSumOfW.get(75));
		favgSumOfW.add(features_avgSumOfW.get(76));
		favgSumOfW.add(features_avgSumOfW.get(77));
		favgSumOfW.add(features_avgSumOfW.get(78));
		
		Dependency dep_avgSumOfW_propos=new Dependency(avgSumOfW,favgSumOfW.toArray(new Feature[favgSumOfW.size()]));
		dep_for_propositionalization.put(avgSumOfW.getPredicate(), dep_avgSumOfW_propos);

		
		//stdMonthInc
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_stdMonthInc=fGen.generateFeatures(stdMonthInc, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fstdMonthInc=new ArrayList<Feature>();
		fstdMonthInc.add(features_stdMonthInc.get(2));
		fstdMonthInc.add(features_stdMonthInc.get(3));
		fstdMonthInc.add(features_stdMonthInc.get(4));
		fstdMonthInc.add(features_stdMonthInc.get(5));
		fstdMonthInc.add(features_stdMonthInc.get(6));
		fstdMonthInc.add(features_stdMonthInc.get(7));
		fstdMonthInc.add(features_stdMonthInc.get(8));
		fstdMonthInc.add(features_stdMonthInc.get(12));
		fstdMonthInc.add(features_stdMonthInc.get(13));
		fstdMonthInc.add(features_stdMonthInc.get(14));
		fstdMonthInc.add(features_stdMonthInc.get(15));
		fstdMonthInc.add(features_stdMonthInc.get(16));
		fstdMonthInc.add(features_stdMonthInc.get(17));
		fstdMonthInc.add(features_stdMonthInc.get(18));
		fstdMonthInc.add(features_stdMonthInc.get(75));
		fstdMonthInc.add(features_stdMonthInc.get(76));
		fstdMonthInc.add(features_stdMonthInc.get(77));
		fstdMonthInc.add(features_stdMonthInc.get(78));
		
		Dependency dep_stdMonthInc_propos=new Dependency(stdMonthInc,fstdMonthInc.toArray(new Feature[fstdMonthInc.size()]));
		dep_for_propositionalization.put(stdMonthInc.getPredicate(), dep_stdMonthInc_propos);
		
		//stdMonthW
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_stdMonthW=fGen.generateFeatures(stdMonthW, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fstdMonthW=new ArrayList<Feature>();
		fstdMonthW.add(features_stdMonthW.get(2));
		fstdMonthW.add(features_stdMonthW.get(3));
		fstdMonthW.add(features_stdMonthW.get(4));
		fstdMonthW.add(features_stdMonthW.get(5));
		fstdMonthW.add(features_stdMonthW.get(6));
		fstdMonthW.add(features_stdMonthW.get(7));
		fstdMonthW.add(features_stdMonthW.get(8));
		fstdMonthW.add(features_stdMonthW.get(12));
		fstdMonthW.add(features_stdMonthW.get(13));
		fstdMonthW.add(features_stdMonthW.get(14));
		fstdMonthW.add(features_stdMonthW.get(15));
		fstdMonthW.add(features_stdMonthW.get(16));
		fstdMonthW.add(features_stdMonthW.get(17));
		fstdMonthW.add(features_stdMonthW.get(18));
		fstdMonthW.add(features_stdMonthW.get(75));
		fstdMonthW.add(features_stdMonthW.get(76));
		fstdMonthW.add(features_stdMonthW.get(77));
		fstdMonthW.add(features_stdMonthW.get(78));
		
		Dependency dep_stdMonthW_propos=new Dependency(stdMonthW,fstdMonthW.toArray(new Feature[fstdMonthW.size()]));
		dep_for_propositionalization.put(stdMonthW.getPredicate(), dep_stdMonthW_propos);
		
		//ratUrbInhab	
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_ratUrbInhab=fGen.generateFeatures(ratUrbInhab, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fratUrbInhab=new ArrayList<Feature>();
		fratUrbInhab.add(features_ratUrbInhab.get(0));
		fratUrbInhab.add(features_ratUrbInhab.get(1));
		fratUrbInhab.add(features_ratUrbInhab.get(2));
		fratUrbInhab.add(features_ratUrbInhab.get(3));
		fratUrbInhab.add(features_ratUrbInhab.get(4));
		fratUrbInhab.add(features_ratUrbInhab.get(5));
		fratUrbInhab.add(features_ratUrbInhab.get(6));
		fratUrbInhab.add(features_ratUrbInhab.get(7));
		fratUrbInhab.add(features_ratUrbInhab.get(8));
		fratUrbInhab.add(features_ratUrbInhab.get(9));
		fratUrbInhab.add(features_ratUrbInhab.get(10));
		fratUrbInhab.add(features_ratUrbInhab.get(11));
		
		Dependency dep_ratUrbInhab_propos=new Dependency(ratUrbInhab,fratUrbInhab.toArray(new Feature[fratUrbInhab.size()]));
		dep_for_propositionalization.put(ratUrbInhab.getPredicate(), dep_stdMonthW_propos);
		
		//hasAccount
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_hasAccount=fGen.generateFeatures(hasAccount, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fhasAccount=new ArrayList<Feature>();
		fhasAccount.add(features_hasAccount.get(0));
		fhasAccount.add(features_hasAccount.get(1));
		fhasAccount.add(features_hasAccount.get(2));
		fhasAccount.add(features_hasAccount.get(4));
		fhasAccount.add(features_hasAccount.get(5));
		fhasAccount.add(features_hasAccount.get(6));
		fhasAccount.add(features_hasAccount.get(7));
		fhasAccount.add(features_hasAccount.get(8));
		fhasAccount.add(features_hasAccount.get(9));
		fhasAccount.add(features_hasAccount.get(10));
		fhasAccount.add(features_hasAccount.get(11));
		fhasAccount.add(features_hasAccount.get(31));
		fhasAccount.add(features_hasAccount.get(32));
		fhasAccount.add(features_hasAccount.get(33));
		fhasAccount.add(features_hasAccount.get(34));
		fhasAccount.add(features_hasAccount.get(35));
		fhasAccount.add(features_hasAccount.get(36));
		fhasAccount.add(features_hasAccount.get(37));
		fhasAccount.add(features_hasAccount.get(38));
		fhasAccount.add(features_hasAccount.get(39));
		fhasAccount.add(features_hasAccount.get(40));
		fhasAccount.add(features_hasAccount.get(41));
		fhasAccount.add(features_hasAccount.get(42));
		fhasAccount.add(features_hasAccount.get(43));
		fhasAccount.add(features_hasAccount.get(44));
		fhasAccount.add(features_hasAccount.get(45));
		fhasAccount.add(features_hasAccount.get(46));
		fhasAccount.add(features_hasAccount.get(47));
		fhasAccount.add(features_hasAccount.get(48));
		fhasAccount.add(features_hasAccount.get(49));
		fhasAccount.add(features_hasAccount.get(50));
		fhasAccount.add(features_hasAccount.get(51));
		fhasAccount.add(features_hasAccount.get(52));
		fhasAccount.add(features_hasAccount.get(53));
		fhasAccount.add(features_hasAccount.get(21));
		fhasAccount.add(features_hasAccount.get(22));
		fhasAccount.add(features_hasAccount.get(23));
		fhasAccount.add(features_hasAccount.get(24));
		fhasAccount.add(features_hasAccount.get(25));
		fhasAccount.add(features_hasAccount.get(26));
		
		Dependency dep_hasAccount_propos=new Dependency(hasAccount,fhasAccount.toArray(new Feature[fhasAccount.size()]));
		dep_for_propositionalization.put(hasAccount.getPredicate(), dep_hasAccount_propos);

	
		return dep_for_propositionalization;

	}
	public NetworkInfo getNtwInfo() {
		return ntwInfo;
	}
}
*/