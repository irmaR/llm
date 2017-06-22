package hybrid.parameters;

import hybrid.network.StringValue;
import hybrid.network.Value;

import java.io.Serializable;
import java.util.List;

public class ExtraInfoDavide extends ExtraInfo implements Serializable {

	private List<Value> seenLabels;
	private List<Value> unseenLabels;
	private List<Value> modelLoaded;
	private Double probabilityUnseen;
	private Double W;
	
	
	
	
	public ExtraInfoDavide(List<Value> seenLabels,
			List<Value> unseenLabels,List<Value> modelLoaded, Double probabilityUnseen, Double w) {
		super();
		this.seenLabels = seenLabels;
		this.unseenLabels = unseenLabels;
		this.probabilityUnseen = probabilityUnseen;
		this.modelLoaded=modelLoaded;
		W = w;
	}
	public List<Value> getSeenLabels() {
		return seenLabels;
	}
	public void setSeenLabels(List<Value> seenLabels) {
		this.seenLabels = seenLabels;
	}
	public List<Value> getUnseenLabels() {
		return unseenLabels;
	}
	public void setUnseenLabels(List<Value> unseenLabels) {
		this.unseenLabels = unseenLabels;
	}
	public Double getProbabilityUnseen() {
		return probabilityUnseen;
	}
	public void setProbabilityUnseen(Double probabilityUnseen) {
		this.probabilityUnseen = probabilityUnseen;
	}
	public Double getW() {
		return W;
	}
	public void setW(Double w) {
		W = w;
	}
	@Override
	public String toString() {
		return "ExtraInfoDavide [seenLabels=" + seenLabels + ", unseenLabels="
				+ unseenLabels + ", probabilityUnseen=" + probabilityUnseen
				+ ", W=" + W + "]";
	}
	public List<Value> getModelLoaded() {
		return modelLoaded;
	}
	public void setModelLoaded(List<Value> modelLoaded) {
		this.modelLoaded = modelLoaded;
	}
	
	
	
	
}
