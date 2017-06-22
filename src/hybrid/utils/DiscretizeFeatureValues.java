package hybrid.utils;
import hybrid.comparators.ThresholdGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

public class DiscretizeFeatureValues {

/**
 * 
 * @param data
 * @param featureIndex
 * @param nrBins
 * @return
 * @throws Exception 
 */
public List<Bin> getCutPoints(Instances data,int featureIndex,int nrBins) throws Exception{
	List<Bin> bins=new ArrayList<Bin>();
	 Discretize d=new Discretize();
	 //String[] options = new String[1];
	 //options[0] = "-F";
	 //d.setOptions(options);
	 d.setBins(nrBins);
	 d.setInputFormat(data);
	 Filter.useFilter(data, d);
	 double[] cutPoints=d.getCutPoints(featureIndex);
	 if(cutPoints==null){
		 return bins;
	 }
	 Bin initial=new Bin(Double.NEGATIVE_INFINITY,cutPoints[0]);
	 Bin finalBin=new Bin(cutPoints[cutPoints.length-1],Double.POSITIVE_INFINITY);
	 bins.add(initial);
	 for(int i=0;i<cutPoints.length-1;i++){
		 bins.add(new Bin(cutPoints[i],cutPoints[i+1]));
	 }
	 bins.add(finalBin);
     return bins;
}


	
public static void main(String[] args) throws Exception{
	String pathToArff="/home/irma/Downloads/bank-data.csv";
	 CSVLoader loader = new CSVLoader();
	    loader.setSource(new File(pathToArff));
	    Instances data = loader.getDataSet();
	    int c=0;
	    for(int i=0;i<data.numInstances();i++){
	    	//System.out.println(data.instance(i));
	    }
	    Discretize d=new Discretize();
	    String[] options = new String[3];
	    options[0] = "-F";
	    options[1] = "-B 5";
	    options[2] = "-R age";
	    d.setOptions(options);
	    d.setBins(5);
	    d.setInputFormat(data);
	    Filter.useFilter(data, d);
	    System.out.println(d.getBins());
	    DiscretizeFeatureValues discr=new DiscretizeFeatureValues();
	    List<Bin> bins=discr.getCutPoints(data,1,10);
	    System.out.println(bins);
	    ThresholdGenerator t=new ThresholdGenerator();
	    System.out.println("Bigger:"+t.getThresholdsBigger(bins));
	    System.out.println("Smaller:"+t.getThresholdsSmaller(bins));
	    System.out.println("In Between:"+t.getThresholdsInBetween(bins));

	    
	 /*   for(Double s:d.getCutPoints(1)){
	    	System.out.println(s);
	    }*/
	    
}
	
	
}
