package hybrid.comparators;

import hybrid.utils.Bin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ThresholdGenerator {

	public List<Comparator> getThresholdsBigger(List<Bin> bins) {
		List<Comparator> tmp=new ArrayList<Comparator>();
		for(Bin b:bins){
			if(b.getFrom()!=Double.NEGATIVE_INFINITY){
				Double truncatedDouble=new BigDecimal(b.getFrom() ).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
				tmp.add(new Bigger(truncatedDouble));
			}

		}
		return tmp;
	}


	public List<Comparator> getThresholdsSmaller(List<Bin> bins) {
		List<Comparator> tmp=new ArrayList<Comparator>();
		for(Bin b:bins){
			if(b.getTo()!=Double.POSITIVE_INFINITY){
				Double truncatedDouble=new BigDecimal(b.getTo()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
				tmp.add(new Smaller(truncatedDouble));
			}

		}
		return tmp;
	}

	public List<Comparator> getThresholdsInBetween(List<Bin> bins) {
		List<Comparator> tmp=new ArrayList<Comparator>();
		for(Bin b:bins){
			if(b.getTo()==Double.POSITIVE_INFINITY || b.getFrom()==Double.NEGATIVE_INFINITY){
				continue;
			}
			Double truncatedFrom=new BigDecimal(b.getFrom()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
			Double truncatedTo=new BigDecimal(b.getTo()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

			tmp.add(new InBetween(truncatedFrom,truncatedTo));

		}
		return tmp;
	}

}
