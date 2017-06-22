import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
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
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.Type;

import java.util.*;

public class PropositionalizedPKDD {
}
/*

	NetworkInfo ntwInfo;

	public PropositionalizedPKDD() {
		// TODO Auto-generated constructor stub
	}

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
		//predicates
		//gender(client)
		//15 possible features
		//disctrict

		Exist client_district=new Exist(new Conjunction(gender,new PosLiteral(clientDistrict)));
		Exist gender_hasLoan_=new Exist(new Conjunction(gender,new PosLiteral(hasAccount),new PosLiteral(hasLoan)));
		ValueFt gender_loanAmount_=new ValueFt(new Conjunction(gender,new PosLiteral(hasAccount),new PosLiteral(hasLoan),new PosLiteral(loanAmount)));
		ValueFt gender_loanStatus_=new ValueFt(new Conjunction(gender,new PosLiteral(hasAccount),new PosLiteral(hasLoan),new PosLiteral(loanStatus)));
		ValueFt gender_monthlyPayments=new ValueFt(new Conjunction(gender,new PosLiteral(hasAccount),new PosLiteral(hasLoan),new PosLiteral(monthlyPayments)));
		Mode gender_frequency=new Mode(new Conjunction(gender,new PosLiteral(hasAccount),new PosLiteral(frequency)));
		Mode gender_avgNrWith=new Mode(new Conjunction(gender,new PosLiteral(hasAccount),new PosLiteral(avgNrWith)));
		Mode gender_avgSumOfW=new Mode(new Conjunction(gender,new PosLiteral(hasAccount),new PosLiteral(avgSumOfW)));
		Mode gender_avgSumOfInc=new Mode(new Conjunction(gender,new PosLiteral(hasAccount),new PosLiteral(avgSumOfInc)));
		Mode gender_stdMonthInc=new Mode(new Conjunction(gender,new PosLiteral(hasAccount),new PosLiteral(stdMonthInc)));
		Mode gender_stdMonthW=new Mode(new Conjunction(gender,new PosLiteral(hasAccount),new PosLiteral(stdMonthW)));
		ValueFt gender_avgSalary=new ValueFt(new Conjunction(gender,new PosLiteral(clientDistrict),new PosLiteral(avgSalary)));
		ValueFt gender_ratUrbInhab=new ValueFt(new Conjunction(gender,new PosLiteral(clientDistrict),new PosLiteral(ratUrbInhab)));
		Exist gender_hasAccount=new Exist(new Conjunction(gender,new PosLiteral(hasAccount)));
		DiscretizedProportion discrPropgender_hasAccount=new DiscretizedProportion(new Conjunction(gender,new PosLiteral(hasAccount)),discr_level);


		List<Feature> features_gender=new ArrayList<Feature>();
		//features_gender.add(client_district);
		features_gender.add(gender_hasLoan_);
		features_gender.add(gender_loanAmount_);
		features_gender.add(gender_loanStatus_);
		features_gender.add(gender_monthlyPayments);
		features_gender.add(gender_frequency);
		features_gender.add(gender_avgNrWith);
		features_gender.add(gender_avgSumOfW);
		features_gender.add(gender_avgSumOfInc);
		features_gender.add(gender_stdMonthInc);
		features_gender.add(gender_stdMonthW);
		features_gender.add(gender_avgSalary);
		features_gender.add(gender_ratUrbInhab);
		features_gender.add(gender_hasAccount);
		features_gender.add(discrPropgender_hasAccount);
		Dependency dep_gender_propos=new Dependency(gender,features_gender.toArray(new Feature[features_gender.size()]));
		dep_for_propositionalization.put(gender.getPredicate(), dep_gender_propos);






		//avgSalary
		Exist exists_client_district_avgSalary=new Exist(new Conjunction(avgSalary,new PosLiteral(clientDistrict)));
		//DiscretizedProportion prop_client_district_avgSalary=new DiscretizedProportion(new Conjunction(avgSalary,new PosLiteral(hasLoan)), discr_level);
		Exist exists_hasLoan_avgSalary=new Exist(new Conjunction(avgSalary,new PosLiteral(clientDistrict),new PosLiteral(hasAccount),new PosLiteral(hasLoan)));
		DiscretizedProportion prop_hasLoan_avgSalary=new DiscretizedProportion(new Conjunction(avgSalary,new PosLiteral(clientDistrict),new PosLiteral(hasAccount),new PosLiteral(hasLoan)),discr_level);
		//Mode mode_loan_amount_avgSalary=new Mode(new Conjunction(avgSalary,new PosLiteral(clientDistrict),new PosLiteral(hasAccount),new PosLiteral(hasLoan),new PosLiteral(loanAmount)));
		//Mode mode_loan_status_avgSalary=new Mode(new Conjunction(avgSalary,new PosLiteral(clientDistrict),new PosLiteral(hasAccount),new PosLiteral(hasLoan),new PosLiteral(loanStatus)));
		//Mode mode_monthlyPayments_avgSalary=new Mode(new Conjunction(avgSalary,new PosLiteral(clientDistrict),new PosLiteral(hasAccount),new PosLiteral(hasLoan),new PosLiteral(monthlyPayments)));
		Mode mode_frequency_avgSalary=new Mode(new Conjunction(avgSalary,new PosLiteral(clientDistrict),new PosLiteral(hasAccount),new PosLiteral(frequency)));
		Mode mode_avgNrWith_avgSalary=new Mode(new Conjunction(avgSalary,new PosLiteral(clientDistrict),new PosLiteral(hasAccount),new PosLiteral(avgNrWith)));
		Mode mode_avgSumOfW_avgSalary=new Mode(new Conjunction(avgSalary,new PosLiteral(clientDistrict),new PosLiteral(hasAccount),new PosLiteral(avgSumOfW)));
		Mode mode_avgSumOfInc_avgSalary=new Mode(new Conjunction(avgSalary,new PosLiteral(clientDistrict),new PosLiteral(hasAccount),new PosLiteral(avgSumOfInc)));
		Mode mode_stdMonthInc_avgSalary=new Mode(new Conjunction(avgSalary,new PosLiteral(clientDistrict),new PosLiteral(hasAccount),new PosLiteral(stdMonthInc)));
		Mode mode_stdMonthW_avgSalary=new Mode(new Conjunction(avgSalary,new PosLiteral(clientDistrict),new PosLiteral(hasAccount),new PosLiteral(stdMonthW)));
		ValueFt value_ratUrbInhab_avgSalary=new ValueFt(new Conjunction(avgSalary,new PosLiteral(ratUrbInhab)));
		Exist exist_gender_hasAccount_avgSalary=new Exist(new Conjunction(avgSalary,new PosLiteral(hasAccount)));
		DiscretizedProportion prop_gender_hasAccount_avgSalary=new DiscretizedProportion(new Conjunction(avgSalary,new PosLiteral(hasAccount)),discr_level);
		DiscretizedProportion prop_gender_hasloanAmountAccount_avgSalary=new DiscretizedProportion(new Conjunction(avgSalary,new PosLiteral(hasAccount)),discr_level);



		List<Feature> features_avgSalary=new ArrayList<Feature>();
		features_avgSalary.add(exists_client_district_avgSalary);
		//features_avgSalary.add(prop_client_district_avgSalary);
		features_avgSalary.add(exists_hasLoan_avgSalary);
		features_avgSalary.add(prop_hasLoan_avgSalary);
		//features_avgSalary.add(mode_loan_amount_avgSalary);
		// features_avgSalary.add(mode_loan_status_avgSalary);
		// features_avgSalary.add(mode_monthlyPayments_avgSalary);
		features_avgSalary.add(mode_frequency_avgSalary);
		features_avgSalary.add(mode_avgNrWith_avgSalary);
		features_avgSalary.add(mode_avgSumOfW_avgSalary);
		features_avgSalary.add(mode_avgSumOfInc_avgSalary);
		features_avgSalary.add(mode_stdMonthInc_avgSalary);
		features_avgSalary.add(mode_stdMonthW_avgSalary);
		features_avgSalary.add(value_ratUrbInhab_avgSalary);
		features_avgSalary.add(exist_gender_hasAccount_avgSalary);
		features_avgSalary.add(prop_gender_hasAccount_avgSalary);

		Dependency dep_avgSalary_propos=new Dependency(avgSalary,features_avgSalary.toArray(new Feature[features_avgSalary.size()]));
		dep_for_propositionalization.put(avgSalary.getPredicate(), dep_avgSalary_propos);

		//clientDistrict
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_clientDistrict=fGen.generateFeatures(clientDistrict, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fclientDistrict=new ArrayList<Feature>();
		fclientDistrict.add(features_clientDistrict.get(0));
		fclientDistrict.add(features_clientDistrict.get(17));
		fclientDistrict.add(features_clientDistrict.get(18));
		fclientDistrict.add(features_clientDistrict.get(58));
		fclientDistrict.add(features_clientDistrict.get(59));
		fclientDistrict.add(features_clientDistrict.get(60));
		fclientDistrict.add(features_clientDistrict.get(39));
		fclientDistrict.add(features_clientDistrict.get(19));
		fclientDistrict.add(features_clientDistrict.get(20));
		fclientDistrict.add(features_clientDistrict.get(21));
		fclientDistrict.add(features_clientDistrict.get(22));
		fclientDistrict.add(features_clientDistrict.get(23));
		fclientDistrict.add(features_clientDistrict.get(24));
		fclientDistrict.add(features_clientDistrict.get(2));
		fclientDistrict.add(features_clientDistrict.get(3));
		fclientDistrict.add(features_clientDistrict.get(4));
		fclientDistrict.add(features_clientDistrict.get(5));		
		Dependency dep_clientDistrict_propos=new Dependency(clientDistrict,fclientDistrict.toArray(new Feature[fclientDistrict.size()]));
		dep_for_propositionalization.put(clientDistrict.getPredicate(), dep_clientDistrict_propos);


		//hasLoan
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_hasLoan=fGen.generateFeatures(hasLoan, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fhasLoan=new ArrayList<Feature>();
		fhasLoan.add(features_hasLoan.get(56));
		fhasLoan.add(features_hasLoan.get(57));
		fhasLoan.add(features_hasLoan.get(58));
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
		fhasLoan.add(features_hasLoan.get(119));
		fhasLoan.add(features_hasLoan.get(120));
		Dependency dep_hasLoan_propos=new Dependency(hasLoan,fhasLoan.toArray(new Feature[fhasLoan.size()]));
		dep_for_propositionalization.put(hasLoan.getPredicate(), dep_hasLoan_propos);


		//loanAmount
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_loanAmount=fGen.generateFeatures(loanAmount, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> floanAmount=new ArrayList<Feature>();
		floanAmount.add(features_loanAmount.get(114));
		floanAmount.add(features_loanAmount.get(115));
		floanAmount.add(features_loanAmount.get(116));

		floanAmount.add(features_loanAmount.get(2));
		floanAmount.add(features_loanAmount.get(3));
		floanAmount.add(features_loanAmount.get(59));
		floanAmount.add(features_loanAmount.get(12));
		floanAmount.add(features_loanAmount.get(13));
		floanAmount.add(features_loanAmount.get(14));
		floanAmount.add(features_loanAmount.get(15));
		floanAmount.add(features_loanAmount.get(16));
		floanAmount.add(features_loanAmount.get(17));
		floanAmount.add(features_loanAmount.get(18));
		floanAmount.add(features_loanAmount.get(19));
		Dependency dep_loanAmount_propos=new Dependency(loanAmount,floanAmount.toArray(new Feature[floanAmount.size()]));
		dep_for_propositionalization.put(loanAmount.getPredicate(), dep_loanAmount_propos);


		//loanStatus
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_loanStatus=fGen.generateFeatures(loanStatus, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> floanStatus=new ArrayList<Feature>();
		floanStatus.add(features_loanStatus.get(62));
		floanStatus.add(features_loanStatus.get(63));
		floanStatus.add(features_loanStatus.get(64));
		floanStatus.add(features_loanStatus.get(2));
		floanStatus.add(features_loanStatus.get(3));
		floanStatus.add(features_loanStatus.get(35));
		floanStatus.add(features_loanStatus.get(8));
		floanStatus.add(features_loanStatus.get(9));
		floanStatus.add(features_loanStatus.get(10));
		floanStatus.add(features_loanStatus.get(11));
		floanStatus.add(features_loanStatus.get(12));
		floanStatus.add(features_loanStatus.get(13));
		floanStatus.add(features_loanStatus.get(14));
		floanStatus.add(features_loanStatus.get(15));
		Dependency dep_loanStatus_propos=new Dependency(loanStatus,floanStatus.toArray(new Feature[floanStatus.size()]));
		dep_for_propositionalization.put(loanStatus.getPredicate(), dep_loanStatus_propos);


		//monthlyPayments
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_monthlyPayments=fGen.generateFeatures(monthlyPayments, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fmonthlyPayments=new ArrayList<Feature>();
		fmonthlyPayments.add(features_monthlyPayments.get(114));
		fmonthlyPayments.add(features_monthlyPayments.get(115));
		fmonthlyPayments.add(features_monthlyPayments.get(116));
		fmonthlyPayments.add(features_monthlyPayments.get(2));
		fmonthlyPayments.add(features_monthlyPayments.get(3));
		fmonthlyPayments.add(features_monthlyPayments.get(4));
		fmonthlyPayments.add(features_monthlyPayments.get(12));
		fmonthlyPayments.add(features_monthlyPayments.get(13));
		fmonthlyPayments.add(features_monthlyPayments.get(14));
		fmonthlyPayments.add(features_monthlyPayments.get(15));
		fmonthlyPayments.add(features_monthlyPayments.get(16));
		fmonthlyPayments.add(features_monthlyPayments.get(17));
		fmonthlyPayments.add(features_monthlyPayments.get(18));
		fmonthlyPayments.add(features_monthlyPayments.get(19));
		Dependency dep_monthlyPayments_propos=new Dependency(monthlyPayments,fmonthlyPayments.toArray(new Feature[fmonthlyPayments.size()]));
		dep_for_propositionalization.put(monthlyPayments.getPredicate(), dep_monthlyPayments_propos);

		//clientAge(C,L)
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_clientAge=fGen.generateFeatures(clientAge, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fclientAge=new ArrayList<Feature>();
		fclientAge.add(features_clientAge.get(0));
		fclientAge.add(features_clientAge.get(1));
		fclientAge.add(features_clientAge.get(2));
		fclientAge.add(features_clientAge.get(5));
		fclientAge.add(features_clientAge.get(6));
		fclientAge.add(features_clientAge.get(7));
		fclientAge.add(features_clientAge.get(29));
		fclientAge.add(features_clientAge.get(30));
		fclientAge.add(features_clientAge.get(32));
		fclientAge.add(features_clientAge.get(33));
		fclientAge.add(features_clientAge.get(34));
		fclientAge.add(features_clientAge.get(17));
		fclientAge.add(features_clientAge.get(18));

		Dependency dep_clientAge_propos=new Dependency(clientAge,fclientAge.toArray(new Feature[fclientAge.size()]));
		dep_for_propositionalization.put(clientAge.getPredicate(), dep_clientAge_propos);


		//freq
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_freq=fGen.generateFeatures(frequency, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> ffreq=new ArrayList<Feature>();
		ffreq.add(features_freq.get(33));
		ffreq.add(features_freq.get(34));
		ffreq.add(features_freq.get(35));
		ffreq.add(features_freq.get(1));
		ffreq.add(features_freq.get(9));
		ffreq.add(features_freq.get(10));
		ffreq.add(features_freq.get(11));
		ffreq.add(features_freq.get(2));
		ffreq.add(features_freq.get(3));
		ffreq.add(features_freq.get(4));
		ffreq.add(features_freq.get(5));
		ffreq.add(features_freq.get(6));
		ffreq.add(features_freq.get(117));
		ffreq.add(features_freq.get(118));
		ffreq.add(features_freq.get(7));
		ffreq.add(features_freq.get(8));
		Dependency dep_freq_propos=new Dependency(frequency,ffreq.toArray(new Feature[ffreq.size()]));
		dep_for_propositionalization.put(frequency.getPredicate(), dep_freq_propos);


		//avgNrWith
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgNrWith=fGen.generateFeatures(avgNrWith, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> favgNrWith=new ArrayList<Feature>();
		favgNrWith.add(features_avgNrWith.get(54));
		favgNrWith.add(features_avgNrWith.get(55));
		favgNrWith.add(features_avgNrWith.get(56));
		favgNrWith.add(features_avgNrWith.get(1));
		favgNrWith.add(features_avgNrWith.get(12));
		favgNrWith.add(features_avgNrWith.get(13));
		favgNrWith.add(features_avgNrWith.get(14));
		favgNrWith.add(features_avgNrWith.get(57));
		favgNrWith.add(features_avgNrWith.get(2));
		favgNrWith.add(features_avgNrWith.get(3));
		favgNrWith.add(features_avgNrWith.get(4));
		favgNrWith.add(features_avgNrWith.get(5));
		favgNrWith.add(features_avgNrWith.get(6));
		favgNrWith.add(features_avgNrWith.get(186));
		favgNrWith.add(features_avgNrWith.get(187));
		favgNrWith.add(features_avgNrWith.get(7));
		favgNrWith.add(features_avgNrWith.get(8));
		Dependency dep_avgNrWith_propos=new Dependency(avgNrWith,favgNrWith.toArray(new Feature[favgNrWith.size()]));
		dep_for_propositionalization.put(avgNrWith.getPredicate(), dep_avgNrWith_propos);


		//avgSumOfInc
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSumOfInc=fGen.generateFeatures(avgSumOfInc, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> favgSumOfInc=new ArrayList<Feature>();
		favgSumOfInc.add(features_avgSumOfInc.get(54));
		favgSumOfInc.add(features_avgSumOfInc.get(55));
		favgSumOfInc.add(features_avgSumOfInc.get(56));
		favgSumOfInc.add(features_avgSumOfInc.get(1));
		favgSumOfInc.add(features_avgSumOfInc.get(12));
		favgSumOfInc.add(features_avgSumOfInc.get(13));
		favgSumOfInc.add(features_avgSumOfInc.get(14));
		favgSumOfInc.add(features_avgSumOfInc.get(57));
		favgSumOfInc.add(features_avgSumOfInc.get(2));
		favgSumOfInc.add(features_avgSumOfInc.get(3));
		favgSumOfInc.add(features_avgSumOfInc.get(4));
		favgSumOfInc.add(features_avgSumOfInc.get(5));
		favgSumOfInc.add(features_avgSumOfInc.get(6));
		favgSumOfInc.add(features_avgSumOfInc.get(186));
		favgSumOfInc.add(features_avgSumOfInc.get(187));
		favgSumOfInc.add(features_avgSumOfInc.get(7));
		favgSumOfInc.add(features_avgSumOfInc.get(8));

		Dependency dep_avgSumOfInc_propos=new Dependency(avgSumOfInc,favgSumOfInc.toArray(new Feature[favgSumOfInc.size()]));
		dep_for_propositionalization.put(avgSumOfInc.getPredicate(), dep_avgSumOfInc_propos);




		//avgSumOfW
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_avgSumOfW=fGen.generateFeatures(avgSumOfW, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> favgSumOfW=new ArrayList<Feature>();
		favgSumOfW.add(features_avgSumOfW.get(54));
		favgSumOfW.add(features_avgSumOfW.get(55));
		favgSumOfW.add(features_avgSumOfW.get(56));
		favgSumOfW.add(features_avgSumOfW.get(1));
		favgSumOfW.add(features_avgSumOfW.get(12));
		favgSumOfW.add(features_avgSumOfW.get(13));
		favgSumOfW.add(features_avgSumOfW.get(14));
		favgSumOfW.add(features_avgSumOfW.get(57));
		favgSumOfW.add(features_avgSumOfW.get(2));
		favgSumOfW.add(features_avgSumOfW.get(3));
		favgSumOfW.add(features_avgSumOfW.get(4));
		favgSumOfW.add(features_avgSumOfW.get(5));
		favgSumOfW.add(features_avgSumOfW.get(6));
		favgSumOfW.add(features_avgSumOfW.get(186));
		favgSumOfW.add(features_avgSumOfW.get(187));
		favgSumOfW.add(features_avgSumOfW.get(7));
		favgSumOfW.add(features_avgSumOfW.get(8));

		Dependency dep_avgSumOfW_propos=new Dependency(avgSumOfW,favgSumOfW.toArray(new Feature[favgSumOfW.size()]));
		dep_for_propositionalization.put(avgSumOfW.getPredicate(), dep_avgSumOfW_propos);


		//stdMonthInc
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_stdMonthInc=fGen.generateFeatures(stdMonthInc, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fstdMonthInc=new ArrayList<Feature>();
		fstdMonthInc.add(features_stdMonthInc.get(54));
		fstdMonthInc.add(features_stdMonthInc.get(55));
		fstdMonthInc.add(features_stdMonthInc.get(56));
		fstdMonthInc.add(features_stdMonthInc.get(0));
		fstdMonthInc.add(features_stdMonthInc.get(1));
		fstdMonthInc.add(features_stdMonthInc.get(12));
		fstdMonthInc.add(features_stdMonthInc.get(13));
		fstdMonthInc.add(features_stdMonthInc.get(14));
		fstdMonthInc.add(features_stdMonthInc.get(57));
		fstdMonthInc.add(features_stdMonthInc.get(2));
		fstdMonthInc.add(features_stdMonthInc.get(3));
		fstdMonthInc.add(features_stdMonthInc.get(4));
		fstdMonthInc.add(features_stdMonthInc.get(5));
		fstdMonthInc.add(features_stdMonthInc.get(6));
		fstdMonthInc.add(features_stdMonthInc.get(186));
		fstdMonthInc.add(features_stdMonthInc.get(187));
		fstdMonthInc.add(features_stdMonthInc.get(7));
		fstdMonthInc.add(features_stdMonthInc.get(8));

		Dependency dep_stdMonthInc_propos=new Dependency(stdMonthInc,fstdMonthInc.toArray(new Feature[fstdMonthInc.size()]));
		dep_for_propositionalization.put(stdMonthInc.getPredicate(), dep_stdMonthInc_propos);



		//stdMonthW
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_stdMonthW=fGen.generateFeatures(stdMonthW, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fstdMonthW=new ArrayList<Feature>();
		fstdMonthW.add(features_stdMonthW.get(54));
		fstdMonthW.add(features_stdMonthW.get(55));
		fstdMonthW.add(features_stdMonthW.get(56));
		fstdMonthW.add(features_stdMonthW.get(0));
		fstdMonthW.add(features_stdMonthW.get(1));
		fstdMonthW.add(features_stdMonthW.get(12));
		fstdMonthW.add(features_stdMonthW.get(13));
		fstdMonthW.add(features_stdMonthW.get(14));
		fstdMonthW.add(features_stdMonthW.get(57));
		fstdMonthW.add(features_stdMonthW.get(2));
		fstdMonthW.add(features_stdMonthW.get(3));
		fstdMonthW.add(features_stdMonthW.get(4));
		fstdMonthW.add(features_stdMonthW.get(5));
		fstdMonthW.add(features_stdMonthW.get(6));
		fstdMonthW.add(features_stdMonthW.get(186));
		fstdMonthW.add(features_stdMonthW.get(187));
		fstdMonthW.add(features_stdMonthW.get(7));
		fstdMonthW.add(features_stdMonthW.get(8));

		Dependency dep_stdMonthW_propos=new Dependency(stdMonthW,fstdMonthW.toArray(new Feature[fstdMonthW.size()]));
		dep_for_propositionalization.put(stdMonthW.getPredicate(), dep_stdMonthW_propos);


		 for(Feature f:features_stdMonthW){
			System.out.println(f);
		} 

		//ratUrbInhab
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_ratUrbInhab=fGen.generateFeatures(ratUrbInhab, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fratUrbInhab=new ArrayList<Feature>();
		fratUrbInhab.add(features_ratUrbInhab.get(0));
		fratUrbInhab.add(features_ratUrbInhab.get(1));
		fratUrbInhab.add(features_ratUrbInhab.get(3));
		fratUrbInhab.add(features_ratUrbInhab.get(24));
		fratUrbInhab.add(features_ratUrbInhab.get(25));
		fratUrbInhab.add(features_ratUrbInhab.get(4));
		fratUrbInhab.add(features_ratUrbInhab.get(26));
		fratUrbInhab.add(features_ratUrbInhab.get(27));
		fratUrbInhab.add(features_ratUrbInhab.get(28));
		fratUrbInhab.add(features_ratUrbInhab.get(29));
		fratUrbInhab.add(features_ratUrbInhab.get(30));
		fratUrbInhab.add(features_ratUrbInhab.get(31));
		fratUrbInhab.add(features_ratUrbInhab.get(2));
		fratUrbInhab.add(features_ratUrbInhab.get(6));
		fratUrbInhab.add(features_ratUrbInhab.get(7));

		Dependency dep_ratUrbInhab_propos=new Dependency(ratUrbInhab,fratUrbInhab.toArray(new Feature[fratUrbInhab.size()]));
		dep_for_propositionalization.put(ratUrbInhab.getPredicate(), dep_ratUrbInhab_propos);

		//hasAccount
		fGen=new FeatureGeneratorNoRestrictions(3, 2);
		List<Feature> features_hasAccount=fGen.generateFeatures(hasAccount, ntwInfo.getAtomsAndEqualityConstraints());
		List<Feature> fhasAccount=new ArrayList<Feature>();
		fhasAccount.add(features_hasAccount.get(0));
		fhasAccount.add(features_hasAccount.get(1));
		fhasAccount.add(features_hasAccount.get(2));
		fhasAccount.add(features_hasAccount.get(4));
		fhasAccount.add(features_hasAccount.get(25));
		fhasAccount.add(features_hasAccount.get(26));
		fhasAccount.add(features_hasAccount.get(27));
		fhasAccount.add(features_hasAccount.get(5));
		fhasAccount.add(features_hasAccount.get(6));
		fhasAccount.add(features_hasAccount.get(7));
		fhasAccount.add(features_hasAccount.get(8));
		fhasAccount.add(features_hasAccount.get(9));
		fhasAccount.add(features_hasAccount.get(10));
		fhasAccount.add(features_hasAccount.get(11));
		fhasAccount.add(features_hasAccount.get(19));
		fhasAccount.add(features_hasAccount.get(20));

		Dependency dep_hasAccount_propos=new Dependency(hasAccount,fhasAccount.toArray(new Feature[fhasAccount.size()]));
		dep_for_propositionalization.put(hasAccount.getPredicate(), dep_hasAccount_propos);


		//Propositionalize 


		return dep_for_propositionalization;

	}

	public NetworkInfo getNtwInfo() {
		return ntwInfo;
	}





}
*/