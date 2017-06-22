package hybrid.converters;

import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features.Average;
import hybrid.features.DiscretizedProportion;
import hybrid.features.Exist;
import hybrid.features.FeatureTypeException;
import hybrid.features.Max;
import hybrid.network.Atom;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.PosLiteral;
import hybrid.network.Type;

import org.junit.Before;
import org.junit.Test;

public class TestConverterPool_DC {
	Atom A;
	Atom B;
	Atom C;
	Atom D;
	
	@Before
	public void setUp() throws FeatureTypeException, ConjunctionConstructionProblem{
		Type objects=new Type("object");
		Logvar object=new Logvar("O",objects);
		CategoricalPred A_pred=new CategoricalPred("A", 1,new String[]{"a1","a2","a3"});
		CategoricalPred B_pred=new CategoricalPred("B", 1,new String[]{"val1","val2","val3"});
		GaussianPred C_pred=new GaussianPred("C",1);
		GaussianPred D_pred=new GaussianPred("D",1);
		 A=new Atom(A_pred, new Logvar[]{object});
		 B=new Atom(B_pred, new Logvar[]{object});
		 C=new Atom(C_pred, new Logvar[]{object});
		 D=new Atom(D_pred, new Logvar[]{object});
	}
	
	@Test
	public void convertFeatureAverage(){
		ConverterPoolDC cov=new ConverterPoolDC();
		try {
			Average averageTest=new Average(new Standard_Conjunction<>(A,new PosLiteral(C)));
			System.out.println(cov.convertFeature(averageTest));
		} catch (ConjunctionConstructionProblem e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void convertFeatureMax(){
		ConverterPoolDC cov=new ConverterPoolDC();
		try {
			Max maxTest=new Max(new Standard_Conjunction<>(A,new PosLiteral(C)));
			System.out.println(cov.convertFeature(maxTest));
		} catch (ConjunctionConstructionProblem e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeatureTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void convertFeatureDiscretizedProportion(){
		ConverterPoolDC cov=new ConverterPoolDC();
		try {
			DiscretizedProportion maxTest=new DiscretizedProportion(new Standard_Conjunction<>(A,new PosLiteral(C)),2);
			System.out.println(cov.convertFeature(maxTest));
		} catch (ConjunctionConstructionProblem e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void convertFeatureExist(){
		ConverterPoolDC cov=new ConverterPoolDC();
		try {
			Exist maxTest=new Exist(new Standard_Conjunction<>(A,new PosLiteral(C)));
		} catch (ConjunctionConstructionProblem e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
}
