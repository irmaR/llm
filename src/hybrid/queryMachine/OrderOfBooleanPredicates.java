package hybrid.queryMachine;

import hybrid.network.Predicate;

import java.util.HashMap;

import com.sun.org.apache.regexp.internal.recompile;

public class OrderOfBooleanPredicates {
	 private static OrderOfBooleanPredicates instance = null;
	 private static HashMap<String,Integer> ranking_of_boolean_preds;
	 
	 public OrderOfBooleanPredicates(){
			this.ranking_of_boolean_preds=new HashMap<String, Integer>();
		}
	 
	 public OrderOfBooleanPredicates(HashMap<String, Integer> map){
			this.ranking_of_boolean_preds=map;
		}
		
	 
	 public static OrderOfBooleanPredicates getInstance() {
	      if(instance == null) {
	         instance = new OrderOfBooleanPredicates();
	      }
	      return instance;
	   }

	
	public static HashMap<String,Integer>  getRankingOfBooleanPreds(){
		return ranking_of_boolean_preds;
	}
	
}

