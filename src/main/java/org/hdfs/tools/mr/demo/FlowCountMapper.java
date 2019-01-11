package org.hdfs.tools.mr.demo;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FlowCountMapper extends Mapper<LongWritable, Text, Text, FlowBean>{
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, FlowBean>.Context context)
			throws IOException, InterruptedException {
		
		String line = value.toString();
		String[] words = line.split("\t");
		
		int up = Integer.parseInt(words[words.length-3]);
		int down = Integer.parseInt(words[words.length-2]);
		int sum = up + down;
		
		context.write(new Text(words[1]), new FlowBean(words[1], up, down, sum));
		
		
	}

}
