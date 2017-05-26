
public class Result {

	private long time_elapsed;
	private double acc;
	private double STEP;
	
	public Result(){
		
	}
	
	public Result(double STEP, double acc){
		this.STEP = STEP;
		this.acc = acc;
	}
	
	/* DEPRECATED
	public Result(long time_elapsed, double acc, double STEP){
		
	}*/
	
	public void addTime(long time){
		time_elapsed = time;
	}
	
	
	public void PrintResult(){
		
		System.out.println("-----------------------------");
		System.out.println("STEP : " + this.STEP + " \t \t \t ACC: " + this.acc + " \t \t \t TIME: " + this.time_elapsed);
		System.out.println("-----------------------------");
	}
	
	public double getSTEP(){
		return this.STEP;
	}
	
	public double getAcc(){
		return this.acc;
	}
	
	public long getTimeElapsed(){
		return this.time_elapsed;
	}
	
	
}
