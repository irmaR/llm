package hybrid.features_evaluators;

import hybrid.network.Value;

import java.util.HashMap;
import java.util.List;

public interface NonDeterministicRangeBoundEvaluator {
	
	public Value calculateValues(HashMap<String, Integer> results);
	public Value calculateValues(List<Value> values);
}
