import java.util.ArrayList;

import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.RelationType;
import hybrid.network.StringValue;
import hybrid.network.Type;

import java.util.*;


public class IMDBHybrid {

	public NetworkInfo getIMDBHybrid(int i) {
		Type movie=new Type("mv");
		Type actor=new Type("actor");
		Type director=new Type("director");
		
		Logvar ac=new Logvar("A",actor);
		Logvar dir=new Logvar("D",director);
		Logvar mov=new Logvar("M",movie);
		
		
		//movie predicates
        Predicate admissionPred=new GaussianPred("admission",1);
        Predicate budgetPred=new GaussianPred("budget",1);
        Predicate ratingPred=new GaussianPred("rating",1);
        Predicate rentalPred=new GaussianPred("rental",1);
        Predicate revenuePred=new GaussianPred("revenue",1);
        
        
        //actor predicates
        BooleanPred actsInPred=new BooleanPred("actsIn", 2);
        BooleanPred wonOscarPred=new BooleanPred("wonoscar", 1);
        Predicate height=new GaussianPred("height",1);
        Predicate gender=new CategoricalPred("gender",1,new StringValue[]{new StringValue("f"),new StringValue("m")});
        BooleanPred workedUnderPred=new BooleanPred("workedUnder", 2);
        
        //director predicates
        BooleanPred directsPred=new BooleanPred("directed", 2);
        BooleanPred wonOscar=new BooleanPred("wonoscar_director",1);
        
        ArrayList<Atom> atoms=new ArrayList<Atom>();
        atoms.add(new Atom(admissionPred,Arrays.asList(mov)));
        atoms.add(new Atom(budgetPred,Arrays.asList(mov)));
        atoms.add(new Atom(ratingPred,Arrays.asList(mov)));
        atoms.add(new Atom(rentalPred,Arrays.asList(mov)));
        atoms.add(new Atom(revenuePred,Arrays.asList(mov)));
        
        Atom actsIn=new Atom(actsInPred,Arrays.asList(ac,mov));
        actsIn.setRelationType(RelationType.INTERNAL);
        atoms.add(actsIn);
        
        atoms.add(new Atom(wonOscarPred,Arrays.asList(ac)));
        atoms.add(new Atom(height,Arrays.asList(ac)));
        atoms.add(new Atom(gender,Arrays.asList(ac)));
        
        Atom workedUnder=new Atom(workedUnderPred,Arrays.asList(ac,dir));
        workedUnder.setRelationType(RelationType.INTERNAL);
        atoms.add(workedUnder);
       
        Atom directs=new Atom(directsPred,Arrays.asList(dir,mov));
        directs.setRelationType(RelationType.INTERNAL);
        atoms.add(directs);
        
        atoms.add(new Atom(wonOscar,Arrays.asList(dir)));
        
        ArrayList<Type> types=new ArrayList<Type>();
        types.add(movie);
        types.add(actor);
        types.add(director);
        
        
		return new NetworkInfo(atoms,types);
	}

}
