package hybrid.tocsvmodule;
import java.util.*;

import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;
import hybrid.network.Value;
import hybrid.querydata.*;


public class DataToCSV {


	public String dataToCSVFile(QueryData qd,Dependency dep,HashMap<Feature,Value> branchValues,boolean firstColumnGroundAtom){
		Integer data_points=null;
		String result="";
		System.out.println("CSV DEP FEATURES: "+dep.getFeatures());
		HashMap<Feature,QueryData> map=new HashMap<Feature,QueryData>();
		for(Feature f:dep.getFeatures()){
			if(data_points==null){
				data_points=qd.getNr_groundings_for_head();
			}
			map.put(f,qd);
		}
		for(int i=0;i<data_points;i++){
			String headValue=null;
			String headGroundAtom=null;
			String fts_string="";
			String row_string="";
			
			for(Feature f:dep.getFeatures()){
				if(branchValues.containsKey(f)){
					continue;
				}
				if(headValue==null){
					headValue=map.get(f).getFlatData().get(i).getHead().getValue().toString();
				}
				if(headGroundAtom==null){
					headGroundAtom=map.get(f).getFlatData().get(i).getHead().getTerm().toString();
				}
				//System.out.println(map.get(f).getFlatData());
				//System.out.println(map.get(f).getFlatData().get(i));
				//System.out.println(map.get(f).getFlatData().get(i).getFeatureValues());
				//System.out.println(map.get(f).getFlatData().get(i).getFeatureValues().get(f));
				fts_string+=map.get(f).getFlatData().get(i).getFeatureValues().get(f).toString()+",";	
			}
			if(headValue==null){
				if(firstColumnGroundAtom){
				    row_string=headGroundAtom+","+qd.getFlatData().get(i).getHead().getValue()+"\n";
				}
				else{
					row_string=qd.getFlatData().get(i).getHead().getValue()+"\n";
				}
			}
			else{
				if(firstColumnGroundAtom){
			       row_string=headGroundAtom.replace(",","_")+","+headValue+","+fts_string+"\n";
				}
				else{
				   row_string=headValue+","+fts_string+"\n";
				}
			}
			result = result.concat(row_string);
		}
		return result;

	}

	public String createNameForBranchData(Dependency dep,HashMap<Feature,Value> brachValues){
        String result="";
		for(Feature f:dep.getFeatures()){
        	result+=f.getIndexInFeatureSpace()+""+brachValues.get(f)+"_";
        }
		return result;

	}

}
