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
 * A thread that deposits one to its account.
 * 
 * @author Franck van Breugel
 */
public class Deposit extends Thread {
	private Account account;
	
	/**
	 * Initializes this deposit transaction with the given
	 * name and account.
	 * 
	 * @param name the name of this deposit transaction
	 * @param account the account of this deposit transaction
	 */
	public Deposit(String name, Account account) {
		super(name);
		this.account = account;
	}
	
	/**
	 * Deposits one to the account of this deposit transaction.
	 */
	public void run() {
		this.account.deposit(1);
	}
}
