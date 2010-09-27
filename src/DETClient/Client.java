package DETClient;

import java.io.*;
import java.rmi.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.CyclicBarrier;

import DETInterface.*;
import DETServer.Repository;
import DETServer.ServerData;

public class Client {
	public static Interface DETRef;
	public static int MaxThreadNumber=1;
	public static BuyThread[] buyThreadArray=new BuyThread[MaxThreadNumber];
	public static SellThread[] sellThreadArray=new SellThread[MaxThreadNumber];
	public static CyclicBarrier barrier=new CyclicBarrier(MaxThreadNumber);


	public static void main(String args[]) {
		try {

			int requestedQuantity=0;
			String requestedItemName="";
			// Connection Settings
			String hostName = "localhost";
			int RMIPort = 1099;
			String etraderName="peach";
			String registryURL = "rmi://" + hostName + ":" + RMIPort
			+"/"+ etraderName;

			// find the remote object and cast it to an interface object
			DETRef = (Interface) Naming.lookup(registryURL);
			System.out.println("Lookup completed - connected to: " +registryURL );

			String[] tokenStr = new String[10];

			while (true) {
				// ask for the input from the user
				System.out.println("Thread Numbers: "+ MaxThreadNumber+"\n");
				System.out.println("Commands: \n");
				System.out.println("buy [item] [quantity]");
				System.out.println("sell [item] [quantity]");
				System.out.println("connect [etrader]");
				System.out.println("maxthread [thread_number]");
				System.out.println("print");
				System.out.println("exit\n");

				System.out.println("Please enter command:\n");
				System.out.print(">> ");
				InputStreamReader is = new InputStreamReader(System.in);
				BufferedReader br = new BufferedReader(is);
				String command = br.readLine();

				StringTokenizer st = new StringTokenizer(command);

				int j = 0;

				while (st.hasMoreTokens())
					tokenStr[j++] =(st.nextToken());

				command = tokenStr[0];

				if (command.equals("exit")) {
					System.exit(0);

				} 
				
				else if (command.equals("connect")){
					etraderName = tokenStr[1];

					registryURL = "rmi://" + hostName + ":" + RMIPort
					+"/"+ etraderName;

					// find the remote object and cast it to an interface object
					DETRef = (Interface) Naming.lookup(registryURL);
					System.out.println("Lookup completed - connected to: " +registryURL +"\n");
				}

				else if (command.equals("maxthread")){
					MaxThreadNumber= Integer.parseInt(tokenStr[1]);
					buyThreadArray=new BuyThread[MaxThreadNumber];
					sellThreadArray=new SellThread[MaxThreadNumber];
					CyclicBarrier barrier=new CyclicBarrier(MaxThreadNumber);
					System.out.println("Maximum Thread(s) Allowed: " +MaxThreadNumber +"\n");
				}

				else if (command.equals("sell")){
					requestedItemName = tokenStr[1];
					requestedQuantity = Integer.parseInt(tokenStr[2]);
					// check if a client try to sell item(s) from stock of an e-trader (which it is manager of it) to it self

					if(!requestedItemName.equals(etraderName)){

						for (int i=0; i<MaxThreadNumber; i++)
						{
							sellThreadArray[i]=new SellThread(requestedItemName,requestedQuantity,i);
							sellThreadArray[i].start();
						}

						for (int i=0; i<MaxThreadNumber; i++)
							sellThreadArray[i].join();	
					}
					else 
						System.out.println("Can't sell own item!\n");


				} 

				else if (command.equals("print"))
				{
					PrintOut(registryURL);
				} 

				else if (command.equals("buy"))
				{	
					requestedItemName = tokenStr[1];
					requestedQuantity = Integer.parseInt(tokenStr[2]);
					if(!requestedItemName.equals(etraderName)){
						for (int i=0; i<MaxThreadNumber; i++)
						{
							buyThreadArray[i]=new BuyThread(requestedItemName,requestedQuantity,i);
							buyThreadArray[i].start();
						}

						for (int i=0; i<MaxThreadNumber; i++)
							buyThreadArray[i].join();		

					} else
						System.out.println("Can't buy own item!\n");

				} else System.out.println("Command Not Found!");

			}

		} // end try

		catch (Exception e) {
			System.out.println("Exception in Client: " + e);

		}
	} // end main

