package algo;

import java.util.Random;

public class Example {

	public static void main(String[] args) {
		try {
			first();
			throw new IndexOutOfBoundsException();
		} catch (Exception e) {
		}
	}

	public static void first() {
		try {
			Random random = new Random();
			second();
			if (random.nextBoolean()) {
				throw new RuntimeException();
			} else {
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
		}
	}

	public static void second() {
		try {
			throw new Exception();
		} catch (Exception e) {
		}
	}
}
