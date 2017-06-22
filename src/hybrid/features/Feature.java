package hybrid.features;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alice.tuprolog.NoSolutionException;
import hybrid.converters.ConvertPoolInterface;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.Range;
import hybrid.network.Value;
import hybrid.queryMachine.ArrayFeatureValues;
import hybrid.queryMachine.FeatureValue;
import hybrid.queryMachine.NumberFeatureValue;
import hybrid.queryMachine.QueryDispatcher;

public abstract class Feature <A extends FeatureValue> implements Serializable{
	protected Atom non_boolean_atom;
	protected AbstractConjunction<Literal> conjunction;
	protected long id;
	private Long saved_hash;
	protected int index_in_feature_space;
	protected SelectorTerm selector_term;
	protected int feature_block;
	protected Value conditional_value;
	//Each feature can have a complex feature as a field. Than it should return average or value of it
	//protected Complex_Feature complex_feature=null;


	public Feature(){
		this.id=hybrid.utils.GenerateUIDForFeature.getID();
		this.saved_hash=new Long(hashCode());
	}


	public Feature(AbstractConjunction c){
		this.id=hybrid.utils.GenerateUIDForFeature.getID();
		this.saved_hash=new Long(hashCode());
		this.conjunction=c;
		if(c.getNon_boolean_literal()!=null){
			this.non_boolean_atom=c.getNon_boolean_literal().getAtom();
		}
	}

	//ABSTRACT METHODS
	public abstract boolean isDiscreteInput();
	public abstract boolean isContinuousInput();
	public abstract boolean isContinuousOutput();
	public abstract boolean isDiscreteOutput();
	public abstract boolean isComplex();
	public abstract boolean isDeterministic();
	public abstract boolean is_with_operator();
	public abstract Value processValue(A featureValue);
	public abstract Range getRange();
	public abstract Value dispatch(QueryDispatcher queryDisp);
	public abstract String convert(ConvertPoolInterface converter);
	/**
	 * This method constructs the feature identifier: i.e, how feature would be uniquely represented
	 * @return
	 */
	public abstract String getFeatureIdentifier();

	public  int getLength(){
		return conjunction.getLiteralList().size();
	}

	public List<Literal> getSelector() {
		return this.conjunction.getBooleanAtoms();
	}

	public Atom getAtom() {
		return non_boolean_atom;
	}

	public void setAtom(Atom a) {
		this.non_boolean_atom = a;
	}

	protected Set<Logvar> getDifference(Set<Logvar> logvars, List<Atom> allatoms) {
		Set<Logvar> outputArguments=new HashSet<Logvar>();
		for(Atom a:allatoms){
			for(Logvar l:a.getArguments()){
				if(!logvars.contains(l)){
					outputArguments.add(l);
				}
			}
		}
		return outputArguments;

	}	
	
	
	public void setConditionalValue(Value val){
		this.conditional_value=val;
	}
	
	public Value getConditionalValue(){
		return this.conditional_value;
	}

	public Set<Logvar> getUnboundArguments() {
		return new HashSet<Logvar>(this.conjunction.getOutputLogvars());
	}


	@Override
	public int hashCode() {
		if(saved_hash==null){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((non_boolean_atom == null) ? 0 : non_boolean_atom.hashCode());
			result = prime * result
					+ ((conjunction == null) ? 0 : conjunction.hashCode());
			return result;
		}
		else{
			return this.saved_hash.intValue();
		}
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		if (non_boolean_atom == null) {
			if (other.non_boolean_atom != null)
				return false;
		} else if (!non_boolean_atom.equals(other.non_boolean_atom))
			return false;
		if (conjunction == null) {
			if (other.conjunction != null)
				return false;
		} else if (!conjunction.equals(other.conjunction))
			return false;
		return true;
	}


	public Atom getA() {
		return non_boolean_atom;
	}


	public AbstractConjunction<Literal> getConjunction() {
		return conjunction;
	}


	public long getId() {
		return id;
	}


	public void setIndexInFeatureSpace(int i) {
		this.index_in_feature_space=i;

	}

	public String toString(){
		return " ";
		//this.index_in_feature_space+" ";
	}

	/**
	 * get number of literals in the feature
	 * @return
	 */
	public int getFeatureLength() {
		int length= conjunction.getNr_booleanVars();
		if(this.non_boolean_atom!=null){
			length++;
		}
		return length;
	}


	public Atom getNon_boolean_atom() {
		return non_boolean_atom;
	}


	public Long getSaved_hash() {
		return saved_hash;
	}


	public int getIndexInFeatureSpace() {
		return index_in_feature_space;
	}


	public Object getSelector_term() {
		if(selector_term==null){
			return null;
		}
		return selector_term.getTerm();
	}


	public void setSelector_term(Object obj) {
		this.selector_term=(SelectorTerm) obj;
	}


	public void setFeatureBlock(int block_number) {
		feature_block=block_number;

	}

	public int getFeatureBlock() {
		return feature_block;

	}

	public String getFeatureIdentifier_weka() {
		return "";
	}






}
