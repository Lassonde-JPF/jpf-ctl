package example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AccountTest {

	@Test
	void test() {
		final int TRIALS = 1000000;
		
		final int INITIAL = 1;
		final int DEPOSIT = 0;
		final int WITHDRAW = 2;
		
		for (int t = 0; t < TRIALS; t++) {
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
}
