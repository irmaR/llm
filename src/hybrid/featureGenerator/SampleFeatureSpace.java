package hybrid.featureGenerator;

import hybrid.features.Feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SampleFeatureSpace {

	private long nr_features;
	
	public SampleFeatureSpace(long nr_features){
		this.nr_features=nr_features;
	}
	
	
	public List<Feature> samplefeatures(List<Feature> featureSpace){
		List<Feature> new_space=new ArrayList<Feature>();
		List<Integer> indices=new ArrayList<Integer>();
		
		//if the asked number of features is bigger or equal to the 
		//size of the given feature space, then return the entire feature space
		if(this.nr_features>=featureSpace.size()){
			return featureSpace;
		}
		
		for(int i=0;i<featureSpace.size();i++){
			if(featureSpace.get(i).getLength()==1){
				new_space.add(featureSpace.get(i));
			}
			indices.add(i);
		}
		Collections.shuffle(indices);
		for(int j=0;j<nr_features;j++){
			new_space.add(featureSpace.get(indices.get(j)));
		}
		
		return new_space;
		
	}
}
