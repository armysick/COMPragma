public class Test {
	
    public static void main(String[] args) {
    	
		int acc = 1;
		int OTHER = 0;
		int flg = 0;
        //@pragma tuner explore OTHER(1, 2) reference(OTHER=1)
		for(int x = 0 ; x < 10000 ; x++){
			for(int i = 0; i < Math.exp(OTHER) ; i++){
				acc +=OTHER;
			}
		}
		if(acc % 2 == 0)
			flg = 1;
		else
			flg = 0;
		//@pragma tuner end max_abs_error acc 5
    }
}

