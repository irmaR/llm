package hybrid.network;

import java.util.List;

public class Partially_grounded_atom extends Atom {
    
	private Subst substitiution;
	private Value value;
	
	
	public Partially_grounded_atom(Predicate p, List<Logvar> arguments) {
		super(p, arguments);
	}
	
	public void ground_argument(Logvar l,Constant domain_object){
		
	}
	
	public void assign_value(Value v){
		this.value=v;
	}
	
	public String createFOLTerm() {
		if(this.arguments.size()==0){
			return this.predicate.getPredicateName();
		}
		if(this.arguments.size()==1){
			return this.predicate.getPredicateName()+"("+this.arguments.get(0).getSymbol()+")";
		}
		String tmp=this.predicate.getPredicateName()+"(";
		for(int i=0;i<this.arguments.size()-1;i++){
			tmp+=this.arguments.get(i).getSymbol()+",";
		}
		tmp+=this.arguments.get(this.arguments.size()-1).getSymbol()+")";
		return tmp;
	}

}
