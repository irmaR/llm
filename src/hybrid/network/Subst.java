package hybrid.network;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class representing a substitution. That is, mapping from logvars to the constants from the domain.
 * @author irma
 *
 */
public class Subst {

	private HashMap<Logvar,Argument> substitution;
	
	/**
	 * Initialize subst by mapping
	 * @param subst
	 */
	public Subst(HashMap<Logvar,Argument> subst){
		this.substitution=subst;
	}
	
	/**
	 * Initialize substitution with two arrays representing a sequence denoting an ordered mapping from logvars to 
	 * elements from the domain
	 * @param logvars
	 * @param el
	 * @throws SubstitutionException
	 */
	public Subst(ArrayList<Logvar> logvars,ArrayList<Constant> el) throws SubstitutionException{
		if(logvars.size()!=el.size()){
			throw new SubstitutionException("The number of logvars should corrrespond to the number of domain elements!");
		}
		else{
			this.substitution=new HashMap<Logvar, Argument>();
			for(int i=0;i<logvars.size();i++){
				substitution.put(logvars.get(i), el.get(i));
			}
		}
	}

	
	public Subst(Logvar[] logvars,Constant[] el) throws SubstitutionException{
		if(logvars.length!=el.length){
			throw new SubstitutionException("The number of logvars should corrrespond to the number of domain elements!");
		}
		else{
			this.substitution=new HashMap<Logvar, Argument>();
			for(int i=0;i<logvars.length;i++){
				substitution.put(logvars[i], el[i]);
			}
		}
	}
	/**
	 * Make substitution for one logvar
	 * @param l
	 * @param el
	 * @throws SubstitutionException
	 */
	public Subst(Logvar l,Constant el) throws SubstitutionException{
			this.substitution=new HashMap<Logvar, Argument>();
			this.substitution.put(l, el);
	}
	
	
	public HashMap<Logvar, Argument> getSubstitution() {
		return substitution;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((substitution == null) ? 0 : substitution.hashCode());
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
		Subst other = (Subst) obj;
		if (substitution == null) {
			if (other.substitution != null)
				return false;
		} else if (!substitution.equals(other.substitution))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Subst [substitution=" + substitution + "]";
	}

	
	
}

