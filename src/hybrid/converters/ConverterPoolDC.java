package hybrid.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hybrid.comparators.InBetween;
import hybrid.featureGenerator.Renaming;
import hybrid.features.Average;
import hybrid.features.ComparisonFeature;
import hybrid.features.ComparisonFeatureContinuousOutput;
import hybrid.features.DiscretizedProportion;
import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.Mode;
import hybrid.features.OperatorFeature;
import hybrid.features.Proportion;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.TestRandvarValue;
import hybrid.network.Type;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.operators.Operator;
import hybrid.parameters.CGCoefficients;
import hybrid.parameters.ExtraInfoDavide;
import hybrid.parameters.GaussianCoefficients;
import hybrid.parameters.LinearGaussianCoeff;
import hybrid.parameters.LogisticCoefficients;
import hybrid.parameters.PMFCoefficients;

public class ConverterPoolDC implements ConvertPoolInterface {


	String precondition_to_cpd;
	boolean actionAlreadyAdded=false;
	boolean fingerActionAlreadyAdded=false;
	HashMap<Literal,String> notation_literal=new HashMap<Literal,String>();
	int feature_notation_counter=1;
	int listNotationCounter=1;
	List<Logvar> free_logvars=new ArrayList<Logvar>();
	List<Logvar>  head_logvars;
	int free_var_counter=1;
	HashMap<Type,List<Logvar>> all_logvars=new HashMap<Type,List<Logvar>>();
	String difference_conditions=new String();
	boolean last_feature=false;
	HashMap<Feature,String> feature_notation=new HashMap<Feature,String>();
	boolean addCondition=true;
	Value valueForFeature=null;
	HashSet<Logvar> renamings=new HashSet<Logvar>();

	public void setPrecondition_to_cpd(String precondition_to_cpd) {
		this.precondition_to_cpd = precondition_to_cpd;
	}

	public int getFeature_notation_counter() {
		return feature_notation_counter;
	}
	public boolean isAddCondition() {
		return addCondition;
	}

	public void setFeature_notation_counter(int feature_notation_counter) {
		this.feature_notation_counter = feature_notation_counter;
	}

