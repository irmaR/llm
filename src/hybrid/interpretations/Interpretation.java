package hybrid.interpretations;
import hybrid.network.Atom;
import hybrid.network.GroundAtom;
import hybrid.network.Logvar;
import hybrid.utils.GenerateUIDForInterpretation;

import java.util.List;
import java.util.Set;

/**
 * Each interpretation consists of a domain for that interpretation 
 * and a mapping from atoms to their ground atoms in the interpretation.
 * Each interpretation also has separate negative atoms information. That is, how many negative
 * atoms we had to include in the learning procedure. These negative examples could be obtained by
 * subsampling a certain number of them, or assign false value to those ground atoms that are not specified in the
 * input interpretation(CWA).
 * @author irma
 *
 */
public class Interpretation {
    
	//represents the domain (that is, constants in the domain)
	private Domain domain;
	//Holds the assignment to ground atoms (that is, ground atom and their values in the interpretation)
	private Assignment groundAtoms;
	//Information over negative examples
	private NegativeVarsInfo subSampleInfo;
	//interpretation ID
	private long id;
	//path to interpretation
	private String path_to_interpretation;
	private String prologFormat=null;
	
	/**
	 * Creating interpretation by specifiying domain and assignments to ground atoms
	 * @param domain - domain for the interpretation
	 * @param groundAtoms - mapping from Atoms to the list of the ground atoms in the intepretation
	 */
	public Interpretation(Domain domain,Assignment groundAtoms,String path_to_interpretation){
		this.domain=domain;
		this.groundAtoms=groundAtoms;
		this.path_to_interpretation=path_to_interpretation;
		this.id=GenerateUIDForInterpretation.getID();
	}

	/**
	 * Creating interpretation with additional subsampling information
	 * @param domain - domain for the interpretation
	 * @param groundAtoms - mapping from Atoms to the list of the ground atoms in the intepretation
	 * @param sI - subSampling information
	 */
	public Interpretation(Domain domain,Assignment groundAtoms,NegativeVarsInfo sI,String path_to_interpretation){
		this.domain=domain;
		this.groundAtoms=groundAtoms;
		this.subSampleInfo=sI;
		this.id=GenerateUIDForInterpretation.getID();
        this.path_to_interpretation=path_to_interpretation;
	}
	
	public Domain getDomain() {
		return domain;
	}

	public Assignment getGroundAtoms() {
		return this.groundAtoms;
	}
	
	/**
	 * Printing inerpretation under CWA. That is, false atoms are not shown.
	 */
	public String toString(){
		 return "INTERPRETATION"+path_to_interpretation+" ID: "+this.id;
	}
	
	/**
	 * Printing inerpretation under CWA in prolog format. That is, false atoms are not shown.
	 */
	public String getPrologFormat(){
		if(prologFormat==null || prologFormat.isEmpty()){
		String tmp="";
		tmp+=domain.prologFormat();
		tmp+=groundAtoms.toPrologFormat();
		prologFormat=tmp;
		return tmp;
		}
		else{
			return prologFormat;
		}
	}
	/**
	 * Printing interpretation for both positive and negative atoms
	 * @return
	 */
	public String getDatabaseFormat(){
		String tmp="";
		tmp+=domain.prologFormat();
		tmp+=groundAtoms.printVarValuePair();
		return tmp;
	}

	/**
	 * Get all ground atoms in this interpretation for atom a
	 * @param a - atom
	 * @return Arraylist of all ground atoms for atom a
	 */
	public List<GroundAtom> getGroundAtoms(Atom a) {
		return this.groundAtoms.getAssignmentFor(a);
	}

	public NegativeVarsInfo getSubSampleInfo() {
		return subSampleInfo;
	}
	
	/**
	 * Calculate the number of possible groundings for logvars in outputArguments.
	 * It just multiply the domains of logvars in the set.
	 * @param outputArguments
	 * @return
	 */
	public int getNrPossibleGroundings(Set<Logvar> outputArguments) {
		int count=1;
		for(Logvar a:outputArguments){
			count*=this.domain.getDomainElements().get(a.getType()).size();
		}
		return count;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Interpretation other = (Interpretation) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public long getId() {
		return id;
	}

	public String getPath_to_interpretation() {
		return path_to_interpretation;
	}

	public String getInfo() {
		String tmp=" Interp: "+this.path_to_interpretation+" \n";
		tmp+="-------- Assignment details: \n ----------------";
		for(Atom a:this.groundAtoms.getAssignment().keySet()){
			tmp+=" ATOM: "+a+" ";
			if(a.getPredicate().isBoolean()){
				tmp+= " Nr true groundings: "+(this.groundAtoms.getAssignmentFor(a).size()-this.groundAtoms.getNegatives().get(a).size())+ " Nr negative groundings: "+this.groundAtoms.getNegatives().get(a).size()+"\n";
			}
			else{
				tmp+= " Nr groundings: "+this.groundAtoms.getAssignmentFor(a).size()+"\n";

			}
			
		}
		return tmp;
	}

  
	
	
	
	
}
