package org.hdfs.tools.mr.demo3;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.hdfs.tools.mr.tools.DeleteFile;

public class IndexCountTwo {
	
	public static void main(String[] args) throws IllegalArgumentException, IOException, ClassNotFoundException, InterruptedException {
		String rootPathStr = new String("E:/Workbench/mrdata/indexcount/");
		
		Configuration configuration = new Configuration();
		Job job = Job.getInstance(configuration);
		
		job.setJarByClass(IndexCountTwo.class);
		
		job.setMapperClass(IndexCountTwoMapper.class);
		job.setReducerClass(IndexCountTwoReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.setInputPaths(job, new Path(rootPathStr+"output1"));
		FileOutputFormat.setOutputPath(job, new Path(rootPathStr+"output2"));
		
		File file = new File(rootPathStr+"output2");
		if (file.exists()) {
			DeleteFile.delAllFile(file);
		}
		
		job.setNumReduceTasks(1);
		boolean waitForCompletion = job.waitForCompletion(true);
		
		System.exit(waitForCompletion?0:1);
	}
	
}

class IndexCountTwoMapper extends Mapper<LongWritable, Text, Text, Text> {
	@Override
	protected void map(LongWritable key, Text value, 
			Mapper<LongWritable, Text, Text, Text>.Context context)
					throws IOException, InterruptedException {
		String[] splits = value.toString().split(" ");
		context.write(new Text(splits[0]), new Text(splits[1].replaceAll("\t", "-->")));
	}
}

class IndexCountTwoReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		StringBuilder sBuilder = new StringBuilder();
		for (Text value : values) {
			sBuilder.append(value.toString() + "\t");
		}
		context.write(key, new Text(sBuilder.toString()));
	}
}