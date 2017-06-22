package hybrid.queryMachine;

public class CountMB {
		int count=0;
		
		
		public CountMB(){
			count=0;
		}
		
		public void incrementCount(){
			count++;
		}
		
		public int getCount(){
			return count;
		}
		
		public String toString(){
			return String.valueOf(count);
		}
		
}
