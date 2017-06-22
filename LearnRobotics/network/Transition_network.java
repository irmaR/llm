package network;

import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Type;

public class Transition_network {
	public NetworkInfo getNetwork(int subsampling_ratio){
		Type objects=new Type("object");
		Type arms=new Type("arm");
		Logvar object=new Logvar("Obj",objects);
		Logvar object1=new Logvar("Obj1",objects);
		Logvar arm=new Logvar("Arm",arms);
		
		
		BooleanPred moved_pred=new BooleanPred("moved",1);
		CategoricalPred dimension_pred=new CategoricalPred("dim", 1);
		//arm predicates
		GaussianPred arm_x_pred=new GaussianPred("arm_x_cur",1);
		GaussianPred arm_y_pred=new GaussianPred("arm_y_cur",1);
		GaussianPred arm_z_pred=new GaussianPred("arm_z_cur",1);
		
		GaussianPred arm_x_next_pred=new GaussianPred("arm_x_next",1);
		GaussianPred arm_y_next_pred=new GaussianPred("arm_y_next",1);
		GaussianPred arm_z_next_pred=new GaussianPred("arm_z_next",1);
		
		GaussianPred arm_roll_pred=new GaussianPred("arm_roll_cur",1);
		GaussianPred arm_pitch_pred=new GaussianPred("arm_pitch_cur",1);
		GaussianPred arm_yaw_pred=new GaussianPred("arm_yaw_cur",1);
		
		GaussianPred arm_roll_next_pred=new GaussianPred("arm_roll_next",1);
		GaussianPred arm_pitch_next_pred=new GaussianPred("arm_pitch_next",1);
		GaussianPred arm_yaw_next_pred=new GaussianPred("arm_yaw_next",1);
		
		GaussianPred arm_finger_pred=new GaussianPred("arm_finger_cur",1);
		GaussianPred arm_finger_next_pred=new GaussianPred("arm_finger_next",1);
		
		
		//object predicates
		GaussianPred x_cur_pred=new GaussianPred("pos_x_cur",1);
		GaussianPred y_cur_pred=new GaussianPred("pos_y_cur",1);
		GaussianPred z_cur_pred=new GaussianPred("pos_z_cur",1);
		
		GaussianPred x_next_pred=new GaussianPred("pos_x_next",1);
		GaussianPred y_next_pred=new GaussianPred("pos_y_next",1);
		GaussianPred z_next_pred=new GaussianPred("pos_z_next",1);
		
		GaussianPred yaw_cur_pred=new GaussianPred("yaw_cur",1);
		GaussianPred pitch_cur_pred=new GaussianPred("pitch_cur",1);
		GaussianPred roll_cur_pred=new GaussianPred("roll_cur",1);
		GaussianPred yaw_next_pred=new GaussianPred("yaw_next",1);
		GaussianPred pitch_next_pred=new GaussianPred("pitch_next",1);
		GaussianPred roll_next_pred=new GaussianPred("roll_next",1);
		
		
		
		Atom arm_x=new Atom(arm_x_pred, new Logvar[]{arm});
		Atom arm_y=new Atom(arm_y_pred, new Logvar[]{arm});
		Atom arm_z=new Atom(arm_z_pred, new Logvar[]{arm});
		
		
		Atom arm_roll=new Atom(arm_roll_pred, new Logvar[]{arm});
		Atom arm_pitch=new Atom(arm_pitch_pred, new Logvar[]{arm});
		Atom arm_yaw=new Atom(arm_yaw_pred, new Logvar[]{arm});
		Atom arm_finger=new Atom(arm_finger_pred, new Logvar[]{arm});
		Atom arm_x_next=new Atom(arm_x_next_pred, new Logvar[]{arm});
		Atom arm_y_next=new Atom(arm_y_next_pred, new Logvar[]{arm});
		Atom arm_z_next=new Atom(arm_z_next_pred, new Logvar[]{arm});
		
		
		
		Atom arm_roll_next=new Atom(arm_roll_next_pred, new Logvar[]{arm});
		Atom arm_pitch_next=new Atom(arm_pitch_next_pred, new Logvar[]{arm});
		Atom arm_yaw_next=new Atom(arm_yaw_next_pred, new Logvar[]{arm});
		Atom arm_finger_next=new Atom(arm_finger_next_pred, new Logvar[]{arm});
		
		Atom x_cur=new Atom(x_cur_pred, new Logvar[]{object});
		Atom y_cur=new Atom(y_cur_pred, new Logvar[]{object});
		Atom z_cur=new Atom(z_cur_pred, new Logvar[]{object});
		
		
		Atom x_next=new Atom(x_next_pred, new Logvar[]{object});
		Atom y_next=new Atom(y_next_pred, new Logvar[]{object});
		Atom z_next=new Atom(z_next_pred, new Logvar[]{object});
		
		Atom yaw_cur=new Atom(yaw_cur_pred, new Logvar[]{object});
		Atom pitch_cur=new Atom(pitch_cur_pred, new Logvar[]{object});
		Atom roll_cur=new Atom(roll_cur_pred, new Logvar[]{object});
		
		
		return new NetworkInfo(new Atom[]{x_next,y_next,z_next,arm_finger_next,arm_finger,x_cur,y_cur,z_cur,yaw_cur,pitch_cur,roll_cur,arm_x,arm_y,arm_z,arm_roll,arm_pitch,arm_yaw,arm_x_next,arm_y_next,arm_z_next,arm_roll_next,arm_pitch_next,arm_yaw_next},new Type[]{objects,arms});	   		
		}
}
