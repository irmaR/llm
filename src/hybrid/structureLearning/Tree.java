package hybrid.structureLearning;

import hybrid.converters.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tree<DecisionTreeData> extends DNdependency implements Serializable{
	private Node<DecisionTreeData> root;

	public Tree(Node<DecisionTreeData> root_node,Converter c) {
		root = root_node;
		root.setChildren(new ArrayList<Node<DecisionTreeData>>());
		root.setParent(null);
	}

	public void addNode(DecisionTreeData data,Node<DecisionTreeData>parent){
		Node<DecisionTreeData> new_node=new Node<DecisionTreeData>(data);
		new_node.setParent(parent);
	}

	public String printTree_DC(Node<DecisionTreeData> node,Converter c){
		String tmp=c.convert((Node<hybrid.structureLearning.DecisionTreeData>) node);
		for(Node<DecisionTreeData> ch:node.getChildren()){
			tmp+=printTree_DC(ch, c)+"\n";
		}
		return tmp;
	}
	
	public int getNrLeaves(Node<DecisionTreeData> node){
		int tmp=0;
		for(Node<DecisionTreeData> ch:node.getChildren()){
			if(ch.isLeaf()){
                 tmp++;
                 continue;
			}
			int nrFromPrev=getNrLeaves(ch);
			tmp+=nrFromPrev;
		}
		return tmp;
	}
	
	public int gerNrLeavesWithNonZeroNRMSE(Node<DecisionTreeData> node){
		int tmp=0;
		for(Node<DecisionTreeData> ch:node.getChildren()){
			if(ch.isLeaf()){
				if(((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_test_data()!=0){
                 tmp++;
				}
			}
			int nrFromPrev=gerNrLeavesWithNonZeroNRMSE(ch);
			tmp+=nrFromPrev;
		}
		return tmp;
	}

	public String printTree1(Node<DecisionTreeData> node,String tmp){
		
		
		tmp+=node;
		for(Node<DecisionTreeData> ch:node.getChildren()){
			if(ch.isLeaf()){
				tmp="LEAF: "+printTree1(ch, tmp);
			}
			else{
				tmp=printTree1(ch, tmp);
			}	
		}
		return tmp;
	}

	public String makeDigraph(Node<DecisionTreeData> node,String tmp){
		String result=printDigraph(node, tmp);
		return "digraph G{\n"+result+"}\n";
	}

	public String getIndicesOfAllFeatures(Node<DecisionTreeData> node,String tmp){
		String result=printDigraph(node, tmp);
		return "digraph G{\n"+result+"}\n";
	}

	public double getAccumulatedError(Node<DecisionTreeData> node){
		double tmp=0;
		if(node.isLeaf){
			System.out.println("UNNORM: "+((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_test_data());
		    tmp=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_test_data();
		}
		for(Node<DecisionTreeData> ch:node.getChildren()){
			double tmp1=getAccumulatedError(ch);
			tmp+=tmp1;
			
		}
		return tmp;
	}
	
	public double getAccumulatedValidationError(Node<DecisionTreeData> node){
		double tmp=0;
		if(node.isLeaf){
			System.out.println("UNNORM VAL: "+((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_validation_data());
		    tmp=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_validation_data();
		}
		for(Node<DecisionTreeData> ch:node.getChildren()){
			double tmp1=getAccumulatedValidationError(ch);
			tmp+=tmp1;
			
		}
		return tmp;
	}
	
	
	public double getAccumulatedError_Unnorm(Node<DecisionTreeData> node){
		double tmp=0;
		if(node.isLeaf){
			System.out.println("UNNORM TEST: "+((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_test_data());
		    tmp=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getError_test_data();
		}
		for(Node<DecisionTreeData> ch:node.getChildren()){
			double tmp1=getAccumulatedError(ch);
			tmp+=tmp1;
			
		}
		return tmp;
	}
	
	public double getAccumulatedNormalizedError(Node<DecisionTreeData> node){
		double tmp=0;
		if(node.isLeaf()){
		   System.out.println("NRMSE: "+((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getNormalizedTestError());
		   tmp=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getNormalizedTestError();
		}
		for(Node<DecisionTreeData> ch:node.getChildren()){
			tmp+=getAccumulatedNormalizedError(ch);
		}
		return tmp;
	}
	
	public double getAccumulatedTestLL(Node<DecisionTreeData> node,double tmp) {
		if(node.isLeaf()){
		   tmp=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getTestDataScore();
		}
		for(Node<DecisionTreeData> ch:node.getChildren()){
			tmp+=getAccumulatedTestLL(ch, tmp);
		}
		return tmp;
	}

	public String printDigraph(Node<DecisionTreeData> node,String tmp){
		tmp+=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDigraphRepresentation((Node<hybrid.structureLearning.DecisionTreeData>) node)+"\n";
		
		for(Node<DecisionTreeData> ch:node.getChildren()){
			tmp=printDigraph(ch, tmp);
		}
		return tmp;
	}


	public String getIndicesOfFeatures(Node<DecisionTreeData> node,String tmp){
		if(!tmp.contains(String.valueOf(((hybrid.structureLearning.DecisionTreeData) node.get_data()).getThis_feature().getIndexInFeatureSpace()))){
			tmp+=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getThis_feature().getIndexInFeatureSpace()+", ";

		}
		for(Node<DecisionTreeData> ch:node.getChildren()){
			tmp=getIndicesOfFeatures(ch, tmp);
		}
		return tmp;
	}


	public Node getRoot(){
		return root;
	}

	public String printTree(Node<DecisionTreeData> node,String tmp){
		System.out.println("-----------  THIS NODE: -----------");
		System.out.println(node);
		System.out.println("CHILDRED LEV1 : ----------");
		for(Node<DecisionTreeData> ch:node.getChildren()){
			System.out.println(ch);
			System.out.println("Childs children: Lev2 ");
			for(Node<DecisionTreeData> ch1:ch.getChildren()){
				System.out.println(ch1);
				System.out.println("Childs children: Lev3 ");
				for(Node<DecisionTreeData> ch2:ch1.getChildren()){
					System.out.println(ch2);
					System.out.println("Childs children: Lev4 ");
					for(Node<DecisionTreeData> ch3:ch2.getChildren()){
						System.out.println(ch3);
						System.out.println("Childs children: Lev5 ");
						for(Node<DecisionTreeData> ch4:ch3.getChildren()){
							System.out.println(ch4);
						}
					}
				}
			}

		}
		return "";
	}






}