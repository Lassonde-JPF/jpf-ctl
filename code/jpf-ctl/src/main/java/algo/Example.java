package algo;

import java.util.Random;

public class Example {
	private static boolean one = true;
	private static boolean two = true;
	private static boolean three = false;

	public static void main(String [] args) {
		Random random = new Random ();
		if (random.nextBoolean ()) {
			three = false;
			two = true;
		} else {
			three = true;
			two = false;
		}
	}
}
