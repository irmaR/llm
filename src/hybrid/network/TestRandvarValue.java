package hybrid.network;

import hybrid.featureGenerator.Renaming;
import hybrid.utils.GenerateUniqueIDforAtom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
/**
 * The class is used to represent randvar value tests e.g., grade(S,C)=low.
 * @author irma
 *
 */
public class TestRandvarValue extends Atom{

	private Value testing_value;

	public TestRandvarValue(Predicate p,List<Logvar> arguments,Value testing_value) {
		super(new BooleanPred(p.getPredicateName(), p.getArity()),arguments);
		this.testing_value=testing_value;
		this.id = GenerateUniqueIDforAtom.getID();
	}


	public String toString(){
		return createFOLTerm();
	}


	@Override
	/**
	 * Renaming external atom
	 * @param renaming - renaming is an object saying what should be renamed logvar type -> to a number of new logvars
	 * @param atom - return all renamed atoms according to the renaming
	 * @return
	 */
	protected List<Atom> renameExternalAtom(Renaming renaming) {
		if(this.arguments.size()==1){
			List<Atom> tmp=new ArrayList<Atom>();
			Type firstArgumentType=this.arguments.get(0).getType();

			//only one argument atom
			if(this.arguments.size()==1){
				for(Logvar l1:renaming.getRenaming().get(firstArgumentType)){
					Logvar[] logvarTemp=new Logvar[1];
					logvarTemp[0]=l1;
					TestRandvarValue newAtom=new TestRandvarValue(super.predicate,Arrays.asList(logvarTemp),this.testing_value);
					//Atom newAtom=new Atom(this.predicate,new Logvar[]{l1});
					tmp.add(newAtom);
				}
				System.out.println("RETURNING: "+tmp);
				return tmp;
			}
			//the internal atom has two possible arguments
			else{
				HashSet<Logvar> renamings=renaming.getRenaming().get(this.arguments.get(0).getType());
				for(Logvar l1:renamings){
					for(Logvar l2:renamings){
						if(!l1.equals(l2)){
							Logvar[] logvarTemp=new Logvar[2];
							logvarTemp[0]=l1;
							logvarTemp[1]=l2;
							Atom newAtom=new Atom(this.predicate,logvarTemp);
							tmp.add(newAtom);
						}
					}
				}
				return tmp;
			}}

		List<Atom> tmp=new ArrayList<Atom>();
		Type firstArgumentType=this.arguments.get(0).getType();
		Type secondArgumentType=this.arguments.get(1).getType();
		HashSet<Logvar> first_argument_renamings=null;
		HashSet<Logvar> second_argument_renamings=null;

		//first argument renamings
		if(renaming.getRenaming().containsKey(firstArgumentType)){
			first_argument_renamings=renaming.getRenaming().get(firstArgumentType);
		}
		else{
			first_argument_renamings=new HashSet<Logvar>();
			first_argument_renamings.add(this.arguments.get(0));
		}

		//second argument renamings
		if(renaming.getRenaming().containsKey(secondArgumentType)){
			second_argument_renamings=renaming.getRenaming().get(secondArgumentType);
		}
		else{
			second_argument_renamings=new HashSet<Logvar>();
			second_argument_renamings.add(this.arguments.get(1));
		}

		for(Logvar l1:first_argument_renamings){
			//second argument type
			for(Logvar l2:second_argument_renamings){
				Logvar[] logvarTemp=new Logvar[2];
				logvarTemp[0]=l1;
				logvarTemp[1]=l2;
				TestRandvarValue newAtom=new TestRandvarValue(super.predicate,Arrays.asList(logvarTemp),this.testing_value);
				tmp.add(newAtom);
			}
		}
		return tmp;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((testing_value == null) ? 0 : testing_value.hashCode());
		return result;
	}

	@Override
	public String createFOLTerm() {
		if(this.arguments.size()==0){
			return this.predicate.getPredicateName();
		}
		if(this.arguments.size()==1){
			return this.predicate.getPredicateName()+"("+arguments.get(0).getSymbol()+","+testing_value+")";
		}
		String tmp=this.predicate.getPredicateName()+"(";
		for(int i=0;i<this.arguments.size()-1;i++){
			tmp+=arguments.get(i).getSymbol()+",";
		}
		tmp+=arguments.get(this.arguments.size()-1).getSymbol()+","+testing_value+")";
		return tmp;
	}


	@Override
	public boolean equals(Object obj) {
		//first checking if the main atom is the same
		if(!super.equals(obj)){
			return false;
		}
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestRandvarValue other = (TestRandvarValue) obj;
		//then checking if the value is equal
		if (testing_value == null) {
			if (other.testing_value != null)
				return false;
		} else if (!testing_value.equals(other.testing_value))
			return false;
		return true;
	}

	public boolean isRandVarTest() {
		return true;
	}


}
