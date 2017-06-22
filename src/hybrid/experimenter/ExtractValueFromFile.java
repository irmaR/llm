package hybrid.experimenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ExtractValueFromFile {

	public String getValueOfLineContainingString(File file,String string,String delimiter,int column) throws IOException{
		BufferedReader br=new BufferedReader(new FileReader(file));

		while(true){
			String line=br.readLine();
			if(line==null){
				break;
			}
			else{
				if(line.contains(string)){
					String[] l=line.split(delimiter);
					return l[column].replace(",","");
				}
			}
		}
		return null;
	}

}
