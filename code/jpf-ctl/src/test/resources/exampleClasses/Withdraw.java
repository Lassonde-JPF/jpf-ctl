package exampleClasses;

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
