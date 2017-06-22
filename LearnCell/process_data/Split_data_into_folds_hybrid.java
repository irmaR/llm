package process_data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.Var;


public class Split_data_into_folds_hybrid {

	private static HashMap<String,String> values;
	private static String c;
	
	public static void main(String[] args){
		
		Split_data_into_folds_hybrid tmp=new Split_data_into_folds_hybrid();
		/*String path_to_data="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/Antoine_data/continuous/datacontinuous.pl";
		String output_path="/cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/Antoine_data/continuous/folds/";*/
		String path_to_data=args[0];
		String output_path=args[1];

		c="c_";
		//c="C_";
		
		int nr_folds=10;
		String cell_indetifying_predicate="cell_id";
		String parent_identifying_predicate="parent";
		List<String> ids=null;
		HashMap<String,List<String>> parent_ids=null;
		HashMap<String,List<String>> children_ids=null;
		try {
			try {
				try {
					ids=tmp.extractCellIDs(path_to_data,cell_indetifying_predicate);
					parent_ids=tmp.getCellParents(path_to_data,ids,parent_identifying_predicate);
					children_ids=tmp.getCellChildren(path_to_data,ids,parent_identifying_predicate);

				} catch (NoSolutionException e) {
					e.printStackTrace();
				}
			} catch (InvalidTheoryException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<List<String>> split_data_in_folds=tmp.split_data_in_folds(nr_folds,ids);

		new File(output_path).mkdirs();

		for(int i=0;i<nr_folds;i++){
			try {
				try {
					try {
						File output_fold=tmp.distributePredicatesInFold(path_to_data,output_path,(i+1),split_data_in_folds.get(i),parent_ids,children_ids);
					} catch (NoSolutionException e) {
						e.printStackTrace();
					}
				} catch (InvalidTheoryException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private File distributePredicatesInFold(String path_to_data,String output_path,int fold_nr,List<String> cell_ids,HashMap<String, List<String>> parent_ids,HashMap<String, List<String>> children_ids) throws IOException, InvalidTheoryException, NoSolutionException {
		Prolog prolog_engine=new Prolog();
		InputStream is = new FileInputStream(new File(path_to_data));
		Theory input_data=new Theory(is);
		prolog_engine.addTheory(input_data);
		File output=new File(output_path+"fold"+fold_nr+".pl");

		BufferedWriter bf=null;
		try {
			bf=new BufferedWriter(new FileWriter(output));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String cell_oldpole="cell_oldpole";
		String cell_age="cell_age";
		String cell_doublingtime="cell_doublingtime";
		String cell_lengthatbirth="cell_lengthatbirth";
		String cell_avglength="cell_avglength";
		String cell_lengthgrowthrate="cell_lengthgrowthrate";
		
		values=new HashMap<String, String>();
		values.put(cell_doublingtime, "");
		values.put(cell_lengthatbirth, "");
		values.put(cell_avglength, "");
		values.put(cell_lengthgrowthrate, "");
		/*values.put(cell_doublingtime, "t_");
		values.put(cell_lengthatbirth, "l_");
		values.put(cell_avglength, "avg_");
		values.put(cell_lengthgrowthrate, "lgr_");*/
		values.put(cell_oldpole, "p_");
		values.put(cell_age, "a_");
		
		/*values.put(cell_doublingtime, "T_");
		values.put(cell_lengthatbirth, "L_");
		values.put(cell_avglength, "Avg_");
		values.put(cell_lengthgrowthrate, "Lgr_");
		values.put(cell_oldpole, "P_");
		values.put(cell_age, "A_");*/
		

		for(String cell_id:cell_ids){
			bf.append("cell_id("+c+cell_id+").\n");
		}

		String cell_old_pole_values_parent=new String();
		String cell_doublingtime_values_parent=new String();
		String cell_age_values_parent=new String();
		String cell_lengthatbirth_values_parent=new String();
		String cell_avglength_values_parent=new String();
		String cell_lengthgrowthrate_values_parent=new String();

		String cell_old_pole_values_child=new String();
		String cell_doublingtime_values_child=new String();
		String cell_age_values_child=new String();
		String cell_lengthatbirth_values_child=new String();
		String cell_avglength_values_child=new String();
		String cell_lengthgrowthrate_values_child=new String();

		String cell_old_pole_values=new String();
		String cell_doublingtime_values=new String();
		String cell_age_values=new String();
		String cell_lengthatbirth_values=new String();
		String cell_avglength_values=new String();
		String cell_lengthgrowthrate_values=new String();

		
		//getting chiildren values
		for(String cell_id:cell_ids){
			List<String> children=children_ids.get(cell_id);
			System.out.println("Children of "+cell_id+ " "+children);

			for(String s:children){
				bf.append("parent("+c+cell_id+","+c+s+").\n");

				try{
					String cell_old_pole_value_child=prolog_engine.solve(new Struct(cell_oldpole,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();
					String cell_doublingtime_child=prolog_engine.solve(new Struct(cell_doublingtime,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();
					String cell_age_child=prolog_engine.solve(new Struct(cell_age,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();
					String cell_lengthatbirth_child=prolog_engine.solve(new Struct(cell_lengthatbirth,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();
					String cell_avglength_child=prolog_engine.solve(new Struct(cell_avglength,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();
					String cell_lengthgrowthrate_child=prolog_engine.solve(new Struct(cell_lengthgrowthrate,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();
					cell_old_pole_values_child=cell_old_pole_values_child.concat(cell_oldpole+"("+c+s+","+values.get(cell_oldpole)+cell_old_pole_value_child+").\n");
					cell_doublingtime_values_child=cell_doublingtime_values_child.concat(cell_doublingtime+"("+c+s+","+values.get(cell_doublingtime)+cell_doublingtime_child+").\n");
					cell_age_values_child=cell_age_values_child.concat(cell_age+"("+c+s+","+values.get(cell_age)+cell_age_child+").\n");
					cell_lengthatbirth_values_child=cell_lengthatbirth_values_child.concat(cell_lengthatbirth+"("+c+s+","+values.get(cell_lengthatbirth)+cell_lengthatbirth_child+").\n");
					cell_avglength_values_child=cell_avglength_values_child.concat(cell_avglength+"("+c+s+","+values.get(cell_avglength)+cell_avglength_child+").\n");
					cell_lengthgrowthrate_values_child=cell_lengthgrowthrate_values_child.concat(cell_lengthgrowthrate+"("+c+s+","+values.get(cell_lengthgrowthrate)+cell_lengthgrowthrate_child+").\n");
				}
				catch(NoSolutionException e){
					continue;
				}

			}
		}
		
		

		for(String cell_id:cell_ids){
			List<String> parents=parent_ids.get(cell_id);
			for(String s:parents){
				bf.append("parent("+c+s+","+c+cell_id+").\n");

				try{
					String cell_old_pole_value_parent=prolog_engine.solve(new Struct(cell_oldpole,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();
					String cell_doublingtime_parent=prolog_engine.solve(new Struct(cell_doublingtime,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();
					String cell_age_parent=prolog_engine.solve(new Struct(cell_age,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();
					String cell_lengthatbirth_parent=prolog_engine.solve(new Struct(cell_lengthatbirth,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();
					String cell_avglength_parent=prolog_engine.solve(new Struct(cell_avglength,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();
					String cell_lengthgrowthrate_parent=prolog_engine.solve(new Struct(cell_lengthgrowthrate,Term.createTerm(s),new Var("Y"))).getVarValue("Y").toString();

					cell_old_pole_values_parent=cell_old_pole_values_parent.concat(cell_oldpole+"("+c+s+","+values.get(cell_oldpole)+cell_old_pole_value_parent+").\n");
					cell_doublingtime_values_parent=cell_doublingtime_values_parent.concat(cell_doublingtime+"("+c+s+","+values.get(cell_doublingtime)+cell_doublingtime_parent+").\n");
					cell_age_values_parent=cell_age_values_parent.concat(cell_age+"("+c+s+","+values.get(cell_age)+cell_age_parent+").\n");
					cell_lengthatbirth_values_parent=cell_lengthatbirth_values_parent.concat(cell_lengthatbirth+"("+c+s+","+values.get(cell_lengthatbirth)+cell_lengthatbirth_parent+").\n");
					cell_avglength_values_parent=cell_avglength_values_parent.concat(cell_avglength+"("+c+s+","+values.get(cell_avglength)+cell_avglength_parent+").\n");
					cell_lengthgrowthrate_values_parent=cell_lengthgrowthrate_values_parent.concat(cell_lengthgrowthrate+"("+c+s+","+values.get(cell_lengthgrowthrate)+cell_lengthgrowthrate_parent+").\n");
				}
				catch(NoSolutionException e){
					continue;
				}

			}
		}
		for(String cell_id:cell_ids){
			String cell_old_pole_value=prolog_engine.solve(new Struct(cell_oldpole,Term.createTerm(cell_id),new Var("Y"))).getVarValue("Y").toString();
			String cell_doublingtime_value=prolog_engine.solve(new Struct(cell_doublingtime,Term.createTerm(cell_id),new Var("Y"))).getVarValue("Y").toString();
			String cell_age_value=prolog_engine.solve(new Struct(cell_age,Term.createTerm(cell_id),new Var("Y"))).getVarValue("Y").toString();
			String cell_lengthatbirth_value=prolog_engine.solve(new Struct(cell_lengthatbirth,Term.createTerm(cell_id),new Var("Y"))).getVarValue("Y").toString();
			String cell_avglength_value=prolog_engine.solve(new Struct(cell_avglength,Term.createTerm(cell_id),new Var("Y"))).getVarValue("Y").toString();
			String cell_lengthgrowthrate_value=prolog_engine.solve(new Struct(cell_lengthgrowthrate,Term.createTerm(cell_id),new Var("Y"))).getVarValue("Y").toString();

		//	System.out.println(" Query: "+new Struct(cell_lengthgrowthrate,Term.createTerm(cell_id),new Var("Y")));
			Struct proba=new Struct(cell_lengthgrowthrate,Term.createTerm(cell_id),new Var("Y"));
			//System.out.println(prolog_engine.solve(proba));

			cell_old_pole_values=cell_old_pole_values.concat(cell_oldpole+"("+c+cell_id+","+values.get(cell_oldpole)+cell_old_pole_value+").\n");
			cell_doublingtime_values=cell_doublingtime_values.concat(cell_doublingtime+"("+c+cell_id+","+values.get(cell_doublingtime)+cell_doublingtime_value+").\n");
			cell_age_values=cell_age_values.concat(cell_age+"("+c+cell_id+","+values.get(cell_age)+cell_age_value+").\n");
			cell_lengthatbirth_values=cell_lengthatbirth_values.concat(cell_lengthatbirth+"("+c+cell_id+","+values.get(cell_lengthatbirth)+cell_lengthatbirth_value+").\n");
			cell_avglength_values=cell_avglength_values.concat(cell_avglength+"("+c+cell_id+","+values.get(cell_avglength)+cell_avglength_value+").\n");
			cell_lengthgrowthrate_values+=cell_lengthgrowthrate+"("+c+cell_id+","+values.get(cell_lengthgrowthrate)+cell_lengthgrowthrate_value+").\n";

		}
		bf.append(cell_old_pole_values);
		bf.append(cell_doublingtime_values);
		bf.append(cell_age_values);
		bf.append(cell_lengthatbirth_values);
		bf.append(cell_avglength_values);
		bf.append(cell_lengthgrowthrate_values);

		bf.append(cell_old_pole_values_parent);
		bf.append(cell_doublingtime_values_parent);
		bf.append(cell_age_values_parent);
		bf.append(cell_lengthatbirth_values_parent);
		bf.append(cell_avglength_values_parent);
		bf.append(cell_lengthgrowthrate_values_parent);
		
		bf.append(cell_old_pole_values_child);
		bf.append(cell_doublingtime_values_child);
		bf.append(cell_age_values_child);
		bf.append(cell_lengthatbirth_values_child);
		bf.append(cell_avglength_values_child);
		bf.append(cell_lengthgrowthrate_values_child);

		bf.close();

		return null;
	}

	private List<List<String>> split_data_in_folds(int nr_folds,List<String> ids) {
		List<List<String>> folds=new ArrayList<List<String>>();
		List<List<String>> tmp=new ArrayList<List<String>>();
		int nr_cells_per_fold=ids.size()/10;
		//shuffle the list
		Collections.shuffle(ids);
		//split the data
		System.out.println(" Nr cells per fold: "+nr_cells_per_fold);

		for (int start = 0; start < ids.size(); start += nr_cells_per_fold) {
			int end = Math.min(start + nr_cells_per_fold, ids.size());
			List<String> sublist = ids.subList(start, end);
			tmp.add(sublist);
		}

		for(int i=0;i<nr_folds;i++){
			folds.add(tmp.get(i));
		}

		if(tmp.size()>nr_folds){
			for(int j=nr_folds;j<tmp.size();j++){
				List<String> extraSublist=new ArrayList<String>();
				extraSublist.addAll(tmp.get(j));

				for(String s:extraSublist){
					Random rG=new Random();
					int index=rG.nextInt(nr_folds);
					List<String> index_list=new ArrayList<String>();
					index_list.addAll(folds.get(index));
					index_list.add(s);
					folds.set(index,index_list);
					//folds.get(index).add(s);
				}
			}
		}
		int counter=1;
		for(List<String> f:folds){
			System.out.println(" Fold: "+(counter++)+" nr: "+f.size()+f);
		}
		return folds;
	}

	private HashMap<String, List<String>> getCellChildren(String path_to_data,List<String> ids,String child_identifying_predicate) throws InvalidTheoryException, NoSolutionException, IOException {
		HashMap<String,List<String>> tmp=new HashMap<String, List<String>>();
		for(String id:ids){
			List<String> id_parents=getIdsOfChildren(id,path_to_data,child_identifying_predicate);
			tmp.put(id,id_parents);
		}
		return tmp;
	}
	
	
	private HashMap<String, List<String>> getCellParents(String path_to_data,List<String> ids,String parent_identifying_predicate) throws InvalidTheoryException, NoSolutionException, IOException {
		HashMap<String,List<String>> tmp=new HashMap<String, List<String>>();
		for(String id:ids){
			List<String> id_parents=getIdsOfParents(id,path_to_data,parent_identifying_predicate);
			tmp.put(id,id_parents);
		}
		return tmp;
	}

	private List<String> getIdsOfParents(String id, String path_to_data,String parent_identifying_predicate) throws IOException, InvalidTheoryException, NoSolutionException {
		List<String> cell_ids=new ArrayList<String>();
		Prolog prolog_engine=new Prolog();
		InputStream is = new FileInputStream(new File(path_to_data));
		Theory input_data=new Theory(is);
		prolog_engine.addTheory(input_data);
		Term query_term=Term.createTerm(parent_identifying_predicate+"("+"Y,"+id+")");
		Struct query=new Struct("findall",Term.createTerm("Y"),query_term,new Var("Value"));
		SolveInfo res= prolog_engine.solve(query);
		Term list=res.getVarValue("Value");
		int length=((Struct)list).listSize();
		Iterator list_iter=((Struct)list).listIterator();
		while(list_iter.hasNext()){
			cell_ids.add(list_iter.next().toString());
		}
		return cell_ids;
	}
	
	private List<String> getIdsOfChildren(String id, String path_to_data,String parent_identifying_predicate) throws IOException, InvalidTheoryException, NoSolutionException {
		List<String> cell_ids=new ArrayList<String>();
		Prolog prolog_engine=new Prolog();
		InputStream is = new FileInputStream(new File(path_to_data));
		Theory input_data=new Theory(is);
		prolog_engine.addTheory(input_data);
		Term query_term=Term.createTerm(parent_identifying_predicate+"("+id+",Y)");
		Struct query=new Struct("findall",Term.createTerm("Y"),query_term,new Var("Value"));
		SolveInfo res= prolog_engine.solve(query);
		Term list=res.getVarValue("Value");
		int length=((Struct)list).listSize();
		Iterator list_iter=((Struct)list).listIterator();
		while(list_iter.hasNext()){
			cell_ids.add(list_iter.next().toString());
		}
		return cell_ids;
	}

	private List<String> extractCellIDs(String path_to_data,String cell_indetifying_predicate) throws IOException, InvalidTheoryException, NoSolutionException {
		List<String> cell_ids=new ArrayList<String>();
		Prolog prolog_engine=new Prolog();
		InputStream is = new FileInputStream(new File(path_to_data));
		Theory input_data=new Theory(is);
		prolog_engine.addTheory(input_data);
		Term query_term=Term.createTerm(cell_indetifying_predicate+"(X)");
		Struct query=new Struct("findall",Term.createTerm("X"),query_term,new Var("Value"));
		SolveInfo res= prolog_engine.solve(query);
		Term list=res.getVarValue("Value");
		int length=((Struct)list).listSize();
		Iterator list_iter=((Struct)list).listIterator();
		while(list_iter.hasNext()){
			cell_ids.add(list_iter.next().toString());
		}
		return cell_ids;
	}

}
