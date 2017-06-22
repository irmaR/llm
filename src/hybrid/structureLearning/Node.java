package hybrid.structureLearning;


import hybrid.converters.Converter;
import hybrid.features.Feature;
import hybrid.network.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class Node<DecisionTreeData> implements Serializable {
        protected DecisionTreeData data;
        protected Node<DecisionTreeData> parent;
        protected List<Node<DecisionTreeData>> children;
        protected boolean isLeaf;
        private transient String identifier;
        private double test_error_data;

        
        public String getIdentifier() {
			return identifier;
		}

		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}

		public Node(DecisionTreeData node_data){
        	this.data=node_data;
        	this.children=new ArrayList<Node<DecisionTreeData>>();
        }
        
        public Node(){
        	
        }
        
        public double getTest_error_data() {
			return test_error_data;
		}

		public void setTest_error_data(double test_error_data) {
			this.test_error_data = test_error_data;
		}

		public void setChildren(List<Node<DecisionTreeData>> children){
        	this.children=children;
        }
        
        public void addChild(Node<DecisionTreeData> child){
        	child.setParent(this);
        	this.children.add(child);
        }
        
        public List<Node<DecisionTreeData>> getChildren(){
        	return this.children;
        }
        
        
        public Node<DecisionTreeData> getParent(){
        	return this.parent;
        }
        
        
		public void setParent(Node<DecisionTreeData> parent){
        	this.parent=parent;
        }
        
        public DecisionTreeData get_data(){
        	return data;
        }
        
        public String toString(){
        	return "Is leaf?"+isLeaf()+" "+this.data.toString();
        }

		public String printNode(String tmp) {
			tmp+=this.toString();
			return tmp;
		}
		
		public boolean isLeaf(){
			return false;
		}
		
		public void setIsLeaf(){
			this.isLeaf=true;
		}
        
        
    }

