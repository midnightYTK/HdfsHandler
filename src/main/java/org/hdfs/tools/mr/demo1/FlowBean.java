package org.hdfs.tools.mr.demo1;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

public class FlowBean implements WritableComparable<FlowBean> {
	
	private String phone;
	private int upFlow;
	private int downFlow;
	private int sumFlow;
	
	public FlowBean(){}
	
	public FlowBean(String phone, int upFlow, int downFlow, int sumFlow) {
		this.phone = phone;
		this.upFlow = upFlow;
		this.downFlow = downFlow;
		this.sumFlow = upFlow + downFlow;
	}
	

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getUpFlow() {
		return upFlow;
	}
	public void setUpFlow(int upFlow) {
		this.upFlow = upFlow;
	}
	public int getDownFlow() {
		return downFlow;
	}
	public void setDownFlow(int downFlow) {
		this.downFlow = downFlow;
	}
	public int getSumFlow() {
		return sumFlow;
	}
	public void setSumFlow(int sumFlow) {
		this.sumFlow = sumFlow;
	}
	
	// 序列化
	public void write(DataOutput out) throws IOException {
		out.writeInt(upFlow);
		out.writeInt(downFlow);
		out.writeInt(sumFlow);
		out.writeUTF(phone);
	}
	
	// 反序列化
	public void readFields(DataInput in) throws IOException {
		this.upFlow = in.readInt();
		this.downFlow = in.readInt();
		this.sumFlow = in.readInt();
		this.phone = in.readUTF();
	}
	
	// 
	public String toSting() {
		return this.phone + ", " + this.upFlow + ", " + this.downFlow + ", " + this.sumFlow; 
	}
	
	
	public int compareTo(FlowBean o) {
		return o.getSumFlow()-this.getSumFlow()==0 ?
				this.getPhone().compareTo(o.getPhone()) : o.getSumFlow()-this.getSumFlow();
	}
	
	
}
