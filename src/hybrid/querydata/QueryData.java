package hybrid.querydata;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.features.Feature;
import hybrid.interpretations.Interpretation;
import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.GroundAtom;
import hybrid.network.NumberValue;
import hybrid.network.RangeDiscrete;
import hybrid.network.StringValue;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.structureLearning.LearnedDependency;
import hybrid.utils.CVFoldPair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;


public class QueryData {
	/**
	 * This data structure contains for each interpretation a list of markov blankets.
	 */
	private HashMap<Interpretation,List<MarkovBlanket>> query_results;
	private Dependency dep;
	private List<MarkovBlanket> flatData;
	private int nr_groundings_for_head=0;
	private HashMap<Atom,Double> inference_time_per_ground_atom;
	private Double averageAlphaTrainingData=null;
	private boolean hadUndefinedValue;

	public QueryData(Dependency dep){
		flatData=new ArrayList<MarkovBlanket>();
		this.dep=dep;
		this.hadUndefinedValue=false;
		this.query_results=new HashMap<Interpretation, List<MarkovBlanket>>();
		inference_time_per_ground_atom=new HashMap<Atom, Double>();

	}
	/**
	 * Add a markov blanket in interpretation i
	 * @param i - interpretation
	 * @param mB - markov blanket
	 */
	public void addMarkovBlanket(Interpretation i,MarkovBlanket mB){
		try{
			this.query_results.get(i).add(mB);
			if(this.hadUndefinedValue==false && mB.hasUndefinedValue()){
				this.hadUndefinedValue=true;
			}
		}
		catch(NullPointerException e){
			this.query_results.put(i, new ArrayList<MarkovBlanket>());
			this.query_results.get(i).add(mB);
		}

		this.flatData.add(mB);
		nr_groundings_for_head++;
	}
	
	
    public List<CVFoldPair> splitIntoFolds(int nrFolds){
    	List<QueryData> qd=new ArrayList<QueryData>();
    	List<Interpretation> interpretations= new ArrayList<Interpretation>(this.query_results.keySet());
    	Collections.shuffle(interpretations,new Random(123456789));
    	List<List<Interpretation>> folds=new ArrayList<List<Interpretation>>();
    	int cuttingIndex=interpretations.size()/nrFolds;
    	int iteration=0;
    	int currentNrFolds=0;
    	while(true){
    		List<Interpretation> fold=new ArrayList<Interpretation>();
    		if(currentNrFolds==(nrFolds-1)){
    			for(int i=iteration;i<interpretations.size();i++){
        		    fold.add(interpretations.get(i));
        		}
    			folds.add(fold);
    			break;
    		}
    		for(int i=iteration;i<iteration+cuttingIndex;i++){
    		    fold.add(interpretations.get(i));
    		}
    		
    		folds.add(fold);
    		currentNrFolds++;
    		iteration+=cuttingIndex;
    	}
    	List<CVFoldPair> pairs=new ArrayList<CVFoldPair>();
    	int valCounter=0;
    	int listCounter=0;
    	for(int i=0;i<folds.size();i++){
    		QueryData validation=new QueryData(this.dep);
    		QueryData training=new QueryData(this.dep);
    		listCounter++;
    		for(int j=0;j<folds.size();j++){
    			if(i==j){
        			for(Interpretation interp:folds.get(j)){
        				validation.addMarkovBlankets(interp, this.query_results.get(interp));
            		}
        		}
    			else{
    				for(Interpretation interp:folds.get(j)){
    					training.addMarkovBlankets(interp, this.query_results.get(interp));
            		}
    			}
    		}
    		pairs.add(new CVFoldPair(training, validation));
    		
    	}
    		
    	return pairs;
    	
    	
    }
	
	
	public boolean isHadUndefinedValue() {
		return hadUndefinedValue;
	}
	public void setHadUndefinedValue(boolean hadUndefinedValue) {
		this.hadUndefinedValue = hadUndefinedValue;
	}
	public HashMap<Interpretation, List<MarkovBlanket>> getQuery_results() {
		return query_results;
	}

	public Dependency getDep() {
		return dep;
	}

	public String toString(){
		String tmp="------------------------------------ Query Data  ---------------------------------\n";
		int counter=1;
		for(Interpretation i:query_results.keySet()){
			tmp+=" Interpretation "+i.getPath_to_interpretation()+" \n";
			for(MarkovBlanket mB:query_results.get(i)){
				tmp+=mB+"\n";
			}
			tmp+="----------------------------------------------------------------------------------------\n";
		}
		return tmp;
	}
	/**
	 * Get flat data over all interpretation
	 * @return
	 */
	public List<MarkovBlanket> getFlatData() {
		List<MarkovBlanket> list=new ArrayList<MarkovBlanket>();
		for(Interpretation i:query_results.keySet()){
			for(MarkovBlanket mB:query_results.get(i)){
				list.add(mB);
				mB.setPathToInterpretation(i.getPath_to_interpretation());
			}
		}
		return list;
	}

	public void addMarkovBlankets(Interpretation inter, List<MarkovBlanket> list) {
		for(MarkovBlanket mb:list){
			if(mb.hasUndefinedValue()){
				this.hadUndefinedValue=true;
			}
		}
		this.query_results.put(inter, list);	
	}

	public void incrementNrGroundingsForHead(int groundings){
		this.nr_groundings_for_head+=groundings;
	}

	public int getNrGroundingsForHeadAllInters(){
		return this.nr_groundings_for_head;

	}

	public void add_inference_time_per_groundAtom(Atom atom,double time){
		this.inference_time_per_ground_atom.put(atom,time);
	}
	public int getNr_groundings_for_head() {
		return this.nr_groundings_for_head;
	}
	public HashMap<Atom, Double> getInference_time_per_ground_atom() {
		return inference_time_per_ground_atom;
	}


