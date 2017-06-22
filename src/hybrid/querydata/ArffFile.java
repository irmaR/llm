package hybrid.querydata;

import hybrid.dependencies.MarkovBlanket;
import hybrid.dependencies.MarkovBlanketToInstanceWeka;
import hybrid.features.Feature;
import hybrid.features.FeatureToWekaAttribute;
import hybrid.network.RangeDiscrete;
import hybrid.network.Value;
import hybrid.network.WrongValueType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
/**
 * Make ARFF file out of query data
 * @author irma
 *
 */
public class ArffFile {

	private QueryData qData;
	
	public ArffFile(QueryData qData) {
		this.qData=qData;
	}
	/**
	 * Create ARFF output file and store it in the output_file
	 * @param output_file
	 * @return
	 * @throws IOException
	 */
	public File getQueryDataAsARFF_file(File output_file) throws IOException{
		FileWriter fw=new FileWriter(output_file);
		FastVector attributes=setAttributes_weka(qData);
		try {
			Instances instances=createInstances(qData,attributes);
			
			fw.append(instances.toString());
			fw.close();
		} catch (WrongValueType e) {
			e.printStackTrace();
		}
		
		return output_file;
	}
	
	/**
	 * Get Instances for the query data
	 * @return
	 * @throws IOException
	 */
	public Instances getQueryDataAsARFF_instances() throws IOException{
		FastVector attributes=setAttributes_weka(qData);
		Instances instances=null;
		try {
			instances=createInstances(qData,attributes);		
		} catch (WrongValueType e) {
			e.printStackTrace();
		}	
		return instances;
	}

	private Instances createInstances(QueryData qData, FastVector attributes) throws WrongValueType {
		Instances data = new Instances(qData.getDep().getHead().getPredicate()+"_arff", attributes, 0);
	    MarkovBlanketToInstanceWeka mbWeka=new MarkovBlanketToInstanceWeka();
		for(MarkovBlanket mb:qData.getFlatData()){
	    	data.add(mbWeka.markovBlanketToInstance(mb,data));
	    }
		return data;
	}

	private FastVector setAttributes_weka(QueryData qData) {
	    FeatureToWekaAttribute fwConvert=new FeatureToWekaAttribute();
		FastVector attributes=new FastVector();
	    for(Feature ft:qData.getDep().getFeatures()){
	    	attributes.addElement(fwConvert.featureToWekaAttribute(ft));
	    }
	    //target predicate
	    if(qData.getDep().getHead().getPredicate().isDiscrete()){
	    	FastVector range=new FastVector();
	    	for(Value rangeValues:((RangeDiscrete)qData.getDep().getHead().getPredicate().getRange()).getValues()){		
	    		range.addElement(rangeValues.toString());
	    	}
	    	attributes.addElement(new Attribute(qData.getDep().getHead().getPredicate().getPredicateName(),range));
	    }
	    else{
	    	attributes.addElement(new Attribute(qData.getDep().getHead().getPredicate().getPredicateName()));

	    }
	    return attributes;
	}
	
	

}
