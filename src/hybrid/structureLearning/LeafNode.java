package hybrid.structureLearning;

import java.util.ArrayList;

public class LeafNode<T> extends Node<T> {

	public LeafNode(T node_data){
        	this.data=node_data;
        	this.children=new ArrayList<Node<T>>();
        }
	
	public boolean isLeaf(){
		return true;
	}

}
