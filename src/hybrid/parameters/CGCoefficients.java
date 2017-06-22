package hybrid.parameters;

import hybrid.converters.ConvertCoefficientsInterface;
import hybrid.converters.ConvertPoolInterface;
import hybrid.converters.Converter;
import hybrid.converters.DC_converter;
import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.network.Value;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class CGCoefficients extends Coefficients implements Serializable {

	private HashMap<AssignmentKey,GaussianCoefficients> parameters;
	private Gaussian marginal;
	
	public CGCoefficients(HashMap<AssignmentKey,GaussianCoefficients> parameters,Gaussian marginal){
		this.parameters=parameters;
		this.marginal=marginal;
	}

	public CGCoefficients(List<AssignmentKey> list) {
		this.parameters=new HashMap<AssignmentKey, GaussianCoefficients>();
		for(AssignmentKey a:list){
			this.parameters.put(a,new GaussianCoefficients());
		}
		this.marginal=new Gaussian();
	}

	public void addConditionalParameter(AssignmentKey key,GaussianCoefficients par) {
		this.parameters.put(key, par);
	}
	
	
	public Gaussian getMarginal() {
		return marginal;
	}

	public void setMarginal(Gaussian marginal) {
		this.marginal = marginal;
	}

	public HashMap<AssignmentKey, GaussianCoefficients> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<AssignmentKey, GaussianCoefficients> parameters) {
		this.parameters = parameters;
	}

	@Override
	public String convert(ConvertPoolInterface converter) {
		return converter.convert(this);
		
	}
	
	public String toString() {
		String tmp=" ";
		for(AssignmentKey key:this.parameters.keySet()){
			tmp+=key+ "-->"+" N("+this.parameters.get(key).getMean()+","+this.parameters.get(key).getSigma()+")";
		}
		return tmp;
	}

	@Override
	public String filterNaN() {
		String tmp=" ";
		for(AssignmentKey key:this.parameters.keySet()){
			if(Double.isNaN(this.parameters.get(key).getMean()) && Double.isNaN(this.parameters.get(key).getSigma())){
				continue;
			}
			tmp+=key+ "-->"+" N("+this.parameters.get(key).getMean()+","+this.parameters.get(key).getSigma()+") \\n ";
		}
		return tmp;
	}
	
	@Override
	public String decisionTreeFormat(Feature select_to_print_feature) {
		String tmp=" ";
		for(AssignmentKey key:this.parameters.keySet()){
			Value val=null;
			if(select_to_print_feature!=null){
				val=key.getFeatureValue(select_to_print_feature);
			}
			if(val==null){
   			if(Double.isNaN(this.parameters.get(key).getMean()) && Double.isNaN(this.parameters.get(key).getSigma())){
				continue;
			}
			tmp+=key+ "-->"+" N("+this.parameters.get(key).getMean()+","+this.parameters.get(key).getSigma()+")";
			}
			else{
				if(Double.isNaN(this.parameters.get(key).getMean()) && Double.isNaN(this.parameters.get(key).getSigma())){
					continue;
				}
				tmp+=select_to_print_feature.toString().trim()+"="+val+ "-->"+" N("+this.parameters.get(key).getMean()+","+this.parameters.get(key).getSigma()+")"+" \\n ";
			}
		}
		return tmp;
	}
	
	public HashMap<Value, String> decisionTreeFormat2(Feature select_to_print_feature) {
		HashMap<Value,String> tmp=new HashMap<Value,String>();
		String tmp1;
		for(AssignmentKey key:this.parameters.keySet()){
			Value val=null;
			if(select_to_print_feature!=null){
				System.out.println(select_to_print_feature);
				System.out.println("brr: "+key+" - "+key.getFeatureValue(select_to_print_feature));
				val=key.getFeatureValue(select_to_print_feature);
			}
			if(val==null){
   			/*if(Double.isNaN(this.parameters.get(key).getMean()) && Double.isNaN(this.parameters.get(key).getSigma())){
				continue;
			}*/
   			tmp1=" N("+this.parameters.get(key).getMean()+","+this.parameters.get(key).getSigma()+")";
			}
			else{
				/*if(Double.isNaN(this.parameters.get(key).getMean()) && Double.isNaN(this.parameters.get(key).getSigma())){
					continue;
				}*/
				tmp1=" N("+this.parameters.get(key).getMean()+","+this.parameters.get(key).getSigma()+")"+" \\n ";
			}
			tmp.put(key.getFeatureValue(select_to_print_feature),tmp1);	
		}
		return tmp;
	}

	@Override
	public void convert(ConvertCoefficientsInterface converter,AssignmentKey key,Dependency dep) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
