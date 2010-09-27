package distributed.system.bank;

public class Account {

	private String no;
	private String name;
	private String info;
	private float balance;
	
	public Account(String no, String name, String info, float balance){
		this.no = no;
		this.name = name;
		this.info = info;
		this.balance = balance;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public float getBalance() {
		return balance;
	}

	public float setBalance(float balance) {
		this.balance = balance;
		return this.balance;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(this.no);
		sb.append(" ");
		sb.append(this.name);
		sb.append(" : ");
		sb.append(this.balance);
		return sb.toString();
	}
	
}
