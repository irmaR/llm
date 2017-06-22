package hybrid.dependencies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import hybrid.cpds.CPD;
import hybrid.cpds.CPDGenerator;
import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.Predicate;
import hybrid.queryMachine.FeatureCache;
/**
 * Class representing a dependency. Each dependency is associated with a list of features in the parent set (warning:
 * the order of features matters, and they will be processed in the order fixed with this constructor).
 * A dependency is associated with an appropriate CPD distribution.
 * @author irma
 *
 */
public class Dependency implements Serializable{
	private Atom head;
	private List<Feature> features;
	private CPD cpd;
	private List<Feature> discreteFeatures;
	private List<Feature> continuousFeatures;
	private boolean logRegOnlyContinuous=false;
	
	/**
	 * Construct a dependency
	 * @param head
	 * @param features
	 * @param cpd
	 */
	public Dependency(Atom head,Feature[] features){
		CPDGenerator cpdGenerator=new CPDGenerator();
		discreteFeatures=new ArrayList<Feature>();
		continuousFeatures=new ArrayList<Feature>();
		this.head=head;
		this.features=new ArrayList<Feature>(Arrays.asList(features));
		try {
			fixFeatureOrder(features);
		} catch (WrongTargetAtomSpecification e) {
			e.printStackTrace();
		}
		this.cpd=cpdGenerator.getAppropriateCPD(this);
	}
	
	/**
	 * Construct a dependency
	 * @param head
	 * @param features
	 * @param cpd
	 */
	public Dependency(Atom head,Feature[] features,boolean constructCPD,boolean dummy){
		CPDGenerator cpdGenerator=new CPDGenerator();
		discreteFeatures=new ArrayList<Feature>();
		continuousFeatures=new ArrayList<Feature>();
		this.head=head;
		this.features=new ArrayList<Feature>(Arrays.asList(features));
	}

	
	public Dependency(Atom head,Feature[] features,boolean special){
		this.logRegOnlyContinuous=special;
		CPDGenerator cpdGenerator=new CPDGenerator();
		discreteFeatures=new ArrayList<Feature>();
		continuousFeatures=new ArrayList<Feature>();
		this.head=head;
		this.features=new ArrayList<Feature>(Arrays.asList(features));
		try {
			fixFeatureOrder(features);
		} catch (WrongTargetAtomSpecification e) {
			e.printStackTrace();
		}
		this.cpd=cpdGenerator.getAppropriateCPD(this);
	}
	
	/**
	 * Extend existing dependency with a new feature
	 * @param ft - new feature
	 * @return new extended dependency
	 * @throws FeatureAlreadyExists
	 */
	public Dependency extend(Feature ft) throws FeatureAlreadyExists{
		if(this.features.contains(ft)){
			throw new FeatureAlreadyExists();
		}
		List<Feature> old=new ArrayList<Feature>();
		old.addAll(this.features);
		old.add(ft);
		Dependency newDep=null;
		if(this.logRegOnlyContinuous){
			 newDep=new Dependency(this.head,old.toArray(new Feature[old.size()]),this.logRegOnlyContinuous);
		}
		else{
			 newDep=new Dependency(this.head,old.toArray(new Feature[old.size()]));
		}
		
		
		return newDep;
	}
	
	public Dependency copy(){
		List<Feature> old=new ArrayList<Feature>();
		old.addAll(this.features);
		Dependency newDep=null;
		if(this.logRegOnlyContinuous){
			 newDep=new Dependency(this.head,old.toArray(new Feature[old.size()]),this.logRegOnlyContinuous);
		}
		else{
			 newDep=new Dependency(this.head,old.toArray(new Feature[old.size()]));
		}
		
		
		return newDep;
	}


/**
 * Fix feature order. This will be important when dealing with e.g., CLG distributions when it is important
 * to have a fixed order on discrete parents as we need to deal with all their instantiations. 
 * @param features2
 * @throws WrongTargetAtomSpecification 
 */
	private void fixFeatureOrder(Feature[] features2) throws WrongTargetAtomSpecification {
		for(Feature f:features2){
			if(!f.getConjunction().getHead().equals(this.head)){
				throw new WrongTargetAtomSpecification(" The features you are trying to add to the parent set of "+this.head+ " were not created specifically for this atom!");
			}
			if(f.isDiscreteOutput()){
				this.discreteFeatures.add(f);
			}
			else if(f.isContinuousOutput()){
				this.continuousFeatures.add(f);
			}
		}
		
	}

	public Atom getHead() {
		return head;
	}

	public void setHead(Atom head) {
		this.head = head;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public CPD getCpd() {
		return cpd;
	}

	public void setCpd(CPD cpd) {
		this.cpd = cpd;
	}


	public List<Feature> getDiscreteFeatures() {
		return discreteFeatures;
	}


	public List<Feature> getContinuousFeatures() {
		return continuousFeatures;
	}
	
	public String toString(){
		String tmp=this.head+" | ";
		int i=1;
		for(Feature f:this.features){
			tmp+="["+f+"]_";
		}
		return tmp;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((features == null) ? 0 : features.hashCode());
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dependency other = (Dependency) obj;
		if (features == null) {
			if (other.features != null)
				return false;
		} else if (!features.equals(other.features))
			return false;
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		return true;
	}

	public int getNumLiterals() {
		int tmp=1;
		for(Feature f:this.features){
			tmp+=f.getFeatureLength();
		}
		return tmp;
	}

	public void setLogRegressionOnlyContinuous() {
		this.logRegOnlyContinuous=true;
		
	}

	public boolean isLogRegOnlyContinuous() {
		return this.logRegOnlyContinuous;
	}

	
}