	public static void PrintOut(String registryURL) throws RemoteException{
		Hashtable outPut = new Hashtable();
		outPut = (Hashtable) DETRef.printReport();

		Set set = outPut.keySet(); 
		Iterator itr = set.iterator();
		String key=null;


		System.out.println("Print out for: "+registryURL+" : \n");
		ServerData myRep = (ServerData) outPut.get("#REP#");
		System.out.println("Item Name: "+ myRep.getItemName());
		System.out.println("Item Price: "+ myRep.getItemPrice());
		System.out.println("Item Quantity: "+ myRep.getItemQuantity());
		System.out.println("Balance: "+ myRep.getBalance() + "\n");

		System.out.println("Item(s) in Stock:\n");

		while (itr.hasNext()) {
			key = (String) itr.next();
			if (!key.startsWith("#PL#") && !key.startsWith("#REP#")){
				Repository itemPurchased = (Repository) outPut.get(key);
				System.out.println("Item Name: "+ itemPurchased.getItemName());
				System.out.println("Item Quantity: "+ itemPurchased.getItemQuantity());
				System.out.println("Purchsed From: "+ itemPurchased.getPurchasedFrom()+"\n");

			}
		} 

		System.out.println("Purchase History: \n");

		set = null;
		itr = null;
		set = outPut.keySet();
		itr = set.iterator();

		while (itr.hasNext()) {
			key = (String) itr.next();
			if (key.startsWith("#PL#")) {
				Repository itemPurchased = (Repository) outPut.get(key);
				System.out.println("Item Name: "+ itemPurchased.getItemName());
				System.out.println("Item Quantity: "+ itemPurchased.getItemQuantity());
				System.out.println("Item Price: " + itemPurchased.getItemPrice());
				System.out.println("Purchsed From: "+ itemPurchased.getPurchasedFrom()+"\n");

			}
		} 
	}

	public static class BuyThread extends Thread
	{		
		private int requestedQuantity=0;
		private String requestedItemName="";
		private double systemResponse =0;
		private int ThreadID = 0;

		public BuyThread(String requestedItemName,int requestedQuantity, int ThreadID){
			this.requestedQuantity = requestedQuantity;
			this.requestedItemName = requestedItemName;
			this.ThreadID = ThreadID;
		}


		public void run()
		{
			try
			{
				barrier.await();	
				// check if a client try to buy item(s) from stock of an e-trader (which it is manager of it) to it self

				systemResponse = DETRef.buyItem(requestedItemName, requestedQuantity);

				if (systemResponse > 0)
					System.out.println("THREAD ID: "+ ThreadID+" item purchased with price: " +systemResponse + " each \n");				 
				else
					System.out.println("THREAD ID: "+ ThreadID+ " The required item doesn't exist in required quantity\n");


			}
			catch(Exception er)
			{
				System.out.println(er);
			}
		}
	}


	public static class SellThread extends Thread
	{		
		private int requestedQuantity=0;
		private String requestedItemName="";
		private double systemResponse =0;
		private int ThreadID = 0;

		// Constructor //
		public SellThread(String requestedItemName,int requestedQuantity, int ThreadID){
			this.requestedQuantity = requestedQuantity;
			this.requestedItemName = requestedItemName;
			this.ThreadID = ThreadID;
		}

		public void run()
		{
			try
			{
				barrier.await();	

				// invoke the remote method
				systemResponse = DETRef.sellItem(requestedItemName, requestedQuantity);


				if (systemResponse > 0)
					System.out.println("THREAD ID: "+ ThreadID+" Item(s) sold with price: " + systemResponse + " each\n");				 
				else
					System.out.println("THREAD ID: "+ ThreadID+ " Either the balance of the buyer is not enough or the you don't have enough quantity in e-trader stock\n");

			}
			catch(Exception er)
			{
				System.out.println(er);
			}
		}
	}

}// end class


