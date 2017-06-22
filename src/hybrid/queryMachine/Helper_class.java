package hybrid.queryMachine;

import hybrid.operators.Operator;

import java.util.ArrayList;
import java.util.List;

public class Helper_class{
	private  List<Double> results;
	private  Operator op;
	
	public Helper_class(Operator op){
		this.op=op;
		this.results=new ArrayList<Double>();
	}
    
	public Operator getOperator(){
	    return op;
	}
	
	public  List<Double> getList(){
		return this.results;
	}
	
	public void updateList(Double elem){
		this.results.add(elem);
	}
	
	public String toString(){
		return "String representation of helper "+this.results+" "+this.op;
	}
	
	public String printSomething(){
		return "LALALA";
	}
}
