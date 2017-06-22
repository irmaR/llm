package hybrid.features;

import alice.tuprolog.Term;

public class SelectorTermTuProlog extends SelectorTerm {

	private Term term;
	
	
	public SelectorTermTuProlog(Term term){
		this.term=term;
	}
	
	@Override
	public Object getTerm() {
		return term;
	}

	@Override
	public void setTerm(Object obj) {
		this.term=(Term)obj;
		
	}
	
}
