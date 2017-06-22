package process_data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.Var;

public class GetRangedNumericPredicates {

	public static void main(String[] args) throws IOException, InvalidTheoryException, NoSolutionException, MalformedGoalException{
		int nr_bins=5;
		String path=args[0];
		String output_file=args[1];
		
		//predicates
		String cell_doublingtime="cell_doublingtime";
		String cell_lengthatbirth="cell_lengthatbirth";
		String cell_avglength="cell_avglength";
		String cell_lengthgrowthrate="cell_lengthgrowthrate";
		
		Prolog prolog_engine=new Prolog();
		InputStream is = new FileInputStream(new File(path));
		Theory input_data=new Theory(is);
		prolog_engine.addTheory(input_data);
		
		
		Struct query_findall_doubling_time=(Struct) prolog_engine.solve("findall(Value,cell_doublingtime(Cell,Value),List).").getVarValue("List");
		double min_doubling_time=findMinimum(query_findall_doubling_time);
		double max_doubling_time=findMaximum(query_findall_doubling_time);
		System.out.println("Doubling time: "+min_doubling_time+","+max_doubling_time);
		
		
		Struct query_findall_cell_lengthatbirth=(Struct) prolog_engine.solve("findall(Value,cell_lengthatbirth(Cell,Value),List).").getVarValue("List");
		double min_cell_lengthatbirth=findMinimum(query_findall_cell_lengthatbirth);
		double max_cell_lengthatbirth=findMaximum(query_findall_cell_lengthatbirth);
		System.out.println("cell_lengthatbirth: "+min_cell_lengthatbirth+","+max_cell_lengthatbirth);
		
		Struct query_findall_cell_avglength=(Struct) prolog_engine.solve("findall(Value,cell_avglength(Cell,Value),List).").getVarValue("List");
		double min_cell_avglength=findMinimum(query_findall_cell_avglength);
		double max_cell_avglength=findMaximum(query_findall_cell_avglength);
		System.out.println("cell_avglength: "+min_cell_avglength+","+max_cell_avglength);
		
		Struct query_findall_cell_lengthgrowthrate=(Struct) prolog_engine.solve("findall(Value,cell_lengthgrowthrate(Cell,Value),List).").getVarValue("List");
		double min_cell_lengthgrowthrate=findMinimum(query_findall_cell_lengthgrowthrate);
		double max_ccell_lengthgrowthrate=findMaximum(query_findall_cell_lengthgrowthrate);
		System.out.println("cell_lengthgrowthrate: "+min_cell_lengthgrowthrate+","+max_ccell_lengthgrowthrate);
		
		File output=new File(output_file);
		BufferedWriter bw=new BufferedWriter(new FileWriter(output));
		bw.append(cell_doublingtime+"\t"+nr_bins+"\t"+min_doubling_time+"\t"+max_doubling_time+"\n");
		bw.append(cell_lengthatbirth+"\t"+nr_bins+"\t"+min_cell_lengthatbirth+"\t"+max_cell_lengthatbirth+"\n");
		bw.append(cell_avglength+"\t"+nr_bins+"\t"+min_cell_avglength+"\t"+max_cell_avglength+"\n");
		bw.append(cell_lengthgrowthrate+"\t"+nr_bins+"\t"+min_cell_lengthgrowthrate+"\t"+max_ccell_lengthgrowthrate+"\n");
		bw.close();
	}

	private static double findMinimum(Struct query_findall_doubling_time) {
		double min=Double.POSITIVE_INFINITY;
		Iterator it=query_findall_doubling_time.listIterator();
		while(it.hasNext()){
			double new_value=((alice.tuprolog.Number)it.next()).doubleValue();
			if(new_value<min){
				min=new_value;
			}
		}
		return min;
	}
	
	private static double findMaximum(Struct query_findall_doubling_time) {
		double max=0;
		Iterator it=query_findall_doubling_time.listIterator();
		while(it.hasNext()){
			double new_value=((alice.tuprolog.Number)it.next()).doubleValue();
			if(new_value>max){
				max=new_value;
			}
		}
		return max;
	}
	
}
