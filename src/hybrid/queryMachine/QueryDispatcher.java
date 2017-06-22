package hybrid.queryMachine;

import hybrid.features.Average;
import hybrid.features.ComparisonFeature;
import hybrid.features.ComparisonFeatureContinuousOutput;
import hybrid.features.ContinuousOutputAggregate;
import hybrid.features.DiscreteInputContinuousOutput;
import hybrid.features.DiscretizedProportion;
import hybrid.features.Exist;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.Mode;
import hybrid.features.OperatorFeature;
import hybrid.features.Proportion;
import hybrid.features.ValueFt;
import hybrid.interpretations.Interpretation;
import hybrid.network.GroundAtom;
import hybrid.network.Value;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;

public interface QueryDispatcher {
	Value getValue(DiscreteInputContinuousOutput ft);
	Value getValue(ComparisonFeature ft);
	Value getValue(ComparisonFeatureContinuousOutput ft);
	Value getValue(Mode ft);
	Value getValue(ValueFt ft);
	Value getValue(ContinuousOutputAggregate ft) ;
	Value getValue(OperatorFeature operator_Feature);	
}
