package hybrid.utils;

import hybrid.dependencies.Dependency;

import java.util.HashMap;

public class DavideOutputFiles {

	private static HashMap<String, String> dataCSV=new HashMap<String, String>();
	private static HashMap<String, String> modelCSV=new HashMap<String, String>();
	private static HashMap<String, String> testCSV=new HashMap<String, String>();
	
	
	
	public static HashMap<String, String> getDataCSV() {
		return dataCSV;
	}
	public static void setDataCSV(HashMap<String, String> dataCSV) {
		DavideOutputFiles.dataCSV = dataCSV;
	}
	public static HashMap<String, String> getModelCSV() {
		return modelCSV;
	}
	public static void setModelCSV(HashMap<String, String> modelCSV) {
		DavideOutputFiles.modelCSV = modelCSV;
	}
	public static HashMap<String, String> getTestCSV() {
		return testCSV;
	}
	public static void setTestCSV(HashMap<String, String> testCSV) {
		DavideOutputFiles.testCSV = testCSV;
	}
	
	public static void addData(String dep,String f){
		dataCSV.put(dep, f);
	}
	
	public static void addModel(String dep,String f){
		modelCSV.put(dep, f);
	}
	
	public static void addTest(String dep,String f){
		testCSV.put(dep, f);
	}
	
	
	
	
}
