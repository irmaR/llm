package hybrid.interpretations;

import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.GroundAtom;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains all the randvars (positive/negative examples) of atoms in an interpretation
 * @author irma
 *
 */

public class Assignment {
    //maps each atom to the list of its groundings in an interpretation
	private HashMap<Atom,List<GroundAtom>> assignment;
	//maps atom to a number of its groundings in the assignment
	private HashMap<Atom,Integer> numberofAllRandvars;
	//the overall number of ground atoms in this assignment
	private int nrRandVars=0;
	//the mapping from atom to its negatives in this assignment
	private HashMap<Atom,List<GroundAtom>> negatives;
	

	/**
	 * create an empty assignment for a list of atoms
	 * @param atoms
	 */
	public Assignment(){
	    assignment=new HashMap<Atom, List<GroundAtom>>();
	    numberofAllRandvars=new HashMap<Atom, Integer>();
	    negatives=new HashMap<Atom, List<GroundAtom>>();
	}
	
	public Assignment(List<GroundAtom> atoms){
	    assignment=new HashMap<Atom, List<GroundAtom>>();
	    numberofAllRandvars=new HashMap<Atom, Integer>();
	    negatives=new HashMap<Atom, List<GroundAtom>>();
		for(GroundAtom a:atoms){
			if(assignment.containsKey(a.getAtom())){
				assignment.get(a.getAtom()).add(a);
				numberofAllRandvars.put(a.getAtom(),numberofAllRandvars.get(a.getAtom())+1);
				if(a.getAtom().getPredicate().isBoolean() && ((BoolValue)a.getValue()).getValue()==false){
					this.negatives.get(a.getAtom()).add(a);
				}
			}
			else{
				assignment.put(a.getAtom(), new ArrayList<GroundAtom>());
				numberofAllRandvars.put(a.getAtom(), new Integer(0));
				assignment.get(a.getAtom()).add(a);
				numberofAllRandvars.put(a.getAtom(),numberofAllRandvars.get(a.getAtom())+1);
				negatives.put(a.getAtom(), new ArrayList<GroundAtom>());
				if(a.getAtom().getPredicate().isBoolean() && ((BoolValue)a.getValue()).getValue()==false){
					this.negatives.get(a.getAtom()).add(a);
				}
			}
		}
	}
	
	/**
	 * Add a random variable for an atom
	 * @param atom
	 * @param grAtom
	 */
	public void addRandVar(Atom atom, GroundAtom grAtom) {
		if(!this.assignment.containsKey(atom)){
			this.assignment.put(atom, new ArrayList<GroundAtom>());
		}
		if(!this.numberofAllRandvars.containsKey(atom)){
			this.numberofAllRandvars.put(atom, new Integer(0));
		}
		if(!this.negatives.containsKey(atom)){
			this.negatives.put(atom, new ArrayList<GroundAtom>());
		}
		this.assignment.get(atom).add(grAtom);
		this.numberofAllRandvars.put(atom, (this.numberofAllRandvars.get(atom)+1));
		if(atom.getPredicate().isBoolean() && ((BoolValue)grAtom.getValue()).getValue()==false){
			this.negatives.get(atom).add(grAtom);
		}
		this.nrRandVars++;
	}
	
	/**
	 * Add a negative ground atom (e.g., friend(s1,s2)=false). It will be also added to the overall 
	 * randvars for atom a
	 * @param atom
	 * @param grAtom
	 */
	public void addSubSample(Atom a, GroundAtom grAtom) {
		this.assignment.get(a).add(grAtom);
		this.numberofAllRandvars.put(a, (this.numberofAllRandvars.get(a)+1));
		this.negatives.get(a).add(grAtom);
		this.nrRandVars++;
	}
	/**
	 * Turn this assignment into a prolog format. This means that each line contains one ground atom
	 * and the line is ended with a period.
	 * @return
	 */
	public String toPrologFormat(){
		StringBuffer strB=new StringBuffer();
		
		for(Atom a:assignment.keySet()){
			for(int i=0;i<assignment.get(a).size();i++){
				if(a.getPredicate().isBoolean()){
					if(((BoolValue)assignment.get(a).get(i).getValue()).getValue()){
					strB.append(assignment.get(a).get(i)+".\n");
					}
				 }
				else{
					strB.append(assignment.get(a).get(i)+".\n");
				}
			}
		}
		return strB.toString();
	}
	
	public String toString(){
		String tmp="";
		for(Atom a:assignment.keySet()){
			for(int i=0;i<assignment.get(a).size();i++){
				if(a.getPredicate().isBoolean()){
					tmp=tmp.concat(assignment.get(a).get(i)+" = "+assignment.get(a).get(i).getValue().toString()+".\n");
				}
				else{
					tmp=tmp.concat(assignment.get(a).get(i)+".\n");
				}
			}
		}
		return tmp;
	}

	/**
	 * Get list of randvars for atom a
	 * @param a
	 * @return
	 */
	public List<GroundAtom> getAssignmentFor(Atom a) {
		if(this.assignment.containsKey(a)){
		return this.assignment.get(a);
		}
		else{
			return new ArrayList<GroundAtom>();
		}
	}
	
	/**
	 * Get number of randvars for atom a
	 * @param a
	 * @return
	 */
	public Integer getNrRandVars(Atom a){
		return this.numberofAllRandvars.get(a);
	}
	
	/**
	 * Get overall number of randvars
	 * @return
	 */
	public int getNrRandvars(){
		return this.nrRandVars;
	}
	/**
	 * Print the complete assignment where for each ground atom a value is
	 * specified as well. This is different from a prolog format because we 
	 * print boolean atoms as atom(arg1,arg2)={true,false}
	 * @return
	 */
	public String printVarValuePair() {
		String tmp="";
		for(Atom a:assignment.keySet()){
			for(int i=0;i<assignment.get(a).size();i++){
			tmp+=assignment.get(a).get(i).printVarValuePair()+".\n";
			}
		}
		return tmp;
	}

	public HashMap<Atom, List<GroundAtom>> getAssignment() {
		return assignment;
	}

	public HashMap<Atom, Integer> getNumberofRandvars() {
		return numberofAllRandvars;
	}

	public int getNrRandVars() {
		return nrRandVars;
	}

	public HashMap<Atom, List<GroundAtom>> getNegatives() {
		return negatives;
	}
	

	
	
}
