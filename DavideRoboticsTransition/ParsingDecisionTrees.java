import hybrid.converters.DC_converter;
import hybrid.structureLearning.Tree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import weka.filters.unsupervised.attribute.Center;

public class ParsingDecisionTrees {
	public static void main(String[] args){
		//String path1="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/DisplacementDataSimple/mix1Object/score/EntireData/";
		//String path2="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/DisplacementDataSimple/mix2objNew/DummyValidation/score/EntireData/DisplacementNotAggregated/";
		//String path3="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/DisplacementDataSimple/mix2And3Objects/DummyValidation/score/DisplacementNotAggregated/";
		//String path4="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/DisplacementDataSimple/mix3obj/DummyValidation/score/DisplacementNotAggregated/";

		//String path5="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/DisplacementDataSimple/mix1Object/score/EntireData/";
		String path6="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/DisplacementDataSimple/mix2objNew/DummyValidation/score/EntireData/DisplacementNotAggregated/";
		String path7="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/DisplacementDataSimple/mix2And3Objects/DummyValidation/score/EntireData/DisplacementNotAggregated/";
		String path8="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/DisplacementDataSimple/mix3obj/DummyValidation/score/EntireData/DisplacementNotAggregated/";
		String path9="/cw/dtaijupiter/NoCsBack/dtai/irma/Davide_experiments/Results/DisplacementDataSimple/mix2objExtended/DummyValidation/score/EntireData/DisplacementNotAggregated/";



		String pathOutput="/home/irma/Desktop/models/";

		List<String> penalties=new ArrayList<String>();
		penalties.add("Pen100");
		penalties.add("Pen500");
		penalties.add("Pen1000");
		penalties.add("Pen2000");
		penalties.add("Pen3000");
		penalties.add("PenNone");

		List<String> paths=new ArrayList<String>();
		//paths.add(path1);
		//paths.add(path2);
		//paths.add(path3);
		//paths.add(path4);
		//paths.add(path5);
		paths.add(path6);
		paths.add(path7);
		paths.add(path8);
		paths.add(path9);

		List<String> nrObj=new ArrayList<String>();
		//nrObj.add("model_1_objNotAggr");
		//nrObj.add("model_2_objNotAggr");
		//nrObj.add("model_2_and_3_objNotAggr");
		//nrObj.add("model_3_objNotAggr");
		//nrObj.add("model_1_objNotAggr");
		nrObj.add("model_2_objAggr");
		nrObj.add("model_2_and_3_objAggr");
		nrObj.add("model_3_objAggr");
		nrObj.add("model_2_extendedAggr");

		List<String> predicates=new ArrayList<String>();
		predicates.add("pos_x_next");
		predicates.add("pos_y_next");
		int counter=0;
		for(String pathRes:paths){
			for(String p:penalties){		
				
				String penaltyFeaturesnone=pathRes+"/"+p+"/";
				String penaltyFeatures2=pathRes+"/"+p+"/"+"PenaltyFts2/";
				String penaltyFeatures3=pathRes+"/"+p+"/"+"PenaltyFts3/";
				List<String> subpaths=new ArrayList<String>();
				subpaths.add(penaltyFeaturesnone);
				subpaths.add(penaltyFeatures2);
				subpaths.add(penaltyFeatures3);

				for(String subpath:subpaths){
					List<String> pathsToTrees=new ArrayList<String>();
					String entireConversion="";
					for(String pred:predicates){
						Tree learned_tree = null;
						String inputTree=subpath+"/"+pred+".ser";
						System.out.println("Input tree: "+inputTree);
						System.out.println(inputTree);
						System.out.println(new File(inputTree).exists());
						try
						{

							FileInputStream fileIn = new FileInputStream(inputTree);
							ObjectInputStream in = new ObjectInputStream(fileIn);
							learned_tree = (Tree) in.readObject();
							FileWriter fw=new FileWriter(new File(inputTree.replace(".ser",".dclause")));
							pathsToTrees.add(subpath+"/"+pred+".dot");
							String conversionPred=learned_tree.printTree_DC(learned_tree.getRoot(),new DC_converter());
							entireConversion=entireConversion.concat(conversionPred);
							fw.append(conversionPred);
							fw.close();
						}catch(FileNotFoundException i)
						{
							System.out.println("Result not there: "+inputTree);
							continue;
						}catch(ClassNotFoundException c)
						{
							System.out.println("Result not there: "+inputTree);
							continue;
						} catch (IOException e) {
							System.out.println("Result not there: "+inputTree);
							continue;
						}

					}

					try {
						
						if(!entireConversion.equals("") || !entireConversion.isEmpty()){
							String[] subpathsplit=subpath.split("/");
							String nameFolder=nrObj.get(counter)+"_"+subpathsplit[subpathsplit.length-1]+"_"+p;
							File dir = new File(pathOutput+"/"+nameFolder);
							dir.mkdir();
							String pathOutputFile=pathOutput+nameFolder+"/"+nrObj.get(counter)+"_"+subpathsplit[subpathsplit.length-1]+"_"+p+".pl";
							for(String treePath:pathsToTrees){
								copyFile(new File(treePath),new File(pathOutput+"/"+nameFolder+"/"+treePath.split("/")[treePath.split("/").length-1]+".dot"));
							}
							
							
							FileWriter fw=new FileWriter(new File(pathOutputFile));
							fw.append(entireConversion+"\n");
							fw.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("Halooo"+pathOutput+"/"+nrObj.get(counter)+"_"+p+".pl");
						continue;
					}
				}
			}
			counter++;
		}

	}
	
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
}

