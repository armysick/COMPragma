public class Example3{
	
    public static void main(String[] args) {
    	
		int acc = 2;
		int STEP = 0;
		int flaggerina = 0;
        //@pragma tuner steepdesc STEP(1, 10) reference(STEP=5)
		for(int x = 0 ; x < 10*STEP ; x++){
			if(Math.random() < 0.5)
				acc +=2;
			else
				acc+=1;
		}
		
		if(acc%2 == 0){
			flaggerina = 1;
		}
		else{
			flaggerina = 0;
		}
		//@pragma tuner end flag flaggerina 1 
    }
}

