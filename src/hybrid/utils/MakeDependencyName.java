package hybrid.utils;

import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.network.Value;

import java.util.HashMap;


public class MakeDependencyName {

	public static String makeName(Dependency dep,HashMap<Feature,Value> vals){
		String res="";
		for(Feature f:dep.getFeatures()){
			res+=f.toString().replace(" ","_")+"="+vals.get(f);
		}
		return res;
	}
	
}
