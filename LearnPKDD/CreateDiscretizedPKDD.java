

import java.util.ArrayList;
import java.util.List;

import hybrid.network.*;

public class CreateDiscretizedPKDD {

}
/*
	
	public NetworkInfo getDiscretizedPKDD(int discretization_level,double subsampling_ratio){
		Type cl=new Type("client");
		Type acc=new Type("account");
		Type dis=new Type("district");
		Type lo=new Type("loan");
		
		Logvar client=new Logvar("C",cl);
		Logvar account=new Logvar("A",acc);
		Logvar district=new Logvar("D",dis);
		Logvar loan=new Logvar("L",lo);
		
		//client related
		BooleanPred clDistrP=new BooleanPred("clientDistrict",2);
		clDistrP.setSubsampleingProcedure(new TuPrologSubSample(subsampling_ratio));
		
		Predicate genderP=new CategoricalPred("gender",1,new StringValue[]{new StringValue("m"),new StringValue("f")});
		
		//loan related
		BooleanPred hasLoanP=new BooleanPred("hasLoan", 2);
		hasLoanP.setSubsampleingProcedure(new TuPrologSubSample(subsampling_ratio));

		Predicate loanAmountP=new DiscretizedPredicate("loanAmount", 1, discretization_level);
		Predicate loanStatusP=new CategoricalPred("loanStatus", 1, new StringValue[]{new StringValue("a"),new StringValue("b"),new StringValue("c"),new StringValue("d")});
		Predicate monthlyPaymentsP=new DiscretizedPredicate("monthlyPayments", 1, discretization_level);
		Predicate clientAgeP=new DiscretizedPredicate("clientAge", 2, discretization_level);
		
		//account related
		Predicate freqP=new CategoricalPred("freq", 1, new StringValue[]{new StringValue("m"),new StringValue("w"),new StringValue("i")});
		Predicate avgNrWithP=new DiscretizedPredicate("avgNrWith", 1, discretization_level);
		Predicate avgSumOfWP=new DiscretizedPredicate("avgSumOfW", 1, discretization_level);
		Predicate avgSumOfIncP=new DiscretizedPredicate("avgSumOfInc", 1, discretization_level);
		Predicate stdMonthIncP=new DiscretizedPredicate("stdMonthInc", 1, discretization_level);
		Predicate stdMonthWP=new DiscretizedPredicate("stdMonthW", 1, discretization_level);
		
		
		//district related
		Predicate avgSalaryP=new DiscretizedPredicate("avgSalary", 1, discretization_level);

		Predicate ratUrbInhabP=new DiscretizedPredicate("ratUrbInhab", 1, discretization_level);
		
		//disposition
		BooleanPred hasAccountP=new BooleanPred("hasAccount", 2);
		hasAccountP.setSubsampleingProcedure(new TuPrologSubSample(subsampling_ratio));



		Atom clientDistrict=new Atom(clDistrP,new Logvar[]{client,district});
		
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
		
		ArrayList<Type> types=new ArrayList<Type>();
		types.add(cl);
		types.add(acc);
		types.add(dis);
		types.add(lo);
	
		NetworkInfo ntw=new NetworkInfo(atoms,types);
return ntw;
	}
	
}
*/