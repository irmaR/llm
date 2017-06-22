package hybrid.utils;

public class GenerateUIDForInterpretation {
	private static long ID = 0;

	public static long getID() {
		return ID++;
	}
	
	public static void reset(){
		ID=0;
	}
}
