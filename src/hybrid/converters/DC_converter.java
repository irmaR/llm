package hybrid.converters;

import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.Value;
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.CGCoefficients;
import hybrid.parameters.CLGCoefficients;
import hybrid.parameters.Coefficients;
import hybrid.parameters.FeatureValuePair;
import hybrid.parameters.LinearGaussianCoeff;
import hybrid.structureLearning.DecisionTreeData;
import hybrid.structureLearning.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DC_converter implements Converter, Serializable {

	int leaf_last_index=0;
    boolean targetLogvarBound=false;
    HashMap<Feature,Value> featureSpecificValueMapping;
	
	@Override
	public String convert(Node<DecisionTreeData> n){
		String head=headPredicate(n.get_data().getDependency().getDep().getHead());
		head=head.replace("_next","").replace("_cur","");
		if(n.isLeaf()){
			featureSpecificValueMapping=new HashMap<Feature, Value>();
			System.out.println("******** NEW LEAF **********");
			System.out.println(n.get_data().getValue_mapping());
			this.leaf_last_index=1;
			//System.out.println("----------------------------------------");
			//System.out.println("LEAF "+ n.get_data().getDependency().getDep().getFeatures()+" ---------------------- \n");
			if(n.get_data().get_coefficients()!=null){
				ConverterPoolDC cp_precond=new ConverterPoolDC();
				String condition_conversion=convert_conditions(n.get_data().getDependency().getDep(),n.get_data().getValue_mapping(),cp_precond);
				//System.out.println("CONDITION: "+condition_conversion);
				cp_precond.setPrecondition_to_cpd(condition_conversion);
				//System.out.println(n.get_data().get_coefficients().getClass());
				System.out.println("HEAD CONVERSION: "+head);
				//System.out.println("CPD conversion: "+n.get_data().get_coefficients());
				String cpd_conversion="";
				if(n.get_data().get_coefficients() instanceof CLGCoefficients){
					n.get_data().get_coefficients();
					System.exit(1);
					//Feature f=n.get_data().getThis_feature();
					//Value v=n.get_data().getValue_mapping().get(f);
					//System.out.println(n.getParent().get_data().getDependency().getParametersPerBranch().get(v));
					//LinearGaussianCoeff coeffs=(LinearGaussianCoeff) n.getParent().get_data().getDependency().getParametersPerBranch().get(v).getCoefficients();
					//cpd_conversion=coeffs.convert(cp_precond);
				}
					/*List<FeatureValuePair> fvs=new ArrayList<FeatureValuePair>();
					for(Feature f:n.get_data().getDependency().getDep().getDiscreteFeatures()){
						fvs.add(new FeatureValuePair(f, n.get_data().getValue_mapping().get(f)));
					}

					AssignmentKey key=new AssignmentKey(fvs);
					System.out.println(coeffs);
					System.out.println(key);
					cpd_conversion=coeffs.convert(cp_precond);*/

				//}
				//else{
					cpd_conversion=n.get_data().get_coefficients().convert(cp_precond);
				//}
				//System.out.println("CPD CONVERS: "+cpd_conversion);
				//System.out.println(n.get_data().getValue_mapping());
				//System.out.println("CPD conversion: "+cpd_conversion);
				System.out.println(" ****** FINISHED PROCESSING LEAF ************");
				return head+cpd_conversion+"\n";
			}
			return "";

		}

		else{
			return "";
		}

	}

	public String convert_conditions(Dependency dep,HashMap<Feature, Value> value_mapping, ConvertPoolInterface cp) {
		String tmp="";
		int counter=0;
		if(dep.getHead().getPredicate().getPredicateName().equals("pos_x_next")){
			tmp+="pos_x(OBJ):t ~= _, \n";
		}
		if(dep.getHead().getPredicate().getPredicateName().equals("pos_y_next")){
			tmp+="pos_y(OBJ):t ~= _, \n";
		}

		for(Feature f:dep.getFeatures()){
			System.out.println("Converting FT: "+f);
			counter++;
			if((counter)==dep.getFeatures().size()){
				cp.setLastFeature();
			}
			
			if(value_mapping.containsKey(f)){
				cp.setValueForFeature(value_mapping.get(f));
				if(f.getConjunction()!=null){
					for(Literal l:(List<Literal>)f.getConjunction().getLiteralList()){
						for(Logvar log:l.getAtom().getArguments()){
							for(Logvar log1:dep.getHead().getArguments()){
								if(log.equals(log1)){
									this.targetLogvarBound=true;
								}
							}
						}
					}
				}
				f.setConditionalValue(value_mapping.get(f));
				this.leaf_last_index=counter;
				String featureString=f.convert(cp);
				//System.out.println("FEATURE CONVERSION DONE: ---> "+featureString);
				cp.setValueForFeature(null);
				tmp+=featureString;
			}
		}
		//System.out.println("Cond: "+tmp);
        //System.out.println("TARGET LOGVAR BOUND? "+this.targetLogvarBound);
		return tmp;
	}

	public String headPredicate(Atom head){
		String postfix="";
		if(head.getPredicate().getPredicateName().contains("_next")){
			postfix=":t+1";
		}
		if(head.getPredicate().getPredicateName().contains("cur")){
			postfix=":t";
		}
		return head.toString()+postfix;

	}







}
