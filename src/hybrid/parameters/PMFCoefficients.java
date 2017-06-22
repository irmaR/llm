package hybrid.parameters;

import hybrid.converters.ConvertCoefficientsInterface;
import hybrid.converters.ConvertPoolInterface;
import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.network.RangeDiscrete;
import hybrid.network.Value;

import java.io.Serializable;
import java.util.HashMap;

public class PMFCoefficients extends Coefficients implements Serializable{

	private HashMap<Value,Double> pmf_coeff;
	
	public PMFCoefficients(HashMap<Value,Double> pars){
		this.pmf_coeff=pars;
	}

	public PMFCoefficients(Dependency dep) {
		this.pmf_coeff=new HashMap<Value,Double>();
		for(Value v:((RangeDiscrete)dep.getHead().getPredicate().getRange()).getValues()){
			this.pmf_coeff.put(v, 0.0);
		}
	}

	@Override
	public String convert(ConvertPoolInterface converter) {
		return converter.convert(this);
		
	}

	@Override
	public String filterNaN() {
		return pmf_coeff.toString();
	}

	@Override
	public void convert(ConvertCoefficientsInterface converter,AssignmentKey key,Dependency dep) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public HashMap<Value, String> decisionTreeFormat2(Feature select_to_print_feature) {
		HashMap<Value,String> pars=new HashMap<Value,String>();
		for(Value v:pmf_coeff.keySet()){
			pars.put(v, this.pmf_coeff.get(v).toString());
		}
		return pars;
	}

	public void addParameter(Value v, Double double1) {
		this.pmf_coeff.put(v, double1);
		
	}
	
	public String toString(){
		String tmp=" ";
		for(Value val:pmf_coeff.keySet()){
			tmp+=val+ " ---> "+pmf_coeff.get(val)+"\n";
		}
		return tmp;
	}

	public HashMap<Value, Double> getParameters() {
		return this.pmf_coeff;
	}
	

}
