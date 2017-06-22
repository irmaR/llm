package hybrid.featureGenerator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import hybrid.dependencies.Dependency;
import hybrid.features.FeatureTypeException;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.LogvarRestrictionLiteral;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.Type;

import org.junit.Before;
import org.junit.Test;

public class RenamingTest{

	static HashMap<Type,HashSet<Logvar>> renaming;
	static HashMap<Type,HashSet<Logvar>> renaming_two_types;
	/**
	 * Creating very simple network of only grade and intelligence predicate. 
	 * Testing on two dependencies:
	 * intelligence(S) | Mode{grade(S,c)}
	 * grade(S,C) | Value{intelligence(S)}
	 * @throws FeatureTypeException 
	 */
	@Before
	public void setUp() throws FeatureTypeException{
		renaming=new HashMap<Type, HashSet<Logvar>>();
		renaming_two_types=new HashMap<Type, HashSet<Logvar>>();
		Type stud=new Type("student");
		Type cou=new Type("course");
		Logvar student=new Logvar("S",stud);
		Logvar student1=new Logvar("S1",stud);
		Logvar student2=new Logvar("S2",stud);
		Logvar course=new Logvar("C",cou);
		Logvar course1=new Logvar("C1",cou);
		Logvar course2=new Logvar("C2",cou);
		
		
		HashSet<Logvar> friends=new HashSet<Logvar>();
		friends.add(student);
		friends.add(student1);
		friends.add(student2);
		renaming.put(stud, friends);
		
		HashSet<Logvar> courses=new HashSet<Logvar>();
		courses.add(course);
		courses.add(course1);
		courses.add(course2);
		renaming_two_types.put(stud, friends);
		renaming_two_types.put(cou,courses );
	}
	
	@Test
	public void TestCreatingLogvarDifferences(){
		Renaming ren=new Renaming();
		ren.setRenaming(renaming);
		List<LogvarRestrictionLiteral> lists=ren.getDifferentLogvarsRestriction("\\==");
		assertEquals("S\\==S2",lists.get(0).toString());
		assertEquals("S\\==S1",lists.get(1).toString());
		assertEquals("S2\\==S1",lists.get(2).toString());
	}
	
	@Test
	public void TestCreatingLogvarDifferencesTwoTypes(){
		Renaming ren=new Renaming();
		ren.setRenaming(renaming_two_types);
		List<LogvarRestrictionLiteral> lists=ren.getDifferentLogvarsRestriction("\\==");
		assertEquals("C2\\==C1",lists.get(0).toString());
		assertEquals("C2\\==C",lists.get(1).toString());
		assertEquals("C1\\==C",lists.get(2).toString());
		assertEquals("S\\==S2",lists.get(3).toString());
		assertEquals("S\\==S1",lists.get(4).toString());
		assertEquals("S2\\==S1",lists.get(5).toString());
	}
		
		
}
