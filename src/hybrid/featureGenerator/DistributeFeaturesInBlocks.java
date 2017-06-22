package hybrid.featureGenerator;

import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DistributeFeaturesInBlocks {

	
	public List<FeatureBlock> distributFeaturesToBlocks(List<Feature> featureSpace){
		
		//if features are not to be grouped in blocks, then each feature is in a separate block
		if(!AlgorithmParameters.isUsingFeatureBlocks()){
			return noGrouping(featureSpace);
		}
		
		List<FeatureBlock> block=new ArrayList<FeatureBlock>();
		int current_block_nr=-1;
		int current_block=-1;
		
		for(Feature f:featureSpace){
			//System.out.println(" F: "+f+ "Block: "+f.getFeatureBlock());
			int block_nr=f.getFeatureBlock();
			
			if(block_nr==current_block){
				block.get(current_block_nr).addFeature(f);
				//current_block_nr=f.getFeatureBlock();
			}
			else{
			    block.add(new FeatureBlock(Arrays.asList(f)));
				current_block_nr++;
				current_block=f.getFeatureBlock();
			}
		}
		//System.out.println(" Extracted blocks: "+block.size()+" "+block);
		return block;
	}

	/**
	 * If there are no feature groupings set through algorithms parameters then each feature is in a separate block
	 * @param featureSpace
	 * @return
	 */
	private List<FeatureBlock> noGrouping(List<Feature> featureSpace) {
		List<FeatureBlock> blocks=new ArrayList<FeatureBlock>();
		for(Feature f:featureSpace){
			blocks.add(new FeatureBlock(Arrays.asList(f)));
		}
		return blocks;
		
	}
	
}
