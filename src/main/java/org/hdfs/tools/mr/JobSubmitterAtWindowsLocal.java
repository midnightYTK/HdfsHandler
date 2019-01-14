package org.hdfs.tools.mr;

import java.io.File;

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

		File file = new File("E:/Workbench/mrdata/wordcount/output");
		if (file.exists()) {
			delAllFile(file);
		}

		FileInputFormat.setInputPaths(job, new Path("E:/Workbench/mrdata/wordcount/input"));
		FileOutputFormat.setOutputPath(job, new Path("E:/Workbench/mrdata/wordcount/output"));

		job.setNumReduceTasks(1);

		boolean res = job.waitForCompletion(true);
		System.exit(res ? 0 : 1);

	}

	public static void delAllFile(File directory) {
		if (!directory.isDirectory()) {
			directory.delete();
		} else {
			File[] files = directory.listFiles();
			if (files.length != 0)
				for (File file : files) {
					delAllFile(file);
				}
			directory.delete();
			return;
		}
	}

}