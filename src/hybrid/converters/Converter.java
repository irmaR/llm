package hybrid.converters;

import hybrid.features.Feature;
import hybrid.network.Value;
import hybrid.parameters.Coefficients;
import hybrid.parameters.LinearGaussianCoeff;
import hybrid.structureLearning.DecisionTreeData;
import hybrid.structureLearning.Node;

import java.util.HashMap;

public interface Converter {

	public String convert(Node<DecisionTreeData> n);
	
}
