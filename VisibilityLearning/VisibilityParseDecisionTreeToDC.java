import hybrid.converters.DC_converter;
import hybrid.structureLearning.Tree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class VisibilityParseDecisionTreeToDC {
	
	public void parse(String path_to_decision_tree,String output) throws IOException{
		// path_to_decision_tree="//cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/visibledata/shorter/visible_cur.ser";
        // output="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/visibledata/shorter/";
		//String path_to_decision_tree="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/box_data/transition_new_22_09_DT/pos_x_next.ser";
		
        Tree e = null;
		try
		{
			System.out.println(path_to_decision_tree);
			FileInputStream fileIn = new FileInputStream(path_to_decision_tree);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			e = (Tree) in.readObject();
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
		System.out.println("TREE TO DC CONVERSION ");
		FileWriter fw=new FileWriter(new File(output));
	    fw.append(e.printTree_DC(e.getRoot(),"", new DC_converter()));
		fw.close();
	}
}
