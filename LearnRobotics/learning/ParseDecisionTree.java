package learning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

import hybrid.converters.DC_converter;
import hybrid.structureLearning.Tree;

public class ParseDecisionTree {


	public static void main(String[] args) throws IOException{
		String path_to_decision_tree="/home/irma/workspace/Davide_experiments/Results/pos_z_next.ser";
        //String path_to_decision_tree="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/box_data/transition_new_22_09_DT/pos_x_next.ser";
		
        Tree e = null;
		try
		{
			FileInputStream fileIn = new FileInputStream(path_to_decision_tree);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			e = (Tree) in.readObject();
			in.close();
			fileIn.close();
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
		//System.out.println(e.printDigraph(e.getRoot(),""));
		//System.out.println(e.printTree_DC(e.getRoot(),"", new DC_converter()));
		FileWriter fw=new FileWriter(new File("/home/irma/workspace/Davide_experiments/Results/pos_z_next.dc"));
	    fw.append(e.printTree_DC(e.getRoot(),new DC_converter()));
	    //fw.append(e.makeDigraph(e.getRoot(),""));

		fw.close();
	}

}
