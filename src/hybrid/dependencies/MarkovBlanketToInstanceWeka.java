package hybrid.dependencies;

import hybrid.features.Feature;
import hybrid.network.UndefinedValue;
import hybrid.network.WrongValueType;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Turn Markov blanket to an instance in Weka for specific
 * instances sceleton (data)
 * @author irma
 *
 */
public class MarkovBlanketToInstanceWeka {

	
	public Instance markovBlanketToInstance(MarkovBlanket mb,Instances data) throws WrongValueType{
	   Instance inst=new Instance(mb.getDep().getFeatures().size()+1);
	   inst.setDataset(data);
	   int index=0;
	   for(Feature ft:mb.getDep().getFeatures()){
		   if(ft.isContinuousOutput()){
			   inst.setValue(index, mb.getFeatureValues().get(ft).toNumber());  
		   }
		   else{
			   if(mb.getFeatureValues().get(ft) instanceof UndefinedValue){
				   inst.setMissing(index);
			   }
			   else{
			   inst.setValue(index, mb.getFeatureValues().get(ft).toString());
			   }
		   }
		   index++;
	   }
	   if(mb.getHead().getAtom().getPredicate().isDiscrete()){
	       inst.setValue(index, mb.getHead().getValue().toString());
	   }
	   else{
		   inst.setValue(index, mb.getHead().getValue().toNumber());
	   }   
	   return inst;
	}

}
