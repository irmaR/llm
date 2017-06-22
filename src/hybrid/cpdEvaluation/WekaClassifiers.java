package hybrid.cpdEvaluation;

import java.util.List;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.parameters.Parameters;
import hybrid.querydata.QueryData;
import weka.core.Instances;

public interface WekaClassifiers {

	
	public Instances initializeWekaInstanceSceleton(Dependency dep) throws BadParentSpecification;
	public Instances fillInTheValueS(QueryData trainingData,Instances instancesSceleton) throws BadParentSpecification;
	public Parameters trainClassifier(QueryData data, Instances trainingInstances) throws Exception;
}
