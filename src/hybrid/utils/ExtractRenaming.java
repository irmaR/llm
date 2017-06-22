package hybrid.utils;

import hybrid.featureGenerator.Renaming;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.Type;

import java.util.*
;
public class ExtractRenaming {
	
	public static Renaming extractRenaming(List<? extends Literal> literals){
		Renaming ren=new Renaming();
		for(Literal a:literals){
			for(Logvar l:a.getAtom().getArguments()){
				ren.addRenaming(l.getType(), l);	
			}
		}
	      return ren;	
	}
	
	
}
