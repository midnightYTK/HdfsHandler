package org.hdfs.tools.mr.demo5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.hdfs.tools.mr.tools.DeleteFile;

/**
 * 原始数据：
 * 	order001,u001,小米6,1999.9,2
 *  order001,u001,雀巢咖啡,99.0,2
 *  order001,u001,安慕希,250.0,2
 *  order001,u001,经典红双喜,200.0,4
 *  order001,u001,防水电脑包,400.0,2
 *  order002,u002,小米手环,199.0,3
 *  order002,u002,榴莲,15.0,10
 *  order002,u002,苹果,4.5,20
 *  order002,u002,肥皂,10.0,40
 *  order003,u001,小米6,1999.9,2
 *  order003,u001,雀巢咖啡,99.0,2
 *  order003,u001,安慕希,250.0,2
 *  order003,u001,经典红双喜,200.0,4
 *  order003,u001,防水电脑包,400.0,2
 *  
 * @author TheDK
 *
 */
public class OrderTopNDemo {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		
		String rootPathStr = new String("E:/Workbench/mrdata/ordertopn/");
		
		Configuration configuration = new Configuration();
		Job job = Job.getInstance(configuration);
		
		job.setJarByClass(OrderTopNDemo.class);
		
		job.setMapperClass(OrderTopNDemoMapper.class);
		job.setReducerClass(OrderTopNDemoReducer.class);
		
//		job.setGroupingComparatorClass(OrderIdGroupingComparator.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(OrderBean.class);
		job.setOutputKeyClass(OrderBean.class);
		job.setOutputValueClass(NullWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path(rootPathStr+"input"));
		FileOutputFormat.setOutputPath(job, new Path(rootPathStr+"output"));
		
		File file = new File(rootPathStr+"output");
		if (file.exists()) {
			DeleteFile.delAllFile(file);
		}
		
		job.setNumReduceTasks(1);
		boolean waitForCompletion = job.waitForCompletion(true);
		
		System.exit(waitForCompletion?0:1);
	}

}


class OrderTopNDemoMapper extends Mapper<LongWritable, Text, Text, OrderBean> {
	
	OrderBean ob = new OrderBean();
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, OrderBean>.Context context)
			throws IOException, InterruptedException {
		String[] splits = value.toString().split(",");
		ob.setOrderBean(splits[0], splits[1], splits[2], Float.parseFloat(splits[3]),
				Integer.parseInt(splits[4]));
		context.write(new Text(splits[0]), ob);
	}
}


class OrderTopNDemoReducer extends Reducer<Text, OrderBean, OrderBean, NullWritable> {
	
	@Override
	protected void reduce(Text key, Iterable<OrderBean> values,
			Reducer<Text, OrderBean, OrderBean, NullWritable>.Context context)
			throws IOException, InterruptedException {
		
		ArrayList<OrderBean> obList = new ArrayList<OrderBean>();
		for (OrderBean orderBean : values) {
			OrderBean oBean = new OrderBean();
			oBean = orderBean.clone();
			obList.add(oBean);
		}
		obList.sort(null);
		for(int i = 0; i <= 2; i++) {
			context.write(obList.get(i), NullWritable.get());
		}
	}
	
}