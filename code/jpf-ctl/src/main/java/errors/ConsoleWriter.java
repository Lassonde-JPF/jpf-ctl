package errors;

/**
 * A class to print the console errors and messages in synchronized way.
 * 
 */
public class ConsoleWriter {
	
	 public synchronized void printout(String message) {
	        System.out.print(message);
	 }

	 public synchronized void printerr(String message) {
	        System.err.print(message);
	 }
	 public synchronized void printlnout(String message) {
	        System.out.println(message);
	 }

	 public synchronized void printlnerr(String message) {
	        System.err.println(message);
	 }
}
