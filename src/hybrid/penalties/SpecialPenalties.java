package hybrid.penalties;

public interface SpecialPenalties<P> {

	
	public abstract double scalePenalty(P p,Double currentPenalty);
}
