package networks;

import hybrid.interpretations.NoCycles;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.RelationType;
import hybrid.network.StringValue;
import hybrid.network.Type;

import java.util.ArrayList;

public class PKDD_in_same_region_added_hybrid {
	
}
/*
	public NetworkInfo getHybridPKDD(int subsampling_ratio){
		Type cl=new Type("client");
		Type acc=new Type("account");
		Type dis=new Type("district");
		Type lo=new Type("loan");
		
		Logvar client=new Logvar("C",cl);
		Logvar account=new Logvar("A",acc);
		Logvar district=new Logvar("D",dis);
		Logvar district1=new Logvar("D1",dis);
		Logvar loan=new Logvar("L",lo);
		
		//client related
		BooleanPred clDistrP=new BooleanPred("clientDistrict",2);
		clDistrP.setSubsampleingProcedure(new TuPrologSubSample(new NoCycles(),subsampling_ratio));
		
		Predicate genderP=new CategoricalPred("gender",1,new StringValue[]{new StringValue("m"),new StringValue("f")});
		
		//loan related
		BooleanPred hasLoanP=new BooleanPred("hasLoan", 2);
		hasLoanP.setSubsampleingProcedure(new TuPrologSubSample(new NoCycles(),subsampling_ratio));

		Predicate loanAmountP=new GaussianPred("loanAmount", 1,0,10000);
		Predicate loanStatusP=new CategoricalPred("loanStatus", 1, new StringValue[]{new StringValue("a"),new StringValue("b"),new StringValue("c"),new StringValue("d")});
		Predicate monthlyPaymentsP=new GaussianPred("monthlyPayments", 1, 0,10000);
		Predicate clientAgeP=new GaussianPred("clientAge", 2, 1,80);
		
		//account related
		Predicate freqP=new CategoricalPred("freq", 1, new StringValue[]{new StringValue("m"),new StringValue("w"),new StringValue("i")});
		Predicate avgNrWithP=new GaussianPred("avgNrWith", 1);
		Predicate avgSumOfWP=new GaussianPred("avgSumOfW", 1);
		Predicate avgSumOfIncP=new GaussianPred("avgSumOfInc", 1);
		Predicate stdMonthIncP=new GaussianPred("stdMonthInc", 1);
		Predicate stdMonthWP=new GaussianPred("stdMonthW", 1);
		
		
		//district related
		Predicate avgSalaryP=new GaussianPred("avgSalary", 1);
		BooleanPred in_same_region_P=new BooleanPred("in_same_region", 2);
		in_same_region_P.setSubsampleingProcedure(new TuPrologSubSample(new NoCycles(),subsampling_ratio));
		Predicate ratUrbInhabP=new GaussianPred("ratUrbInhab", 1);
		
		//disposition
		BooleanPred hasAccountP=new BooleanPred("hasAccount", 2);
		hasAccountP.setSubsampleingProcedure(new TuPrologSubSample(new NoCycles(),subsampling_ratio));



		Atom clientDistrict=new Atom(clDistrP,new Logvar[]{client,district});
		clientDistrict.setRelationType(RelationType.INTERNAL);
		Atom gender=new Atom(genderP,new Logvar[]{client});
		Atom hasLoan=new Atom(hasLoanP,new Logvar[]{account,loan});
		Atom loanAmount=new Atom(loanAmountP,new Logvar[]{loan});
		Atom loanStatus=new Atom(loanStatusP,new Logvar[]{loan});
		Atom monthlyPayments=new Atom(monthlyPaymentsP,new Logvar[]{loan});
		Atom clientAge=new Atom(clientAgeP,new Logvar[]{client,loan});
		Atom frequency=new Atom(freqP,new Logvar[]{account});
		Atom avgNrWith=new Atom(avgNrWithP,new Logvar[]{account});
		Atom avgSumOfW=new Atom(avgSumOfWP,new Logvar[]{account});
		Atom avgSumOfInc=new Atom(avgSumOfIncP,new Logvar[]{account});
		Atom stdMonthInc=new Atom(stdMonthIncP,new Logvar[]{account});
		Atom stdMonthW=new Atom(stdMonthWP,new Logvar[]{account});
		Atom avgSalary=new Atom(avgSalaryP,new Logvar[]{district});
		Atom ratUrbInhab=new Atom(ratUrbInhabP,new Logvar[]{district});
		Atom hasAccount=new Atom(hasAccountP,new Logvar[]{client,account});
		Atom in_same_region=new Atom(in_same_region_P,new Logvar[]{district,district1});
		in_same_region.setRelationType(RelationType.INTERNAL);
		hasAccount.setRelationType(RelationType.INTERNAL);
		
		ArrayList<Atom> atoms=new ArrayList<Atom>();
		atoms.add(clientDistrict);
		atoms.add(gender);
		atoms.add(hasLoan);
		atoms.add(loanAmount);
		atoms.add(loanStatus);
		atoms.add(monthlyPayments);
		atoms.add(clientAge);
		atoms.add(frequency);
		atoms.add(avgNrWith);
		atoms.add(avgSumOfW);
		atoms.add(avgSumOfInc);
		atoms.add(stdMonthInc);
		atoms.add(stdMonthW);
		atoms.add(avgSalary);
		atoms.add(ratUrbInhab);
		atoms.add(hasAccount);
		atoms.add(in_same_region);
		
		ArrayList<Type> types=new ArrayList<Type>();
		types.add(cl);
		types.add(acc);
		types.add(dis);
		types.add(lo);
	
		return new NetworkInfo(atoms,types);

	}
}
*/