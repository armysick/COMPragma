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


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


public class JavaParserTester {

    public static void main(String[] args) {
        try {

            // creates an input stream for the file to be parsed
            FileInputStream in = new FileInputStream("Test.java");

            // parse the file
            CompilationUnit cu;
            cu = JavaParser.parse(in);

            // visit and change the methods names and parameters
            new MethodChangerVisitor().visit(cu, null);

            // prints the resulting compilation unit to default system output
            System.out.println(cu.toString());
        } catch (ParseException e) {
            throw new RuntimeException("Error message:\n", e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error message:\n", e);
        }

    }

    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private static class MethodChangerVisitor extends VoidVisitorAdapter<Void> {

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (n.getName().equals("main")) {          	
                // change the name of the method to upper case
                //n.setName("test2");
            	for(int i=0; i<n.getBody().getAllContainedComments().size();i++){
            		String[] str = n.getBody().getAllContainedComments().get(i).getContent().split(" ");
            		if(str[0].toUpperCase().equals("@PRAGMA"))
            			 System.out.println("FOUND A PRAGMA:" + n.getBody().getAllContainedComments().get(i).getContent());
            	}
               
            }

        }
    }
}
