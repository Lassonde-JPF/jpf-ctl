package exampleClasses;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.RepeatedTest;

class AccountTest {

	private static final int TRIALS = 1000;

	@RepeatedTest(TRIALS)
	void test() {
		final int INITIAL = 1;
		final int DEPOSIT = 0;
		final int WITHDRAW = 2;
		
		Account account = new Account();
		account.deposit(INITIAL);
		for (int d = 0; d < DEPOSIT; d++) {
			new Deposit("d" + d, account).start();
			assertTrue(account.getBalance() >= 0);
		}
		for (int w = 0; w < WITHDRAW; w++) {
			new Withdraw("w" + w, account).start();
			assertTrue(account.getBalance() >= 0);
		}
	}
}
