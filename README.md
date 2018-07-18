# COMPragma

** SUMMARY: 
Our tool is an autotuner for Java programs that determines the fastest execution time given a  constant,
 and makes it so that outcome is in the conditions specified.
To do this, the user may add some pragmas to a block of code, and then run our tool with that example in 
order to get the results, our tool will see that there is a pragma, run it through our grammar and if it passes, 
we start the pragma itself. After all this is done, the results are analysed and given to the user.
 
 
** EXECUTE: 
To run our tool you need to open cmd and run the jar: pragmatuner.jar with one of the following commands:
 
java -jar pragmatuner.jar run <outputfile>.txt  <inputfile>.java
 
<outputfile>.txt : name of the file to save the output values of the run.
<inputfile>.java : name of the java file to analyse.
 
Runs the tuner with the given arguments.
------------------------------------------------------------------------------------------------------
 
java -jar pragmatuner.jar help
 
Gives some info about the tuner and how to use it.
------------------------------------------------------------------------------------------------------
java -jar pragmatuner.jar faq
 
Frequently asked questions;
 
 
 
**DEALING WITH SYNTACTIC ERRORS: 
If there are any syntactic error in the pragmas, we stop the execution of the tool, outputting the errors. 
If there is a compile error related to the none initialization of the variable in the pragma, 
we initialize it with default type to double.
 
 
 
**SEMANTIC ANALYSIS:
We can separate our pragmas in two different types: “Mode” and “Condition”.
Any Mode pragma must be “closed” by a Condition one.
 
The Mode pragmas are the following:
    
-EXPLORE
    	//@pragma tuner explore Step(Min,Max) reference(Step=[Min,Max])
    
    	In this mode the tuner will explore a variable named Step, and repeat the code with the Var value going in a integer range between Min and Max, 
   	 giving out a reference base value inside that range.
   	 
-STEEPEST DESCEND
	//@pragma tuner steepdesc Step(Min,Max) reference(Step=[Min,Max])
    
	In this mode the tuner will first search for the fastest value and start exploring from there, starting in the middle value between Min and Max.
------------------------------------------------------------------------------------------------------------------
The Condition pragmas are the following:
 
-MAXIMUM ABSOLUTE ERROR
   	 //@pragma tuner end max_abs_error Acc X
 
	With this condition the user sets a maximum absolute error of X for the variable Acc, and only the values, with an absolute error of less than X , 
	compared to the reference corresponding value given in the Mode pragma are considered to the results.
 
-FLAG CONDITION
   	 //@pragma tuner end flag Acc Bin 
    
	Being Bin a 0 or a 1;
 
	This condition allows an int-format flag(0 or 1) to be tested. Thus, allowing only values that correspond of a flag with the value of Bin.
    
 
 
 
**INTERMEDIATE REPRESENTATIONS: 
All Intermediate Representations are handled by the included JavaParser. Its manual can be consulted here https://github.com/javaparser/javaparser/wiki/Manual . 
During the development process, it has been found that some functions have been deprecated and not correctly updated on the wiki, so take that documentation with 
a grain of salt.
 
 
**CODE GENERATION: 
The use of the above mentioned JavaParser tool brought some utility but also some limitations. Mainly, the way comments seem to be structured, 
they have to be associated with a statement or - at the very least and only sometimes - a token. 
Thus, if the pragma condition inserted has a blank line separating it from the closest statement or token, the tool will not identify it. 
Also, it presents some limitations in the way the insertion of new lines of code work. For example, the insertion of a print had to be manually made,
written directly on the file after a specific token, in order to work, for it has not been found a feasible way to insert such statement in the way necessary 
to turn the developed tool functional.
 
 
 
**OVERVIEW: 
As external tools, we used JavaParser https://github.com/javaparser/javaparser/wiki/Manual.
A relevant algorithm is the way the results are calculated. We run each iteration 10 times, 
store them in an array, sort it, remove the 2 results on the extremities (to ignore any low or high spikes) and calculate the average. 
That is our final execution duration.

 
 
**TESTSUITE AND TEST INFRASTRUCTURE:
 We don’t have any automated tests, we only did manual tests in order to test our code and see if the results were the correct.
 
**TASK DISTRIBUTION: 
JavaParser tool setup - José Gomes;
Steepest Descent mode - José Gomes;
Explore mode - José Gomes, Gustavo Pinto;
Flag end condition - Gustavo Pinto;
Max_Abs_Error end condition - José Gomes, Gustavo Pinto, Paulo Babo;
Results output - João Fidalgo, Paulo Babo;
 
 
**PROS: 
The most positive aspect of our tool is that it can save a lot of work analysing results,
and always presents best choice automatically, together with other feasible alternatives.
We believe this tool can shine in code sections that require lots of processing or high computation time.
Loops with at least hundreds of iterations, such as presented in optimization and search algorithms like A*, 
Depth-first search and Breadth-first search could benefit from this, as the fastest factors - or parameters - can be easily discovered.
Additionally, in an iteration heavy code section that must meet some condition, this tool will also be of great help, 
as it can select with the FLAG end mode solutions that satisfy it.
Lastly, steepest descent mode will enable faster possible results, although it may not be the optimal solution - inherent to a greedy-like algorithm.
 
In conclusion, we believe this tool can be of great help mainly on complex computationally heavy algorithms and that the way it is loosely 
designed allows for very smart and flexible uses.
 
 
**CONS: 
The most negative aspect of our tool is that it has a really limited number of pragmas and conditions, and may not satisfy all of the user needs. 
Probably with some more time we would be able to  add some more.
Also, the flexibility of the tool may also come as negative, as it requires some comprehensive understanding of the tool by the user in order to 
maximize the tool’s potential.
