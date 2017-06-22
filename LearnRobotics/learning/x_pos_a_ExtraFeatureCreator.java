package learning;

import hybrid.comparators.InBetween;
import hybrid.featureGenerator.ComplexConjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.Renaming;
import hybrid.features.Average;
import hybrid.features.OperatorFeature;
import hybrid.features.Feature;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.operators.Addition;
import hybrid.operators.Division;
import hybrid.operators.Multiplication;
import hybrid.operators.Subtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class x_pos_a_ExtraFeatureCreator {

	public static List<Feature> getListOfFeatures(Atom at,NetworkInfo ntw) throws ConjunctionConstructionProblem{
	//add special features for x_pos_a  
	Renaming ren_object=new Renaming();
	
	Atom x_pos_b=ntw.getPredicateNameToAtom().get("x_pos_b");
	ren_object=new Renaming();
	ren_object.addRenaming(x_pos_b.getArgument(0).getType(), new Logvar("Obj_1",x_pos_b.getArgument(0).getType()));
    Atom x_pos_b1=x_pos_b.applyRenaming(ren_object).get(0);
	
	Atom x_pos_a=ntw.getPredicateNameToAtom().get("x_pos_a");
	ren_object=new Renaming();
	ren_object.addRenaming(x_pos_a.getArgument(0).getType(), new Logvar("Obj_1",x_pos_a.getArgument(0).getType()));
	Atom x_pos_a1=x_pos_a.applyRenaming(ren_object).get(0);
	
	Atom main_object=ntw.getPredicateNameToAtom().get("main_object");
    ren_object=new Renaming();
    ren_object.addRenaming(main_object.getArgument(0).getType(), new Logvar("Obj_1",main_object.getArgument(0).getType()));
    Atom main_object_1=main_object.applyRenaming(ren_object).get(0);
    
    Atom secondary_object=ntw.getPredicateNameToAtom().get("secondary_object");
    ren_object=new Renaming();
    ren_object.addRenaming(secondary_object.getArgument(0).getType(), new Logvar("Obj_1",secondary_object.getArgument(0).getType()));
    Atom secondary_object_1=secondary_object.applyRenaming(ren_object).get(0);
	
	Literal[] context_2=new Literal[]{new PosLiteral(main_object),new PosLiteral(secondary_object_1)};
    Literal[] context_3=new Literal[]{new PosLiteral(main_object_1),new PosLiteral(secondary_object)};
	
    List<ComplexConjunction> x_pos_a_conj=new ArrayList<ComplexConjunction>();
   
    x_pos_a_conj.add(new ComplexConjunction(x_pos_a,new PosLiteral(x_pos_b),new PosLiteral(x_pos_b1),context_2,new Addition()));
    x_pos_a_conj.add(new ComplexConjunction(x_pos_a,new PosLiteral(x_pos_b),new PosLiteral(x_pos_b1),context_2,new Subtraction()));
    x_pos_a_conj.add(new ComplexConjunction(x_pos_a,new PosLiteral(x_pos_b),new PosLiteral(x_pos_b1),context_2,new Division()));
    x_pos_a_conj.add(new ComplexConjunction(x_pos_a,new PosLiteral(x_pos_b),new PosLiteral(x_pos_b1),context_2,new Multiplication()));
    
    x_pos_a_conj.add(new ComplexConjunction(x_pos_a,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_3,new Addition()));
    x_pos_a_conj.add(new ComplexConjunction(x_pos_a,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_3,new Subtraction()));
    x_pos_a_conj.add(new ComplexConjunction(x_pos_a,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_3,new Division()));
    x_pos_a_conj.add(new ComplexConjunction(x_pos_a,new PosLiteral(x_pos_b1),new PosLiteral(x_pos_b),context_3,new Multiplication()));
    
  //add thresholds for distance features
  		double min_threshold=-12;
  		double max_threshold=27;
          
  		HashMap<String,List<Feature>> additional_features=new HashMap<String, List<Feature>>();
  		double jump=0.5;
  		List<Feature> fts=new ArrayList<Feature>();
  		for(ComplexConjunction c:x_pos_a_conj){
  				for(double i=min_threshold;i<max_threshold;i+=jump){
  				fts.add(new OperatorFeature(c, new InBetween(i,i-jump),new Average()));
  				fts.add(new OperatorFeature(c,new InBetween(i,i-jump),new Average()));
  				fts.add(new OperatorFeature(c, new InBetween(i,i-jump),new Min()));
  				fts.add(new OperatorFeature(c,new InBetween(i,i-jump),new Min()));
  				fts.add(new OperatorFeature(c, new InBetween(i,i-jump),new Max()));
  				fts.add(new OperatorFeature(c, new InBetween(i,i-jump),new Max()));
  				}
  		}
  		
  		return fts;
    
	}
}
