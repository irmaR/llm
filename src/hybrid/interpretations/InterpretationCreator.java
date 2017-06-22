package hybrid.interpretations;

import hybrid.network.NetworkInfo;

/**
 * Interface for creating an interpretation for a network ntw, 
 * path to a specific input data file and querying engine P
 * @author irma
 *
 * @param <P>
 */
public interface InterpretationCreator {
    /**
     * 
     * @param ntw
     * @param pathToFile
     * @param data_type - test, train or validation
     * @return
     */
	public Interpretation createInterpretation(NetworkInfo ntw,String pathToFile,String data_type);	
}
