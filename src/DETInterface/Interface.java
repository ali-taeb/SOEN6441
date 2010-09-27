package DETInterface;

import java.rmi.*;
import java.util.Hashtable;

import DETServer.Repository;

public interface Interface extends Remote {

   public double buyItem(String requestedItemName,int requestedQuantity) throws java.rmi.RemoteException;
   
   public double sellItem(String item,int quantity) throws java.rmi.RemoteException;
   
   public Hashtable printReport() throws java.rmi.RemoteException;
   
} //end interface
