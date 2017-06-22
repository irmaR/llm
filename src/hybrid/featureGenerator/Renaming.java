package hybrid.featureGenerator;

import hybrid.network.Atom;
import hybrid.network.Logvar;
import hybrid.network.LogvarRestrictionLiteral;
import hybrid.network.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
/**
 * This class represents a renaming from type of a variable to a list of possible renaming logvars.
 * The Renaming class maps a type of a logvar e.g., student to all possible logvars it can 
 * have e.g. , student -> S1,S2, ... 
 * @author irma
 *
 */
public class Renaming implements Serializable {

	//give a type and then a set of logical variables that will serve as renamings for that logvar type
	private HashMap<Type,HashSet<Logvar>> renaming;
	
	public Renaming(HashMap<Type,Logvar[]> renaming){
		this.renaming=new HashMap<Type,HashSet<Logvar>>();
		for(Type t:renaming.keySet()){
			this.renaming.put(t, new HashSet<Logvar>(Arrays.asList(renaming.get(t))));
		}
	}
	
	public Renaming(){
		this.renaming=new HashMap<Type,HashSet<Logvar>>();
	}

	public HashMap<Type,HashSet<Logvar>> getRenaming() {
		return renaming;
	}
	
	/**
	 * Get logvar restriction denoted with the string restriction
	 * @param restriction
	 * @return
	 */
	public List<LogvarRestrictionLiteral> getDifferentLogvarsRestriction(String restriction){
		List<LogvarRestrictionLiteral> logvarRestrictionLiterals=new ArrayList<LogvarRestrictionLiteral>();
		for(Type t:renaming.keySet()){
			List<Logvar> logvars=new ArrayList<Logvar>(renaming.get(t));
			for(int i=0;i<logvars.size();i++){
				for(int j=(i+1);j<logvars.size();j++){
					logvarRestrictionLiterals.add(new LogvarRestrictionLiteral(restriction, logvars.get(i), logvars.get(j)));
				}
			}
			
		}
		return logvarRestrictionLiterals;
	}

	@Override
	public String toString() {
		return "Renaming [" + renaming + "]";
	}
	
	/**
	 * Add renaming for type t with logvar l
	 * @param t
	 * @param l
	 */
	public void addRenaming(Type t,Logvar l){
		if(renaming.get(t)!=null){
			renaming.get(t).add(l);
		}
		else{
			if(renaming.size()==0){
			renaming=new HashMap<Type,HashSet<Logvar>>();
			}
			renaming.put(t, new HashSet<Logvar>(Arrays.asList(new Logvar[]{l})));
		}
	}
    
	/**
	 * Set the renaming
	 * @param renaming
	 */
	public void setRenaming(HashMap<Type, HashSet<Logvar>> renaming) {
		this.renaming = renaming;
	}
	
	
	public Renaming deep_copy(){
		HashMap<Type,Logvar[]> tmp=new HashMap<Type, Logvar[]>();
		for(Type t:this.renaming.keySet()){
			Logvar[] tmp1=new Logvar[this.getRenaming().get(t).size()];
			int i=0;
			for(Logvar l:this.getRenaming().get(t)){
				tmp1[i++]=l;
			}
			tmp.put(t, tmp1);
		}
		return new Renaming(tmp);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((renaming == null) ? 0 : renaming.hashCode());
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
		Renaming other = (Renaming) obj;
		if (renaming == null) {
			if (other.renaming != null)
				return false;
		} else if (!renaming.equals(other.renaming))
			return false;
		return true;
	}


    /**
     * Checks if the renaming is applicable for this atom
     * @param atom
     * @return
     */
	public boolean possible(Atom atom) {
		for(Logvar l:atom.getArguments()){
			if(this.renaming.containsKey(l.getType())){
				return true;
			}
		}
		return false;
	}
	
	
	
	
}
