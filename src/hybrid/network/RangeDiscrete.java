package hybrid.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Discrete range of randvars
 * @author irma
 *
 */
public class RangeDiscrete extends Range {

	public List<Value> values;
	
	public RangeDiscrete(){
		this.values=new ArrayList();
	}
	
	public RangeDiscrete(Value[] range){
        values=new ArrayList<Value>();
		this.values=new ArrayList(Arrays.asList(range));
	}
	
	public RangeDiscrete(String[] range){
        values=new ArrayList<Value>();
		for(String r:range){
			this.values.add(new StringValue(r));
		}
	}

	public List<Value> getValues() {
		return values;
	}

	public void setValues(List<Value> values) {
		this.values = values;
	}
	
	@Override
	public int getIndexOfValue(Value value){
		int i=-1;
		for(Value v:this.values){
			if(v.equals(value)){
				return ++i;
			}
			i++;
		}
		return i;
	}

	@Override
	public boolean isInRange(Value val) {
		for(Value v:this.values){
			if(val.equals(v)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void addValueToRange(Value val) {
		if(!this.values.contains(val)){
			this.values.add(val);	
		}
	}

	@Override
	public String toString() {
		return "RangeDiscrete=" + values;
	}

	@Override
	public boolean isDiscreteRange() {
		return true;
	}

	@Override
	public boolean isNumericRange() {
		return false;
	}

	@Override
	public boolean isBooleanRange() {
		if(this.values.size()==2){
			if(this.values.get(0).isBoolean() && this.values.get(1).isBoolean()){
				return true;
			}
			return false;
		}
		return false;
	}
	
	
	
	
}