	@Override
	public String convert(LogisticCoefficients coeff) {
		System.out.println("CONVERTING LR COEFFICIENTS: ");
		String tmp="";
		if(((ExtraInfoDavide)coeff.getExtraInfo()).getSeenLabels().size()==1){
			tmp="~ finite([0.5:true,0.5:false]) := ";
			tmp+=this.precondition_to_cpd;
			return tmp;
		}
		tmp=" ~ finite(D) :=";
		tmp+=this.precondition_to_cpd;
		if(tmp.trim().endsWith("."))
		{
			tmp = tmp.substring(0,tmp.trim().length())+",";
		}
		List<String> modelValues=new ArrayList<String>();
		for(Value v:coeff.getRegressors().keySet()){
			System.out.println(((ExtraInfoDavide)coeff.getExtraInfo()).getModelLoaded());
			if(!((ExtraInfoDavide)coeff.getExtraInfo()).getModelLoaded().contains(v)){
				continue;
			}
			System.out.println("Getting coeffs for value: "+v);
			for(Feature f:coeff.getRegressors().get(v).getWeights().keySet()){
				tmp+="\n"+f.convert(this);
			}
			modelValues.add("NET"+v);
			tmp+="NET"+v+" is "+coeff.getRegressors().get(v).getIntercept()+" + ";

			int counter=1;
			for(Feature f:coeff.getRegressors().get(v).getWeights().keySet()){
				if(counter==coeff.getRegressors().get(v).getWeights().keySet().size()){
					tmp+=this.feature_notation.get(f)+"*"+coeff.getRegressors().get(v).getWeights().get(f)+",";
				}
				else{
					tmp+=this.feature_notation.get(f)+"*"+coeff.getRegressors().get(v).getWeights().get(f)+"+";
				}
			}
			if(tmp.endsWith("+")){
				tmp = tmp.substring(0,tmp.trim().length())+",";
			}

		}
		if(((ExtraInfoDavide)coeff.getExtraInfo()).getSeenLabels().size()==2){
			tmp+="\nlogisticw(NET";
			for(Value v:((ExtraInfoDavide)coeff.getExtraInfo()).getModelLoaded()){
				tmp+=v+","+v+",";
			}
			for(Value v:((ExtraInfoDavide)coeff.getExtraInfo()).getSeenLabels()){
				if(((ExtraInfoDavide)coeff.getExtraInfo()).getModelLoaded().contains(v)){
					continue;
				}
				tmp+=v+",";
			}
			String array="[";
			int nrUnseen=((ExtraInfoDavide)coeff.getExtraInfo()).getUnseenLabels().size();
			int c=0;
			for(Value v:((ExtraInfoDavide)coeff.getExtraInfo()).getUnseenLabels()){
				if(c!=nrUnseen){
					array+=v+",";
				}
				else{
					array+=v;
				}
			}
			array+="]";
			tmp+=array+","+((ExtraInfoDavide)coeff.getExtraInfo()).getW()+","+((ExtraInfoDavide)coeff.getExtraInfo()).getProbabilityUnseen()+",D).";
		}
		if(((ExtraInfoDavide)coeff.getExtraInfo()).getSeenLabels().size()>2){
			tmp+="\nsoftmaxw(NET";
			String arrayOfValues="[";
			for(String v:modelValues){
				arrayOfValues+=v+",";
			}
			arrayOfValues = arrayOfValues.substring(0,arrayOfValues.trim().length())+",";
			arrayOfValues+="]";
			tmp+=arrayOfValues+",";

			for(Value v:((ExtraInfoDavide)coeff.getExtraInfo()).getModelLoaded()){
				tmp+=v+",";
			}
			for(Value v:((ExtraInfoDavide)coeff.getExtraInfo()).getSeenLabels()){
				if(((ExtraInfoDavide)coeff.getExtraInfo()).getModelLoaded().contains(v)){
					continue;
				}
				tmp+=v+",";
			}
			String array="[";
			int nrUnseen=((ExtraInfoDavide)coeff.getExtraInfo()).getUnseenLabels().size();
			int c=0;
			for(Value v:((ExtraInfoDavide)coeff.getExtraInfo()).getUnseenLabels()){
				if(c!=nrUnseen){
					array+=v+",";
				}
				else{
					array+=v;
				}
			}
			array+="]";
			tmp+=array+","+((ExtraInfoDavide)coeff.getExtraInfo()).getW()+","+((ExtraInfoDavide)coeff.getExtraInfo()).getProbabilityUnseen()+",D).";
		}
		return tmp;
	}


	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convert(hybrid.parameters.LinearGaussianCoeff)
	 */
	@Override
	public String convert(LinearGaussianCoeff coeff){
		System.out.println("Converting Linear Gaussian");
		String tmp=" ~ gaussian(M,V) := \n";
		tmp+=this.precondition_to_cpd;
		for(Feature f:coeff.getReg_coeff().getWeights().keySet()){
			tmp+=f.convert(this);
		}
		tmp+="M is "+coeff.getReg_coeff().getIntercept()+" + ";

		int counter=1;
		for(Feature f:coeff.getReg_coeff().getWeights().keySet()){
			if(counter==coeff.getReg_coeff().getWeights().keySet().size()){
				tmp+=this.feature_notation.get(f)+"* "+coeff.getReg_coeff().getWeights().get(f)+",";
			}
			else{
				tmp+=this.feature_notation.get(f)+"* "+coeff.getReg_coeff().getWeights().get(f)+"+";
			}
		}
		if(tmp.endsWith("+")){
			tmp = tmp.substring(0,tmp.trim().length())+",";
		}
		tmp+="\nV is "+Math.pow(coeff.getStd(),2)+".";
		return tmp;
	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convert(hybrid.parameters.CGCoefficients)
	 */
	@Override
	public String convert(CGCoefficients coeff){
		System.out.println("Converting CG");
		String tmp=" ~ gaussian(M,V) := \n";

		tmp+=this.precondition_to_cpd;
		/*if(tmp.trim().endsWith("."))
		{
			tmp = tmp.substring(0,tmp.trim().length() - 1)+",\n";
		}*/
		tmp+="M is "+"BLAH"+",\n"+"V is "+"BLAH";
		return tmp;
	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convert(hybrid.parameters.GaussianParameters)
	 */
	@Override
	public String convert(GaussianCoefficients coeff){
		String tmp=" ~ gaussian(M,V) := \n";
		tmp+=this.precondition_to_cpd;
		if(tmp.trim().endsWith("."))
		{
			tmp = tmp.substring(0,tmp.trim().length() - 1)+",\n";
		}
		tmp+="M is "+coeff.getMean()+",\n"+"V is "+Math.pow(coeff.getSigma(),2)+".";
		return tmp;
	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convert(hybrid.parameters.PMFCoefficients)
	 */
	@Override
	public String convert(PMFCoefficients pmfCoefficients) {
		return "converting lg coefficients";

	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convertFeature(hybrid.features.Comparison_Feature)
	 */
	@Override
	public String convertFeature(ComparisonFeature comparison_Feature) {
		System.out.println("Converting comparison feature");
		this.free_logvars=new ArrayList<Logvar>();
		this.head_logvars=comparison_Feature.getConjunction().getHead().getArguments();
		String tmp="";
		Literal l1=comparison_Feature.getConjunction().getNon_boolean_literal();

		//extract free variables
		List<Logvar> l1_free=extractFreeVariables(l1);

		Atom l1_atom=l1.getAtom();
		for(Logvar l:l1_free){
			HashMap<Type,Logvar[]> renaming=new HashMap<Type,Logvar[]>();
			Logvar new_logvar=new Logvar(l.getSymbol()+""+(this.free_var_counter++),l.getType());
			renaming.put(l.getType(),new Logvar[]{new_logvar});
			Renaming ren=new Renaming(renaming);
			l1_atom=l1.getAtom().applyRenaming(ren).get(0);
		}
		boolean lt1Action=false;
		boolean lt1Finger=false;
		String lt1Coordinate="";
		Pattern pattern = Pattern.compile("_(.*?)_");
		if(l1_atom.getPredicate().getPredicateName().contains("arm_finger_next")){
			lt1Finger=true;
		}
		if(l1_atom.getPredicate().getPredicateName().contains("arm_x_next") || l1_atom.getPredicate().getPredicateName().contains("arm_y_next") || l1_atom.getPredicate().getPredicateName().contains("arm_z_next")){
			lt1Action=true;
			Matcher matcher = pattern.matcher(l1_atom.getPredicate().getPredicateName());
			if (matcher.find())
			{
				lt1Coordinate=matcher.group(0).replace("_","").toUpperCase().trim();
			}


		}
		difference_conditions="";
		for(Logvar l:l1_atom.getArguments()){
			for(Logvar log1:this.head_logvars){
				System.out.println(l+"vs "+log1);
				if(l.getType().equals(log1.getType())){
					if(!l.equals(log1)){
					if(!difference_conditions.contains(l+"\\="+log1)){
						difference_conditions+=l+"\\="+log1+"";
					}
					}
				}
			}
		}		
		String lt1="";
		String lt2="";
		String literal1=null;
		if(lt1Action){
			lt1=lt1Coordinate;
			literal1="action(moveto(X,Y,Z,RR,PP,YY))";
		}
		else if(lt1Finger){
			lt1="Finger";
			literal1="action(moveto(X,Y,Z,RR,PP,YY,Finger))";
		}
		else{
			lt1="F"+(this.feature_notation_counter++);
			literal1=headPredicate(l1_atom)+" ~= "+ lt1;
		}
		String findall="findall_forward(A,("+literal1+","+"A is "+lt1+"),List"+this.listNotationCounter+"),";
		String valueOfFeature=null;
		String aggregate_cond=comparison_Feature.getNon_deterministic_feature().toString().toLowerCase().trim()+"_list"+"(List"+(this.listNotationCounter++)+","+valueOfFeature+")";


		 if(l1_atom.getPredicate().getPredicateName().equals("action") || l1_atom.getPredicate().getPredicateName().equals("displacement") || l1_atom.getPredicate().getPredicateName().equals("disp") ){
			tmp+=this.convertActionDispDisplacementAtom(l1_atom)+":t,\n";
			if(l1_atom.getPredicate().getPredicateName().equals("action")){
				valueOfFeature="A"+this.free_var_counter;
			}
			if(l1_atom.getPredicate().getPredicateName().equals("disp")){
				valueOfFeature="Th"+this.free_var_counter;
			}
			if(l1_atom.getPredicate().getPredicateName().equals("displacement")){
				valueOfFeature="D"+this.free_var_counter;
			}
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!! VALUE: "+valueOfFeature);
		}
		 else if(l1_atom.getPredicate().getPredicateName().equals("arm_finger_cur") || lt1Finger || lt1Action  || comparison_Feature.isDeterministic()){
			System.out.println("DETERMINISTIC!");
			aggregate_cond="";
			findall="";
			if(lt1Finger && !fingerActionAlreadyAdded){
				tmp+=literal1+",\n";
				valueOfFeature=lt1;
				fingerActionAlreadyAdded=true;
				actionAlreadyAdded=true;
			}
			else if(lt1Action && !actionAlreadyAdded){
				valueOfFeature=lt1;
				tmp+=literal1+",\n";
				actionAlreadyAdded=true;
			}
			else{
				tmp+=literal1+",\n";
				valueOfFeature=lt1;
			}

		}

		else{
			tmp+=findall+"\n";
			//tmp+=aggregate_cond+",\n";
			valueOfFeature="F"+this.feature_notation_counter++;
		}



		System.out.println("TMP so far: "+tmp);
		System.out.println("FINDALL: "+findall);
		System.out.println("Literal1: "+literal1);
		System.out.println("Difference conditions: "+difference_conditions);
		System.out.println("Aggr codn: "+aggregate_cond);

		Value val=comparison_Feature.getConditionalValue();
		System.out.println("GETTING VALUE OF COMPARISON FEATURE: "+comparison_Feature+" = "+val);
		if(val==null){
			return tmp;
		}
		if(val.equals(new BoolValue(true))){
			if(comparison_Feature.getComparator() instanceof InBetween){
				InBetween comp=((InBetween)comparison_Feature.getComparator());
				tmp+=comp.getThreshold1()+" < "+valueOfFeature+" < "+comp.getThreshold2()+",\n";
			}
			else{
				tmp+=valueOfFeature+comparison_Feature.getComparator()+",\n";
			}
		}
		else if(val.equals(new BoolValue(false))){
			if(comparison_Feature.getComparator() instanceof InBetween){
				InBetween comp=((InBetween)comparison_Feature.getComparator());
				tmp+="\\+ ("+comp.getThreshold1()+" < "+valueOfFeature+" < "+comp.getThreshold2()+"),\n ";
			}
			else{
				tmp+="\\+ ("+valueOfFeature+comparison_Feature.getComparator()+"),\n";
			}
		}
	
		return tmp;
	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convertFeature(hybrid.features.Average)
	 */
	@Override
	public String convertFeature(Average average) {
		this.free_logvars=new ArrayList<Logvar>();
		if(this.head_logvars==null){
			this.head_logvars=average.getConjunction().getHead().getArguments();
			for(Logvar l:this.head_logvars){
				this.all_logvars.put(l.getType(),new ArrayList<Logvar>());
				this.all_logvars.get(l.getType()).add(l);
			}
		}
		String tmp="";
		Literal l1=average.getConjunction().getNon_boolean_literal();
		//extract free variables
		List<Logvar> l1_free=extractFreeVariables(l1);

		Atom l1_atom=l1.getAtom();

		for(Logvar l:l1_free){
			HashMap<Type,Logvar[]> renaming=new HashMap<Type,Logvar[]>();
			Logvar new_logvar=new Logvar(l.getSymbol()+""+(this.free_var_counter++),l.getType());
			if(!this.all_logvars.containsKey(new_logvar.getType())){
				this.all_logvars.put(new_logvar.getType(),new ArrayList<Logvar>());
			}
			else{
				this.all_logvars.get(new_logvar.getType()).add(new_logvar);
			}
			renaming.put(l.getType(),new Logvar[]{new_logvar});
			Renaming ren=new Renaming(renaming);
			l1_atom=l1.getAtom().applyRenaming(ren).get(0);
		}

		//make difference conditions
		/*for(Type t:this.all_logvars.keySet()){
			for(Logvar l:this.all_logvars.get(t)){
				for(Logvar l5:this.all_logvars.get(t)){
					if(!l.equals(l5)){
						String diff=l+"\\="+l5+",\n";
						if(!difference_conditions.contains(diff)){
							difference_conditions+=diff;
						}
					}
				}
			}
		}*/
		difference_conditions="";
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!  AVERAGE  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("DIFFERENCE CONDITIONS BEFORE!!!! "+difference_conditions);

		for(Logvar l:l1_atom.getArguments()){
			for(Logvar log1:this.head_logvars){
				System.out.println(l+"vs "+log1);
				if(l.getType().equals(log1.getType())){
					if(!l.equals(log1)){
					if(!difference_conditions.contains(l+"\\="+log1)){
						difference_conditions+=l+"\\="+log1+"";
					}
					}
				}
			}
		}		
		System.out.println("DIFFERENCE CONDITIONS!!!! "+difference_conditions);

		String lt1="F"+(this.feature_notation_counter++);
		String lt2="F"+(this.feature_notation_counter++);

		Value val=average.getConditionalValue();
		String ft_var="F"+(this.feature_notation_counter++);
		feature_notation.put(average, ft_var);

		System.out.println(l1_atom);
		System.out.println(headPredicate(l1_atom));
		String findall="findall_forward(A,("+headPredicate(l1_atom)+","+difference_conditions+"),List"+(this.listNotationCounter)+"),\n";
		String aggregate_cond="avglist"+"(List"+(this.listNotationCounter++)+","+ft_var+"), ";

		/*if(val!=null){
			tmp+=headPredicate(l1_atom)+" ~= "+ val+",\n";	
		}
		else{
			tmp+=headPredicate(l1_atom)+" ~= "+ ft_var+",\n";	
		}
		String end_string=tmp+difference_conditions.replace(".",",");
		if(this.last_feature){
			if(end_string.trim().endsWith(","))
			{
				end_string = end_string.substring(0,end_string.trim().length() - 1)+".";
			}
		}*/
		return findall+aggregate_cond+"\n";

	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convertFeature(hybrid.features.DiscretizedProportion)
	 */
	@Override
	public String convertFeature(DiscretizedProportion discretizedProportion) {
		return this.getStandardFeatureForm(discretizedProportion,"discrprop");
	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convertFeature(hybrid.features.Exist)
	 */
	@Override
	public String convertFeature(Exist exist) {
		System.out.println("Exist: "+exist);
		return this.getStandardFeatureForm(exist,"exist");
	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convertFeature(hybrid.features.Max)
	 */
	@Override
	public String convertFeature(Max max) {
		this.free_logvars=new ArrayList<Logvar>();
		if(this.head_logvars==null){
			this.head_logvars=max.getConjunction().getHead().getArguments();
			for(Logvar l:this.head_logvars){
				this.all_logvars.put(l.getType(),new ArrayList<Logvar>());
				this.all_logvars.get(l.getType()).add(l);
			}
		}
		String tmp="";
		Literal l1=max.getConjunction().getNon_boolean_literal();
		//extract free variables
		List<Logvar> l1_free=extractFreeVariables(l1);

		Atom l1_atom=l1.getAtom();

		for(Logvar l:l1_free){
			HashMap<Type,Logvar[]> renaming=new HashMap<Type,Logvar[]>();
			Logvar new_logvar=new Logvar(l.getSymbol()+""+(this.free_var_counter++),l.getType());
			if(!this.all_logvars.containsKey(new_logvar.getType())){
				this.all_logvars.put(new_logvar.getType(),new ArrayList<Logvar>());
			}
			else{
				this.all_logvars.get(new_logvar.getType()).add(new_logvar);
			}
			renaming.put(l.getType(),new Logvar[]{new_logvar});
			Renaming ren=new Renaming(renaming);
			l1_atom=l1.getAtom().applyRenaming(ren).get(0);
		}
		difference_conditions="";

		for(Logvar l:l1_atom.getArguments()){
			for(Logvar log1:this.head_logvars){
				if(l.getType().equals(log1.getType())){
					if(!l.equals(log1)){
					difference_conditions+=l+"\\="+log1+"";
					}
				}
			}
		}
		String lt1="F"+(this.feature_notation_counter++);
		String lt2="F"+(this.feature_notation_counter++);
		System.out.println("******************** MAX ***********************");
		Value val=max.getConditionalValue();
		String ft_var="F"+(this.feature_notation_counter++);
		feature_notation.put(max, ft_var);
		System.out.println(l1_atom);
		System.out.println(headPredicate(l1_atom));
		String findall="findall_forward(A,("+headPredicate(l1_atom)+","+difference_conditions+"),List"+(this.listNotationCounter)+"),\n";
		String aggregate_cond="maxlist"+"(List"+(this.listNotationCounter++)+","+ft_var+"), ";
		return findall+aggregate_cond+"\n";

	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convertFeature(hybrid.features.Min)
	 */
	@Override
	public String convertFeature(Min min) {
		this.free_logvars=new ArrayList<Logvar>();
		if(this.head_logvars==null){
			this.head_logvars=min.getConjunction().getHead().getArguments();
			for(Logvar l:this.head_logvars){
				this.all_logvars.put(l.getType(),new ArrayList<Logvar>());
				this.all_logvars.get(l.getType()).add(l);
			}
		}
		String tmp="";
		Literal l1=min.getConjunction().getNon_boolean_literal();
		//extract free variables
		List<Logvar> l1_free=extractFreeVariables(l1);

		Atom l1_atom=l1.getAtom();

		for(Logvar l:l1_free){
			HashMap<Type,Logvar[]> renaming=new HashMap<Type,Logvar[]>();
			Logvar new_logvar=new Logvar(l.getSymbol()+""+(this.free_var_counter++),l.getType());
			if(!this.all_logvars.containsKey(new_logvar.getType())){
				this.all_logvars.put(new_logvar.getType(),new ArrayList<Logvar>());
			}
			else{
				this.all_logvars.get(new_logvar.getType()).add(new_logvar);
			}
			renaming.put(l.getType(),new Logvar[]{new_logvar});
			Renaming ren=new Renaming(renaming);
			l1_atom=l1.getAtom().applyRenaming(ren).get(0);
		}
		difference_conditions="";
		String lt1="F"+(this.feature_notation_counter++);
		String lt2="F"+(this.feature_notation_counter++);
		for(Logvar l:l1_atom.getArguments()){
			for(Logvar log1:this.head_logvars){
				if(l.getType().equals(log1.getType())){
					if(!l.equals(log1)){
					difference_conditions+=l+"\\="+log1+"";
					}
				}
			}
		}
		Value val=min.getConditionalValue();
		String ft_var="F"+(this.feature_notation_counter++);
		feature_notation.put(min, ft_var);

		String findall="findall_forward(A,("+headPredicate(l1_atom)+","+difference_conditions+"),List"+(this.listNotationCounter)+"),\n";
		String aggregate_cond="minlist"+"(List"+(this.listNotationCounter++)+","+ft_var+"), ";
		System.out.println("----------------- MIN ----------------");
		System.out.println(findall+aggregate_cond+"\n");
		return findall+aggregate_cond+"\n";
	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convertFeature(hybrid.features.Mode)
	 */
	@Override
	public String convertFeature(Mode mode) {
		this.free_logvars=new ArrayList<Logvar>();
		if(this.head_logvars==null){
			this.head_logvars=mode.getConjunction().getHead().getArguments();
			for(Logvar l:this.head_logvars){
				this.all_logvars.put(l.getType(),new ArrayList<Logvar>());
				this.all_logvars.get(l.getType()).add(l);
			}
		}
		String tmp="";
		Literal l1=mode.getConjunction().getNon_boolean_literal();
		//extract free variables
		List<Logvar> l1_free=extractFreeVariables(l1);

		Atom l1_atom=l1.getAtom();

		for(Logvar l:l1_free){
			HashMap<Type,Logvar[]> renaming=new HashMap<Type,Logvar[]>();
			Logvar new_logvar=new Logvar(l.getSymbol()+""+(this.free_var_counter++),l.getType());
			if(!this.all_logvars.containsKey(new_logvar.getType())){
				this.all_logvars.put(new_logvar.getType(),new ArrayList<Logvar>());
			}
			else{
				this.all_logvars.get(new_logvar.getType()).add(new_logvar);
			}
			renaming.put(l.getType(),new Logvar[]{new_logvar});
			Renaming ren=new Renaming(renaming);
			l1_atom=l1.getAtom().applyRenaming(ren).get(0);
		}
		this.difference_conditions="";
		for(Logvar l:l1_atom.getArguments()){
			for(Logvar log1:this.head_logvars){
				System.out.println(l+"vs "+log1);
				if(l.getType().equals(log1.getType())){
					if(!difference_conditions.contains(l+"\\="+log1)){
						difference_conditions+=l+"\\="+log1+"";
					}
				}
			}
		}		
		String lt1="F"+(this.feature_notation_counter++);
		String lt2="F"+(this.feature_notation_counter++);
        
		Value val=mode.getConditionalValue();
		String ft_var="F"+(this.feature_notation_counter++);
		feature_notation.put(mode, ft_var);
		System.out.println(l1_atom);
		System.out.println(headPredicate(l1_atom));
		String findall="findall_forward(A,"+headPredicate(l1_atom)+",List"+(this.listNotationCounter)+"),\n";
		String aggregate_cond="mode_list"+"(List"+(this.listNotationCounter++)+","+ft_var+"), ";
		String ftVarSpecialAtoms="";
		if(l1_atom.getPredicate().getPredicateName().equals("action")){
			System.out.println("!!!!!!!! MODE !!!!!!!!!!!!!!!!");
			if(this.valueForFeature!=null){ //screw up
				TestRandvarValue t=new TestRandvarValue(l1_atom.getPredicate(),l1_atom.getArguments(),this.valueForFeature);
				ftVarSpecialAtoms=convertActionDispDisplacementAtom(t)+":t";
				System.out.println(ftVarSpecialAtoms);
				if(!this.difference_conditions.isEmpty()){
					return ftVarSpecialAtoms+",\n"+this.difference_conditions+",\n";
				}
				return ftVarSpecialAtoms+",\n";

			}
		}

		return findall+aggregate_cond+"\n";
	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convert(hybrid.features.Operator_Feature)
	 */
	@Override
	public String convert(OperatorFeature operator_Feature) {
		this.difference_conditions="";
		Value val=operator_Feature.getConditionalValue();
		
		System.out.println("Converting operator feature ");
		System.out.println("VALUE FOR THIS FEATURE: "+this.valueForFeature);
		this.free_logvars=new ArrayList<Logvar>();
		this.head_logvars=operator_Feature.getConjunction().getHead().getArguments();
		String tmp="";
		//tmp+="-- VALUE: -- "+val+ " "+operator_Feature+"\n";
		Literal l1=operator_Feature.getConjunction().getFirstLiteral();
		Literal l2=operator_Feature.getConjunction().getSecondLiteral();

		//extract free variables
		List<Logvar> l1_free=extractFreeVariables(l1);
		List<Logvar> l2_free=extractFreeVariables(l2);

		Atom l1_atom=l1.getAtom();
		Atom l2_atom=l2.getAtom();

		for(Logvar l:l1_free){
			HashMap<Type,Logvar[]> renaming=new HashMap<Type,Logvar[]>();
			Logvar new_logvar=new Logvar(l.getSymbol()+""+(this.free_var_counter++),l.getType());
			renaming.put(l.getType(),new Logvar[]{new_logvar});
			Renaming ren=new Renaming(renaming);
			l1_atom=l1.getAtom().applyRenaming(ren).get(0);
		}
		Renaming ren2=null;
		for(Logvar l:l2_free){
			HashMap<Type,Logvar[]> renaming=new HashMap<Type,Logvar[]>();
			Logvar new_logvar=new Logvar(l.getSymbol()+""+(this.free_var_counter++),l.getType());
			renaming.put(l.getType(),new Logvar[]{new_logvar});
			Renaming ren=new Renaming(renaming);
			l2_atom=l2.getAtom().applyRenaming(ren).get(0);
			ren2=ren;
		}

		String selector="";
		if(operator_Feature.getSelector()!=null){
			for(Literal l:operator_Feature.getSelector()){
				if(l.getAtom() instanceof TestRandvarValue){
					selector+=convertActionDispDisplacementAtom(l.getAtom().applyRenaming(ren2).get(0))+":t,";
				}
			}
		}

		boolean lt1Action=false;
		boolean lt1Finger=false;
		String lt1Coordinate="";
		boolean lt2Action=false;
		boolean lt2finger=false;
		String lt2Coordinate="";
		Pattern pattern = Pattern.compile("_(.*?)_");
		if(l1_atom.getPredicate().getPredicateName().contains("arm_finger_next")){
			lt1Finger=true;
		}
		if(l2_atom.getPredicate().getPredicateName().contains("arm_finger_next")){
			lt2finger=true;
		}

		if(l1_atom.getPredicate().getPredicateName().contains("arm_x_next") || l1_atom.getPredicate().getPredicateName().contains("arm_y_next") || l1_atom.getPredicate().getPredicateName().contains("arm_z_next")){
			lt1Action=true;
			Matcher matcher = pattern.matcher(l1_atom.getPredicate().getPredicateName());
			if (matcher.find())
			{
				lt1Coordinate=matcher.group(0).replace("_","").toUpperCase().trim();
			}


		}
		if(l2_atom.getPredicate().getPredicateName().contains("arm_x_next") || l2_atom.getPredicate().getPredicateName().contains("arm_y_next") || l2_atom.getPredicate().getPredicateName().contains("arm_z_next")){
			lt2Action=true;
			Matcher matcher = pattern.matcher(l2_atom.getPredicate().getPredicateName());
			if (matcher.find())
			{
				lt2Coordinate=matcher.group(0).replace("_","").toUpperCase().trim();
			}
		}
		String lt1="";
		String lt2="";
		String literal1=null;
		String literal2=null;
		if(lt1Action){
			lt1=lt1Coordinate;
			literal1="action(moveto(X,Y,Z,RR,PP,YY,Finger))";
		}
		else if(lt1Finger){
			lt1="Finger";
			literal1="action(moveto(X,Y,Z,RR,PP,YY,Finger))";
		}
		else{
			lt1="F"+(this.feature_notation_counter++);
			literal1=headPredicate(l1_atom)+" ~= "+ lt1;
		}

		if(lt2Action){
			lt2=lt2Coordinate;
			literal2="action(moveto(X,Y,Z,RR,PP,YY,Finger))";
		}
		else if(lt2finger){
			lt2="Finger";
			literal2="action(moveto(X,Y,Z,RR,PP,YY,Finger))";
		}
		else{
			lt2="F"+(this.feature_notation_counter++);
			literal2=headPredicate(l2_atom)+" ~= "+lt2;
		}
		
		System.out.println("Literal 1 action? "+lt1Action+" Literal 2 action? "+lt2Action);
		Operator op=operator_Feature.getConjunction().get_operator();
		String valueOfFeature="F"+(this.feature_notation_counter++);
		String findall="";
		String aggregate_cond="";
		difference_conditions=l1_atom.getArgument(0)+"\\="+l2_atom.getArgument(0);
		String cond="";
		if(!operator_Feature.isDeterministic()){
			System.out.println("LITERAL: "+literal1);
			System.out.println("ACTION ALREADY ADDED? "+actionAlreadyAdded);
			if(lt2Action || lt1Action || lt1Finger || lt2finger){
				String action=null;
				if(lt1Action | lt1Finger){
					if(!actionAlreadyAdded){
						tmp+=literal1+",\n";
						actionAlreadyAdded=true;
					}
					findall="findall_forward(A,("+literal2+","+selector+"A is "+lt1+op+lt2+"),List"+(this.listNotationCounter)+"),";
				}
				if(lt2Action || lt2finger){
					if(!actionAlreadyAdded){
						tmp+=literal2+",\n";
						System.out.println("Setting action added to true");
						actionAlreadyAdded=true;
					}
					findall="findall_forward(A,("+literal1+","+selector+"A is "+lt1+op+lt2+"),List"+(this.listNotationCounter)+"),";
				}
			}
			else{
				findall="findall_forward(A,("+literal1+","+literal2+","+selector+difference_conditions+","+"A is "+lt1+op+lt2+"),List"+(this.listNotationCounter)+"),";
			}
			System.out.println("Value of feature: "+valueOfFeature);
			aggregate_cond=operator_Feature.getProcessingFeature().toString().toLowerCase().trim()+"list"+"(List"+(this.listNotationCounter++)+","+valueOfFeature+"), ";
			System.out.println("FINDALL: "+findall);
			System.out.println("Literal1: "+literal1+" Literal2: "+literal2);
			System.out.println("Difference conditions: "+difference_conditions);
			
			if(val!=null && val.equals(new BoolValue(false))){
				//tmp+="NEGATION";
				tmp+="\\+ ( ";
			}
			
			tmp+=findall+"\n";
			tmp+=aggregate_cond+"\n";
			//tmp+="TMP:"+tmp;
			//cond=findall+"\n"+aggregate_cond;
			//tmp+="----------------------------\n";
		}
		else{
			if(val!=null && val.equals(new BoolValue(false))){
				//tmp+="NEGATION";
				tmp+="\\+ ( ";
			} 
			if(lt1Action){
				if(!actionAlreadyAdded){
					tmp+=literal1+",\n";
					actionAlreadyAdded=true;
				}
			}
			else{
				tmp+=literal1+",\n";
			}
			if(lt2Action){
				if(!actionAlreadyAdded){
					tmp+=literal2+",\n";
					actionAlreadyAdded=true;
				}
			}
			else{
				tmp+=literal2+",\n";
				tmp+=selector+"\n"+difference_conditions+",\n";
			}
			valueOfFeature="("+lt1+op+lt2+")";
		}

		this.feature_notation.put(operator_Feature, valueOfFeature);
		
		
		
		if(val==null){
			return tmp;
		}
		if(val.equals(new BoolValue(true))){
			if(operator_Feature.getComparator() instanceof InBetween){
				InBetween comp=((InBetween)operator_Feature.getComparator());
				tmp+=comp.getThreshold1()+" < "+valueOfFeature+" < "+comp.getThreshold2()+",\n";
			}
			else{
				tmp+=valueOfFeature+operator_Feature.getComparator()+",\n";
			}
		}
	    

		
		else if(val.equals(new BoolValue(false))){
			if(operator_Feature.getComparator() instanceof InBetween){
				InBetween comp=((InBetween)operator_Feature.getComparator());
				if(this.valueForFeature.isBoolean() && ((BoolValue)this.valueForFeature).equals(new BoolValue(false))){
					tmp+=comp.getThreshold1()+" < "+valueOfFeature+" < "+comp.getThreshold2()+"";
				}
				else{
					tmp+=comp.getThreshold1()+" < "+valueOfFeature+" < "+comp.getThreshold2()+"";
				}
			}
			else{
				if(operator_Feature.isDeterministic() && this.valueForFeature.isBoolean() && ((BoolValue)this.valueForFeature).equals(new BoolValue(false))){
					tmp+=""+valueOfFeature+operator_Feature.getComparator()+"";
				}
				else if(operator_Feature.isDeterministic() && this.valueForFeature.isBoolean() && ((BoolValue)this.valueForFeature).equals(new BoolValue(true))){
					tmp+="  "+valueOfFeature+operator_Feature.getComparator()+"";
				}
				else{
					tmp+=valueOfFeature+operator_Feature.getComparator()+"";
				}
			}
		}

		
		if(val.equals(new BoolValue(false))){
			tmp+="),\n";
		}
		System.out.println("SELECTOR!!! ******************************************");
		System.out.println(operator_Feature.getSelector());
		this.valueForFeature=null;
		return tmp;
	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convertFeature(hybrid.features.Proportion)
	 */
	@Override
	public String convertFeature(Proportion proportion) {
		return "converting proportion feature";
	}

	/* (non-Javadoc)
	 * @see hybrid.converters.ConvertPoolInterface#convertFeature(hybrid.features.ValueFt)
	 */
	@Override
	public String convertFeature(ValueFt valueFt) {
		this.head_logvars=valueFt.getConjunction().getHead().getArguments();
		renamings.addAll(this.head_logvars);
		System.out.println("@@@@@@@@@ Converting ValueFT @@@@@@@@@@@@@@@@@!!!");
		System.out.println(valueFt);

		System.out.println("Feature notation counter: "+this.free_var_counter);
		this.free_logvars=new ArrayList<Logvar>();
		if(this.head_logvars!=null){
			this.head_logvars=valueFt.getConjunction().getHead().getArguments();
			for(Logvar l:this.head_logvars){
				this.all_logvars.put(l.getType(),new ArrayList<Logvar>());
				this.all_logvars.get(l.getType()).add(l);
			}
		}
		String tmp="";
		Literal l1=valueFt.getConjunction().getNon_boolean_literal();
		//extract free variables
		List<Logvar> l1_free=extractFreeVariables(l1);

		Atom l1_atom=l1.getAtom();
		System.out.println("ALL LOGVAR SET BEFORE: "+this.all_logvars);
		for(Logvar l:l1_free){
			HashMap<Type,Logvar[]> renaming=new HashMap<Type,Logvar[]>();
			Logvar new_logvar=new Logvar(l.getSymbol()+""+(this.free_var_counter++),l.getType());
			if(!this.all_logvars.containsKey(new_logvar.getType())){
				this.all_logvars.put(new_logvar.getType(),new ArrayList<Logvar>());
				this.all_logvars.get(new_logvar.getType()).add(new_logvar);
			}
			else{
				this.all_logvars.get(new_logvar.getType()).add(new_logvar);
			}
			renaming.put(l.getType(),new Logvar[]{new_logvar});
			Renaming ren=new Renaming(renaming);
			l1_atom=l1.getAtom().applyRenaming(ren).get(0);
			renamings.add(l1_atom.getArgument(0));
		}
		System.out.println("Renamed Literal: "+l1_atom);
		System.out.println("ALL LOGVAR SET: "+this.all_logvars);
		this.difference_conditions="";
		System.out.println("DIFF CONDITIONS BEFOREHAND: "+this.difference_conditions);

		for(Logvar l:l1_atom.getArguments()){
			System.out.println("LOG: "+l+" HEAD: "+this.head_logvars);
			for(Logvar log1:this.head_logvars){
				if(l.getType().equals(log1.getType())){
					if(!l.equals(log1)){
					difference_conditions+=l+"\\="+log1+",\n";
					}
				}
			}
		}
		
		System.out.println("DIFF CONDS: "+difference_conditions);
		Value val=valueFt.getConditionalValue();
		String ft_var="F"+(this.feature_notation_counter++);
		
		System.out.println("FEATURE VALUE: "+val);
		if(val!=null){
			if(l1_atom.getPredicate().getPredicateName().equals("action") || l1_atom.getPredicate().getPredicateName().equals("displacement") || l1_atom.getPredicate().getPredicateName().equals("disp")){
				if(val instanceof UndefinedValue){
					String ending="";
					if(!difference_conditions.equals("")){
					    ending=difference_conditions;
					}
					if(!ending.equals("")){
					   tmp+="\\+ ("+actionPredicate(l1_atom,val)+","+ending.replace(",","").replace("\n","")+"),\n";
					}
					else{
						 tmp+="\\+ ("+actionPredicate(l1_atom,val)+"),\n"; 
					}
					   return tmp;
				}
				else{
			    	tmp+=actionPredicate(l1_atom,val)+",\n";
				}
			}
			else{
				tmp+=headPredicate(l1_atom)+" ~= "+ val+",\n";
			}
		}
		else{
			System.out.println("Here: "+l1_atom);
			if(l1_atom.getPredicate().getPredicateName().equals("action") || l1_atom.getPredicate().getPredicateName().equals("displacement") || l1_atom.getPredicate().getPredicateName().equals("disp") ){
				tmp+=this.convertActionDispDisplacementAtom(l1_atom)+":t,\n";
				System.out.println("Action or whatever: "+this.convertActionDispDisplacementAtom(l1_atom));
				if(l1_atom.getPredicate().getPredicateName().equals("action")){
					ft_var="A"+this.free_var_counter;
				}
				if(l1_atom.getPredicate().getPredicateName().equals("disp")){
					ft_var="Th"+this.free_var_counter;
				}
				if(l1_atom.getPredicate().getPredicateName().equals("displacement")){
					ft_var="D"+this.free_var_counter;
				}

			}
			else{
				tmp+=headPredicate(l1_atom)+" ~= "+ ft_var+",\n";
			}
		}
		if(!difference_conditions.equals("")){
		    tmp+=difference_conditions+"";
		}
		feature_notation.put(valueFt, ft_var);
		String end_string=tmp;
		System.out.println("Output: "+end_string);
		return end_string;
	}


	public String headPredicate(Atom head){
		String postfix="";
		if(head.getPredicate().getPredicateName().contains("displacement")){
			return head.createFOLTerm().replace(")",",A)"+":t");
		}
		if(head.getPredicate().getPredicateName().contains("action")){
			return head.createFOLTerm().replace(")",",A)"+":t");
		}
		if(head.getPredicate().getPredicateName().contains("disp")){
			return head.createFOLTerm().replace(")",",A)"+":t");
		}
		if(head.getPredicate().getPredicateName().contains("_next")){
			postfix=":t+1";
		}
		if(head.getPredicate().getPredicateName().contains("cur")){
			postfix=":t";
		}
		String headString=head.createFOLTerm().toString();
		headString=headString.replace("_cur","");
		headString=headString.replace("_next","");
		return headString+postfix;

	}

	public String convertActionDispDisplacementAtom(Atom atom){
		String variableName="";
		String prefix="";
		if(atom.getPredicate().getPredicateName().equals("action")){
			variableName="A"+this.free_var_counter;
			prefix="type";
		}
		if(atom.getPredicate().getPredicateName().equals("disp")){
			variableName="Th"+this.free_var_counter;
		}
		if(atom.getPredicate().getPredicateName().equals("displacement")){
			variableName="D"+this.free_var_counter;
		}
		String atomString=null;

		System.out.println("ATOM: "+atom+" "+atom.getClass());
		if(atom instanceof TestRandvarValue){
			System.out.println("TEST RANDVAR: "+atom);
			atomString=prefix+atom.createFOLTerm();
		}
		else{
			atomString=prefix+atom.getPredicate().getPredicateName()+"("+atom.getArguments().get(0)+","+variableName+")";
		}
		return atomString;

	}

	public String actionPredicate(Atom head,Value v){
		TestRandvarValue atom=new TestRandvarValue(head.getPredicate(),head.getArguments(),v);
		String postfix=":t";
		if(head.getPredicate().getPredicateName().equals("action")){
			return "type"+atom.createFOLTerm().replace("NDVal", "_")+postfix;
		}
		else{
			return atom.createFOLTerm().replace("NDVal", "_")+postfix;
		}

	}



	private List<Logvar> extractFreeVariables(Literal l1){
		List<Logvar> tmp=new ArrayList<Logvar>();
		for(Logvar log1:l1.getAtom().getArguments()){
			for(Logvar hlog:this.head_logvars){
				if(!(log1.equals(hlog))){
					tmp.add(log1);
				}
			}
		}
		return tmp;
	}

	@Override
	public void setLastFeature() {
		this.last_feature=true;

	}


	@Override
	public String convertFeature(ComparisonFeatureContinuousOutput comparison_Feature) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getStandardFeatureForm(Feature f,String featureName){
		System.out.println("Standard Feature Form conversion");
		this.free_logvars=new ArrayList<Logvar>();
		if(this.head_logvars==null){
			this.head_logvars=f.getConjunction().getHead().getArguments();
			for(Logvar l:this.head_logvars){
				this.all_logvars.put(l.getType(),new ArrayList<Logvar>());
				this.all_logvars.get(l.getType()).add(l);
			}
		}
		String tmp="";
		Literal l1=f.getConjunction().getNon_boolean_literal();
		//extract free variables
		if(l1==null){
			l1=(Literal) f.getSelector().get(0);
		}
		List<Logvar> l1_free=extractFreeVariables(l1);
		boolean isTestRandvar=l1.getAtom().isRandVarTest();
		Atom l1_atom=l1.getAtom();
		System.out.println("Type of atom: "+l1_atom.getClass());
		System.out.println("BEFORE RENAMING: "+l1_atom);
		for(Logvar l:l1_free){
			HashMap<Type,Logvar[]> renaming=new HashMap<Type,Logvar[]>();
			Logvar new_logvar=new Logvar(l.getSymbol()+""+(this.free_var_counter++),l.getType());
			if(!this.all_logvars.containsKey(new_logvar.getType())){
				this.all_logvars.put(new_logvar.getType(),new ArrayList<Logvar>());
			}
			else{
				this.all_logvars.get(new_logvar.getType()).add(new_logvar);
			}
			renaming.put(l.getType(),new Logvar[]{new_logvar});
			Renaming ren=new Renaming(renaming);
			if(l1_atom instanceof TestRandvarValue){
				l1_atom=l1.getAtom().applyRenaming(ren).get(0);
			}
			else{
				l1_atom=l1.getAtom().applyRenaming(ren).get(0);
			}

		}
		difference_conditions="";
		System.out.println(this.all_logvars);
		for(Logvar l:l1_atom.getArguments()){
			for(Logvar log1:this.head_logvars){
				System.out.println(l+"vs "+log1);
				if(l.getType().equals(log1.getType())){
					if(!l.equals(log1)){
					if(!difference_conditions.contains(l+"\\="+log1)){
						difference_conditions+=l+"\\="+log1+"";
					}
					}
				}
			}
		}		
		String lt1="F"+(this.feature_notation_counter++);
		String lt2="F"+(this.feature_notation_counter++);

		Value val=f.getConditionalValue();
		String ft_var="F"+(this.feature_notation_counter++);
		String ftVarSpecialAtoms=null;

		if(l1_atom.getPredicate().getPredicateName().equals("action") || l1_atom.getPredicate().getPredicateName().equals("disp") ||  l1_atom.getPredicate().getPredicateName().equals("displacement")){
			ftVarSpecialAtoms=convertActionDispDisplacementAtom(l1_atom)+":t";
			ft_var=ftVarSpecialAtoms;
			if(!isTestRandvar){
				if(l1_atom.getPredicate().getPredicateName().equals("action")){
					ft_var="A"+this.free_var_counter;
				}
				if(l1_atom.getPredicate().getPredicateName().equals("disp")){
					ft_var="Th"+this.free_var_counter;
				}
				if(l1_atom.getPredicate().getPredicateName().equals("displacement")){
					ft_var="D"+this.free_var_counter;
				}
			}
		}

		else{
			tmp+=headPredicate(l1_atom)+" ~= "+ ft_var+",\n";
		}
		if(val!=null && !val.isBoolean()){
			if(l1_atom.getPredicate().getPredicateName().equals("action") || l1_atom.getPredicate().getPredicateName().equals("disp") || l1_atom.getPredicate().getPredicateName().equals("displacement")){
				ftVarSpecialAtoms=actionPredicate(l1_atom,val)+",\n";
			}
			else{
				tmp+=headPredicate(l1_atom)+" ~= "+ val+",\n";
			}
		}
		String end_string="";
		String diff="";
		if(difference_conditions!=""){
			diff=difference_conditions+",";
		}
		System.out.println(ftVarSpecialAtoms);
		if(val!=null && val.isBoolean()){
			if(((BoolValue)val).getValue()==true){
				if(featureName.equals("exist")){
					if(l1_atom.getPredicate().getPredicateName().equals("action") || l1_atom.getPredicate().getPredicateName().equals("disp") || l1_atom.getPredicate().getPredicateName().equals("displacement")){
						return end_string+ftVarSpecialAtoms+","+diff+"\n";
					}
					else{
						return end_string+ft_var+",\n"+diff+"\n";
					}
				}
			}else{
				if(featureName.equals("exist")){
					if(diff!=""){
					    return end_string+"\\+ ("+ft_var+","+diff.replace(",","")+"),\n";
					}
					else{
						return end_string+"\\+ ("+ft_var+"),\n";
					}
				}
				else{
					if(diff!=""){
					    return end_string+"\\+ ("+featureName+"("+ft_var+","+diff.replace(",","")+")),\n";
					}
					else{
						return end_string+"\\+ ("+featureName+"("+ft_var+")),\n";
					}
				}
			}
		}
		if(featureName.equals("exist")){
			if(l1_atom.getPredicate().getPredicateName().equals("action") || l1_atom.getPredicate().getPredicateName().equals("disp") || l1_atom.getPredicate().getPredicateName().equals("displacement")){
				return end_string+""+ftVarSpecialAtoms+","+diff+""+"\n";
			}
		}
		if(difference_conditions.equals("")){
			if(!tmp.equals("")){
			end_string=tmp+",\n";
			}
		}
		else{
		   end_string=tmp+diff+",\n";
		}
		feature_notation.put(f, ft_var);
        System.out.println("OUTPUT: "+end_string);
		return end_string;
	}




	@Override
	public void setValueForFeature(Value value) {
		this.valueForFeature=value;

	}





}
