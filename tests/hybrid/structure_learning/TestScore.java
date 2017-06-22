package hybrid.structure_learning;

import static org.junit.Assert.*;
import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.Type;
import hybrid.structureLearning.LearnedDependency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestScore {

	
	@Test
	public void testCompareScore(){
		LearnedDependency sc1=new LearnedDependency(null,-145.34);
		LearnedDependency sc2=new LearnedDependency(null,-178.34);
		assertEquals(true,sc1.betterThan(sc2));
	}
	
	@Test
	public void testSortArray(){
		List<LearnedDependency> scores=new ArrayList<LearnedDependency>();
		LearnedDependency sc1=new LearnedDependency(null,-145.34);
		LearnedDependency sc2=new LearnedDependency(null,-178.34);
		LearnedDependency sc3=new LearnedDependency(null,-100.34);
		LearnedDependency sc4=new LearnedDependency(null,-50.34);
		scores.add(sc1);
		scores.add(sc2);
		scores.add(sc3);
		scores.add(sc4);
        
		List<LearnedDependency> sorted=new ArrayList<LearnedDependency>();
		sorted.add(sc4);
		sorted.add(sc3);
		sorted.add(sc1);
		sorted.add(sc2);
		Collections.sort(scores);		
		assertEquals(true,scores.equals(sorted));
	}
	
	
}
