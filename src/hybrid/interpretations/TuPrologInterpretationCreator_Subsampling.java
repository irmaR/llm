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
import hybrid.network.SubstitutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;

/**
 * This class creates interpretation from input files and for network ntw,
 * such that it performs subsampling of negative examples (how each 
 * predicate is to be subsampled is defined through the constructor of the predicate)
 * @author irma
 *
 */
public class TuPrologInterpretationCreator_Subsampling implements InterpretationCreator {
	//ratio of negative examples to positive examples ; by default = 1
	private double ratio=1.0;
	//subsampling constraints; e.g., no cycles [friend(ann,ann)] or so
	private SubSamplingConstraints constraints;
	private Prolog engine;


	/**
	 * Initialize NoSubSampling procedure of creating interpretations from input data
	 * with a constraint on arguments/constants.
	 * @param constraints
	 */
	public TuPrologInterpretationCreator_Subsampling(SubSamplingConstraints constraints,double ratio){
		this.constraints=constraints;
		this.ratio=ratio;
	}

	/**
	 * Create TuPrologSubsample object for subsampling negative examples to ratio positive examples
	 * @param ratio
	 */
	public TuPrologInterpretationCreator_Subsampling(double ratio){
		this.ratio=ratio;
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
		System.out.println("------------ Building interpretation with subsampling:   "+pathToFile+" ---------------");
		TuPrologInterpretationCreator_positive_examples positive_examples_only=new TuPrologInterpretationCreator_positive_examples();
		Interpretation tmp=positive_examples_only.createInterpretation(ntw, pathToFile,data_type);
		NegativeVarsInfo subInfo=new NegativeVarsInfo();
		for(Atom a:ntw.getBooleanAtoms()){
			try {
				subSampleNegativeExamples(a,subInfo,pathToFile,tmp.getDomain(),tmp.getGroundAtoms(),ntw.getBooleanAtoms(),engine);
			} catch (MalformedGoalException e) {
				e.printStackTrace();
			} catch (SubstitutionException e) {
				e.printStackTrace();
			} catch (NotPossibleToSubsampleException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new Interpretation(tmp.getDomain(),tmp.getGroundAtoms(),subInfo,pathToFile);
	}

	/**
	 * SubSampling negative examples - assuming predicate has only two arguments!!!
	 * @param assgn 
	 * @param atoms - atoms for which subsampling is needed
	 * @return
	 * @throws MalformedGoalException 
	 * @throws SubstitutionException 
	 * @throws NotPossibleToSubsampleException 
	 * @throws IOException 
	 */
	private void subSampleNegativeExamples(Atom a,NegativeVarsInfo subInfo,String log_subsamples_path,Domain dm,Assignment assgn, ArrayList<Atom> atoms,Prolog engine) throws SubstitutionException, MalformedGoalException, NotPossibleToSubsampleException, IOException{
		String file_name=log_subsamples_path.substring(log_subsamples_path.lastIndexOf("/")+1,log_subsamples_path.lastIndexOf("."));

		FileWriter fW=null;
		try {
			fW=new FileWriter(new File(log_subsamples_path.substring(0,log_subsamples_path.lastIndexOf("/"))+file_name+".neg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.sampleForAtom(a, dm, assgn, subInfo, engine, fW);
		fW.close();
	}

	private void sampleForAtom(Atom a,Domain dm,Assignment assgn,NegativeVarsInfo sI,Prolog engine,FileWriter fW) throws MalformedGoalException, IOException, SubstitutionException, NotPossibleToSubsampleException{
		if(a.getPredicate() instanceof BooleanPred){	
			System.out.println("////////////////// Subsampling atom "+a+" ////////////////////////////");
			long nrOfPossibleGroundings=1;
			
			//calcualte the number of possible groundings given the domain of the logvar
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
			Integer nr_negative_Groundings=(int) Math.ceil(ratio*nr_trueGroundings);

			if((nr_negative_Groundings+nr_trueGroundings)>nrOfPossibleGroundings){
				nr_negative_Groundings=(int) (nrOfPossibleGroundings-nr_trueGroundings);
			}


			sI.addNegativesPercentage(a,((double)(nrOfPossibleGroundings-nr_trueGroundings))/nrOfPossibleGroundings);
			sI.addPositivesPercentage(a,((double)nr_trueGroundings)/nrOfPossibleGroundings);
			sI.set_true_number_of_groundigs(a, nrOfPossibleGroundings);

			//System.out.println(" Nr true groundings "+nr_trueGroundings);
			//System.out.println(" Nr negative groundings "+nr_negative_Groundings);

			if(nr_negative_Groundings+nr_trueGroundings>nrOfPossibleGroundings){
				//	System.out.println(" Number of negatives bigger than the number of positive groundings");
				throw new NotPossibleToSubsampleException(" It is not possible the number of negative examples equal to positive examples. The reason is that the number of possible groundings for: "+a+" is: "+nrOfPossibleGroundings+" and number of positive groundings is: "+nr_trueGroundings+ " and it is possible to get at most "+(nrOfPossibleGroundings-nr_trueGroundings) +" negative groundings. \n Therefore, for this dataset choose subsampling with different ratio");

			}

			double alpha=this.ratio*nr_trueGroundings/(nrOfPossibleGroundings-nr_trueGroundings);

			sI.addSubSampleInfo(a, alpha);
			sI.addNrGroundings(a,new Integer(nr_negative_Groundings+nr_trueGroundings));
			sI.setNr_negatives(a, nr_negative_Groundings);
			sI.setNr_positives(a,nr_trueGroundings);
			sI.setSamplingPerformed(a, true);	
			int nr_negatives=0;

			if((nrOfPossibleGroundings-nr_trueGroundings)<nr_negative_Groundings){
				throw new NotPossibleToSubsampleException(" It is not possible the number of negative examples equal to positive examples. The reason is that the number of possible groundings for: "+a+" is: "+nrOfPossibleGroundings+" and number of positive groundings is: "+nr_trueGroundings+ " and it is possible to get at most "+(nrOfPossibleGroundings-nr_trueGroundings) +" negative groundings. \n Therefore, for this dataset choose subsampling with different ratio");
			}


			List<Integer> generatedCombinations=new ArrayList<Integer>();
			int nr_subsamples_created=0;

			List<List<Constant>> cartesian_product_negatives=new ArrayList<List<Constant>>();


			if(a.getArguments().size()==2){
				List<List<Constant>> domain=new ArrayList<List<Constant>>();
				domain.add(dm.getDomainElements().get(a.getArguments().get(0).getType()));
				domain.add(dm.getDomainElements().get(a.getArguments().get(1).getType()));
				hybrid.utils.CartesianProduct cP=new hybrid.utils.CartesianProduct<Constant>();
				cartesian_product_negatives=cP.cartesianProduct(domain);			
			}		
			Random rGen=new Random();
			for(int i=1;i<=nr_negative_Groundings;i++){				
				int flag=0;

				while(flag==0){
					List<Constant> selected=cartesian_product_negatives.get(rGen.nextInt(cartesian_product_negatives.size()));
					GroundAtom tmp=null;
					Constant c1=null;
					Constant c2=null;		

					if(a.getArguments().size()==2){
						c1=selected.get(0);
						c2=selected.get(1);

						//checking if we already generated this combination
						if(generatedCombinations.contains(new Integer(c1.hashCode()*2+c2.hashCode()*3))){
							cartesian_product_negatives.remove(selected);
							continue;
						}
						if(this.constraints!=null && !this.constraints.conformsToConstraint(selected, a)){
							cartesian_product_negatives.remove(selected);
							continue;
						}
						tmp= new GroundAtom(a,new Subst(new Logvar[]{a.getArgument(0),a.getArgument(1)},new Constant[]{c1,c2}),new BoolValue("true"));
					}
					else if(a.getArguments().size()==1){
						c1=selected.get(0);
						tmp = new GroundAtom(a,new Subst(new Logvar[]{a.getArgument(0)},new Constant[]{c1}),new BoolValue("true"));
					}
					
					if (engine.solve(tmp.getTerm()+".").isSuccess()) {	
						cartesian_product_negatives.remove(selected);
						continue;
					} else {
						flag=1;
						tmp.setValue(new BoolValue("false"));
						nr_negatives++;
						fW.append(tmp.createTermWithoutValue()+".\n");
						assgn.addSubSample(a, tmp);
						nr_subsamples_created++;
						cartesian_product_negatives.remove(selected);
					}
				}
			}
			sI.setNr_negatives(a,nr_negatives);

		}

	}


}
