package distributed.system;

import java.io.File;

import distributed.system.bank.Bank;
import distributed.system.bank.exception.InitBankServerException;
import distributed.system.thread.Client;

public class BankSystem {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Bank bank = new Bank("Concordia Bank");
		try {
			bank.init("input" + File.separator + "BranchA.txt");
		} catch (InitBankServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Client c1 = new Client("client 1", bank, Client.ACTION.DEPOSIT, "AA001", 100, 40);
		Client c2 = new Client("client 2", bank, Client.ACTION.WITHDRAW, "AA001", 150, 40);
		
		c2.start();
		c1.start();
		
	}

}
