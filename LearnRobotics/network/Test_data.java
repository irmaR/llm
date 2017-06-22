package network;

import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.Type;

public class Test_data {
	public NetworkInfo getNetwork(int subsampling_ratio){
		Type objects=new Type("object");
		Logvar object=new Logvar("Obj",objects);
		Predicate main_object_pred=new BooleanPred("object",1);
		GaussianPred in_pred=new GaussianPred("in",1);
		GaussianPred out_pred=new GaussianPred("out",1);
		
		Atom in=new Atom(in_pred, new Logvar[]{object});
		Atom out=new Atom(out_pred, new Logvar[]{object});
		return new NetworkInfo(new Atom[]{in,out},new Type[]{objects});
	}
}
