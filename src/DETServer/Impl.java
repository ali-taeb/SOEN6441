package DETServer;
import java.rmi.*;
import java.rmi.server.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import DETInterface.*;

public class Impl extends UnicastRemoteObject  implements Interface
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Interface DETRef;
	private int PID=0;
	private Hashtable purchased = new Hashtable();
	private Timer timer;
	private ServerData myServer=null;

	// Locks
	private Object lock1 = new Object();
	private Object lock2 = new Object();
	private Object lock3 = new Object();

	// Connection Settings
	String hostName = "localhost";
	int RMIPort = 1099;

	// Constructor //
	public Impl(String itemName, int itemQuantity, int itemPrice, int balance, int mseconds) throws java.rmi.RemoteException
	{
		myServer= new ServerData(balance,itemName,itemPrice,itemQuantity);

		// Starting timer //
		timer = new Timer (); 
		timer.schedule ( new priceChange() , mseconds,mseconds) ;

	}


	// changes the item price randomly
	class priceChange extends TimerTask  {  
		private double itemPrice;
		private String itemName;
		
		public void run (){
			long seed=System.currentTimeMillis();
			Random randomGenerator = new Random(seed);
			int randomInt = randomGenerator.nextInt(100);
			itemName = myServer.getItemName();
			if (randomInt <= 50){
					synchronized(lock1){
					itemPrice=myServer.getItemPrice();
					itemPrice = Math.round(itemPrice + ((10*itemPrice)/100)); // +10%
					myServer.setItemPrice(itemPrice);
					System.out.println("Item price changing +10% for: "+ itemName + " New price: "+ itemPrice);
				}

			}
			else {

				synchronized(lock1){
					itemPrice=myServer.getItemPrice();
					double tempItemPrice  = Math.round(itemPrice - ((10*itemPrice)/100)); // -10%
					if (tempItemPrice > 1){
						myServer.setItemPrice(tempItemPrice);	
						System.out.println("Item price changing -10% for: " + itemName + " New price: "+ itemPrice);
					}

				}

			}
		}  
	}  

	// client order e-trader to buy from other e-traders //
	public double buyItem(String requestedItemName,int requestedQuantity) throws java.rmi.RemoteException
	{
		int itemQuantity;
		double balance;
		String itemName;
		
		

		itemName=myServer.getItemName();
		balance = myServer.getBalance();
		
		// CASE 1: A third-party e-trader is the owner of the item requested by the client
		if (!requestedItemName.equals(itemName))
		{

			String registryURL = "rmi://" + hostName + ":" + RMIPort
			+ "/" + requestedItemName;

			// find the remote object and cast it to an interface object
			try {
				DETRef = (Interface) Naming.lookup(registryURL);
			} catch (Exception e) {
				System.out.println("Exception occured on E-Trader\n");
			}

			synchronized(lock2)
			{
				double reqItemPrice=0;
				balance = myServer.getBalance();
				
				// invoke the remote method on third-party e-trader
				reqItemPrice = DETRef.buyItem(requestedItemName, requestedQuantity);

				// checks validity of the transaction
				if (reqItemPrice >0)
				{
					// Incrementing Unique Purchase Identifier
					PID++;
					// modifying balance ==> purchasing from a third-party e-trader (cash-out)

					balance = balance - (reqItemPrice*requestedQuantity);
					myServer.setBalance(balance);

					if (purchased.containsKey(requestedItemName)){
						Repository itemObj = new Repository();
						itemObj = (Repository) purchased.get(requestedItemName);
						itemObj.setItemQuantity(itemObj.getItemQuantity()+requestedQuantity);
						purchased.put("#PL#"+PID,new Repository(requestedItemName,reqItemPrice,requestedQuantity, registryURL));
						return reqItemPrice;
					} 
					else 
					{
						purchased.put("#PL#"+PID,new Repository(requestedItemName,reqItemPrice,requestedQuantity, registryURL));
						purchased.put(requestedItemName, new Repository(requestedItemName,requestedQuantity, registryURL));
						return reqItemPrice;
					}
				}

				else
					return -1;
			}
		} else 

			// Mutual Exclusion Start
			synchronized(lock2){
				double itemPrice=0;
				balance = myServer.getBalance();
				itemQuantity= myServer.getItemQuantity();
				
				// CASE 2: e-trader is the owner of the item which is requested by the client
				if (requestedQuantity <= itemQuantity && requestedItemName.equals(itemName)) {
					// reducing the quantity of the item

					itemQuantity=itemQuantity-requestedQuantity;
					myServer.setItemQuantity(itemQuantity);

					itemPrice=myServer.getItemPrice();
	
					// modifying balance ==> sales (cash-in)
					balance = balance + (requestedQuantity*itemPrice);
					myServer.setBalance(balance);

					System.out.println("Received! request from e-trder for buying item: " + 
							requestedItemName + " Quantity: "+requestedQuantity + ", Price(each): "+itemPrice+ ", Remaining Quantity: " + itemQuantity+"\n");

					return itemPrice; // returning the price ==> successful transaction

				} else
					return -1;  // returns -1 if item does not exist in the required quantity ==> unsuccessful transaction
			}
	}

	public Hashtable printReport() throws java.rmi.RemoteException
	{
		purchased.put("#REP#", this.myServer);
		return purchased;
	}

	public double sellItem(String item, int quantity) throws RemoteException
	{
		int itemQuantity;
		double balance;
		String itemName;
		
		itemName=myServer.getItemName();
		
		// CASE 1: E-trader is not the owner of the item which client requires it to sell ==? e-trader forward the request to owner e-trader
		if (!item.equals(itemName)) 
		{
			// checking the stock of itself 
			if (purchased.containsKey(item)) {
				Repository itemObj = new Repository();
				itemObj = (Repository) purchased.get(item);
				// check if item is available for selling in the quantity
				synchronized(lock3){
					double reqItemPrice;
					balance = myServer.getBalance();
					
					int stockItemQuantity = itemObj.getItemQuantity();
					if (quantity <= stockItemQuantity){

						String registryURL = "rmi://" + hostName + ":" + RMIPort
						+ "/" + item;

						// find the remote object and cast it to an interface object
						try {
							DETRef = (Interface) Naming.lookup(registryURL);
						} catch (Exception e) {
							System.out.println("Exception occured on E-Trader\n");
						}

						// invoke the remote method on third-party e-trader
						reqItemPrice = DETRef.sellItem(item, quantity);			

						// Reducing quantity of item being sold to other e-trader from e-trader repository
						if (reqItemPrice >0) {
							itemObj.setItemQuantity(itemObj.getItemQuantity()-quantity);
							balance = balance + (reqItemPrice*quantity); // (cash-in)
							myServer.setBalance(balance);
							return reqItemPrice; // Transaction Successful
						} else
							return -1;	// Transaction Not Successful
					}

				}

			}
		} 

		// Mutual Exclusion Start
		synchronized(lock3){
			double itemPrice=0;
			itemPrice=myServer.getItemPrice();
			balance = myServer.getBalance();
			itemQuantity= myServer.getItemQuantity();
			
			// CASE 2: e-trader is the owner of the item which is requested by the client to sell ==> e-trader will buy
			if (item.equals(itemName) && balance >= (quantity*itemPrice)) {
				// reducing the quantity of the item   quantity <= itemQuantity 

				// adding the item to e-trader repository
				itemQuantity=itemQuantity+quantity;
				myServer.setItemQuantity(itemQuantity);

				// modifying balance ==> buying (cash-out)
				balance = balance - (quantity*itemPrice);
				myServer.setBalance(balance);

				System.out.println("Received! request from e-trder for selling item: " + 
						item+ ", Quantity: "+quantity +"  Price(each): " + itemPrice+ ", New Balance: " +balance+", New Quantity: " +itemQuantity+"\n");
				return itemPrice; // returning the price ==> successful transaction
			} else
				return -1;  // returns -1 if item does not exist in the required quantity ==> unsuccessful transaction
		}
	}

} // end class
