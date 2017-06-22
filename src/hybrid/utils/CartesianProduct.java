package hybrid.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for finding a Cartesian product of lists
 * @author irma
 * @param <T>
 *
 */
public class CartesianProduct<T> {
	/**
	 * Find a cartesian product over a list of objects
	 * @param lists
	 * @return 
	 * @return
	 */
	public List<List<T>> cartesianProduct(List<List<T>> lists) {
		List<List<T>> resultLists = new ArrayList<List<T>>();
		if (lists.size() == 0) {
			resultLists.add(new ArrayList<T>());
			return resultLists;
		} else {
			List<T> firstList = lists.get(0);
			List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
			for (T condition : firstList) {
				for (List<T> remainingList : remainingLists) {
					ArrayList<T> resultList = new ArrayList<T>();
					resultList.add(condition);
					resultList.addAll(remainingList);
					resultLists.add(resultList);
				}
			}
		}
		return resultLists;
	}
	
	public String printListOfPairs(List<List<T>> lists){
		String tmp="";
		for(List<T> l:lists){
			tmp+=l.toString()+"\n";
		}
		return tmp;
	}
	
}
