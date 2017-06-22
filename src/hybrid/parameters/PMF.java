package hybrid.parameters;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.features.Feature;
import hybrid.network.*;

/**
 * The class representing Probability Mass function which gives the probability that a discrete
 * obtains a single value
 * @author irma
 *
 */

public class PMF extends Parameters<PMFCoefficients,PMFCoefficients> implements Serializable {

	private Dependency dep;
	//private HashMap<Value,Double> parameters;
	private Double training_data_averaged_alphas;
	
	/**
	 * Initializing marginal parameters with probabilities given a head of a dependency (since the dependency doesn't have
	 * any parent set, we only care about the head value)
	 * @param head
	 * @throws BadProbabilityDistribution 
	 */
	public PMF(Dependency dep,Atom head,HashMap<Value,Double> parameters,Double training_data_averaged_alpha) throws BadProbabilityDistribution {
		this.coeffs=new PMFCoefficients(dep);
		this.training_data_averaged_alphas=training_data_averaged_alpha;
		double d=0;
		this.dep=dep;
		for(Value v:((RangeDiscrete)head.getPredicate().getRange()).getValues()){
			//System.out.println("Parameters: "+parameters+ " "+parameters.get(v));
			this.coeffs.addParameter(v, parameters.get(v));
			try{
			  d+=parameters.get(v);
			}
			catch(NullPointerException e){
				throw new BadProbabilityDistribution("No value: "+v+ " in the specified probability for "+head);
			}
		}
		DecimalFormat df = new DecimalFormat("#.00");
		if(d!=1.0 && !(d>=(1-0.00001))){
			throw new BadProbabilityDistribution(" The probabilities for " + head+ " CPT prior don't sum up to one!");
		}
	}



	public PMF(Dependency dep2) {
		this.dep=dep2;
		this.coeffs=new PMFCoefficients(dep2);

	}



	@Override
	public String toString() {
		return this.coeffs.toString();
		/*System.out.println("Halo: "+this.dep);
		String tmp=" ";
		for(Value val:parameters.keySet()){
			tmp+=val+ " ---> "+parameters.get(val)+"\n";
		}
		return tmp;*/
	}
	
	
	public HashMap<Value,Double> getParameters(){
		return this.coeffs.getParameters();
	}
	
	@Override
	public int getNumberOfFreeParameters() {
		return this.coeffs.getParameters().size();
	}

	public Double getTraining_data_averaged_alphas() {
		return training_data_averaged_alphas;
	}

	@Override
	public PMFCoefficients getCoefficients(AssignmentKey key) {
		HashMap<Value,Double> filtered_parameters=new HashMap<Value,Double>();
		if(this.dep.getFeatures()==null){
			//marginal
			return this.coeffs;
		}
		for(Feature f:this.dep.getFeatures()){
			filtered_parameters.put(key.getFeatureValue(f),this.coeffs.getParameters().get(f));
		}
		return new PMFCoefficients(filtered_parameters);
	}



	@Override
	public PMFCoefficients getCoefficients() {
		System.out.println("Getting pmf coeffs"+this.coeffs);

		return this.coeffs;
	}

	@Override
	public void setCoefficients(PMFCoefficients coeffs) {
		// TODO Auto-generated method stub
		
	}

	
	
	

}
