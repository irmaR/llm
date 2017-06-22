package hybrid.interpretations;

import hybrid.network.NetworkInfo;

import java.io.IOException;

public interface DataLoader {

	public Data loadData(String pathToFiles, String name, String extension, NetworkInfo ntw,DataType dt) throws IOException;
	
}
