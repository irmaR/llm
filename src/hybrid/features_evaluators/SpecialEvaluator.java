package hybrid.features_evaluators;

import hybrid.network.Atom;
import hybrid.network.Logvar;
import hybrid.network.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface SpecialEvaluator <T> {
	
	public Value calculateValues(Integer count,int numberOfPossibleGroundings);
}
