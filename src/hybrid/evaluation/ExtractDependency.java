package hybrid.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Average;
import hybrid.features.DiscretizedProportion;
import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.Mode;
import hybrid.features.Proportion;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.LogvarRestrictionLiteral;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.TestRandvarValue;
import hybrid.network.Type;

import java.util.*;

/**
 * Give a file with learned dependencies, extract the learned dependency
 * for a specific atom
 * @author irma
 *
 */
public class ExtractDependency {

	private int discretization_level;
	
	public ExtractDependency(int discretizationLevel){
		this.discretization_level=discretizationLevel;
	}
	

	/**
	 * Extract the learned dependency from a dependency file for a specific atom 
	 * @param learned_dependency_file
	 * @param head
	 * @param ntw
	 * @return
	 * @throws FileNotFoundException
	 */
	public Dependency extractDependency(File learned_dependency_file,Atom head,NetworkInfo ntw) throws FileNotFoundException{
		FileReader fR=new FileReader(learned_dependency_file);
		String line_with_dependency=null;
		try {
			line_with_dependency=extractDesiredLine(fR);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int number_of_features=extractNumberOfDependencies(line_with_dependency);
		String features=line_with_dependency.split("\\|")[1].trim();
		List<Feature> extracted_features=extractFeatures(head,features,ntw,number_of_features);
		return new Dependency(head,extracted_features.toArray(new Feature[extracted_features.size()]));	
	}

	private List<Feature> extractFeatures(Atom head,String features, NetworkInfo ntw,int number_of_features) {
		List<Feature> resulting_features=new ArrayList<Feature>();
		String[] features_to_be_processed=features.split("\\_\\[");
		for(int i=0;i<number_of_features;i++){
			resulting_features.add(createFeature(head,features_to_be_processed[i],ntw));
		}
		return resulting_features;
	}

	private Feature createFeature(Atom head,String string, NetworkInfo ntw) {
	    string=string.replaceAll("\\[", "");
	    string=string.replaceAll("\\]", "");
	    string=string.replaceAll(" \\}", " ");
	    string=string.replaceAll("\\{", " ");
		String[] feature_fields=string.split(" ");
		long feature_index=Long.valueOf(feature_fields[0].replaceAll("\\[", ""));
		String feature_name=feature_fields[1];
		int atom_index_start=2;
		List<Literal> atoms=new ArrayList<Literal>();
		List<LogvarRestrictionLiteral> restrictions=new ArrayList<LogvarRestrictionLiteral>();
		HashMap<String,Logvar> string_to_logvar=new HashMap<String, Logvar>();
		
		for(int j=atom_index_start;j<feature_fields.length;j++){
			if(feature_fields[j].isEmpty()){
				continue;
			}
			Literal atom=null;
			try{
			   atom=extractAtom(feature_fields[j],ntw);
			   for(Logvar l:atom.getAtom().getArguments()){
					string_to_logvar.put(l.toString(), l);
				}
			}
			catch(StringIndexOutOfBoundsException e){
				try{
				atom=extractAtomProblemDiscreteProb(feature_fields[j],ntw);
				for(Logvar l:atom.getAtom().getArguments()){
					string_to_logvar.put(l.toString(), l);
				}
				}
				catch (StringIndexOutOfBoundsException ex) {
					if(feature_fields[j].contains("\\==")){
					String[] split=feature_fields[j].split("\\==");
					Logvar maps_to=new Logvar(split[1].trim(),string_to_logvar.get(split[0].replace("\\"," ").trim()).getType());
					restrictions.add(new LogvarRestrictionLiteral("\\==",string_to_logvar.get(split[0].replace("\\"," ").trim()),maps_to));
					continue;
					}
				}
			}
			if(atom!=null){
			atoms.add(atom);
			}
		}
		try {
			try {
				return createAppropriateFeature(head,atoms,feature_name,restrictions);
			} catch (ConjunctionConstructionProblem e) {
				e.printStackTrace();
			}
		} catch (FeatureTypeException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	private Literal extractAtomProblemDiscreteProb(String atom,NetworkInfo ntw) {
				String[] split_on_bracket=atom.trim().split("\\{");
				String predicate_name=split_on_bracket[0];
				int argument_index_start=atom.indexOf("(");
				int argument_index_end=atom.indexOf(")");
				System.out.println(argument_index_start);
				System.out.println(argument_index_end);
 
				String arguments=atom.substring(argument_index_start+1, argument_index_end);				
				Atom atom_ntw=ntw.getPredicateNameToAtom().get(predicate_name);
				List<Logvar> logvars=extractArguments(atom_ntw,predicate_name,arguments.split(","),ntw);
				
				if(arguments.split(",").length>atom_ntw.getArguments().size()){
					//it means that this is a randvar test
					TestRandvarValue t= new TestRandvarValue(atom_ntw.getPredicate(),logvars,new StringValue(arguments.split(",")[arguments.split(",").length-1]));
				    return new PosLiteral(t);
				}
				else{
					Atom a= new Atom(atom_ntw.getPredicate(),logvars);
					return new PosLiteral(a);
				}

	}

	private Feature createAppropriateFeature(Atom head,List<Literal> atoms,String feature_name, List<LogvarRestrictionLiteral> restrictions) throws FeatureTypeException, ConjunctionConstructionProblem {
		Feature tmp=null;
		if(feature_name.equals("Value")){
			tmp= new ValueFt(new Standard_Conjunction(head,atoms));
		}
        if(feature_name.equals("Average")){
        	tmp= new Average(new Standard_Conjunction(head,atoms));
		}
        if(feature_name.equals("DiscretePROP")){
        	tmp= new DiscretizedProportion(new Standard_Conjunction(head,atoms),this.discretization_level);
		}
        if(feature_name.equals("Exist")){
        	tmp= new Exist(new Standard_Conjunction(head,atoms));
		}
        if(feature_name.equals("Max")){
        	tmp= new Max(new Standard_Conjunction(head,atoms));

		}
        if(feature_name.equals("Min")){
        	tmp= new Min(new Standard_Conjunction(head,atoms));

		}
        if(feature_name.equals("Mode")){
        	tmp= new Mode(new Standard_Conjunction(head,atoms));

		}
        if(feature_name.equals("PROP")){
        	tmp= new Proportion(new Standard_Conjunction(head,atoms));

        }
        tmp.getConjunction().addRestrictionForLogvar(restrictions);
        return tmp;
			
	}

	private Literal extractAtom(String atom, NetworkInfo ntw) {
		String[] split_on_bracket=atom.trim().split("\\(");
		String predicate_name=split_on_bracket[0];
		int argument_index_start=atom.indexOf("(");
		int argument_index_end=atom.indexOf(")");
		String arguments=atom.substring(argument_index_start+1, argument_index_end);		
		Atom atom_ntw=ntw.getPredicateNameToAtom().get(predicate_name);
		List<Logvar> logvars=extractArguments(atom_ntw,predicate_name,arguments.split(","),ntw);
		
		if(arguments.split(",").length>atom_ntw.getArguments().size()){
			//it means that this is a randvar test
			TestRandvarValue t= new TestRandvarValue(atom_ntw.getPredicate(),logvars,new StringValue(arguments.split(",")[arguments.split(",").length-1]));
		    return new PosLiteral(t);
		}
		else{
			Atom a= new Atom(atom_ntw.getPredicate(),logvars);
			return new PosLiteral(a);
		}

	}

	private List<Logvar> extractArguments(Atom atom,String predicateName,String[] arguments_string, NetworkInfo ntw) {
		List<Logvar> logvars=new ArrayList<Logvar>();
		for(int i=0;i<atom.getArguments().size();i++){
				Logvar logvar=new Logvar(arguments_string[i],atom.getArgument(i).getType());
				logvars.add(logvar);
			}
		return logvars;
	}

	private int extractNumberOfDependencies(String line_with_dependency) {
		String[] strings=line_with_dependency.split("NR:");
		return Character.getNumericValue(strings[1].trim().charAt(0));
	}

	private String extractDesiredLine(FileReader fR) throws IOException {
		BufferedReader reader = new BufferedReader(fR);
		String line=null;
		while((line=reader.readLine())!=null){
			if(line.startsWith(" Learned dep:")){
				return line;
			}
		}
		return null;
	}
	
}
