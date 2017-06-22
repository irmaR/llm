package hybrid.featureGenerator;

import hybrid.features.Feature;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class containing the features from the same block.
 * This means that the features have the same conjunction but they are performing
 * different actions on the groundings of the conjunction
 * @author irma
 *
 */
public class FeatureBlock {

	private ArrayList<Feature> features;
	
	
	public FeatureBlock(){
		this.features=new ArrayList<Feature>();
	}
	
	public FeatureBlock(List<Feature> features){
		this.features=new ArrayList(features);
	}
	
	public List<Feature> getFeaturesInThisBlock(){
		return this.features;
	}
	
	public String toString(){
		String tmp=" ";
		for(Feature f:this.features){
			tmp+=f.toString()+"\n";
		}
		return tmp;
	}

	public void addFeature(Feature f) {
		this.features.add(f);
	}
	
}
