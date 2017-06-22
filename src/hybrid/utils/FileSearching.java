package hybrid.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileSearching {

	/**
	 * Tells if a document in the path contains a line starting with a string
	 * @param string
	 * @param path
	 * @return
	 * @throws FileNotFoundException 
	 */
	public boolean containsString(String string,String path) throws FileNotFoundException{
		try{
			Scanner scanner=new Scanner(new File(path));
			while(scanner.hasNextLine()){
                String line=scanner.nextLine();
				if(line.trim().startsWith(string)){
					return true;
				}
			}
			return false;
		}
		catch(FileNotFoundException e){
			return false;
		}

	}

	public double extractWpll(String path,String extract, int i) {
		try{
			Scanner scanner=new Scanner(new File(path));
			while(scanner.hasNextLine()){
                String line=scanner.nextLine();
				if(line.trim().startsWith(extract)){
					System.out.println(" LINE: "+line);
					return Double.valueOf(line.split(extract)[i].replaceAll(",",""));
				}
			}
		}
		catch(FileNotFoundException e){
			return Double.NaN;
		}
		return Double.NaN;
	}
}


