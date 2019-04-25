package org.hdfs.tools.mr.demo5;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class OrderBean implements WritableComparable<OrderBean>,Cloneable {
	
	private String OrderID;
	private String userID;
	private String pdtName;
	private float price;
	private int number;
	private float amount;
	
	public OrderBean() {}
	
	public OrderBean(String orderID, String userID, String pdtName, float price, int number) {
		this.OrderID = orderID;
		this.userID = userID;
		this.pdtName = pdtName;
		this.price = price;
		this.number = number;
		this.amount = price * number;
	}
	
	public void setOrderBean(String orderID, String userID, String pdtName, float price, int number) {
		this.OrderID = orderID;
		this.userID = userID;
		this.pdtName = pdtName;
		this.price = price;
		this.number = number;
		this.amount = price * number;
	}
	
	
	public String getOrderID() {
		return OrderID;
	}
	public void setOrderID(String orderID) {
		OrderID = orderID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPdtName() {
		return pdtName;
	}
	public void setPdtName(String pdtName) {
		this.pdtName = pdtName;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}

	public void write(DataOutput out) throws IOException {
		out.writeUTF(this.OrderID);
		out.writeUTF(this.userID);
		out.writeUTF(this.pdtName);
		out.writeFloat(this.price);
		out.writeInt(this.number);
		out.writeFloat(this.amount);
		
	}

	public void readFields(DataInput in) throws IOException {
		this.OrderID = in.readUTF();
		this.userID = in.readUTF();
		this.pdtName = in.readUTF();
		this.price = in.readFloat();
		this.number = in.readInt();
		this.amount = in.readFloat();
	}

	public int compareTo(OrderBean o) {
		int compare = (int) (o.getAmount()-this.getAmount());
		if(compare == 0) {
			return this.getPdtName().compareTo(o.getPdtName());
		} else {
			return compare;
		}
	}
	
	@Override
	public String toString() {
		return this.OrderID + ", " + this.userID + ", " + this.pdtName + ", " 
			+ this.price + ", " + this.number + ", " + this.amount;
	}
	
	@Override
	protected OrderBean clone() {
		OrderBean obBean = null;
		try {
			obBean = (OrderBean) super.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obBean;
	}
	
}
