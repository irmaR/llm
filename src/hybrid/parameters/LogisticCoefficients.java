package hybrid.parameters;

import java.io.Serializable;
import java.util.HashMap;

import hybrid.converters.ConvertCoefficientsInterface;
import hybrid.converters.ConvertPoolInterface;
import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.network.Value;

public class LogisticCoefficients extends Coefficients implements Serializable{

	private HashMap<Value,Regression> regressors;
	private Integer nr_free_pars;
	private ExtraInfo extraInfo;
	
	public LogisticCoefficients(HashMap<Value,Regression> tmp){
		this.regressors=tmp;
	}
	
	
	

	public LogisticCoefficients() {
		this.regressors=new HashMap<Value,Regression>();
	}




	@Override
	public String toString() {
		String tmp="";
		for(Value val:regressors.keySet()){
			tmp+=val+" ---> "+regressors.get(val)+"\n";
		}
		return tmp;
	}
	
	public String decisionTreeFormat(Feature select_to_print_feature) {
		return this.toString()+" \n "+this.extraInfo;
	}
	public HashMap<Value, String> decisionTreeFormat2(Feature select_to_print_feature) {
		HashMap<Value, String> tmp=new HashMap<Value,String>();
		for(Value v:this.regressors.keySet()){
			tmp.put(v, this.regressors.get(v).toString());
		}
		return tmp;
	}
	
	public HashMap<Value, Regression> getRegressors() {
		return regressors;
	}


	public void setRegressors(HashMap<Value, Regression> regressors) {
		this.regressors = regressors;
	}


	public Integer getNr_free_pars() {
		return nr_free_pars;
	}


	public void setNr_free_pars(Integer nr_free_pars) {
		this.nr_free_pars = nr_free_pars;
	}


	@Override
	public String convert(ConvertPoolInterface converter) {
		return converter.convert(this);
	}

	@Override
	public String filterNaN() {
       return this.toString();		

	}


	public void add(Value val, Regression parameters) {
		this.regressors.put(val, parameters);
		
	}


	@Override
	public void convert(ConvertCoefficientsInterface converter,AssignmentKey key,Dependency dep) {
		converter.convert(this,key,dep);
		
	}


	public ExtraInfo getExtraInfo() {
		return extraInfo;
	}




	public void setExtraInfo(ExtraInfo extraInfo) {
		this.extraInfo = extraInfo;
	}


	
	
}
