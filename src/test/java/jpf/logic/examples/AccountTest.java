
/*
 * Copyright (C)  2022
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package jpf.logic.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

/**
 * Tests the classes Account, Deposit, and Withdraw.
 *
 * @author Matt Walker
 */
class AccountTest {

	/**
	 * Number of times that the test is repeated.
	 */
	private static final int TRIALS = 1000;

	/**
	 * Tests the classes Account, Deposit, and Withdraw.
	 */
	@RepeatedTest(TRIALS)
	void test() {
		final int INITIAL = 1;
		final int DEPOSIT = 2;
		final int WITHDRAW = 2;
		
		Account account = new Account();
		account.deposit(INITIAL);
		for (int d = 0; d < DEPOSIT; d++) {
			new Deposit("d" + d, account).start();
			Assertions.assertTrue(account.getBalance() >= 0);
		}
		for (int w = 0; w < WITHDRAW; w++) {
			new Withdraw("w" + w, account).start();
			Assertions.assertTrue(account.getBalance() >= 0);
		}
	}
}
