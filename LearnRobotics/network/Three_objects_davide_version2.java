package network;

import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Type;

public class Three_objects_davide_version2 {
	public NetworkInfo getNetwork(int subsampling_ratio){
		Type objects=new Type("object");
		Logvar object=new Logvar("Obj",objects);
		Logvar object1=new Logvar("Obj1",objects);
		BooleanPred moved_pred=new BooleanPred("moved",1);
		GaussianPred displacement_pred=new GaussianPred("displacement",1);
		CategoricalPred dimension_pred=new CategoricalPred("dim",1);
		GaussianPred current_x_pred=new GaussianPred("current_x",1);
		GaussianPred current_z_pred=new GaussianPred("current_z",1);
		GaussianPred next_x_pred_dimension1=new GaussianPred("next_x_dimension1",1);
		GaussianPred next_x_pred_dimension2=new GaussianPred("next_x_dimension2",1);
		GaussianPred next_x_pred_dimension3=new GaussianPred("next_x_dimension3",1);
					
		Atom moved=new Atom(moved_pred, new Logvar[]{object});
		Atom displacement=new Atom(displacement_pred, new Logvar[]{object});
		Atom dimension=new Atom(dimension_pred, new Logvar[]{object});
		Atom current_x=new Atom(current_x_pred, new Logvar[]{object});
		Atom current_z=new Atom(current_z_pred, new Logvar[]{object});
		Atom next_x_dimension1=new Atom(next_x_pred_dimension1, new Logvar[]{object});
		Atom next_x_dimension2=new Atom(next_x_pred_dimension2, new Logvar[]{object});
		Atom next_x_dimension3=new Atom(next_x_pred_dimension3, new Logvar[]{object});
		return new NetworkInfo(new Atom[]{dimension,moved,displacement,current_x,current_z,next_x_dimension1,next_x_dimension2,next_x_dimension3},new Type[]{objects});	   		
		}
}
