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

            
            
            										
            
        	// Rename it so you don't have to create differently named classes to run again.
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
            
            try{
            	// Rename original file to original name, replacing intermediate generated files
            	Path secondpath = Paths.get("Test2.java");
                Files.move(secondpath, secondpath.resolveSibling("Test.java"), StandardCopyOption.REPLACE_EXISTING);
            }
            catch(Exception e){
            	throw new RuntimeException("Error! File may have been renamed to <filename>2.java and not sucessfully renamed back!");
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
            if (n.getName().equals("main")) {          	
                            	
            	for(int i=0; i<n.getBody().getAllContainedComments().size();i++){
            		
            		System.out.println(n.getBody().getAllContainedComments().get(i));
            		
            		parsingString = n.getBody().getAllContainedComments().get(i).getContent().split(" ");
            		if(parsingString[0].toUpperCase().equals("@PRAGMA") && parsingString[1].toUpperCase().equals("TUNER")){
            			
            			String command = parsingString[2].toUpperCase();
            			startLine=n.getBody().getAllContainedComments().get(i).getBeginLine();
            			switch(command){
            			
            			case "EXPLORE":
            				explore(n);
            				break;
            				
            			default: System.out.println("Command "+ command +" not supported.");
            			
            			}
            		}
            			 
            	}
               
            }

        }
        
        private void explore(MethodDeclaration n){
        	int min=Integer.parseInt(parsingString[3].split("\\(")[1].split(",")[0]);
        	int max=Integer.parseInt(parsingString[4].split("\\)")[0]);
        	for(int i=min;i<=max;i++){
        		initializeVariable(n,i);
        		//printOutputCode();
        		writeToOutputFiles();
        		runCreatedFile();
        	}
        	
        }
        
        private void initializeVariable(MethodDeclaration n, int currentNum){
        	
        	List<Statement> list= new ArrayList<Statement>();
        	
        	Type type = new PrimitiveType (Primitive.Int);
        	
        	IntegerLiteralExpr integer = new IntegerLiteralExpr(currentNum+"");
        	
        	VariableDeclarator var = new VariableDeclarator(new VariableDeclaratorId(parsingString[3].split("\\(")[0]),integer);
        	
        	List<VariableDeclarator> args = new ArrayList<VariableDeclarator>();
        	
        	args.add(var);
        	
        	Expression expr = new VariableDeclarationExpr(type,args);
        	
        	Statement asserts = new ExpressionStmt(expr);
        	
        	list=n.getBody().getStmts();
        	
        	for(int i=0;i<list.size();i++){
        		if(list.get(i).getBeginLine()==startLine+1){
        			list.add(i, asserts);
        			break;
        		}
        		if(list.get(i).toString().equals("int "+parsingString[3].split("\\(")[0]+" = "+(currentNum-1)+";")){
        			list.remove(i);
        			list.add(i, asserts);
        			break;
        		}
        	}
        	//list.add(asserts);
        	
        	n.getBody().setStmts(list);
        }
        
        private void printOutputCode(){
        	// prints the resulting compilation unit to default system output
            System.out.println(cu.toString());
        }
        
        
        // Writes to Intermediate generated files (Named <classname>.java) one at a time, replacing the previous one
        private void writeToOutputFiles() {
        	try{
        		PrintWriter writer = new PrintWriter("Test.java", "UTF-8");
        	    writer.println(cu.toString());
        	    writer.close();
        	} catch (IOException e) {
        	   // do something
        		System.out.println("Error writing to files!");
        		System.exit(0);
        	}
        	
        }
        
        
        // Runs, one by one, the generated files on writeToOutputFiles()
        private void runCreatedFile(){
        	try{
        		//Process compile = Runtime.getRuntime().exec("javac pragmaf"+(file_num-1)+".java");
        		//Process execute = Runtime.getRuntime().exec("java pragmaf"+(file_num-1));
        		//runProcess("javac pragmaf"+(file_num-1)+".java");
        		runProcess("javac Test.java");
        		runProcess("java Test");
        	}catch(Exception e ){
        		System.out.println("Error on running generated test files!");
        	}
        }
        
        //http://stackoverflow.com/questions/4842684/how-to-compile-run-java-program-in-another-java-program
        private static void runProcess(String command) throws Exception {
            Process pro = Runtime.getRuntime().exec(command);
            printLines(command + " stdout:", pro.getInputStream());
            printLines(command + " stderr:", pro.getErrorStream());
            pro.waitFor();
            System.out.println(command + " exitValue() " + pro.exitValue());
          }
        
        //http://stackoverflow.com/questions/4842684/how-to-compile-run-java-program-in-another-java-program
        private static void printLines(String name, InputStream ins) throws Exception {
            String line = null;
            BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
            while ((line = in.readLine()) != null) {
                System.out.println(name + " " + line);
            }
          }
        
        
    }
}
