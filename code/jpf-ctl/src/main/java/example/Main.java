package example;

/**
 * 
 * @author Franck van Breugel
 */
public class Main {
	/**
	 * Whether the balance of the account is negative.
	 */
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
		args = new String[] {
				"1", "2", "2"
		};
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
