package hybrid.converters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.network.BoolValue;
import hybrid.network.RangeDiscrete;
import hybrid.network.StringValue;
import hybrid.network.Value;
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.CLGCoefficients;
import hybrid.parameters.ExtraInfo;
import hybrid.parameters.ExtraInfoDavide;
import hybrid.parameters.LinearGaussianCoeff;
import hybrid.parameters.LogisticCoefficients;
import hybrid.parameters.Regression;

public class ParConverterFromCSV implements ConvertCoefficientsInterface {

	private String pathToCSVFile;

	public String getPathToCSVFile() {
		return pathToCSVFile;
	}

	public void setPathToCSVFile(String pathToCSVFile) {
		this.pathToCSVFile = pathToCSVFile;
	}


	@Override
	public void convert(LinearGaussianCoeff coeff,AssignmentKey key,Dependency dep) {
		System.out.println("Parsing lg coefficients from a CSV file");
		File csvFile=new File(this.pathToCSVFile);
		BufferedReader br;
		Double intercept=null;
		HashMap<Feature,Double> coeffs=new HashMap<Feature,Double>();
		Double std=null;
		try {
			br = new BufferedReader(new FileReader(csvFile));
			String line = br.readLine();
			Double result;
			int lineNumber=0;
			Double score=null;
			while (line != null) {
				lineNumber++;
				if(lineNumber==1){ //read regression coefficients
					String[] coeffsstring=line.split(",");
					intercept=Double.valueOf(coeffsstring[1]);
					int counterString=2;
					for(Feature f:dep.getContinuousFeatures()){
						coeffs.put(f, Double.valueOf(coeffsstring[counterString++]));
					}
				}	
				if(lineNumber==2){ //load std
					std=Double.valueOf(line.split(",")[1]);
				}
				if(lineNumber>2){
					break;
				}
				line = br.readLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Regression regrCoeffs=new Regression(intercept,coeffs);
		coeff.setReg_coeff(regrCoeffs);
		coeff.setStd(std);

	}

	@Override
	public void convert(CLGCoefficients clgCoefficients,AssignmentKey key,Dependency dep) {
		System.out.println("Parsing clg coefficients from a CSV file");
		File csvFile=new File(this.pathToCSVFile);
		BufferedReader br;
		Double intercept=null;
		HashMap<Feature,Double> coeffs=new HashMap<Feature,Double>();
		Double std=null;
		try {
			br = new BufferedReader(new FileReader(csvFile));
			String line = br.readLine();
			Double result;
			int lineNumber=0;
			Double score=null;
			while (line != null) {
				lineNumber++;
				if(lineNumber==1){ //read regression coefficients
					String[] coeffsstring=line.split(",");
					intercept=Double.valueOf(coeffsstring[1]);
					int counterString=2;
					for(Feature f:dep.getContinuousFeatures()){
						coeffs.put(f, Double.valueOf(coeffsstring[counterString++]));
					}
				}	
				if(lineNumber==2){ //load std
					std=Double.valueOf(line.split(",")[1]);
				}
				if(lineNumber>2){
					break;
				}
				line = br.readLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//add loaded parameters to CLG
		Regression regrCoeffs=new Regression(intercept,coeffs);
		LinearGaussianCoeff lgcoeff=new LinearGaussianCoeff(regrCoeffs, std);
		clgCoefficients.addParameter(key, lgcoeff);
	}

	@Override
	public void convert(LogisticCoefficients logisticCoefficients,AssignmentKey key, Dependency dep) {
		System.out.println("Parsing LR coefficients from a CSV file");
		File csvFile=new File(this.pathToCSVFile);
		BufferedReader br;
		HashMap<Value,Regression> pars=new HashMap<Value,Regression>();
		int linesToReadForCoefficients=((RangeDiscrete)dep.getHead().getPredicate().getRange()).getValues().size();
		int nrDepPars=linesToReadForCoefficients-1;
		int startingLine=3;
		List<Value> labelsLoaded=new ArrayList<Value>();
		List<Value> modelLoaded=new ArrayList<Value>();
		List<Value> unseenLabels=new ArrayList<Value>();
		Double W=null;
		Double probUnseen=null;
		try {
			br = new BufferedReader(new FileReader(csvFile));
			String line = br.readLine();
			Double result;
			int lineNumber=0;
			Double score=null;
			while (line != null) {
				lineNumber++;
				if(line.startsWith("label bias")){
					HashMap<Feature,Double> coeffs=new HashMap<Feature,Double>();
					String[] coeffsstring=line.split(",");
					StringValue labelValue=new StringValue(coeffsstring[1]);
					modelLoaded.add(labelValue);
					Double intercept=Double.valueOf(coeffsstring[2]);
					int counterString=3;
					for(Feature f:dep.getContinuousFeatures()){
						coeffs.put(f, Double.valueOf(coeffsstring[counterString++]));
					}
					logisticCoefficients.add(labelValue, new Regression(intercept,coeffs));
					System.out.println("Adding pars for: "+labelValue+" int: "+intercept+" coeffs: "+coeffs);
				}
				if(line.startsWith("weight")){
					W=Double.valueOf((line.split(",")[1]));
				}
				if(line.startsWith("seen labels")){
					String[] tmp=line.split(",");
					for(int i=1;i<tmp.length;i++){
						labelsLoaded.add(new StringValue(tmp[i]));
					}
				}
				if(line.startsWith("probability unseen")){
					probUnseen=Double.valueOf((line.split(",")[1]));
				}
				line = br.readLine();
			}	
			
		}
	 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		System.out.println("SEEN LABELS: "+labelsLoaded);
		System.out.println("SEEN LABELS: "+modelLoaded);
		ExtraInfoDavide extraInfo=new ExtraInfoDavide(labelsLoaded,unseenLabels,modelLoaded,probUnseen,W);
		logisticCoefficients.setExtraInfo(extraInfo);
	//if not all the parameters are loaded, set them to weight 0 and intercept 0


}

}
