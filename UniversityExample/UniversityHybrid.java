import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.Renaming;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Mode;
import hybrid.features.Proportion;
import hybrid.features.ValueFt;
import hybrid.interpretations.NoCycles;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.ApplicationNetwork;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.Constant;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.Type;

import java.util.*;

/**
 * 
 * @author irma
 *
 */
public class UniversityHybrid implements ApplicationNetwork {

	public NetworkInfo defineApplicationNetwork(int subsampling_ratio){
		//define types in your application
		Type stud=new Type("student");
		Type c=new Type("course");
		Type p=new Type("prof");
		//define logvars you would need to specify your initial atoms
		Logvar student=new Logvar("S",stud);
		//we will need a renaming logvar here for friend(S,S1)
		Logvar student1=new Logvar("S1",stud);
		Logvar course=new Logvar("C",c);
		Logvar professor=new Logvar("P",p);

		//define your predicates - predicate name + arity 
		//Gaussian predicates
		GaussianPred intel=new GaussianPred("intelligence",1);
		GaussianPred ab=new GaussianPred("ability",1,20,100);
		GaussianPred nrhours=new GaussianPred("nrhours",1,0,200);
		
		//categorical predicates
		CategoricalPred gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		CategoricalPred sat=new CategoricalPred("satisfaction",2,new String[]{"low","mid","high"});
		CategoricalPred diff=new CategoricalPred("difficulty",1,new String[]{"easy","medium","hard"});
        
		//define your Boolean predicates
		BooleanPred tk=new BooleanPred("takes",1);
		//this is a special construct for performing subsampling of negative atoms. You specify 
		//negative to positive subsampling ratio (e.g., 1 or 1/2. The first means we have as many positive
		//atoms as negatives. 1/2 means that the number of negatives is half the number of positives)
		//if subsampling procedureis not specified, then there is no subsampling of negative atoms, and
		//all the negatives are used in the learning. We use it here for "teaches" because its domain is 
		//rather small. While for example, the domain for "friend" is |student| x |student| and it grows
		//exponentially
		//tk.setSubsampleingProcedure(new TuPrologInterpretationCreator_Subsampling(new NoCycles(),subsampling_ratio));
		BooleanPred tch=new BooleanPred("teaches",2);		
		BooleanPred fr=new BooleanPred("friend",2);

	    //Given the predicates, make atoms. They additionaly specify what the logvars are
		Atom intelligence=new Atom(intel, new Logvar[]{student});
		Atom grade=new Atom(gr, new Logvar[]{student,course});
		Atom satisfaction=new Atom(sat, new Logvar[]{student,course});
		Atom takes=new Atom(tk, new Logvar[]{student,course});
		Atom ability=new Atom(ab,new Logvar[]{professor});
		Atom teaches=new Atom(tch,new Logvar[]{professor,course});
		Atom difficulty=new Atom(diff,new Logvar[]{course});
		Atom friend=new Atom(fr,new Logvar[]{student,student1});
		Atom numhours=new Atom(nrhours,new Logvar[]{course});
		return new NetworkInfo(new Atom[]{ability,difficulty,numhours,intelligence,grade,satisfaction,takes,teaches,friend},new Type[]{stud,c,p});	   
	}

	@Override
	public HashMap<Atom, Dependency> getTrueDependencies(NetworkInfo ntw)throws FeatureTypeException, ConjunctionConstructionProblem {
		return null;
	}
}
