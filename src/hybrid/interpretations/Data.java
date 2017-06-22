package hybrid.interpretations;

import hybrid.network.Atom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Class representing data consisting of a set of interpretations.
 * @author irma
 *
 */
public class Data {
	//all interpretations in the data
	private List<Interpretation> interpretations;
	//for each atom, the number of groundings accross all the interpretations
	private HashMap<Atom,Integer> nr_groundings_all_interpretations;
	//total number of randvars
	private long nr_randvars=0;


	public Data(){
		this.interpretations=new ArrayList<Interpretation>();
		this.nr_groundings_all_interpretations=new HashMap<Atom, Integer>();
	}

	/**
	 * Create data with a number of interpretations.
	 * @param interpretations
	 */
	public Data(List<Interpretation> interpretations) {
		this.nr_groundings_all_interpretations=new HashMap<Atom, Integer>();
		this.interpretations=interpretations;

		for(Interpretation i:interpretations){
			for(Atom a:i.getGroundAtoms().getAssignment().keySet()){
				try{
					this.nr_groundings_all_interpretations.put(a,this.nr_groundings_all_interpretations.get(a)+i.getGroundAtoms().getAssignmentFor(a).size());
					nr_randvars+=i.getGroundAtoms().getAssignmentFor(a).size();
				}
				catch(NullPointerException e){
					this.nr_groundings_all_interpretations.put(a,i.getGroundAtoms().getAssignmentFor(a).size());
					nr_randvars+=i.getGroundAtoms().getAssignmentFor(a).size();
				}
			}
		}
	}

    /**
     * Print data in the database format; atom-value pairs
     * @return
     */
	public String toDatabaseFormat(){
		String tmp=" ";
		for(Interpretation i:this.interpretations){
			tmp+=i.getDatabaseFormat();
			tmp+=" ---------------------------------------------------------------\n";
		}
		return tmp;
	}
	
	/**
	 * Get information about the data. 
	 * 1) The number of interpretations
	 * 2) Number of groundings per atom accross all interpretations
	 * 3) Domain size for each logvar
	 * @return
	 */
	public String getInfo() {
		String tmp=" Number of interpretations : "+interpretations.size();
		tmp+="Nr groundings per atom: \n";
		for(Atom a:this.nr_groundings_all_interpretations.keySet()){
			tmp+=a+" = "+this.nr_groundings_all_interpretations.get(a)+" ";
		}
		tmp+="\n---------------DOMAIN SIZES------------------------------------ \n";
		for(Interpretation i:interpretations){
			tmp+=i.getPath_to_interpretation()+" \n"+i.getDomain().getDomainSize()+"\n*******************************\n";
		}
		return tmp;
	}

    /**
     * Get all interpretation in the data
     * @return
     */
	public List<Interpretation> getInterpretations() {
		return interpretations;
	}

	
	public String toString(){
		String tmp="";
		int j=0;
		for(Interpretation i:interpretations){
			tmp+="--------------------- DATA "+(j++)+"-------------------\n";
			tmp+=i+"\n";
		}
		return tmp;	
	}

	/**
	 * Get number of groundings for an atom
	 * @param a
	 * @return
	 */
	public int getNrGroundingsInData(Atom a){
		if(!this.nr_groundings_all_interpretations.containsKey(a)){
			throw new NullPointerException(" SOmething wrong. There are no ground atoms for this atom "+a+". Check if the network corresponds to the input data!");
		}
		return this.nr_groundings_all_interpretations.get(a);
	}

	
   /**
    * Get total number of random variables
    * @return
    */
	public long getNrRandvars() {
		return this.nr_randvars;
	}

public HashMap<Interpretation,Integer> getNrGroundingsOfAtomPerInterpetation(Atom atom) {
	HashMap<Interpretation,Integer> counter=new HashMap<Interpretation,Integer>();
	for(Interpretation i:this.interpretations){
		counter.put(i, i.getGroundAtoms(atom).size());
	}
	return counter;
}

}


