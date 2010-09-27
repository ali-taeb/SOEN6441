package DETServer;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.net.*;
import java.io.*;
import DETInterface.*;


public class Server
{
	public static Interface DETRef;
	public static void main(String args[])
   {
      String registryURL;
	  String myName="localhost";
	  int RMIPortNum=1099;

      try
	  {
		 startRegistry(RMIPortNum);

		 // To create a new server ("productname",quantity,itemprice,balance,item_price_change in ms)
         Impl ETrader1 = new Impl("apple",1000,1780,3000000,3000);
         registryURL = "rmi://"+myName+":" + RMIPortNum+ "/apple";
         Naming.rebind(registryURL, ETrader1);
         
         Impl ETrader3 = new Impl("bannana",200,2200,4000000,5000);
         registryURL = "rmi://"+myName+":" + RMIPortNum+ "/bannana";
         Naming.rebind(registryURL, ETrader3);
         
         
         Impl ETrader2 = new Impl("peach",1000,2500,5000000,10000);
         registryURL = "rmi://"+myName+":" + RMIPortNum+ "/peach";
         Naming.rebind(registryURL, ETrader2);
         
     	//DETRef = (Interface) Naming.lookup(registryURL);
		System.out.println("Lookup completed ");

		System.out.println("Server registered.  Registry currently contains:");
		listRegistry(registryURL);
		 
         System.out.println("Server ready.");
      }// end try
      catch (Exception re) {
         System.out.println("Exception in Server.main: " + re);
      } // end catch
  } // end main


	private static void listRegistry(String registryURL){
		try {
			String [ ] names = Naming.list(registryURL);
			for (int i=0; i < names.length; i++)
				System.out.println("E-Trader "+i+": "+names[i]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
   // This method starts a RMI registry on the local host, if it
   // does not already exists at the specified port number.
   private static void startRegistry(int RMIPortNum)
      throws RemoteException{
      try {
         Registry registry = LocateRegistry.getRegistry(RMIPortNum);
         registry.list( );  // This call will throw an exception
                            // if the registry does not already exist
         
      }
      catch (RemoteException e) {
         // No valid registry at that port.
/**/     System.out.println
/**/        ("RMI registry cannot be located at port "
/**/        + RMIPortNum);
         Registry registry =
            LocateRegistry.createRegistry(RMIPortNum);
/**/        System.out.println(
/**/           "RMI registry created at port " + RMIPortNum);
      }
   } // end startRegistry

} // end class
