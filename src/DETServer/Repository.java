package DETServer;

import java.io.Serializable;

public class Repository implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double itemPrice;
	private int itemQuantity;
	private String itemName;
	private String purchasedFrom;
	
	// constructor //
	public Repository(String itemName, double itemPrice,int itemQuantity, String purchasedFrom) {
		//super();
		this.itemName = itemName;
		this.itemPrice= itemPrice;
		this.itemQuantity = itemQuantity;
		this.setPurchasedFrom(purchasedFrom);
	
	}
	
	// constructor //
	public Repository(String itemName,int itemQuantity, String purchasedFrom) {
		//super();
		this.itemName = itemName;
		this.itemQuantity = itemQuantity;
		this.setPurchasedFrom(purchasedFrom);
	
	}

	
	
	// constructor //
	public Repository() {
		//super();
	
	}
	
	public double getItemPrice() {
		return itemPrice;
	}
	
	public void setItemPrice(double itemPrice) {
		this.itemPrice = itemPrice;
	}
	
	public int getItemQuantity() {
		return itemQuantity;
	}
	
	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setPurchasedFrom(String purchasedFrom) {
		this.purchasedFrom = purchasedFrom;
	}

	public String getPurchasedFrom() {
		return purchasedFrom;
	}
	
	
}
