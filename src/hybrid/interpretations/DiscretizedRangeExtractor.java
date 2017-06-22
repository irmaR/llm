package hybrid.interpretations;

import hybrid.network.Atom;
import hybrid.network.DiscretizedPredicate;
import hybrid.network.MinMaxValue;
import hybrid.network.Predicate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;


/**
 * This class is used to extract and initialize continuous ranges
 * for predicates that were discretized. The class iterates through given atoms
 * and reads in the non-discretized data what the ranges are (minimum - maximum).
 * This is used later to obtain densities from probabilities.
 * (we divide a probability with the size of the range). 
 * @author irma
 *
 */
public class DiscretizedRangeExtractor {

	public void getMinMaxValuesForPredicates(String path_to_non_discretized,Atom[] atoms) throws FileNotFoundException{
		HashMap<Predicate,MinMaxValue> tmp=new HashMap<Predicate, MinMaxValue>();
		File folder = new File(path_to_non_discretized);		
		File[] listOfFiles = folder.listFiles();


		ArrayList<String> fileNames=new ArrayList<String>();
		for(File f: listOfFiles){
			fileNames.add(f.getName());
		}

		boolean file_found=false;

		HashMap<Atom,List<MinMaxValue>> getMin_max_values=new  HashMap<Atom,List<MinMaxValue>>();

		for(Atom a:atoms){
			getMin_max_values.put(a, new ArrayList<MinMaxValue>());
		}

		try{
			for (int i = 1; i <= listOfFiles.length; i++) {
				if(listOfFiles[i-1].getName().contains(".pl")){
					file_found=true;
					HashMap<Atom,MinMaxValue> tmp1=new HashMap<Atom, MinMaxValue>();
					getMin_max_values(path_to_non_discretized+"/"+listOfFiles[i-1].getName(),tmp1,atoms);
					for(Atom a:atoms){
						getMin_max_values.get(a).add(tmp1.get(a));
					}
				}
			}
		}
		catch(NullPointerException e){
			System.out.println("No interpretation created for: "+path_to_non_discretized);
		}

		if(file_found==false){
			throw new FileNotFoundException("The directory specified doesn't contain files for extracting ranges of discretized atoms in path: "+path_to_non_discretized+ " is not found!");
		}

		for(Atom a:atoms){
			MinMaxValue tmp_min_max=findMinMax(getMin_max_values.get(a));
			((DiscretizedPredicate) a.getPredicate()).setMinMaxValueRange(tmp_min_max);
			tmp.put(a.getPredicate(),tmp_min_max);
		}
	}

/**
 * Assuming there is discretization info provided in a certain format, this method then extracts ranges.
 * The discretization info file would be:
 * 
 * predicate_name	#discretization_bins	MIN		MAX
 * 
 * @param path_to_discretization_info
 * @param atoms
 * @return
 * @throws IOException
 */
	public HashMap<Predicate,MinMaxValue> getMinMaxValuesForPredicatesFromDiscretizationInfo(String path_to_discretization_info,Atom[] atoms) throws IOException{
		HashMap<Predicate,MinMaxValue> tmp=new HashMap<Predicate, MinMaxValue>();
		
		for(Atom a:atoms){
		  File discretization_info_file=new File(path_to_discretization_info);
		  String line=null;
		  BufferedReader bfr=new BufferedReader(new FileReader(discretization_info_file));
		  while((line=bfr.readLine())!=null){
			  if(line.contains(a.getPredicate().getPredicateName())){
				  String[] split=line.split("\t");
				  double min=Double.valueOf(split[2]);
				  double max=Double.valueOf(split[3]);
				  MinMaxValue result=new MinMaxValue(min, max);
				  tmp.put(a.getPredicate(), result);
				  ((DiscretizedPredicate) a.getPredicate()).setMinMaxValueRange(result);
			  }
		  }
		}
		return tmp;
	}

	private MinMaxValue findMinMax(List<MinMaxValue> list) {
		double min=Double.POSITIVE_INFINITY;
		double max=0.0;
		for(MinMaxValue a:list){
			if(a.getMin()<min){
				min=a.getMin();
			}
			if(a.getMax()>max){
				max=a.getMax();
			}
		}
		return new MinMaxValue(min,max);
	}

	private void getMin_max_values(String pathToFile,HashMap<Atom, MinMaxValue> tmp1, Atom[] atoms) {
		HashMap<String,Double> minValues=new HashMap<String, Double>();
		HashMap<String, Double> maxValues=new HashMap<String, Double>();
		for(Atom a:atoms){
			minValues.put(a.getPredicate().getPredicateName(), Double.POSITIVE_INFINITY);
			maxValues.put(a.getPredicate().getPredicateName(), 0.0);
		}
		Prolog engine=new Prolog();
		InputStream tmp_stream=null;
		try {
			tmp_stream = new FileInputStream(pathToFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			engine.setTheory(new Theory(tmp_stream));
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		alice.tuprolog.Parser parser=new alice.tuprolog.Parser(engine.getTheory().toString());
		Iterator<Term> it=parser.iterator();	
		Struct curr_term=null;

		while(it.hasNext()){
			try{
				Struct t=(Struct)it.next();
				curr_term=t;
				String pred_name=t.getName();
				double value = 0;

				if(t.getArity()==2 && t.getArg(1).isNumber()){
					try{	
						value=this.getCorrectValue(t.getArg(1));
					}
					catch(Exception e){		
					}
				}

				if(t.getArity()==3 && t.getArg(2).isNumber()){
					value=getCorrectValue(t.getArg(2));
				}


				if(minValues.containsKey(pred_name)){
					if(value<minValues.get(pred_name)){
						minValues.put(pred_name, value);
					}
					if(value>maxValues.get(pred_name)){
						maxValues.put(pred_name, value);
					}
				}
			}
			catch(Exception e){

			}

		}

		for(Atom a:atoms){
			tmp1.put(a, new MinMaxValue(minValues.get(a.getPredicate().getPredicateName()), maxValues.get(a.getPredicate().getPredicateName())));
		}		
	}

	private double getCorrectValue(Term term){
		if(term instanceof alice.tuprolog.Long){
			return getvalueLong(term);
		}
		if(term instanceof alice.tuprolog.Int){
			return getValueInt(term);
		}
		if(term instanceof alice.tuprolog.Double){
			return getDoubleValue(term);
		}

		//otherwise we have a little problem
		return Double.NaN;

	}

	private double getvalueLong(Term t){
		return (double)((alice.tuprolog.Long)t).intValue();
	}

	private double getValueInt(Term t){
		return (double)((alice.tuprolog.Int)t).intValue();
	}

	private double getDoubleValue(Term  t){
		return (double)((alice.tuprolog.Double)t).doubleValue();
	}



}
