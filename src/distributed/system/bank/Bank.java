package distributed.system.bank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import distributed.system.bank.exception.InitBankServerException;
import distributed.system.bank.exception.InsufficientBalanceException;
import distributed.system.bank.exception.NoSuchAccountException;

public class Bank {
	
	protected String branchName;
	
	protected Map<String, Account> accounts = new HashMap<String, Account>();
	
	public Bank(String branchName){
		this.branchName = branchName;
	}
	
	private Account getAccount(String accountNO) throws NoSuchAccountException{
		Account account = accounts.get(accountNO);
		if (account == null)
			throw new NoSuchAccountException("account " + accountNO + " does NOT exist in " + branchName);
		return account;
	}
	
	public float balance(String accountNO) throws NoSuchAccountException{
		return 0;
	}
	
	public void deposit(String accountNO, float amount) throws NoSuchAccountException{
		Account account = getAccount(accountNO);
			
		synchronized(account){
			float before = account.getBalance();
			account.setBalance(account.getBalance() + amount);
			float after = account.getBalance();
			System.out.println(Thread.currentThread().getName() + " : " + accountNO + " from " + before + " to " + after);

		}
	}
	
	public void withdraw(String accountNO, float amount) throws InsufficientBalanceException, NoSuchAccountException{
		Account account = getAccount(accountNO);
		
		synchronized(account){
			float balance = account.getBalance();
			if (balance < amount)
				throw new InsufficientBalanceException(balance+"<"+amount);
			
			account.setBalance(account.getBalance() - amount);
			float after = account.getBalance();
			System.out.println(Thread.currentThread().getName() + " : " + accountNO + " from " + balance + " to " + after);

		}
		
	}
	
	
	public void init(String fileName) throws InitBankServerException{
		try {
			File file = new File(fileName);
			BufferedReader br = 
				new BufferedReader(
						new FileReader(file));
			
			String accountNO = "", name = "", info = "";
			float amount;
			
			String line;
			while ( (line = br.readLine()) != null){
				int index = 0;
				StringTokenizer st = new StringTokenizer(line);
				while(st.hasMoreTokens()){
					index++;
					String token = st.nextToken();
					
					// accountNO
					if (index==1){
						accountNO = token;
						continue;
					}
					
					// name 
					if (index==2){
						name = token;
						continue;
					}
					
					// info
					if (index==3){
						info = token;
						continue;
					}
					
					// amount
					if (index == 4){
						amount = Float.parseFloat(token);
						Account account = new Account(accountNO, name, info, amount);
						this.accounts.put(account.getNo(), account);
						continue;
					}
				}
			}	// end of while
			
		} catch (FileNotFoundException e) {
			throw new InitBankServerException("File " + fileName + " is not found.");
		} catch (IOException e) {
			throw new InitBankServerException("IOException in parsing file " + fileName + " exception.");
		} catch (NumberFormatException e){
			throw new InitBankServerException("Amount should be float");
		}
	}
	
}
