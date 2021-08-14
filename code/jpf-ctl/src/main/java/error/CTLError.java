package error;

import java.util.HashSet;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

/**
 * A class to verify the input formula and recover it
 * 
 * @author Anto Nanah Ji
 *
 */
public class CTLError {
		// Hash set of operators 
		HashSet<Character> operatorsSet = new HashSet<>();
		/**
		*  Inserts the missing operators '&' and '|' into operatorsSet Hash set.
		*  
		*/
		public CTLError() {
				// adding missing operators to the hash set
				operatorsSet.add('&');
				operatorsSet.add('|');
		}		
		
		/**
		 * This method verifies the input formula if it contains '&' or '|' characters.
		 * It also recovers from the errors and returns the recovered input.
		 * 
		 * @return  recovered input if there is an error, the input formal otherwise.
		 */
		public  CharStream errorCheckAndRecover(CharStream input) 
		{
			//the resulted input
			StringBuilder res = new StringBuilder();
			String inputString = input.toString();
			//Split the input string by lines
			String[] lines = inputString.split("\r\n|\r|\n");
			int size = lines.length;
			//Verify if every line in the input contains '&' or '|' characters by calling the helper method
			for (int i = 0; i < size; i++) {
				if(i > 0)
				{
					res.append("\n");
				}
				res.append(errorCheckAndRecoverHelper(lines[i], i+1));
			}
			
			//if the resulted String from the helper method != input String, then print the error message
			if(!res.toString().equals(inputString))
			{
				System.out.print("\nInitial   input: " + inputString + "\n" + "Recovered input: " + res.toString() + "\n\n");
			}
			return CharStreams.fromString(res.toString());		
		}
		/**
		 * This method verifies the input formula if contains '&' or '|' characters.
		 * It also recovers from the errors and returns the recovered input.
		 * 
		 * @param inString - the input formula.
		 * @param lineNum - line number in the input formula
		 * @return recovered input if there is an error, the input formal otherwise. 
		 */
		public CharStream errorCheckAndRecoverHelper(String inString, int lineNum) 
		{
			//the recovered input
			StringBuilder recovedInput = new StringBuilder();				
			int size = inString.length();
			//the error character index in the input string
			int index = 0;			
			boolean hasError = false;
			
			for (int i = 0; i < size ; i++) 
			{
				//check and recover if the input formula missing the operators '&' or '|'
				
				if (operatorsSet.contains(inString.charAt(i)) ) 
				{
					if( ( i == 0    && !operatorsSet.contains(inString.charAt(i + 1)) ) ||
						( i == size - 1 && !operatorsSet.contains(inString.charAt(i - 1)) ) ||
						(!operatorsSet.contains(inString.charAt(i + 1)) && !operatorsSet.contains(inString.charAt(i - 1)))
					   ) 
					{
						hasError = true;
						//call underLineError method to print the error message
						underLineError( inString, index,lineNum, " token recognition error at: '"+ inString.charAt(i) + " '" );
						index += 1;
						recovedInput.append(inString.charAt(i));
						recovedInput.append(inString.charAt(i));
					}
				}			
				else 
				{				
					recovedInput.append(inString.charAt(i));	
					index += 1;
				}			
			}	
			
			//if there is error print message on the console and return the recovered input
			if(hasError)
			{
				return CharStreams.fromString(recovedInput.toString());
			}
			
			return CharStreams.fromString(inString);		
		}
		
		/**
		 * This method prints the error messages on the console.
		 * It also underlines the error location in the input.
		 * 
		 * @param errorLine	- input formula that contains error.
		 * @param charPositionInLine - error location in the input.
		 * @param errorMsg - error message.
		 */
		private void underLineError(String inString, int charPositionInLine, int lineNum, String errorMsg )
		{
			StringBuilder errMsg = new StringBuilder();
			
			errMsg.append("\nline "+ lineNum +":"+ (charPositionInLine + 1) + errorMsg + "\n");
			errMsg.append(inString+ "\n");
			//To underlines the error location
			for(int i=0; i<charPositionInLine; i++)
				errMsg.append(" ");
			
			errMsg.append("^"+ "\n");
			
			System.err.print(errMsg.toString() );
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
