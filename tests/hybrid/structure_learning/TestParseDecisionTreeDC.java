package hybrid.structure_learning;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import hybrid.comparators.Bigger;
import hybrid.converters.DC_converter;
import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features.Average;
import hybrid.features.ComparisonFeature;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.PosLiteral;
import hybrid.network.StringValue;
import hybrid.network.SubstitutionException;
import hybrid.network.Type;
import hybrid.network.Value;
import hybrid.parameters.Gaussian;
import hybrid.parameters.LinearGParameters;
import hybrid.structureLearning.DecisionTreeData;
import hybrid.structureLearning.LeafNode;
import hybrid.structureLearning.LearnedDependency;
import hybrid.structureLearning.Node;
import hybrid.structureLearning.Tree;

import org.junit.Before;
import org.junit.Test;

public class TestParseDecisionTreeDC {
	Tree testTree=null;

	@Before
	public void setUp() throws FeatureTypeException, ConjunctionConstructionProblem{
		Type objects=new Type("object");
		Logvar object=new Logvar("O",objects);
		CategoricalPred A_pred=new CategoricalPred("A", 1,new String[]{"a1","a2","a3"});
		CategoricalPred B_pred=new CategoricalPred("B", 1,new String[]{"val1","val2","val3"});
		GaussianPred C_pred=new GaussianPred("C",1);
		GaussianPred D_pred=new GaussianPred("D",1);
		Atom A=new Atom(A_pred, new Logvar[]{object});
		Atom B=new Atom(B_pred, new Logvar[]{object});
		Atom C=new Atom(C_pred, new Logvar[]{object});
		Atom D=new Atom(D_pred, new Logvar[]{object});

		//independent node
		Dependency dep_independent=new Dependency(C,new Feature[]{});
		Node rootNode=new Node<DecisionTreeData>(new DecisionTreeData(new ValueFt(),null, null, new LearnedDependency(dep_independent,0),new HashMap<Feature,Value>()));
		testTree=new Tree(rootNode,new DC_converter());

		//discrete child
		ValueFt Afeature=new ValueFt(new Standard_Conjunction<Literal>(C,new PosLiteral(A)));
		Dependency A1Added=new Dependency(C,new Feature[]{Afeature});
		A1Added.getCpd().setParameters(A1Added.getCpd().getParameters());
		Node<DecisionTreeData> A1Node=new Node<DecisionTreeData>(new DecisionTreeData(new ValueFt(new Standard_Conjunction<Literal>(C,new PosLiteral(A))),null, null, new LearnedDependency(A1Added,0),new HashMap<Feature,Value>()));
		rootNode.addChild(A1Node);
		A1Node.setParent(rootNode);

		//Branching a1 of A
		Dependency ABranchingA1=new Dependency(C,new Feature[]{Afeature,new ValueFt(new Standard_Conjunction<Literal>(C,new PosLiteral(D)))});
		ABranchingA1.getCpd().setParameters(ABranchingA1.getCpd().getParameters());
		HashMap<Feature,Value> parentValueA1=new HashMap<Feature,Value>();
		parentValueA1.put(A1Node.get_data().getThis_feature(),new StringValue("a1"));
		Node<DecisionTreeData> branchingA1Node=new Node<DecisionTreeData>(new DecisionTreeData(new ValueFt(new Standard_Conjunction<Literal>(C,new PosLiteral(D))),null, null, new LearnedDependency(ABranchingA1,0),parentValueA1));
		LeafNode<DecisionTreeData> branchingA1LNode=new LeafNode<DecisionTreeData>(new DecisionTreeData(new ValueFt(new Standard_Conjunction<Literal>(C,new PosLiteral(D))),null, null, new LearnedDependency(ABranchingA1,0),parentValueA1));
		A1Node.addChild(branchingA1Node);
		branchingA1Node.addChild(branchingA1LNode);
		branchingA1Node.setParent(A1Node);
		branchingA1LNode.setIsLeaf();

		//Branching a2 of A
		ComparisonFeature f1=new ComparisonFeature(new Standard_Conjunction<>(C,new PosLiteral(D)),new Bigger(2.5),new Average());
		ComparisonFeature f2=new ComparisonFeature(new Standard_Conjunction<>(C,new PosLiteral(D)),new Bigger(2.5),new Average());
		Dependency ABranchingA2=new Dependency(C,new Feature[]{Afeature,f1});
		ABranchingA2.getCpd().setParameters(ABranchingA2.getCpd().getParameters());
		HashMap<Feature,Value> parentValueA2=new HashMap<Feature,Value>();
		parentValueA2.put(A1Node.get_data().getThis_feature(),new StringValue("a2"));
		Node<DecisionTreeData> branchingA2Node=new Node<DecisionTreeData>(new DecisionTreeData(f1,null, null, new LearnedDependency(ABranchingA2,0),parentValueA2));
		LeafNode<DecisionTreeData> branchingA2LNode=new LeafNode<DecisionTreeData>(new DecisionTreeData(f1,null, null, new LearnedDependency(ABranchingA2,0),parentValueA2));
		A1Node.addChild(branchingA2Node);
		branchingA2Node.addChild(branchingA2LNode);
		branchingA2Node.setParent(A1Node);
		branchingA2LNode.setIsLeaf();
		
		branchingA2LNode.setParent(branchingA2Node);



	}

	@Test
	public void calculateMeanandSTD() throws SubstitutionException, IOException{
		/*FileWriter fw=new FileWriter(new File("test.dot"));
		fw.append(testTree.makeDigraph(testTree.getRoot(),""));
		fw.close();*/
		FileWriter fw1=new FileWriter(new File("test.dclause"));
		System.out.println(testTree.printTree_DC(testTree.getRoot(),new DC_converter()));
	    fw1.append(testTree.printTree_DC(testTree.getRoot(),new DC_converter()));
	    fw1.close();
		//System.out.println(testTree.makeDigraph(testTree.getRoot(),""));
	}

}
