package hybrid.interpretations;

import hybrid.network.Constant;
import hybrid.network.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * This class represents a specific domain where we get mappings from a type e.g., student to
 * all the constants representing the objects of type "student"
 * @author irma
 *
 */
public class Domain {

	private HashMap<Type,List<Constant>> domainElements;


	/**
	 * Creating domain by mappings from logvars to all possible domain elements
	 * @param domainElements
	 */
	public Domain(HashMap<Type, List<Constant>> domainElements) {
		this.domainElements = domainElements;
	}

	/**
	 * Create an empty domain for types logvars
	 * @param logvars
	 */
	public Domain(List<Type> logvars) {	
		this.domainElements=new HashMap<Type, List<Constant>>();
		for(Type lV:logvars){
			this.domainElements.put(lV, new ArrayList<Constant>());
		}
	}


	public HashMap<Type, List<Constant>> getDomainElements() {
		return domainElements;
	}

	/**
	 * Add domain element of type logvar and constant name specified by a string
	 * @param logvar
	 * @param constName
	 */
	public void addDomainElement(Type logvar, String constName) {
		if(!this.domainElements.get(logvar).contains(new Constant(constName))){
			this.domainElements.get(logvar).add(new Constant(constName));
		}

	}
	/**
	 * Add domain element of a certain type specified by a constant
	 * @param log
	 * @param constant
	 */
	public void addDomainElement(Type type,Constant constant) {
		if(!this.domainElements.get(type).contains(constant)){
			this.domainElements.get(type).add(constant);
		}

	}

	public String toString(){
		String tmp="";
		for(Type lV:domainElements.keySet()){
			tmp+=lV+"\n ------------------------------------------- \n";
			for(Constant c:domainElements.get(lV)){
				tmp+=c+"\n";
			}
		}
		return tmp;
	}
	/**
	 * Get domain in the prolog format. That means, we don't print 
	 * only constant, but we also use the predicate name denoting the type of
	 * the constant. E.g., prolog format of a constant "pete" of type student
	 * would be "student(pete)." Each constant ends with a period.
	 * @return
	 */
	public String prologFormat(){
		String tmp="";
		for(Type lV:domainElements.keySet()){
			for(Constant c:domainElements.get(lV)){
				tmp.concat(lV.getName()+"("+c+")"+".\n");
			}
		}
		return tmp;
	}

	/**
	 * Get constant from the domain of type l specified with a string
	 * @param l
	 * @param string
	 * @return
	 */
	public Constant getElement(Type l, String string) {
		Constant con=new Constant(string);
		try{
			int i=this.domainElements.get(l).indexOf(con);
			//element not found
			if(i==-1){
				addDomainElement(l, con);
			}

		}
		catch(NullPointerException e){
			System.out.println(" Type "+l +" is missing in the network specification");
			System.exit(0);
		}

		return con;
	}

	/**
	 * Get a random constant from the domain.
	 * @param l
	 * @return
	 */
	public Constant getRandomAssignment(Type l){
		Random rGen=new Random();
		return this.domainElements.get(l).get(rGen.nextInt(this.domainElements.get(l).size()));
	}

	public String getDomainSize(){
		String tmp="domain size: \n----------------------\n";	
		for(Type lV:domainElements.keySet()){
			tmp+=lV+" --> "+domainElements.get(lV).size()+" objects ";
		}
		return tmp;
	}



}
