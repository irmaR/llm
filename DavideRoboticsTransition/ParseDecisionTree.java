import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import hybrid.converters.DC_converter;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.structureLearning.DecisionTreeData;
import hybrid.structureLearning.Node;
import hybrid.structureLearning.Tree;

public class ParseDecisionTree {

	public static double returnSumofTestLLs(Node<DecisionTreeData> node){
		//System.out.println(((hybrid.structureLearning.DecisionTreeData) node.get_data()).getTestError((Node<hybrid.structureLearning.DecisionTreeData>) node));
		        double tmp=0;
		        if(node.isLeaf()){
		        	 Double value=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getTestDataScore();
		        	 if(value.equals(Double.NEGATIVE_INFINITY)){
		        		 value=0.0;
		        	 }
		             tmp+=value;
		        }
				for(Node<DecisionTreeData> ch:node.getChildren()){
					double tmp1=returnSumofTestLLs(ch);
					tmp+=tmp1;
					
				}
				return tmp;
	}
	
	public static double returnSumofValidationScores(Node<DecisionTreeData> node){
        double tmp=0;
        if(node.isLeaf()){
        	//System.out.println(node);
        	System.out.println(((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getScore());
             tmp+=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getScore();
        }
		for(Node<DecisionTreeData> ch:node.getChildren()){
			double tmp1=returnSumofValidationScores(ch);
			tmp+=tmp1;
			
		}
		return tmp;
}
	
	public static double returnSumofNormalizedTestLL(Node<DecisionTreeData> node){
        double tmp=0;
        if(node.isLeaf()){
        	//System.out.println(node);
        	System.out.println(((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getNormalizedTestScore());
        	double val=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getNormalizedTestScore();
        	if(val==Double.NEGATIVE_INFINITY){
        		val=0;
        	}
             tmp+=val;
        }
		for(Node<DecisionTreeData> ch:node.getChildren()){
			double tmp1=returnSumofNormalizedTestLL(ch);
			tmp+=tmp1;
			
		}
		return tmp;
}
	public static double getAccumulatedError(Node<DecisionTreeData> node){
		double tmp=0;
		if(node.isLeaf()){
			//System.out.println("UNNORM: "+((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_test_data());
		    tmp=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_test_data();
		}
		for(Node<DecisionTreeData> ch:node.getChildren()){
			double tmp1=getAccumulatedError(ch);
			tmp+=tmp1;
			
		}
		return tmp;
	}
	
	public static double getAccumulatedValidationError(Node<DecisionTreeData> node){
		double tmp=0;
		if(node.isLeaf()){
			//System.out.println("UNNORM: "+((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_validation_data());
		    Double val=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_validation_data();
			System.out.println(val);
			if(val>1){
			System.out.println("BLA:"+((hybrid.structureLearning.DecisionTreeData) node.get_data()).getNr_datapoints_validation());
			System.out.println(node);
			}
		    tmp=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_validation_data();
		}
		for(Node<DecisionTreeData> ch:node.getChildren()){
			double tmp1=getAccumulatedValidationError(ch);
			tmp+=tmp1;
			
		}
		return tmp;
	}
	
	
	
	public static void main(String[] args) throws IOException{
		ArgumentParser parser = ArgumentParsers.newArgumentParser("prog")
			    .defaultHelp(true)
				.description("Process paths.");
				
				/**
				 * input represents input to the data
				 */
				parser.addArgument("-i")
				.dest("input")
				.type(String.class)
				.help(".ser file")
				.required(true)
				;
				parser.addArgument("-o")
				.dest("output")
				.type(String.class)
				.help("output folder")
				.required(true)
				;
				parser.addArgument("-f")
				.dest("file")
				.type(String.class)
				.help("file name")
				.required(true)
				;
				
				parser.addArgument("-n")
				.dest("n")
				.type(Integer.class)
				.help("number of data points")
				.required(true)
				;
				
		Namespace res=null;

				try {
					res = parser.parseArgs(args);
				} catch (ArgumentParserException e) {
					System.out.println(" Didn't succeed to parse the parameters");
					parser.handleError(e);
					System.exit(1);
				}
		String path_to_decision_tree=res.getString("input");
        String output=res.getString("output");
		String file_name=res.getString("file");
		Integer n=res.getInt("n");
		System.out.println("N: "+n);
        Tree learned_tree = null;
		try
		{
			FileInputStream fileIn = new FileInputStream(path_to_decision_tree);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			learned_tree = (Tree) in.readObject();
			
			/*double testDataLL=returnSumofNormalizedTestLL(learned_tree.getRoot());
			FileWriter fwll=new FileWriter(new File(output+"/"+"pos_y_next"+".normTestLL"));
			fwll.append(String.valueOf(testDataLL)+"\n");
			fwll.close();
			*/
			
			double acc_error=getAccumulatedError(learned_tree.getRoot());
			FileWriter fw1=new FileWriter(new File(output+"/"+file_name+".rmse"));
			Double nrmse=Math.sqrt(acc_error/n);
			fw1.append(String.valueOf(nrmse)+"\n");
			fw1.append(String.valueOf(n));
			fw1.close();
			
			double acc_validation_error=getAccumulatedValidationError(learned_tree.getRoot());
			FileWriter fw3=new FileWriter(new File(output+"/"+file_name+".validation_rmse"));
			Double rmse_validarion=Math.sqrt(acc_validation_error/32);
			fw3.append(String.valueOf(rmse_validarion)+"\n");
			fw3.close();
			
			/*double acc_error=learned_tree.getAccumulatedError(learned_tree.getRoot());
			FileWriter fw1=new FileWriter(new File(output+"/"+file_name+".rmse"));
			System.out.println("ACC: "+acc_error);
			Double nrmse=Math.sqrt(acc_error/11);
			fw1.append(String.valueOf(nrmse)+"\n");
			fw1.close();*/
			
			/*double testDataLL=returnSumofTestLLs(learned_tree.getRoot());
			System.out.println("Sum: "+testDataLL);
			FileWriter fwll=new FileWriter(new File(output+"/output.testLL"));
			fwll.append(String.valueOf(testDataLL)+"\n");
			fwll.close();*/
			
			
			/*double accNormError=learned_tree.getAccumulatedNormalizedError(learned_tree.getRoot());
			int nr_leavesNonZero=learned_tree.gerNrLeavesWithNonZeroNRMSE(learned_tree.getRoot());
			Double avgnrmse=accNormError/Double.valueOf(nr_leavesNonZero);
			System.out.println("ACC: "+avgnrmse);
			FileWriter fw4=new FileWriter(new File(output+"/"+"output.avgnrmse"));
			fw4.append(String.valueOf(avgnrmse));
			System.out.println("Number of leaves: "+learned_tree.getNrLeaves(learned_tree.getRoot()));
			fw4.close();*/
			
			//int nr_leaves=e.getNrLeaves(e.getRoot());
			//double testLL=e.getAccumulatedTestLL(e.getRoot(),0);

			/*FileOutputStream fileOut =new FileOutputStream(output+"/"+"output.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(e);
			out.close();
			fileOut.close();*/
			FileWriter fw=new FileWriter(new File(output+"/"+file_name+".dot"));
			fw.append(learned_tree.makeDigraph(learned_tree.getRoot(),""));
			fw.close();
			
			/*double accNormError=e.getAccumulatedNormalizedError(e.getRoot());
			FileWriter fw4=new FileWriter(new File(output+"/output.avgnrse"));
			
			int nr_leavesNonZero=e.gerNrLeavesWithNonZeroNRMSE(e.getRoot());
			System.out.println(accNormError);
			fw4.append(String.valueOf(accNormError/nr_leaves));
			System.out.println("Number of leaves: "+nr_leaves);
			System.out.println("Number of leaves non zero: "+nr_leavesNonZero);
			fw4.close();*/
			/*double acc_error=e.getAccumulatedError(e.getRoot());
			System.out.println("Acc error: "+acc_error);
			FileWriter fw1=new FileWriter(new File(output+"/output.nrmse"));
			fw1.append(String.valueOf(Math.sqrt(acc_error)));
			fw1.close();
			
			fw.close();
			in.close();
			fileIn.close();*/
		}catch(IOException i)
		{
			i.printStackTrace();
			return;
		}catch(ClassNotFoundException c)
		{
			System.out.println("Employee class not found");
			c.printStackTrace();
			return;
		}
	//	System.out.println("TREE TO DC CONVERSION ");
		//System.out.println(e.printDigraph(e.getRoot(),""));
		//System.out.println(e.printTree_DC(e.getRoot(),"", new DC_converter()));
	/*FileWriter fw=new FileWriter(new File(output+"/"+res.getString("file")+".dclause"));
	fw.append(learned_tree.printTree_DC(learned_tree.getRoot(),new DC_converter()));
	fw.close();*/
	//fw.append(e.makeDigraph(e.getRoot(),""));
    //fw.close();
	    
	    
	    
	//	fw.close();
	}

}

