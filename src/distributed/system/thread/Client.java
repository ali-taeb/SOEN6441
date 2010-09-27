package distributed.system.thread;

import distributed.system.bank.Bank;
import distributed.system.bank.exception.InsufficientBalanceException;
import distributed.system.bank.exception.NoSuchAccountException;

public class Client extends Thread {

	public enum ACTION {DEPOSIT, WITHDRAW, TRANSFER, BALANCE};
	
	private ACTION action;
	
	private Bank bank;
	private String accountNO1, accountNO2;
	private float amount;
	private int totalTimes;
	
	public Client(String name, Bank bank, ACTION action, String accountNO1, float amount, int totalTimes){
		this(name, bank, action, accountNO1, null, amount, totalTimes);
	}
	
	public Client(String name, Bank bank, ACTION action, String accountNO1, String accountNO2, float amount, int totalTimes){
		super(name);
		this.bank = bank;
		this.action = action;
		this.accountNO1 = accountNO1;
		this.accountNO2 = accountNO2;
		this.amount = amount;
		this.totalTimes = totalTimes;
	}
	
	public void run(){
		switch (this.action){
		case DEPOSIT:
			for (int t = 0; t<this.totalTimes; t++){
				try {
					this.bank.deposit(this.accountNO1, this.amount);
				} catch (NoSuchAccountException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case WITHDRAW:
			for (int t = 0; t<this.totalTimes; t++){
				try {
					this.bank.withdraw(this.accountNO1, this.amount);
				} catch (NoSuchAccountException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InsufficientBalanceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case TRANSFER:
			break;
		case BALANCE:
			for (int t = 0; t<this.totalTimes; t++){
				try {
					this.bank.balance(this.accountNO1);
				} catch (NoSuchAccountException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
			default:
				// none
			
		}
		
	}
	
	
	
}
