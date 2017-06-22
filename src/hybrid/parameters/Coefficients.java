package hybrid.parameters;

import java.io.Serializable;
import java.util.HashMap;

import hybrid.converters.ConvertCoefficientsInterface;
import hybrid.converters.ConvertPoolInterface;
import hybrid.converters.Converter;
import hybrid.converters.DC_converter;
import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.network.Value;

public abstract class Coefficients implements Serializable {

	public abstract String convert(ConvertPoolInterface converter);
	public abstract void convert(ConvertCoefficientsInterface converter,AssignmentKey key,Dependency dep);
	public abstract String filterNaN();

	public String decisionTreeFormat(Feature select_to_print_feature) {
		// TODO Auto-generated method stub
		return null;
	}
	public HashMap<Value, String> decisionTreeFormat2(
			Feature select_to_print_feature) {
		// TODO Auto-generated method stub
		return null;
	}


}
