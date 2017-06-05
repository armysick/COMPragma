import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
	
	public void calculateError(int flag, String filename)
	{
		ArrayList<Float> inside = new ArrayList<Float>();//guarda valores com flag a 1.
		System.out.println("Size "+ steps.size());
		
		
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
					
					try {
						//Files.write(file,(inside.get(i)+", a value of: " + accs.get(steps.indexOf(inside.get(i))) +" and with a time of "+times.get(steps.indexOf(inside.get(i)))+" ms" + "\n").getBytes(), StandardOpenOption.APPEND);
						bw.write(inside.get(i)+", a value of: " + accs.get(steps.indexOf(inside.get(i))) +" and with a time of "+times.get(steps.indexOf(inside.get(i)))+" ms\r\n");
						bw.newLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
			try {
				//Files.write(file,("The best one is STEP = "  +bestStep + " ,with a time of " +bestStepTime +" ms.\n").getBytes(), StandardOpenOption.APPEND);
				bw.write("The best one is STEP = "  +bestStep + " ,with a time of " +bestStepTime +" ms.\r\n");
				bw.newLine();
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		
		}
	}
}
