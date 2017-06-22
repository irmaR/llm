package hybrid.interpretations;

import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.BooleanPred;
import hybrid.network.Constant;
import hybrid.network.GroundAtom;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.RelationType;
import hybrid.network.Subst;
import hybrid.utils.CartesianProduct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;

/**
   This class creates an interpretation from input files and for network ntw,
 * such that it doesn't perform subsampling of negative examples. That is
 * we have all negative examples for a boolean atom.
 * @author irma
 *
 */

public class TuPrologInterpretationCreator_NoSubsampling implements InterpretationCreator{

	private SubSamplingConstraints constraints;
	private Prolog engine;

	/**
	 * Initialize NoSubSampling procedure of creating interpretations from input data
	 * with a constraint on arguments/constants.
	 * @param constraints
	 */
	public TuPrologInterpretationCreator_NoSubsampling(SubSamplingConstraints constraints){
		this.constraints=constraints;
	}

	public TuPrologInterpretationCreator_NoSubsampling(){

	}

	@Override
	public Interpretation createInterpretation(NetworkInfo ntw,String pathToFile,String data_type) {
		engine=new Prolog();
		try {
			Theory th;
			try {
				th = new Theory(new FileInputStream(new File(pathToFile)));
				engine.addTheory(th);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (InvalidTheoryException e1) {
			e1.printStackTrace();
		}
		NegativeVarsInfo subInfo=new NegativeVarsInfo();
		TuPrologInterpretationCreator_positive_examples positive_examples_only=new TuPrologInterpretationCreator_positive_examples();
		Interpretation tmp=positive_examples_only.createInterpretation(ntw, pathToFile,data_type);

		for(Atom a:ntw.getBooleanAtoms()){
			try {
				generateNegativeExamples(a,subInfo,tmp.getDomain(),tmp.getGroundAtoms(),ntw.getBooleanAtoms(),engine);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tmp;
	}

	/**
	 * For atom a, no subsampling
	 * @param a
	 * @param sI
	 * @param dm
	 * @param assgn
	 * @param atoms
	 * @param engine
	 * @throws Exception
	 */
	private void generateNegativeExamples(Atom a,NegativeVarsInfo sI,Domain dm,Assignment assgn, ArrayList<Atom> atoms,Prolog engine) throws Exception {
		System.out.println(" Generating negative examples without subsampling ...");
		sI.setSamplingPerformed(a, false);

		if(a.getPredicate() instanceof BooleanPred){		
			long nrOfPossibleGroundings=1;
			for(Logvar l:a.getArguments()){
				if(Character.isUpperCase(l.getSymbol().charAt(0))){
					nrOfPossibleGroundings*=dm.getDomainElements().get(l.getType()).size();
				}
				else{
					nrOfPossibleGroundings*=dm.getDomainElements().get(l.getType()).size();
				}
			}

			//In case of e.g., friend(S,S1) the number of possible groundings is
			//|S|x|S1|-|S| as a person is not a friend to herself
			if(a.getRelationType().equals(RelationType.INTERNAL)){
				nrOfPossibleGroundings-=dm.getDomainElements().get(a.getArguments().get(0).getType()).size();
			}
			Integer nr_trueGroundings=assgn.getAssignmentFor(a).size();
			//System.out.println(" Nr possible groundingS: "+nrOfPossibleGroundings);
			Integer nr_negative_Groundings=(int) (nrOfPossibleGroundings-nr_trueGroundings);
			//System.out.println(a+" Negative groundings: "+nr_negative_Groundings);
			sI.addNrGroundings(a, nr_negative_Groundings+nr_trueGroundings);
			sI.addNegativesPercentage(a,((double)nr_negative_Groundings)/nrOfPossibleGroundings);
			sI.addPositivesPercentage(a,((double)nr_trueGroundings)/nrOfPossibleGroundings);
			sI.addSubSampleInfo(a, 1.0);
			sI.set_true_number_of_groundigs(a, nrOfPossibleGroundings);

			//two arguments
			if(a.getArguments().size()==2){
				CartesianProduct<Constant> prod=new CartesianProduct<Constant>();
				List<List<Constant>> domain=new ArrayList<List<Constant>>();
				domain.add(dm.getDomainElements().get(a.getArguments().get(0).getType()));
				domain.add(dm.getDomainElements().get(a.getArguments().get(1).getType()));

				List<List<Constant>> domainCartesianProduct=prod.cartesianProduct(domain);

				for(List<Constant> l:domainCartesianProduct){
					if(this.constraints!=null && !this.constraints.conformsToConstraint(l, a)){
						continue;
					}
					GroundAtom tmp= new GroundAtom(a,new Subst(new Logvar[]{a.getArgument(0),a.getArgument(1)},new Constant[]{l.get(0),l.get(1)}),new BoolValue("true"));
					if (engine.solve(tmp.getTerm()+".").isSuccess()) {					
						continue;
					} else {
						tmp.setValue(new BoolValue("false"));
						assgn.addRandVar(a, tmp);
					}	
				}
			}
			else{
				for(Constant c:dm.getDomainElements().get(a.getArguments().get(0).getType())){
					GroundAtom tmp= new GroundAtom(a,new Subst(new Logvar[]{a.getArgument(0)},new Constant[]{c}),new BoolValue("true"));
					if (engine.solve(tmp.getTerm()+".").isSuccess()) {					
						continue;
					} else {
						tmp.setValue(new BoolValue("false"));
						assgn.addRandVar(a, tmp);
					}	
				}
			}
		}
	}



}
