package org.hdfs.tools.mr.demo5;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class OrderIdPartitioner extends Partitioner<OrderBean, NullWritable>{

	@Override
	public int getPartition(OrderBean key, NullWritable value, int numPartitions) {
		// 按照订单的orderid来分发数据
		return (key.getOrderID().hashCode() & Integer.MAX_VALUE) % numPartitions;
	}

}
