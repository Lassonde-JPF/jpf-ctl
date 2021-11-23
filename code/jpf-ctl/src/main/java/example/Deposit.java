package example;

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
