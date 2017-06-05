import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class ResultAbsError {
	
	public ArrayList<Float> steps;
	public ArrayList<Float> accs;
	public ArrayList<Integer> times;
	
	public ResultAbsError()
	{
		steps = new ArrayList<Float>();
		accs = new ArrayList<Float>();
		times = new ArrayList<Integer>();
	}
	
	public void parseFile() throws FileNotFoundException
	{
		Scanner in = new Scanner(new FileReader("results.txt"));
		while(in.hasNext()) 
		{
			String string = in.nextLine();
			steps.add(Float.parseFloat(string.split(";;")[0]));
			accs.add(Float.parseFloat(string.split(";;")[1].split("__")[0]));
			times.add(Integer.parseInt(string.split(";;")[1].split("__")[1]));
		}
		in.close();
	}
	
	public void calculateError(int max_abs_error, double reference, String filename)
	{
		float ref = (float)reference;
		ArrayList<Float> inside = new ArrayList<Float>();//guarda valores que estao dentro do erro.
		float accReference = accs.get(steps.indexOf(ref));
		
		BufferedWriter bw = null;
		try {
			File fout = new File(filename);
			FileOutputStream fos = new FileOutputStream(fout);
		 
			bw = new BufferedWriter(new OutputStreamWriter(fos));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		
		for(int i=0; i<steps.size();i++)
		{
			if(Math.abs(accs.get(i)-accReference)<max_abs_error)
			{
				inside.add(steps.get(i));
			}
		}
		if(inside.size()==0)
		{
			System.out.println("No one matched the requirements");
		}
		else
		{
			float bestStep = inside.get(0);
			int bestStepTime = times.get(steps.indexOf(inside.get(0)));
			float bestAcc = accs.get(steps.indexOf(inside.get(0)));
			System.out.println("The valid are:");
			for (int i=0;i<inside.size();i++)
			{
				if(times.get(steps.indexOf(inside.get(i)))<bestStepTime)
				{
					bestStep = inside.get(i);
					bestStepTime = times.get(steps.indexOf(inside.get(i)));
					bestAcc = accs.get(steps.indexOf(inside.get(i)));
				}
				try {
					bw.write(inside.get(i)+", a value of: " + accs.get(steps.indexOf(inside.get(i))) +" and with a time of "+times.get(steps.indexOf(inside.get(i)))+" ms");
					bw.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				bw.write("\nTHE BEST ONE IS: "  +bestStep + ", a value of: " + bestAcc + " and with a time of " +bestStepTime +" ms.");
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
