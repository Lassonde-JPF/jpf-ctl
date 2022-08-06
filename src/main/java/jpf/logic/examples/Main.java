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

/**
 * App that uses Account, Deposit, and Withdraw.  
 *
 * @author Franck van Breugel
 */
public class Main {

	/**
	 * Whether the balance of the account is negative.
	 */
	@SuppressWarnings("unused")
	private static boolean negative = false;
	
	/**
	 * Creates an account, deposits an amount into the account.
	 * Subsequently, creates a number of deposit and withdrawal transactions
	 * that each deposit and try to withdraw one.
	 * 
	 * @param args[0] the initial balance of the account
	 * @param args[1] the number of deposit transactions
	 * @param args[2] the number of withdrawal transactions
	 */
	public static void main(String[] args) {
		Account account = new Account();
		account.deposit(Integer.parseInt(args[0]));
		for (int d = 0; d < Integer.parseInt(args[1]); d++) {
			new Deposit("d" + d, account).start();
			negative = account.getBalance() < 0;
		}
		for (int w = 0; w < Integer.parseInt(args[2]); w++) {
			new Withdraw("w" + w, account).start();
			negative = account.getBalance() < 0;
		}
	}
}
