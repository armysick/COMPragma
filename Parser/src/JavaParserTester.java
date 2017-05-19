/**
 * Copyright 2017 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class JavaParserTester {

	private static String[] parsingString;
	private static CompilationUnit cu;
	private static int startLine;
	private static List<SimpleEntry<Double,Double>> results = new ArrayList<SimpleEntry<Double,Double>>();
	private static double currentNo;
	private static long timeBegin, timeEnd;
	
	private static long[][] results_array;
	private static int aux_index = 0;
	
	public static void main(String[] args) {
		try {

			// Rename it so you don't have to create differently named classes
			// to run again.
			// In the end of the execution it shall be reset to the original
			Path ogpath = Paths.get("Test.java");
			Files.move(ogpath, ogpath.resolveSibling("Test2.java"));

			// creates an input stream for the file to be parsed
			FileInputStream in = new FileInputStream("Test2.java");
			// parse the file
			cu = JavaParser.parse(in);
			// visit and change the methods names and parameters
			new MethodChangerVisitor().visit(cu, null);

			in.close();

			try {
				// Rename original file to original name, replacing intermediate
				// generated files
				Path secondpath = Paths.get("Test2.java");
				Files.move(secondpath, secondpath.resolveSibling("Test.java"), StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				throw new RuntimeException(
						"Error! File may have been renamed to <filename>2.java and not sucessfully renamed back!");
			}

		} catch (ParseException e) {
			throw new RuntimeException("Error message:\n", e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error message:\n", e);
		} catch (IOException e) {
			throw new RuntimeException("Error changing file name\n", e);
		}

		FileOutputStream out;
		Path file = null;
		try {
			out = new FileOutputStream("results.txt");
			file = Paths.get("results.txt");
			out.write("".getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(SimpleEntry p:results){
			
			try {
				Files.write(file, (p.getKey()+ "||" + p.getValue()+" __ ").getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				System.out.println("ERROR WRITING TO FILE");
				e.printStackTrace();
			}
			System.out.println(p.getKey()+ " " + p.getValue());
		}
	}

	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes.
	 */
	private static class MethodChangerVisitor extends VoidVisitorAdapter<Void> {

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			String var_name = null; // Variable to compare [acc in our example]
			int max_abs_error = -420691337; // maximum absolute error!
			if (n.getName().equals("main")) {

				// This here actually searches from bottom up, which lets us
				// First store a value (expected result) and only then calculate
				// it.
				for (int i = 0; i < n.getBody().getAllContainedComments().size(); i++) {

					System.out.println(n.getBody().getAllContainedComments().get(i));

					parsingString = n.getBody().getAllContainedComments().get(i).getContent().split(" ");

					/*
					 * DEBUG System.out.println("Parsing string " + i); for(int
					 * x = 0; x < parsingString.length; x++)
					 * System.out.println("Parstr line:  " + parsingString[x]);
					 */

					if (parsingString[0].toUpperCase().equals("@PRAGMA")
							&& parsingString[1].toUpperCase().equals("TUNER")) {
						String command;
						if(!parsingString[2].toUpperCase().equals("END")){
							command= parsingString[2].toUpperCase();
						}
						else command =  parsingString[3].toUpperCase();
						
						startLine = n.getBody().getAllContainedComments().get(i).getBeginLine();
						switch (command) {

						case "EXPLORE":
							
							double reference = Double.parseDouble(parsingString[5].split("=")[1].substring(0, parsingString[5].split("=")[1].length()-1));
							System.out.println("REFERENCIA: "+ reference);
							explore(n, var_name, max_abs_error);
							var_name = null; // Set to null, so it doesn't
												// "carry over"
												// to the next explore.
							break;

						case "MAX_ABS_ERROR":
							var_name = parsingString[4];
							max_abs_error = Integer.parseInt(parsingString[5]);
							System.out.println("\nmax_abs_error:\n var_name received: " + var_name
									+ "\n max_err_received: " + max_abs_error);
							break;
						default:
							System.out.println("Command " + command + " not supported.");

						}
					}

				}

			}

		}

		private void explore(MethodDeclaration n, String var_name, int max_abs_error) {
			if (var_name == null || max_abs_error == -420691337) {
				System.out.println("No end Pragma condition was given! :( ");
				return;
			}

			int min = Integer.parseInt(parsingString[3].split("\\(")[1].split(",")[0]);
			int max = Integer.parseInt(parsingString[4].split("\\)")[0]);
			
			aux_index = 0;
			results_array = new long[(max-min)+1][10];
			
			for (currentNo = min; currentNo <= max; currentNo++) {
				try {
					initializeVariable(n);
				} catch (Exception e) {
					return;
				}			
				writeToOutputFiles(var_name, max_abs_error);
				runCreatedFile();
				aux_index++;
			}
			System.out.println("Done!");
		}
		
		private String getVariableType(List<Statement> list, String variableName){
			for(int i = 0; i < list.size(); i++){
				if(list.get(i).getChildrenNodes().get(0).getChildrenNodes().get(1).toString().equals(variableName)){
					String type = list.get(i).getChildrenNodes().get(0).getChildrenNodes().get(0).toString();
					if(type.equals("int")||type.equals("double")||type.equals("float")){
						return type;
					}
					else if(!list.get(i).getChildrenNodes().get(0).toString().matches("[\\w\\s]*[*+-/]?=[\\w\\s]*" + variableName)) return "erro";
				}
			}
			return "";
		}

		private Expression initializeExpression(String type){
			Expression expr = null;
			switch(type){
			case "int" : 
				expr = new IntegerLiteralExpr((int)currentNo + "");
				break;
			case "double" : 
				expr = new DoubleLiteralExpr(currentNo + "");
				break;
			case "float" : 
				expr = new DoubleLiteralExpr(currentNo + "");
				break;
			}
			return expr;
		}
		
		private void initializeVariable(MethodDeclaration n) throws Exception {

			List<Statement> list = new ArrayList<Statement>();
			Expression exprAdded = null;
			list = n.getBody().getStmts();
			String type = getVariableType(list,parsingString[3].split("\\(")[0]);
			
			if(type.equals("erro")){
				//caso nao seja um tipo válido
				System.out.println("Wrong type of variable declared in pragma. Only int/double/float are allowed!");
				Exception e = new Exception();
				throw e;
			}else if(!type.equals("")){
				//caseo ja esteja inicializado
				Expression initialized = initializeExpression(type);
				exprAdded = new AssignExpr(new NameExpr(parsingString[3].split("\\(")[0]),initialized,Operator.assign);
			}else{
				//se não estiver inicializado
				Type typeOfVariable = new PrimitiveType(Primitive.Double);
				VariableDeclarator var = new VariableDeclarator(new VariableDeclaratorId(parsingString[3].split("\\(")[0]),new DoubleLiteralExpr(currentNo + ""));
				List<VariableDeclarator> args = new ArrayList<VariableDeclarator>();
				args.add(var);
				exprAdded = new VariableDeclarationExpr(typeOfVariable, args);
			}
			
			Statement asserts = new ExpressionStmt(exprAdded);
			
			constructCode(list, asserts,n);
		}
		
		//constroi e actualiza o codigo
		private void constructCode(List<Statement> list , Statement asserts, MethodDeclaration n){
			DecimalFormat format=new DecimalFormat("#,###.#");
			
			for (int i = 0; i < list.size(); i++) {
				
				if (list.get(i).getBeginLine() == startLine + 1) {
					list.add(i, asserts);
					break;
				}

				if (list.get(i).toString()
						.contains(parsingString[3].split("\\(")[0] + " = " + format.format(currentNo - 1) + ";") || list.get(i).toString()
						.contains(parsingString[3].split("\\(")[0] + " = " + (currentNo - 1) + ";")) {
					list.remove(i);
					list.add(i, asserts);
					break;
				}
			}

			n.getBody().setStmts(list);
		}

		private void printOutputCode() {
			// prints the resulting compilation unit to default system output
			System.out.println(cu.toString());
		}

		// Writes to Intermediate generated files (Named <classname>.java) one
		// at a time, replacing the previous one
		private void writeToOutputFiles(String var_name, int max_abs_error) {
			try {
				String unedited_string = cu.toString();
				
				String full_string = getFullString(unedited_string, var_name, max_abs_error);
				
				PrintWriter writer = new PrintWriter("Test.java", "UTF-8");
				writer.println(full_string);
				writer.close();
			} catch (IOException e) {
				// do something
				System.out.println("Error writing to files!");
				System.exit(0);
			}

		}

		// Runs, one by one, the generated files on writeToOutputFiles()
		private void runCreatedFile() {
			try {
				// Process compile = Runtime.getRuntime().exec("javac
				// pragmaf"+(file_num-1)+".java");
				// Process execute = Runtime.getRuntime().exec("java
				// pragmaf"+(file_num-1));
				// runProcess("javac pragmaf"+(file_num-1)+".java");
				runProcess("C:\\Program Files (x86)\\Java\\jdk1.8.0_101\\bin\\javac Test.java", false);
				long res;
				for(int u = 0; u < 10; u++){
					res = runProcess("java Test", true);
					ComputeToArray(res, u);
				}
				ComputeOnArray();
				
			} catch (Exception e) {
				System.out.println(
						"Error on running generated test files! Please make sure you have javac and java installed and has a valid path!");
			}
		}

		// http://stackoverflow.com/questions/4842684/how-to-compile-run-java-program-in-another-java-program
		private static long runProcess(String command, boolean save_timing) throws Exception {
			
			timeBegin = System.currentTimeMillis();
			Process pro = Runtime.getRuntime().exec(command);
			printLines(command + " stdout:", pro.getInputStream());  
			printLines(command + " stderr:", pro.getErrorStream()); 
			pro.waitFor();
			timeEnd = System.currentTimeMillis();
			if (save_timing)
			System.out.println("diferença de tempo = " + (timeEnd-timeBegin));
			System.out.println(command + " exitValue() " + pro.exitValue());
			return (timeEnd-timeBegin);
		}

		// http://stackoverflow.com/questions/4842684/how-to-compile-run-java-program-in-another-java-program
		private static void printLines(String name, InputStream ins) throws Exception {
			String line = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(ins));
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				if(line.split(":")[0].equals("420691337")){
					results.add(new SimpleEntry<Double,Double>(currentNo,Double.parseDouble(line.split(":")[1])));
					
				}
					
			}
		}

		
		// Adds the other comment (final comment) + Prints the variable !
		private static String getFullString(String unedited_string, String var_name, int max_abs_error) {
			List<Comment> orphans = cu.getAllContainedComments();
			
			Comment orphan=null;
			
			for(int i=0; i < orphans.size();i++){
				if(orphans.get(i).getContent().contains("@pragma tuner end"))
					orphan = orphans.get(i);
			}
			
			
			
			String[] unedited_split = unedited_string.split("\n");
			
			int line_nr = orphan.getEndLine();
			
			String[] test = cu.toStringWithoutComments().split("\n");
			
			String[] appended_split = new String[unedited_split.length + 2];

			line_nr=1+line_nr-(cu.getEndLine()-unedited_split.length-(unedited_split.length-test.length));
			//System.out.println(unedited_string);
			//System.out.println(cu.toStringWithoutComments());
			//System.out.println("yolo " + line_nr + " " + cu.getEndLine() + " " + unedited_split.length + " " + test.length);
			
			for (int z = 0; z < unedited_split.length + 2 ; z++) {
				if (z == line_nr-1)
					appended_split[z] = "//" + orphan.getContent();
				else if (z == line_nr)
					appended_split[z] = "System.out.println(\"420691337:\" + " + var_name + " );";
				else if (z >= line_nr + 1)
					appended_split[z] = unedited_split[z - 2];
				else if(z>unedited_split.length-1){
					appended_split[z] = unedited_split[z- (unedited_split.length-test.length)];
				}
				else
					appended_split[z] = unedited_split[z];
			} 
			
			/* DEBUG
			 * for (int t = 0; t < appended_split.length; t++)
			 *System.out.println("appended split " + t + " : " + appended_split[t]);
			 **/
			 
			//http://stackoverflow.com/questions/14957964/concatenating-elements-in-an-array-to-a-string
			StringBuilder strBuilder = new StringBuilder();
			for (int i = 0; i < appended_split.length; i++) {
			   strBuilder.append(appended_split[i]);
			   strBuilder.append("\n");
			}
			return strBuilder.toString();
			
		}
		
		private static void ComputeToArray(long res, int index){
			results_array[aux_index][index] = res;
		}
		
		private static void ComputeOnArray(){
			Arrays.sort(results_array[aux_index]);	
			
			long soma = 0;
			for(int x = 2; x < 8 ; x++)
				soma += results_array[aux_index][x];
		
			results_array[aux_index][0] = (soma/6);
			System.out.println("STEP " + (aux_index + 1) + ": " + results_array[aux_index][0]);
			
		}

	}
}
