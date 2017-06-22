package hybrid.utils;

import java.util.HashSet;
import java.util.Set;

public class SetManipulations {

	/**
	 * find what are the elements in set1 also present in set2
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static Set<Object> getIntersection(Set<?extends Object> set1,Set<? extends Object> set2){
		Set<Object> intersection=new HashSet<Object>();
		for(Object o:set1){
			if(set2.contains(o)){
				intersection.add(o);
			}
		}
		return intersection;
	}
	
}
