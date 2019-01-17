package org.hdfs.tools.mr.demo2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class PageCount implements WritableComparable<PageCount>{
	
	private String uri;
	private int count;
	
	public PageCount() { }
	
	public void setPageCount(String uri, int count) {
		this.uri = uri;
		this.count = count;
	}
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}


	public void write(DataOutput out) throws IOException {
		out.writeUTF(uri);
		out.writeInt(count);
	}


	public void readFields(DataInput in) throws IOException {
		this.uri = in.readUTF();
		this.count = in.readInt();
	}
	
	
	public String toString() {
		return this.uri + ", " + this.count;
	}
	
	public int compareTo(PageCount o) {
		return o.getCount() - this.getCount() == 0 ? 
				o.getUri().compareTo(this.getUri()) : o.getCount() - this.getCount();
	}
	
	
}
