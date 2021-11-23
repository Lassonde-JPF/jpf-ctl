package example;

/**
 * An account with a balance.
 * 
 * @author Franck van Breugel
 */
public class Account {
	private double balance;

	/**
	 * Initializes the balance of this account to zero.
	 */
	public Account() {
		this.balance = 0;
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
		if (this.balance >= amount) {
			this.balance -= amount;
			return true;
		} else {
			return false;
		}
	}
}
