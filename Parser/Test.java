public class Test {
	
    public static void main(String[] args) {
    	
		int acc = 2;
		int STEP = 0;
		int flg = 0;
        //@pragma tuner explore STEP(1, 10) reference(STEP=1)
		for(int x = 0 ; x < 10000 ; x++){
			for(int i = 0; i < Math.exp(STEP) ; i++){
				acc +=STEP;
			}
		}
		if(acc % 2 == 0)
			flg = 1;
		else
			flg = 0;
		//@pragma tuner end flag flg 1
    }
}

