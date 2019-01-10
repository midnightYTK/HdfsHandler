package org.hdfs.tools.mr;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WordcountReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
	
	@Override
	protected void reduce(Text key, Iterable<IntWritable> value,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context)
					throws IOException, InterruptedException {
		
		Iterator<IntWritable> iterator = value.iterator();
		int count = 0;
		
		while (iterator.hasNext()) {
			count += iterator.next().get();
		}
		context.write(key, new IntWritable(count));
	}
	
}
