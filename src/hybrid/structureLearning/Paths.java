package hybrid.structureLearning;

import java.io.File;

public class Paths {

	private String input_path;
	private String non_discretized_path;
	private String results_path;
	
	
	public String getInput_path() {
		return input_path;
	}
	public void setInput_path(String input_path) {
		this.input_path = input_path;
	}
	public String getNon_discretized_path() {
		return non_discretized_path;
	}
	public void setNon_discretized_path(String non_discretized_path) {
		this.non_discretized_path = non_discretized_path;
	}
	public String getResults_path() {
		return results_path;
	}
	public void setResults_path(String results_path) {
		File f=new File(results_path);
		f.mkdirs();
		System.out.println(f.exists());
		this.results_path = results_path;
	}
	
	
	
}
