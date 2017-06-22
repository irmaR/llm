package hybrid.comparators;

import java.io.Serializable;
import java.util.List;

import hybrid.network.Value;
import hybrid.utils.Bin;

/**
 * Abstract comparator
 * @author irma
 *
 */
public interface Comparator extends Serializable{
  
	public abstract boolean compare(Value v);
}
