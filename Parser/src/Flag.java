import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Flag {
	
	public ArrayList<Float> steps;
	public ArrayList<Float> accs;
	public ArrayList<Integer> times;
	
	public Flag()
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
	
	public void calculateError(int flag)
	{
		ArrayList<Float> inside = new ArrayList<Float>();//guarda valores com flag a 1.
		System.out.println("Size "+ steps.size());
		for(int i=0; i<steps.size();i++)
		{
			if(accs.get(i)==flag)
			{
				inside.add(steps.get(i));
			}
		}
		if(inside.size()==0)
		{
			System.out.println("No step matched the requirements");
		}
		else
		{
			float bestStep = inside.get(0);
			int bestStepTime = times.get(steps.indexOf(inside.get(0)));
			for (int i=0;i<inside.size();i++)
			{
				if(times.get(steps.indexOf(inside.get(i)))<bestStepTime)
				{
					bestStep = inside.get(i);
					bestStepTime = times.get(steps.indexOf(inside.get(i)));
				}
			}
			System.out.println("The best one is STEP = "  +bestStep + " ,with a time of " +bestStepTime +" ms.");
		}
	}
}
