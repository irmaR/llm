package network;

import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Type;

public class Three_objects_davide_continuous_dimension {
	public NetworkInfo getNetwork(int subsampling_ratio){
		Type objects=new Type("object");
		Logvar object=new Logvar("Obj",objects);
		Logvar object1=new Logvar("Obj1",objects);
		BooleanPred moved_pred=new BooleanPred("moved",1);
		GaussianPred displacement_pred=new GaussianPred("displacement",1);
		GaussianPred dimension_pred=new GaussianPred("dim",1);
		GaussianPred current_x_pred=new GaussianPred("current_x",1);
		GaussianPred current_z_pred=new GaussianPred("current_z",1);
		GaussianPred next_x_pred=new GaussianPred("next_x",1);
					
		Atom moved=new Atom(moved_pred, new Logvar[]{object});
		Atom displacement=new Atom(displacement_pred, new Logvar[]{object});
		Atom dimension=new Atom(dimension_pred, new Logvar[]{object});
		Atom current_x=new Atom(current_x_pred, new Logvar[]{object});
		Atom current_z=new Atom(current_z_pred, new Logvar[]{object});
		Atom next_x=new Atom(next_x_pred, new Logvar[]{object});
		return new NetworkInfo(new Atom[]{dimension,moved,displacement,current_x,current_z,next_x},new Type[]{objects});	   		
		}
}
