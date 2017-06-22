package hybrid.parameters;

import hybrid.converters.ConvertCoefficientsInterface;
import hybrid.converters.ConvertPoolInterface;
import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.network.Value;

import java.io.Serializable;
import java.util.HashMap;

public class CLGCoefficients extends Coefficients implements Serializable {

	private HashMap<AssignmentKey,LinearGaussianCoeff> parameters;
	private Gaussian marginal;

	public CLGCoefficients(HashMap<AssignmentKey,LinearGaussianCoeff> pars){
		this.parameters=pars;
		this.marginal=new Gaussian(1.0,0.0);
	}
	
	
	public String toString(){
		String tmp="";
		for(AssignmentKey k:parameters.keySet()){
			tmp+=k+" -> "+parameters.get(k)+"\n";
		}
		return tmp;
	}

	public Gaussian getMarginal() {
		return marginal;
	}



	public HashMap<AssignmentKey, LinearGaussianCoeff> getParameters() {
		return parameters;
	}

   
	public void addParameter(AssignmentKey key, LinearGaussianCoeff coeffs) {
		this.parameters.put(key, coeffs);
	}
	


	public void setParameters(HashMap<AssignmentKey, LinearGaussianCoeff> parameters) {
		this.parameters = parameters;
	}



	public void setMarginal(Gaussian marginal) {
		this.marginal = marginal;
	}



	@Override
	public String convert(ConvertPoolInterface converter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String filterNaN() {
		// TODO Auto-generated method stub
		return null;
	}

	public String decisionTreeFormat(Feature select_to_print_feature) {
		String tmp=" ";
		for(AssignmentKey key:this.parameters.keySet()){
			try{
				tmp+=this.parameters.get(key).decisionTreeFormat(select_to_print_feature)+" \\n ";
			}
			catch(NullPointerException e){
				continue;
			}
		}
		return tmp;
	}
	
	@Override
	public HashMap<Value,String> decisionTreeFormat2(Feature select_to_print_feature) {
		String tmp=" ";
		HashMap<Value,String> pars=new HashMap<Value,String>();
		for(AssignmentKey key:this.parameters.keySet()){
			try{
				tmp=this.parameters.get(key).decisionTreeFormat(select_to_print_feature)+" \\n ";
				pars.put(key.getFeatureValue(select_to_print_feature),tmp);
			}
			catch(NullPointerException e){
				continue;
			}
		}
		return pars;
	}

	@Override
	public void convert(ConvertCoefficientsInterface converter,AssignmentKey key,Dependency dep) {
		converter.convert(this,key,dep);
		
	}


}
