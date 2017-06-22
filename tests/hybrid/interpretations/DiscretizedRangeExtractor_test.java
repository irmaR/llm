package hybrid.interpretations;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.DiscretizedPredicate;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.MinMaxValue;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.Type;

public class DiscretizedRangeExtractor_test {
	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Logvar student;
	private static Type student_type;
	
	@BeforeClass
	public static void setUp(){
		System.out.println(pathToInterpretations);
		student_type=new Type("student");
		student=new Logvar("S",student_type);
		DiscretizedPredicate intel=new DiscretizedPredicate("intelligence",1,2);
		BooleanPred takesP=new BooleanPred("takes",2);
		//takesP.setSubsampleingProcedure(new TuPrologInterpretationCreator_Subsampling(1));
		intelligence=new Atom(intel, new Logvar[]{student});
		ntw=new NetworkInfo(new Atom[]{intelligence},new Type[]{student_type});
	}
	
	@Test
	public void getMinMaxValuesForPredicates(){
		DiscretizedRangeExtractor dRangeExtr=new DiscretizedRangeExtractor();
		Atom[] continuous_atoms=new Atom[]{intelligence};
		try {
			dRangeExtr.getMinMaxValuesForPredicates(pathToInterpretations, continuous_atoms);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		assertEquals(57.77,intelligence.getPredicate().get_minimum_maximum_value().getMin(),0.02);
		assertEquals(122.44,intelligence.getPredicate().get_minimum_maximum_value().getMax(),0.02);
	}
	
}
