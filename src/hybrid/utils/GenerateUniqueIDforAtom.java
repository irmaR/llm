package hybrid.utils;
public class GenerateUniqueIDforAtom {

	private static long ID = 0;

	public static long getID() {
		return ID++;
	}
	
	public static void reset(){
		ID=0;
	}

}