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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
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
						String command = parsingString[2].toUpperCase();
						startLine = n.getBody().getAllContainedComments().get(i).getBeginLine();
						switch (command) {

						case "EXPLORE":
							explore(n, var_name, max_abs_error);
							var_name = null; // Set to null, so it doesn't
												// "carry over"
												// to the next explore.
							break;

						case "MAX_ABS_ERROR":
							var_name = parsingString[3];
							max_abs_error = Integer.parseInt(parsingString[4]);
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
			for (int i = min; i <= max; i++) {
				initializeVariable(n, i);
				// printOutputCode();
				writeToOutputFiles(var_name, max_abs_error);
				runCreatedFile();
			}
			System.out.println("Done!");
		}

		private void initializeVariable(MethodDeclaration n, int currentNum) {

			List<Statement> list = new ArrayList<Statement>();

			Type type = new PrimitiveType(Primitive.Int);

			IntegerLiteralExpr integer = new IntegerLiteralExpr(currentNum + "");

			VariableDeclarator var = new VariableDeclarator(new VariableDeclaratorId(parsingString[3].split("\\(")[0]),
					integer);

			List<VariableDeclarator> args = new ArrayList<VariableDeclarator>();

			args.add(var);

			Expression expr = new VariableDeclarationExpr(type, args);

			Statement asserts = new ExpressionStmt(expr);

			list = n.getBody().getStmts();

			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getBeginLine() == startLine + 1) {
					list.add(i, asserts);
					break;
				}
				if (list.get(i).toString()
						.equals("int " + parsingString[3].split("\\(")[0] + " = " + (currentNum - 1) + ";")) {
					list.remove(i);
					list.add(i, asserts);
					break;
				}
			}
			// list.add(asserts);

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
				
				//System.out.println("full str: \n " + full_string);
				
				PrintWriter writer = new PrintWriter("Test.java", "UTF-8");
				// writer.println(cu.toString());
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
				runProcess("C:\\Program Files (x86)\\Java\\jdk1.8.0_101\\bin\\javac Test.java");
				runProcess("java Test");
			} catch (Exception e) {
				System.out.println(
						"Error on running generated test files! Please make sure you have javac and java installed and has a valid path!");
			}
		}

		// http://stackoverflow.com/questions/4842684/how-to-compile-run-java-program-in-another-java-program
		private static void runProcess(String command) throws Exception {
			Process pro = Runtime.getRuntime().exec(command);
			printLines(command + " stdout:", pro.getInputStream());
			printLines(command + " stderr:", pro.getErrorStream());
			pro.waitFor();
			System.out.println(command + " exitValue() " + pro.exitValue());
		}

		// http://stackoverflow.com/questions/4842684/how-to-compile-run-java-program-in-another-java-program
		private static void printLines(String name, InputStream ins) throws Exception {
			String line = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(ins));
			while ((line = in.readLine()) != null) {
				System.out.println(name + " " + line);
			}
		}

		
		// Adds the other comment (final comment) + Prints the variable !
		private static String getFullString(String unedited_string, String var_name, int max_abs_error) {
			List<Comment> orphans = cu.getAllContainedComments();
			Comment orphan = orphans.get(0);
			String[] unedited_split = unedited_string.split("\n");
			int line_nr = orphan.getEndLine();
			String[] appended_split = new String[unedited_split.length + 2];
			for (int z = 0; z < unedited_split.length + 2; z++) {
				if (z == line_nr)
					appended_split[z] = "//" + orphan.getContent();
				else if (z == line_nr+1)
					appended_split[z] = "System.out.println(\"acc: \" + " + var_name + " );";
				else if (z >= line_nr + 2)
					appended_split[z] = unedited_split[z - 2];
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

	}
}
