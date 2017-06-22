package hybrid.interpretations;

import hybrid.experimenter.AlgorithmParameters;
import hybrid.network.Argument;
import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.BooleanPred;
import hybrid.network.Constant;
import hybrid.network.GroundAtom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.Subst;
import hybrid.network.TermRDN;
import hybrid.network.Type;
import hybrid.network.Value;
import hybrid.network.ValueFactory;
import hybrid.network.WrongValueType;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;

/**
 * This class creates an interpretation which contains only positive examples found in the
 * input interpretation. That is, if we have friend(bob,ann)=true and friend(bob,maria)=true
 * in the input, it means that our interpretation will consists of only these two facts, but not for instance
 * of friend(ann,maria) as that is false in the input (i.e., friend(ann,maria)=false)
 * @author irma
 *
 */
public class TuPrologInterpretationCreator_positive_examples implements InterpretationCreator {
	
	@Override
	public Interpretation createInterpretation(NetworkInfo ntw,String pathToFile,String data_type) {
		Prolog engine=new Prolog();
		try {
				new BufferedWriter(new FileWriter(AlgorithmParameters.getDataLoadingFile(),true)).append("------------ Building interpretation for non_booleans :   "+pathToFile+" ---------------\n");
			} 
			catch(NullPointerException e){
				//parameter not set, ignore
			}
			catch (IOException e1) {
				//just ignore
			}
			InputStream tmp1=null;
			try {
				tmp1 = new FileInputStream(pathToFile);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			try {
				engine.setTheory(new Theory(tmp1));
			} catch (InvalidTheoryException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			alice.tuprolog.Parser parser=null;
			try{
				parser=new alice.tuprolog.Parser(engine.getTheory().toString());
			}
			catch(Exception e){
				System.out.println("Creating tuProlog Parser didn't succeed!! ");
				System.exit(1);
			}
			Domain domain=null;
			domain=new Domain(ntw.getTypes());
			Assignment assgn=new Assignment();
			ValueFactory vFactory=new ValueFactory();
			Iterator<Term> it=parser.iterator();	
			HashMap<TermRDN,Long> term_counts=new HashMap<TermRDN, Long>();

			while(it.hasNext()){
				Struct t=(Struct)it.next();
				Predicate pr=null;
				TermRDN term=ntw.getTerm(((Struct)t).getName());

				if(!term_counts.containsKey(term)){
					term_counts.put(term,new Long(0));
				}

				if(term!=null && (term instanceof Type)){
					domain.addDomainElement((Type)term,((Struct)t).getArg(0).toString());
				}

				else if(term!=null && (term instanceof Literal)){
					HashMap<Logvar,Argument> temp=new HashMap<Logvar, Argument>();
					int i=0;
					for(Logvar l: ((Literal)term).getAtom().getArguments()){
						temp.put(l, domain.getElement(l.getType(),((Struct)t).getArg(i++).toString()));
					}	
					Value v=null;
					try {
						v = getValue(((Literal)term).getAtom(),((Struct)t),vFactory);
					} catch (WrongArgumentNumber e1) {
						e1.printStackTrace();
					}
					//the range is not set (the predicate was either discretized and the range has to be determined from the
					//data, or when predicate was created, the range was not set
					//Only for discrete predicates!
					if(((Literal)term).getAtom().getPredicate().isDiscrete() || (((Literal)term).getAtom().getPredicate().isDiscretized() || !((Literal)term).getAtom().getPredicate().isRangeDetermined())){                    
						if(v.isnumeric()){
							try {
								v=new StringValue(String.valueOf(v.toNumber().intValue()));
							} catch (WrongValueType e) {
								e.printStackTrace();
							}
						}
						//add to range only if it is from the training datas
						if(data_type.equals("training")){
						((Literal)term).getAtom().getPredicate().addToRange(v);
						}
					}
					Subst sub=new Subst(temp);
					GroundAtom grAtom = null;
					try {
						grAtom = new GroundAtom(((Literal)term).getAtom(),sub,t.toString(),v);
						if(data_type.equals("training")){
						((Literal)term).getAtom().getPredicate().addToRange(v);
						}
						if(grAtom.getAtom().getPredicate() instanceof BooleanPred){

						}
					} catch (WrongValueType e) {
						e.printStackTrace();
					}
					term_counts.put(term, term_counts.get(term)+1);
					assgn.addRandVar(((Literal)term).getAtom(), grAtom);
				}
			}
			return new Interpretation(domain,assgn,pathToFile);
		}

	/**
	 * Given a prolog term ,extract a value of the atom and create the appropriate value
	 * (StringValue, Number or so) given the type of the value returned from Prolog
	 * @param term
	 * @param t
	 * @param vFactory
	 * @return
	 * @throws WrongArgumentNumber 
	 */

	 Value getValue(Atom term, Struct t,ValueFactory vFactory) throws WrongArgumentNumber {
		if(term.getPredicate() instanceof BooleanPred){
			return new BoolValue("true");
		}
		Value val=null;
		if(((Struct)t).getArity()-1!=term.getPredicate().getArity()){
			throw new WrongArgumentNumber("Maybe the predicate should be specified as Boolean? Otherwise, The predicate specificiation for "+term.getPredicate()+" had wrong number of parameters. According to the current interpretation: Arity= "+(((Struct)t).getArity()-1));
		}
		Object value=((Struct)t).getArg(term.getPredicate().getArity());
		if(value instanceof alice.tuprolog.Double){
			val=vFactory.getValue(((alice.tuprolog.Double)value).doubleValue());
		}
		else if(value instanceof alice.tuprolog.Int){
			val=vFactory.getValue(((alice.tuprolog.Int)value).doubleValue());
		}
		else{
			val=vFactory.getValue(value.toString());
		}
		return val;
	}





}
