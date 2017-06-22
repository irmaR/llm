package hybrid.interpretations;

import hybrid.network.Atom;

import java.util.HashMap;
/**
 * This class represents all necesseary information 
 * on negative example subsampling
 * @author irma
 *
 */
public class NegativeVarsInfo {
	private HashMap<Atom,Double> alphas;
	private HashMap<Atom,Integer> nrGroundings;
	private HashMap<Atom,Boolean> samplingPerformed;
	private HashMap<Atom, Integer> nr_positives;
	private HashMap<Atom, Integer> nr_negatives;
	private HashMap<Atom,Double> percentage_negatives;
	private HashMap<Atom,Double> percentage_positives;
	private HashMap<Atom,Long> true_number_of_groundings;
	
	public NegativeVarsInfo(){
		nr_positives=new HashMap<Atom, Integer>();
		nr_negatives=new HashMap<Atom, Integer>();
		percentage_positives=new HashMap<Atom, Double>();
		percentage_negatives=new HashMap<Atom, Double>();
		samplingPerformed=new HashMap<Atom, Boolean>();
		this.alphas=new HashMap<Atom, Double>();
		this.nrGroundings=new HashMap<Atom, Integer>();
		true_number_of_groundings=new HashMap<Atom, Long>();
	}
	
	public void addSubSampleInfo(Atom a,Double d){
		this.alphas.put(a, d);
	}
	
	public void addPositivesPercentage(Atom a,Double perc){
		this.percentage_positives.put(a, perc);
	}
	
	public void addNegativesPercentage(Atom a,Double perc){
		this.percentage_negatives.put(a, perc);
	}

	public HashMap<Atom, Double> getAlphas() {
		return alphas;
	}

	public void addNrGroundings(Atom a, Integer nr) {
		this.nrGroundings.put(a, nr);	
	}
	
	
	
	public HashMap<Atom, Integer> getNrGroundings() {
		return nrGroundings;
	}

	public String toString(){
		String tmp="-------------- Negative examples info ----------------\n";
		System.out.println(alphas);
		for(Atom a:alphas.keySet()){
			tmp+= " Subsampling performed? "+this.samplingPerformed.get(a)+"\n";
			tmp+= " Alpha for atom "+a + " = "+alphas.get(a)+ " nr_true_groundings = "+this.nr_positives.get(a)+" nr negative groundings: "+this.nr_negatives.get(a)+"\n";
	        tmp+= " True positives percentage: "+this.percentage_positives.get(a)+ "\n True data (non subsampled) negatives: "+this.getNegativesPercentage(a)+"\n";	
		}
		
		return tmp;
	}
	
	public boolean isSubSamplingPerformed(Atom a){
		if(!this.samplingPerformed.containsKey(a)){
			return false;
		}
		return this.samplingPerformed.get(a);
	}
	
	public void setSamplingPerformed(Atom a,boolean flag){
		this.samplingPerformed.put(a, flag);
	}

	public int getNr_positives(Atom a) {
		return nr_positives.get(a);
	}

	public void setNr_positives(Atom a,int nr_positives) {
		this.nr_positives.put(a, nr_positives);
	}

	public int getNr_negatives(Atom a) {
		return nr_negatives.get(a);
	}

	public void setNr_negatives(Atom a,int nr_negatives) {
		this.nr_negatives.put(a, nr_negatives);
	}
	
	public double getNegativesPercentage(Atom a) {
		return percentage_negatives.get(a);
	}
	
	public double getPositivesPercentage(Atom a) {
		return percentage_positives.get(a);
	}
	
	public void set_true_number_of_groundigs(Atom a,long nr){
		this.true_number_of_groundings.put(a,new Long(nr));
	}
	
	public long get_true_number_of_groundigs(Atom a){
		return this.true_number_of_groundings.get(a);
	}
	
	
}
