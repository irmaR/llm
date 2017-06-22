import hybrid.structureLearning.DecisionTreeData;
import hybrid.structureLearning.Node;
import hybrid.structureLearning.Tree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class FixValidationError {

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
				
				parser.addArgument("-f")
				.dest("file")
				.type(String.class)
				.help("file name")
				.required(true)
				;
				
				parser.addArgument("-d")
				.dest("data")
				.type(String.class)
				.help("data")
				.required(true)
				;
				
				parser.addArgument("-N")
				.dest("N")
				.type(Integer.class)
				.help("number of objects")
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
		String path_to_results=res.getString("input");
		String file_name=res.getString("file");
		Integer nr_objects=res.getInt("N");
		String dataPath=res.getString("data");
		
		
		for(int i=1;i<=10;i++){
			String pathToFold=path_to_results+"/fold"+i;
			String path_to_ser=pathToFold+"/"+file_name+".ser";
			System.out.println(dataPath+"/fold"+i+"/test");
			Integer nr_foldsTest=getNrInterps(dataPath+"/fold"+i+"/test");
		    Integer nr_foldsValidation=getNrInterps(dataPath+"/fold"+i+"/validate");
		    System.out.println("Nr interps test: "+nr_foldsTest+" Nr interps validation: "+nr_foldsValidation);
		    Integer nrDatapointsTest=nr_foldsTest*nr_objects;
		    Integer nrDatapointsValidation=nr_foldsValidation*nr_objects;
		    fixValidation(path_to_ser,pathToFold,file_name,nrDatapointsValidation);
		}
		
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
		    tmp=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_validation_data();
		}
		for(Node<DecisionTreeData> ch:node.getChildren()){
			double tmp1=getAccumulatedValidationError(ch);
			tmp+=tmp1;
			
		}
		return tmp;
	}
	
	private static void fixValidation(String path_to_ser, String output, String file_name,Integer nrDatapointsValidation) {
		Tree learned_tree = null;
		try
		{
			FileInputStream fileIn = new FileInputStream(path_to_ser);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			learned_tree = (Tree) in.readObject();
			
			/*double testDataLL=returnSumofNormalizedTestLL(learned_tree.getRoot());
			FileWriter fwll=new FileWriter(new File(output+"/"+"pos_y_next"+".normTestLL"));
			fwll.append(String.valueOf(testDataLL)+"\n");
			fwll.close();
			*/
			
			/*double acc_error=getAccumulatedError(learned_tree.getRoot());
			FileWriter fw1=new FileWriter(new File(path_to_ser+"/"+file_name+".rmse"));
			Double nrmse=Math.sqrt(acc_error/n);
			fw1.append(String.valueOf(nrmse)+"\n");
			fw1.append(String.valueOf(n));
			fw1.close();*/
			
			double acc_validation_error=getAccumulatedValidationError(learned_tree.getRoot());
			FileWriter fw3=new FileWriter(new File(output+"/"+file_name+".validation_rmse"));
			Double rmse_validarion=Math.sqrt(acc_validation_error/nrDatapointsValidation);
			fw3.append(String.valueOf(rmse_validarion)+"\n");
			fw3.close();
			
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
	}
		

	private static Integer getNrInterps(String folderPath) {
		File folder = new File(folderPath);
		String[] fileNames = folder.list();
		int total = 0;
		for (int i = 0; i< fileNames.length; i++)
		{
		  if (fileNames[i].contains(".txt"))
		    {
		      total++;
		     }
		  }
		return total;
	}

}
