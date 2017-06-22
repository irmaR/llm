package hybrid.evaluation;

import hybrid.cpdEvaluation.DiscreteEval;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.interpretations.Interpretation;
import hybrid.network.BoolValue;
import hybrid.network.RangeDiscrete;
import hybrid.network.Value;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;

/**
 * Get predictions needed for calculating AUC in matlab file formats
 * @author irma
 *
 */
public class CalculatePredictionForAUC {

	public void calculatePredictionsForAUCMatlab(Dependency learnedDep,TuPrologQueryMachine tuPrologQueryMachine_test, File result_file) throws IOException {
		HashMap<Value,Double[]> predictions_for_reference_class=new HashMap<Value, Double[]>();

		QueryData markov_blankets=tuPrologQueryMachine_test.getQueryResults(learnedDep);
		List<Value> true_labels=new ArrayList<Value>();

		for(Value reference_class:((RangeDiscrete)learnedDep.getHead().getPredicate().getRange()).getValues()){
			//reference class
			List<Double> predictions=new ArrayList<Double>();
			true_labels=new ArrayList<Value>();

			for(Interpretation i:markov_blankets.getQuery_results().keySet()){
				for(MarkovBlanket mB:markov_blankets.getQuery_results().get(i)){
					predictions.add(learnedDep.getCpd().getCpdEvaluator().getProbability(reference_class, mB, learnedDep.getCpd().getParameters()));
					true_labels.add(mB.getHead().getValue());
				}
			}
			predictions_for_reference_class.put(reference_class, predictions.toArray(new Double[predictions.size()]));	
		}

		double[][] matric_scores=new double[predictions_for_reference_class.keySet().size()][true_labels.size()];
		for(int i=0;i<predictions_for_reference_class.keySet().size();i++){
			Value ref_class=((RangeDiscrete)learnedDep.getHead().getPredicate().getRange()).getValues().get(i);
			for(int j=0;j<true_labels.size();j++){
				matric_scores[i][j]=predictions_for_reference_class.get(ref_class)[j];
			}
		}

		ArrayList list_of_variables = new ArrayList();
		MLDouble scores=new MLDouble("scores",matric_scores);
		list_of_variables.add(scores);

		MLCell cell=new MLCell(("labels"), new int[]{1,true_labels.size()});
		MLCell cell_ref_classes=new MLCell(("ref_classes"), new int[]{1,((RangeDiscrete)learnedDep.getHead().getPredicate().getRange()).getValues().size()});
		HashMap<Value,Integer> classes_nr=new HashMap<Value, Integer>();

		int counter=0;
		for(Value v:((RangeDiscrete)learnedDep.getHead().getPredicate().getRange()).getValues()){
			classes_nr.put(v, 0);
			cell_ref_classes.set(new MLChar(v.toString(),v.toString()),counter++);
		}
		list_of_variables.add(cell_ref_classes);
		int i=0;

		for(Value s:true_labels){
			cell.set(new MLChar(s.toString(),s.toString()),i++);
			classes_nr.put(s, classes_nr.get(s)+1);
		}
		list_of_variables.add(cell);

		//ratios
		double[] ratios=new double[((RangeDiscrete)learnedDep.getHead().getPredicate().getRange()).getValues().size()];
		int count=0;
		for(Value v:((RangeDiscrete)learnedDep.getHead().getPredicate().getRange()).getValues()){
			ratios[count++]=((double)classes_nr.get(v))/true_labels.size();
		}
		MLDouble ratios_array=new MLDouble("ratios",ratios,ratios.length);
		list_of_variables.add(ratios_array);


		new MatFileWriter(result_file, list_of_variables);

	}

	public void getProbabilitiesForGroundAtoms(Dependency learnedDep,TuPrologQueryMachine tuPrologQueryMachine_test, File result_file,File true_labels) throws IOException {
		FileWriter fw=new FileWriter(result_file);
		FileWriter fw_true_labels=new FileWriter(true_labels);
		QueryData markov_blankets=tuPrologQueryMachine_test.getQueryResults(learnedDep);
        int ground_atom_counter=0;
		
        for(Interpretation i:markov_blankets.getQuery_results().keySet()){
			for(MarkovBlanket mB:markov_blankets.getQuery_results().get(i)){
				HashMap<Value,Double> probabilities_for_all_values=((DiscreteEval)learnedDep.getCpd().getCpdEvaluator()).getProbabilityDistributionAllValues(mB, learnedDep.getCpd().getParameters());
				for(Value v:probabilities_for_all_values.keySet()){
					if(mB.getHead().getAtom().getPredicate().isBoolean() && v.equals(new BoolValue(true))){
						fw.append(mB.getHead()+" "+probabilities_for_all_values.get(v)+"\n");
						if(mB.getHead().getValue().equals(new BoolValue(true))){
							fw_true_labels.append(mB.getHead()+"\n");
							fw_true_labels.flush();
						}
						break;
					}
					
					mB.getHead().setValue(v);
					fw.append(mB.getHead()+" "+probabilities_for_all_values.get(v)+"\n");						
				}	
				fw_true_labels.append(mB.getHead()+"\n");
				fw_true_labels.flush();
				fw.flush(); 
		        ground_atom_counter++;
			}
		}
		fw.close();
		fw_true_labels.close();
	}

	
public void getProbabilitiesForGroundAtoms_no_noise(Dependency learnedDep,TuPrologQueryMachine tuPrologQueryMachine_test, File result_file,File true_labels) throws IOException {
	FileWriter fw=new FileWriter(result_file);
	FileWriter fw_true_labels=new FileWriter(true_labels);
	QueryData markov_blankets=tuPrologQueryMachine_test.getQueryResults(learnedDep);
    int ground_atom_counter=0;
	
    for(Interpretation i:markov_blankets.getQuery_results().keySet()){
		for(MarkovBlanket mB:markov_blankets.getQuery_results().get(i)){
			HashMap<Value,Double> probabilities_for_all_values=((DiscreteEval)learnedDep.getCpd().getCpdEvaluator()).getProbabilityDistributionAllValues(mB, learnedDep.getCpd().getParameters());
		 
			for(Value v:probabilities_for_all_values.keySet()){
				if(mB.getHead().getAtom().getPredicate().isBoolean() && v.equals(new BoolValue(true))){
					fw.append(mB.getHead()+" "+probabilities_for_all_values.get(v)+"\n");
					if(mB.getHead().getValue().equals(new BoolValue(true))){
						fw_true_labels.append(mB.getHead()+"\n");
						fw_true_labels.flush();
					}
					break;
				}
				
				mB.getHead().setValue(v);
				fw.append(mB.getHead()+" "+probabilities_for_all_values.get(v)+"\n");						
			}	
			fw_true_labels.append(mB.getHead()+"\n");
			fw_true_labels.flush();
			fw.flush(); 
	        ground_atom_counter++;
		}
	}
	fw.close();
	fw_true_labels.close();
}
}