	public Double getAverageAlpha(Atom head) {
		if(!this.dep.getHead().getPredicate().isBoolean()){
			return 1.0;
		}
		if(this.averageAlphaTrainingData!=null){
			return this.averageAlphaTrainingData;
		}
		else{
			double alpha=0;
			int counter=0;
			for(Interpretation i:this.query_results.keySet()){
				counter++;
				//if no subsampling performed!
				if(i.getSubSampleInfo()==null){
					alpha+=1;
				}
				else{
					alpha+=i.getSubSampleInfo().getAlphas().get(head);
				}
			}
			this.averageAlphaTrainingData=alpha/counter;
			return this.averageAlphaTrainingData;
		}
	}
	public void getMatlabFile(String file_path,String file_name) throws IOException {
		String result_file=file_path+"/"+file_name;
		ArrayList list_of_variables = new ArrayList();
		list_of_variables.add(getHeadValue(nr_groundings_for_head));
		list_of_variables.add(getHeadGroundAtoms(nr_groundings_for_head));

		for(Feature f:this.dep.getFeatures()){
			System.out.println(" Mat feature"+f);
			List<Value> value_all_interpretations=extractValues(f);
			if(f.isContinuousOutput()){
				System.out.println(" Mat: continuous output");
				try {
					double[] continuous_output=convertValuestoprimitives_continuous(value_all_interpretations);
					list_of_variables.add(getMatlabVariable(continuous_output,f));
				} catch (WrongTypeException e) {
					e.printStackTrace();
				}
			}
			else{
				System.out.println(" Mat: discrete output");

				try {
					String[] string_output=convertValuestoprimitives_Discrete(value_all_interpretations);
					list_of_variables.add(getMatlabVariable(string_output,f));
				} catch (WrongTypeException e) {
					e.printStackTrace();
				}
			}
		}
		new MatFileWriter(new File(result_file), list_of_variables);
	}

	private Object getHeadGroundAtoms(int nr_groundings_for_head2) {
		MLCell cell=new MLCell("randvars_"+this.dep.getHead().getPredicate().getPredicateName(), new int[]{1,nr_groundings_for_head});
		int i=0;
		for(Interpretation interp:this.query_results.keySet()){
			for(MarkovBlanket mb:this.query_results.get(interp)){
				cell.set(new MLChar(mb.getHead().toString(),mb.getHead().toString()),i++);
			}
		}
		return cell;
	}
	private Object getHeadValue(int nr_groundings_for_head) {
		MLCell cell=new MLCell("head_values_"+this.dep.getHead().getPredicate().getPredicateName(), new int[]{1,nr_groundings_for_head});
		int i=0;
		for(Interpretation interp:this.query_results.keySet()){
			for(MarkovBlanket mb:this.query_results.get(interp)){
				cell.set(new MLChar(mb.getHead().getValue().toString(),mb.getHead().getValue().toString()),i++);
			}
		}
		return cell;
	}
	private MLCell getMatlabVariable(String[] string_output, Feature f) {
		MLCell cell=new MLCell("feature_index_"+String.valueOf(f.getId()), new int[]{1,string_output.length});
		int i=0;
		for(String s:string_output){
			cell.set(new MLChar(s.toString(),s.toString()),i++);
		}
		System.out.println(cell.name);
		return cell;
	}
	private MLDouble getMatlabVariable(double[] continuous_output, Feature f) {
		MLDouble scores=new MLDouble("feature_index_"+String.valueOf(f.getId()),continuous_output,continuous_output.length);
		return scores;
	}


	private String[] convertValuestoprimitives_Discrete(List<Value> value_all_interpretations) throws WrongTypeException {
		String[] values=new String[value_all_interpretations.size()];
		int i=0;
		for(Value v:value_all_interpretations){
			if(v instanceof UndefinedValue){
				values[i++]="NaN";
				continue;
			}		
			if(v.isnumeric()){
				throw new WrongTypeException(" You are trying to turn these values to primitive String, but the value is not string!");
			}
			if(v.isBoolean()){
				values[i++]=((BoolValue)v).getStringValue();
			}
			else{
				StringValue value=(StringValue)v;
				values[i++]=value.getValue();
			}
		}
		return values;
	}

	private double[] convertValuestoprimitives_continuous(List<Value> value_all_interpretations) throws WrongTypeException {
		double[] values=new double[value_all_interpretations.size()];
		int i=0;
		for(Value v:value_all_interpretations){
			if(v instanceof UndefinedValue){
				values[i++]=Double.NaN;
				continue;
			}		
			if(!v.isnumeric()){
				throw new WrongTypeException(" You are trying to turn these values to primitive doubles, but the value is not numeric!");
			}
			else{
				NumberValue value=(NumberValue)v;
				values[i++]=value.getNumber();

			}
		}
		return values;
	}




	private List<Value> extractValues(Feature f) {
		List<Value> values=new ArrayList<Value>();
		for(Interpretation i:this.query_results.keySet()){
			for(MarkovBlanket mb:this.query_results.get(i)){
				values.add(mb.getFeatureValues().get(f));
			}
		}
		return values;
	}
	
	
	public void outputGroundAtomsForAllvalues(File output_file) throws IOException {
		FileWriter fw=new FileWriter(output_file);
		for(MarkovBlanket mb:this.flatData){
			GroundAtom head=mb.getHead();
			//if discrete 
			List<GroundAtom> grAtoms=head.getAtom().getPredicate().getGroundAtomsForAllPossibleValues(head);
			for(GroundAtom g:grAtoms){

				fw.append(g.toString()+"\n");
			}
		}
		fw.close();


	}





}
