package hybrid.network;

public class LogvarRestrictionLiteral extends Literal{
    private Logvar logvar1;
    private Logvar logvar2;
	private String restriction;
	
	public LogvarRestrictionLiteral(String restriction,Logvar logvar1,Logvar logvar2){
       this.restriction=restriction;
       this.logvar1=logvar1;
       this.logvar2=logvar2;
	}
	
	@Override
	public  String createFOLTerm(){
		return this.toString();
	}

	public String toString(){
		return logvar1+this.restriction.toString()+logvar2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((logvar1 == null) ? 0 : logvar1.hashCode());
		result = prime * result + ((logvar2 == null) ? 0 : logvar2.hashCode());
		result = prime * result
				+ ((restriction == null) ? 0 : restriction.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogvarRestrictionLiteral other = (LogvarRestrictionLiteral) obj;
		if (logvar1 == null) {
			if (other.logvar1 != null)
				return false;
		} else if (!logvar1.equals(other.logvar1))
			return false;
		if (logvar2 == null) {
			if (other.logvar2 != null)
				return false;
		} else if (!logvar2.equals(other.logvar2))
			return false;
		if (restriction == null) {
			if (other.restriction != null)
				return false;
		} else if (!restriction.equals(other.restriction))
			return false;
		return true;
	}
	
	
	



}
