package org.hdfs.tools.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class JobSubmitterAtWindowsLocal {
	
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		
		// 如果在windows本地执行，则无需配置（仍然需要配置Windows的HADOOP_HOME），默认情况下的fs.FileSystem是local
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(JobSubmitterLinuxToYarn.class);
		
		job.setMapperClass(WordcountMapper.class);
		job.setReducerClass(WordcountReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path("f:/mrdata/wordcount/input"));
		FileOutputFormat.setOutputPath(job, new Path("f:/mrdata/wordcount/output"));
		
		job.setNumReduceTasks(3);
		
		boolean res = job.waitForCompletion(true);
		System.exit(res?0:1);
		
	}
}