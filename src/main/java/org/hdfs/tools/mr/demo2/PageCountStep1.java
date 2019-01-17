package org.hdfs.tools.mr.demo2;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PageCountStep1 {
	
	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException, ClassNotFoundException {
		
		String rootPathStr = new String("E:/Workbench/mrdata/uricount/");
		
		// 在代码中配置JVM参数，用于给job对象来获取访问HDFS的用户身份,以下所有注释部分都是从Windows向yarn提交需要配置/设置的参数
//		System.setProperty("HADOOP_USER_NAME", "root");
		
		Configuration configuration = new Configuration();
//		configuration.addResource("conf/core-site.xml");
//		configuration.addResource("conf/hdfs-site.xml");
//		configuration.addResource("conf/mapred-site.xml");
//		configuration.addResource("conf/yarn-site.xml");
        
		Job job = Job.getInstance(configuration);
		
//		job.setJar("wc.jar");
		job.setJarByClass(PageCountStep1.class);
		
		job.setMapperClass(PageCountStep1Mapper.class);
		job.setReducerClass(PageCountStep1Reducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path(rootPathStr+"input"));
		FileOutputFormat.setOutputPath(job, new Path(rootPathStr+"output"));
		
//		FileSystem fs = FileSystem.get(
//				new URI("hdfs://hadoop111:9000/"), configuration, "root");
//		if ( fs.exists(outputPath) ) {
//			fs.delete(outputPath, true);
//		}
		
		File file = new File(rootPathStr+"output");
		if (file.exists()) {
			delAllFile(file);
		}
		
		job.setNumReduceTasks(3);
		boolean waitForCompletion = job.waitForCompletion(true);
		
		System.exit(waitForCompletion?0:1);
		
	}
	
	
	private static void delAllFile(File file) {
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if (listFiles.length != 0) {
				for( File sonFile : listFiles ) {
					delAllFile(sonFile);
				}
			}
		}
		file.delete();
	}

}


class PageCountStep1Mapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		String[] lineSplits = line.split(" ");
		context.write(new Text(lineSplits[1]), new IntWritable(1));
	}
}

class PageCountStep1Reducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		int count = 0;
		Iterator<IntWritable> iterator = values.iterator();
		while( iterator.hasNext() ) {
			IntWritable nextValue = iterator.next();
			count += nextValue.get();
		}
		context.write(key, new IntWritable(count));
	}
}