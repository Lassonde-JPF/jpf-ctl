package labels;

import java.lang.annotation.AnnotationFormatError;
import java.util.Random;
import java.util.zip.DataFormatException;

public class ReflectionExamples {
	public static boolean one = true;
	public static boolean two = true;
	public static boolean three = false;

	static boolean value;

	@SuppressWarnings("unused")
	public static void main(java.lang.String[] args) {
		boolean variable = false;

		Random random = new Random();
		if (random.nextBoolean()) {
			setValue(true);
		}
		
		if (random.nextBoolean()) {
			ReflectionExamples.three = false;
			ReflectionExamples.two = true;
		} else {
			ReflectionExamples.three = true;
			ReflectionExamples.two = false;
		}
		
		variable = true;
		setValueSynchronized(true);

		try {
			if (random.nextBoolean()) {
				throw new DataFormatException("exception");
			} else {
				throw new AnnotationFormatError("error");
			}
		} catch (Throwable e) {
			// Do nothing
		}
		
		ReflectionExamples instance = new  ReflectionExamples();
		instance.getRandom ();
	}

	public static void setValue(boolean x) {
		value = x;
	}

	public synchronized static void setValueSynchronized(boolean x) {
		value = x;
	}
	
	public  boolean  getRandom () {
		Random  random = new  Random ();
		return  random.nextBoolean ();
	}
}
