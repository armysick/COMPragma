public class Test {

    public static void main(String[] args) {
		int acc = 2;
        //@pragma tuner explore STEP(1, 10) reference(STEP=1)
        acc *= STEP;
		//@pragma tuner max_abs_error acc 5
    }
}

