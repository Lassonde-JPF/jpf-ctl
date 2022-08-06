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
 * A thread that withdraws one from its account.
 * 
 * @author Franck van Breugel
 */
public class Withdraw extends Thread {
	private Account account;
	
	/**
	 * Initializes this withdraw transaction with the given
	 * name and account.
	 * 
	 * @param name the name of this withdraw transaction
	 * @param account the account of this withdraw transaction
	 */
	public Withdraw(String name, Account account) {
		super(name);
		this.account = account;
	}
	
	/**
	 * Attempts to withdraw one from the account of this withdraw transaction.
	 */
	public void run() {
		this.account.withdraw(1);
	}
}
