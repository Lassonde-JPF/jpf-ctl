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
 * An account with a balance.
 * 
 * @author Franck van Breugel
 */
public class Account {
	private double balance;

	private static int numberOfAccounts = 0;

	/**
	 * Initializes the balance of this account to zero.
	 */
	public Account() {
		this.balance = 0;
		numberOfAccounts++;
	}

	/**
	 * Returns the number of created accounts.
	 *
	 * @return the number of created accounts
	 */
	public static synchronized int getNumberOfAccounts() {
		return numberOfAccounts;
	}

	/**
	 * Returns the balance of this account.
	 * 
	 * @return the balance of this account
	 */
	public double getBalance() {
		return this.balance;
	}

	/**
	 * Deposits the given amount in this account.
	 * 
	 * @param amount the amount to be deposited
	 * @throws IllegalArgumentException if the given amount is negative (the balance remains unchanged)
	 */
	public void deposit(double amount) throws IllegalArgumentException {
		if (amount < 0) {
			throw new IllegalArgumentException("A negative amount cannot be deposited");
		}
		this.balance += amount;
	}
	
	/**
	 * Tries to withdraw the given amount.  The withdrawal is successful if the resulting
	 * balance is non-negative.  Returns whether the withdrawal is successful.  If the
	 * withdrawal is not successful then the balance remains unchanged.
	 * 
	 * @param amount the amount to be withdrawn
	 * @return true if the withdrawal is successful, false otherwise
	 * @throws IllegalArgumentException if the given amount is negative (the balance remains unchanged)
	 */
	public boolean withdraw(double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("A negative amount cannot be withdrawn");
		}
		boolean success;
		if (this.balance >= amount) {
			this.balance -= amount;
			success = true;
		} else {
			success = false;
		}
		return success;
	}
}
